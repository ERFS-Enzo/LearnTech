package com.curso.learntech.api.controller;

import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.Status;
import com.curso.learntech.repository.CategoriaCursoRepository;
import com.curso.learntech.repository.CursoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CursoOperacoesApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaCursoRepository categoriaRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Test
    void deveAlterarCursoERetornar200() throws Exception {
        CategoriaCurso origem = criarCategoria("Origem PUT");
        CategoriaCurso destino = criarCategoria("Destino PUT");
        Curso curso = criarCurso(origem, 9001, "Curso original");

        String json = """
                {
                  "descricao": "Curso alterado",
                  "valorCurso": 59.90,
                  "quantidadeModulos": 8,
                  "categoriaId": %d,
                  "autorId": null
                }
                """.formatted(destino.getId());

        mockMvc.perform(put("/api/cursos/{id}", curso.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Curso alterado"))
                .andExpect(jsonPath("$.quantidadeModulos").value(8))
                .andExpect(jsonPath("$.categoriaId").value(destino.getId()))
                .andExpect(jsonPath("$.codigoCurso").value(9001));
    }

    @Test
    void deveRetornar404AoAlterarCursoInexistente() throws Exception {
        CategoriaCurso categoria = criarCategoria("Categoria 404 PUT");

        String json = """
                {
                  "descricao": "Qualquer",
                  "valorCurso": 10.00,
                  "quantidadeModulos": 1,
                  "categoriaId": %d
                }
                """.formatted(categoria.getId());

        mockMvc.perform(put("/api/cursos/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar409AoAlterarCategoriaParaNomeDuplicado() throws Exception {
        criarCategoria("Alfa Duplicada");
        CategoriaCurso beta = criarCategoria("Beta Duplicada");

        mockMvc.perform(put("/api/categorias/{id}", beta.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"ALFA DUPLICADA\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar400QuandoStatusInvalido() throws Exception {
        Curso curso = criarCurso(criarCategoria("Categoria Status"), 9002, "Curso status");

        mockMvc.perform(put("/api/cursos/{id}/status", curso.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"XYZ\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveInativarCursoERetornarStatus() throws Exception {
        Curso curso = criarCurso(criarCategoria("Categoria Inativar"), 9003, "Curso inativar");

        mockMvc.perform(put("/api/cursos/{id}/status", curso.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INATIVO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));
    }

    @Test
    void deveAcrescentarCargaHoraria() throws Exception {
        Curso curso = criarCurso(criarCategoria("Categoria Acrescimo"), 9004, "Curso acréscimo");

        mockMvc.perform(post("/api/cursos/{id}/carga-horaria/acrescimos", curso.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\": 5.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cargaHoraria").value(15.5));
    }

    @Test
    void deveRetornar400EManterCargaHorariaQuandoReducaoExcedeOLimite() throws Exception {
        Curso curso = criarCurso(criarCategoria("Categoria Reducao"), 9005, "Curso redução");

        mockMvc.perform(post("/api/cursos/{id}/carga-horaria/reducoes", curso.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\": 10.001}"))
                .andExpect(status().isBadRequest());

        Curso recarregado = cursoRepository.findById(curso.getId()).orElseThrow();
        assertEquals(
                0,
                new BigDecimal("10.000").compareTo(recarregado.getCargaHoraria()));
    }

    @Test
    void devePesquisarApenasCursosCompativeisComOFiltro() throws Exception {
        CategoriaCurso categoria = criarCategoria("Categoria Filtro");
        criarCurso(categoria, 9006, "Java Spring Boot");
        criarCurso(categoria, 9007, "Python para Dados");

        mockMvc.perform(get("/api/cursos")
                        .param("descricao", "spring")
                        .param("categoriaId", categoria.getId().toString())
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "descricao,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo.length()").value(1))
                .andExpect(jsonPath("$.conteudo[0].descricao").value("Java Spring Boot"))
                .andExpect(jsonPath("$.totalElementos").value(1));
    }

    @Test
    void deveRetornarMetadadosCorretosDaPagina() throws Exception {
        CategoriaCurso categoria = criarCategoria("Categoria Pagina");
        criarCurso(categoria, 9008, "Curso A");
        criarCurso(categoria, 9009, "Curso B");

        mockMvc.perform(get("/api/cursos")
                        .param("categoriaId", categoria.getId().toString())
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "descricao,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo.length()").value(1))
                .andExpect(jsonPath("$.pagina").value(0))
                .andExpect(jsonPath("$.tamanho").value(1))
                .andExpect(jsonPath("$.totalElementos").value(2))
                .andExpect(jsonPath("$.totalPaginas").value(2))
                .andExpect(jsonPath("$.primeira").value(true))
                .andExpect(jsonPath("$.ultima").value(false));
    }

    @Test
    void deveRetornar400QuandoOrdenacaoInvalida() throws Exception {
        mockMvc.perform(get("/api/cursos").param("sort", "cpf,asc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveExcluirCursoERetornar204() throws Exception {
        Curso curso = criarCurso(criarCategoria("Categoria Excluir"), 9010, "Curso excluir");

        mockMvc.perform(delete("/api/cursos/{id}", curso.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/cursos/{id}", curso.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar409AoExcluirCategoriaComCursos() throws Exception {
        CategoriaCurso categoria = criarCategoria("Categoria Em Uso");
        criarCurso(categoria, 9011, "Curso em uso");

        mockMvc.perform(delete("/api/categorias/{id}", categoria.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void deveExcluirCategoriaSemCursos() throws Exception {
        CategoriaCurso categoria = criarCategoria("Categoria Vazia");

        mockMvc.perform(delete("/api/categorias/{id}", categoria.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categorias/{id}", categoria.getId()))
                .andExpect(status().isNotFound());
    }

    private CategoriaCurso criarCategoria(String nome) {
        return categoriaRepository.save(new CategoriaCurso(nome));
    }

    private Curso criarCurso(CategoriaCurso categoria, int codigo, String descricao) {
        Curso curso = new Curso(
                codigo,
                descricao,
                new BigDecimal("10.000"),
                new BigDecimal("100.00"),
                LocalDate.of(2026, 8, 20),
                3,
                Status.ATIVO);
        categoria.adicionarCurso(curso);
        return cursoRepository.save(curso);
    }
}