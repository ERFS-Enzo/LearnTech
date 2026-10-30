package com.curso.learntech.api.controller;

import com.curso.learntech.domain.Aluno;
import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Colaborador;
import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.FuncaoColaborador;
import com.curso.learntech.domain.Inscricao;
import com.curso.learntech.domain.ItemInscricao;
import com.curso.learntech.domain.Pessoa;
import com.curso.learntech.domain.Status;
import com.curso.learntech.repository.AlunoRepository;
import com.curso.learntech.repository.CategoriaCursoRepository;
import com.curso.learntech.repository.ColaboradorRepository;
import com.curso.learntech.repository.CursoRepository;
import com.curso.learntech.repository.InscricaoRepository;
import com.curso.learntech.repository.PessoaRepository;
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
import java.time.LocalDateTime;

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
class InscricaoApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private ColaboradorRepository colaboradorRepository;
    @Autowired private CategoriaCursoRepository categoriaRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private InscricaoRepository inscricaoRepository;

    @Test
    void mesmaPessoaPodeSerAlunoEColaboradorMasNaoDuasVezesAluno() throws Exception {
        Pessoa pessoa = criarPessoa("Ana Souza", "ana@exemplo.com", "52998224725");

        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pessoaId\": %d, \"telefone\": \"11999990000\"}"
                                .formatted(pessoa.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Ana Souza"));

        mockMvc.perform(post("/api/colaboradores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pessoaId": %d, "matricula": "COL-9",
                                 "funcao": "ATENDENTE", "dataAdmissao": "2026-01-15"}
                                """.formatted(pessoa.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pessoaId").value(pessoa.getId()));

        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pessoaId\": %d}".formatted(pessoa.getId())))
                .andExpect(status().isConflict());
    }

    @Test
    void deveAbrirInscricaoERetornar201() throws Exception {
        Aluno aluno = criarAluno();
        Colaborador atendente = criarColaborador(FuncaoColaborador.ATENDENTE);

        mockMvc.perform(post("/api/inscricoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(abrirJson(aluno, atendente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ABERTA"))
                .andExpect(jsonPath("$.alunoNome").value("Ana Souza"))
                .andExpect(jsonPath("$.itens.length()").value(0));
    }

    @Test
    void deveRetornar400QuandoAtendenteNaoPodeRegistrarInscricoes() throws Exception {
        Aluno aluno = criarAluno();
        Colaborador suporte = criarColaborador(FuncaoColaborador.SUPORTE);

        mockMvc.perform(post("/api/inscricoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(abrirJson(aluno, suporte)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveManterOValorPraticadoQuandoOPrecoDoCursoMuda() throws Exception {
        Inscricao inscricao = abrirInscricao();
        Curso curso = criarCurso(7001, "Spring Boot", "100.00", 5);

        mockMvc.perform(post("/api/inscricoes/{id}/itens", inscricao.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cursoId\": %d}".formatted(curso.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(100.0));

        String alteracao = """
                {
                  "descricao": "Spring Boot",
                  "valorCurso": 200.00,
                  "quantidadeModulos": 3,
                  "categoriaId": %d
                }
                """.formatted(curso.getCategoria().getId());

        mockMvc.perform(put("/api/cursos/{id}", curso.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alteracao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorCurso").value(200.0));

        mockMvc.perform(get("/api/inscricoes/{id}", inscricao.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(100.0))
                .andExpect(jsonPath("$.itens[0].valorPraticado").value(100.0));
    }

    @Test
    void deveFinalizarBaixandoVagaERetornar409AoRepetir() throws Exception {
        Inscricao inscricao = abrirInscricao();
        Curso curso = criarCurso(7003, "Spring Data", "80.00", 1);
        adicionarItem(inscricao, curso);

        mockMvc.perform(post("/api/inscricoes/{id}/finalizacao", inscricao.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZADA"));

        assertEquals(
                0,
                cursoRepository.findById(curso.getId()).orElseThrow().getVagasDisponiveis());

        mockMvc.perform(post("/api/inscricoes/{id}/finalizacao", inscricao.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar400AoFinalizarInscricaoSemCursos() throws Exception {
        Inscricao inscricao = abrirInscricao();

        mockMvc.perform(post("/api/inscricoes/{id}/finalizacao", inscricao.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400AoFinalizarQuandoCursoNaoTemVagas() throws Exception {
        Inscricao inscricao = abrirInscricao();
        Curso semVagas = criarCurso(7004, "Curso Lotado", "80.00", 0);
        adicionarItem(inscricao, semVagas);

        mockMvc.perform(post("/api/inscricoes/{id}/finalizacao", inscricao.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404AoRemoverItemInexistente() throws Exception {
        Inscricao inscricao = abrirInscricao();

        mockMvc.perform(delete("/api/inscricoes/{id}/itens/{itemId}",
                        inscricao.getId(), Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar409AoExcluirCursoQueJaConstaEmInscricao() throws Exception {
        Inscricao inscricao = abrirInscricao();
        Curso curso = criarCurso(7006, "Curso Em Uso", "70.00", 2);
        adicionarItem(inscricao, curso);

        mockMvc.perform(delete("/api/cursos/{id}", curso.getId()))
                .andExpect(status().isConflict());
    }

    private Pessoa criarPessoa(String nome, String email, String cpf) {
        return pessoaRepository.save(new Pessoa(nome, email, cpf, LocalDate.now()));
    }

    private Aluno criarAluno() {
        Pessoa pessoa = criarPessoa("Ana Souza", "ana@exemplo.com", "52998224725");
        return alunoRepository.save(new Aluno(pessoa, "11999990000"));
    }

    private Colaborador criarColaborador(FuncaoColaborador funcao) {
        Pessoa pessoa = criarPessoa("Bruno Lima", "bruno@exemplo.com", "11144477735");
        return colaboradorRepository.save(
                new Colaborador(pessoa, "COL-001", funcao, LocalDate.now()));
    }

    private Inscricao abrirInscricao() {
        return inscricaoRepository.save(new Inscricao(
                criarAluno(),
                criarColaborador(FuncaoColaborador.ATENDENTE),
                LocalDateTime.now()));
    }

    private Curso criarCurso(int codigo, String descricao, String valor, int vagas) {
        CategoriaCurso categoria = categoriaRepository.save(
                new CategoriaCurso("Categoria " + codigo));
        Curso curso = new Curso(
                codigo,
                descricao,
                new BigDecimal("40"),
                new BigDecimal(valor),
                LocalDate.of(2026, 10, 28),
                3,
                Status.ATIVO);
        categoria.adicionarCurso(curso);
        curso.definirVagas(vagas);
        return cursoRepository.save(curso);
    }

    // Adiciona o item pelo domínio e força o INSERT para o item ter ID.
    private ItemInscricao adicionarItem(Inscricao inscricao, Curso curso) {
        ItemInscricao item = inscricao.adicionarItem(curso);
        inscricaoRepository.saveAndFlush(inscricao);
        return item;
    }

    private String abrirJson(Aluno aluno, Colaborador atendente) {
        return "{\"alunoId\": %d, \"atendenteId\": %d}"
                .formatted(aluno.getId(), atendente.getId());
    }
}