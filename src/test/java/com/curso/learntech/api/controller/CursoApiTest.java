package com.curso.learntech.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.curso.learntech.domain.Autor;
import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.repository.AutorRepository;
import com.curso.learntech.repository.CategoriaCursoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CursoApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaCursoRepository categoriaRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Test
    void deveCadastrarCursoERetornar201() throws Exception {
        CategoriaCurso categoria = categoriaRepository.save(new CategoriaCurso("Categoria API"));
        Autor autor = autorRepository.save(new Autor(
                "Autor API", "52998224725"));

        String json = """
                {
                  "codigoCurso": "1",
                  "descricao": "Curso criado pela API",
                  "cargaHoraria": 10.000,
                  "valorCurso": 49.90,
                  "quantidadeModulos": 5,
                  "categoriaId": %d,
                  "autorId": %d
                }
                """.formatted(categoria.getId(), autor.getId());

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.codigoCurso").value("1"));
    }

    @Test
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        String json = """
                {
                  "codigoCurso": 1,
                  "descricao": "",
                  "cargaHoraria": 10,
                  "valorCurso": -5,
                  "quantidadeModulos": 3,
                  "categoriaId": 1
                }
                """;

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.descricao").exists())
                .andExpect(jsonPath("$.fields.valorCurso").exists());
    }

    @Test
    void deveRetornar404QuandoCursoNaoExiste() throws Exception {
        mockMvc.perform(get("/api/cursos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar409QuandoCodigoDoCursoJaExiste() throws Exception {
        CategoriaCurso categoria = categoriaRepository.save(new CategoriaCurso("Categoria 409"));

        String json = """
                {
                  "codigoCurso": 77,
                  "descricao": "Curso duplicado",
                  "cargaHoraria": 10,
                  "valorCurso": 49.90,
                  "quantidadeModulos": 2,
                  "categoriaId": %d
                }
                """.formatted(categoria.getId());

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }
}
