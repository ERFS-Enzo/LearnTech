package com.curso.learntech.application;

import com.curso.learntech.domain.Aluno;
import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Colaborador;
import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.FuncaoColaborador;
import com.curso.learntech.domain.Inscricao;
import com.curso.learntech.domain.Pessoa;
import com.curso.learntech.domain.Status;
import com.curso.learntech.domain.StatusInscricao;
import com.curso.learntech.repository.AlunoRepository;
import com.curso.learntech.repository.CategoriaCursoRepository;
import com.curso.learntech.repository.ColaboradorRepository;
import com.curso.learntech.repository.CursoRepository;
import com.curso.learntech.repository.InscricaoRepository;
import com.curso.learntech.repository.PessoaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Não é @Transactional: o serviço abre e confirma (ou desfaz) a própria transação.
// Por isso os dados são gravados de verdade e removidos no @AfterEach.
@SpringBootTest
@ActiveProfiles("test")
class InscricaoServiceTest {

    @Autowired private InscricaoService inscricaoService;
    @Autowired private CursoService cursoService;
    @Autowired private PessoaService pessoaService;
    @Autowired private AlunoService alunoService;
    @Autowired private ColaboradorService colaboradorService;
    @Autowired private CategoriaCursoService categoriaService;

    @Autowired private InscricaoRepository inscricaoRepository;
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private ColaboradorRepository colaboradorRepository;
    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private CategoriaCursoRepository categoriaRepository;

    private Long inscricaoId;
    private Long alunoId;
    private Long atendenteId;
    private Long categoriaId;
    private final List<Long> pessoaIds = new ArrayList<>();
    private final List<Long> cursoIds = new ArrayList<>();

    @AfterEach
    void limparDados() {
        if (inscricaoId != null) {
            inscricaoRepository.deleteById(inscricaoId);
        }
        cursoIds.forEach(cursoRepository::deleteById);
        if (categoriaId != null) {
            categoriaRepository.deleteById(categoriaId);
        }
        if (atendenteId != null) {
            colaboradorRepository.deleteById(atendenteId);
        }
        if (alunoId != null) {
            alunoRepository.deleteById(alunoId);
        }
        pessoaIds.forEach(pessoaRepository::deleteById);
    }

    @Test
    void deveFinalizarInscricaoBaixandoUmaVagaDeCadaCurso() {
        prepararCenario(3, 5);

        Inscricao finalizada = inscricaoService.finalizar(inscricaoId);

        assertEquals(StatusInscricao.FINALIZADA, finalizada.getStatus());
        assertEquals(2, vagasDoCurso(0));
        assertEquals(4, vagasDoCurso(1));
    }

    @Test
    void deveDesfazerTodasAsBaixasQuandoUmCursoNaoTemVaga() {
        prepararCenario(3, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> inscricaoService.finalizar(inscricaoId));

        // O primeiro curso chegou a ter a vaga baixada em memória, mas a transação
        // inteira foi desfeita: o banco continua com o valor original.
        assertEquals(3, vagasDoCurso(0));
        assertEquals(0, vagasDoCurso(1));

        Inscricao relida = inscricaoRepository.findOneById(inscricaoId).orElseThrow();
        assertEquals(StatusInscricao.ABERTA, relida.getStatus());
    }

    private int vagasDoCurso(int posicao) {
        return cursoRepository.findById(cursoIds.get(posicao))
                .orElseThrow()
                .getVagasDisponiveis();
    }

    private void prepararCenario(int vagasPrimeiroCurso, int vagasSegundoCurso) {
        long marca = System.nanoTime();

        Pessoa pessoaAluno = pessoaService.cadastrar(new Pessoa(
                "Aluno Teste", "aluno" + marca + "@exemplo.com",
                cpfValido(marca), LocalDate.now()));
        Pessoa pessoaAtendente = pessoaService.cadastrar(new Pessoa(
                "Atendente Teste", "atendente" + marca + "@exemplo.com",
                cpfValido(marca + 1), LocalDate.now()));
        pessoaIds.add(pessoaAluno.getId());
        pessoaIds.add(pessoaAtendente.getId());

        Aluno aluno = alunoService.cadastrar(pessoaAluno.getId(), "11999990000");
        Colaborador atendente = colaboradorService.cadastrar(
                pessoaAtendente.getId(), "COL-" + marca,
                FuncaoColaborador.ATENDENTE, LocalDate.now());
        alunoId = aluno.getId();
        atendenteId = atendente.getId();

        CategoriaCurso categoria = categoriaService.cadastrar("Categoria Inscricao " + marca);
        categoriaId = categoria.getId();

        int codigoBase = (int) (marca % 900_000_000L) + 1000;
        Curso primeiro = cursoService.cadastrar(
                novoCurso(codigoBase, "Curso Um"), categoriaId, null);
        Curso segundo = cursoService.cadastrar(
                novoCurso(codigoBase + 1, "Curso Dois"), categoriaId, null);
        cursoIds.add(primeiro.getId());
        cursoIds.add(segundo.getId());

        cursoService.definirVagas(primeiro.getId(), vagasPrimeiroCurso);
        cursoService.definirVagas(segundo.getId(), vagasSegundoCurso);

        Inscricao inscricao = inscricaoService.abrir(aluno.getId(), atendente.getId());
        inscricaoId = inscricao.getId();
        inscricaoService.adicionarItem(inscricaoId, primeiro.getId());
        inscricaoService.adicionarItem(inscricaoId, segundo.getId());
    }

    private Curso novoCurso(int codigo, String descricao) {
        return new Curso(
                codigo,
                descricao,
                new BigDecimal("40"),
                new BigDecimal("100.00"),
                LocalDate.now(),
                3,
                Status.ATIVO);
    }

    // CPF fictício com dígitos verificadores corretos, único por execução.
    private static String cpfValido(long semente) {
        String base = "1" + String.format("%08d", Math.abs(semente) % 100_000_000L);
        int primeiro = digito(base, 10);
        int segundo = digito(base + primeiro, 11);
        return base + primeiro + segundo;
    }

    private static int digito(String base, int pesoInicial) {
        int soma = 0;
        for (int indice = 0; indice < base.length(); indice++) {
            soma += Character.getNumericValue(base.charAt(indice)) * (pesoInicial - indice);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}