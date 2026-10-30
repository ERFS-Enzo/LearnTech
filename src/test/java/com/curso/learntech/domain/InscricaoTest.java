package com.curso.learntech.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InscricaoTest {

    private static final LocalDate DIA = LocalDate.of(2026, 10, 28);
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 28, 10, 0);

    @Test
    void deveAbrirInscricaoComStatusAberta() {
        Inscricao inscricao = novaInscricao();

        assertEquals(StatusInscricao.ABERTA, inscricao.getStatus());
        assertEquals(0, inscricao.getItens().size());
    }

    @Test
    void deveManterValorHistoricoQuandoPrecoDoCursoMuda() {
        Inscricao inscricao = novaInscricao();
        Curso curso = novoCurso(101, "Spring Boot", "100.00");

        inscricao.adicionarItem(curso);
        curso.alterarValorCurso(new BigDecimal("150.00"));

        assertEquals(new BigDecimal("100.00"), inscricao.calcularTotal());
        assertEquals(
                new BigDecimal("100.00"),
                inscricao.getItens().get(0).getValorPraticado());
    }

    @Test
    void deveCalcularTotalSomandoOsValoresPraticados() {
        Inscricao inscricao = novaInscricao();

        inscricao.adicionarItem(novoCurso(101, "Spring Boot", "100.00"));
        inscricao.adicionarItem(novoCurso(102, "Java Básico", "49.90"));

        assertEquals(new BigDecimal("149.90"), inscricao.calcularTotal());
    }

    @Test
    void naoDeveIncluirOMesmoCursoDuasVezes() {
        Inscricao inscricao = novaInscricao();
        Curso curso = novoCurso(101, "Spring Boot", "100.00");
        inscricao.adicionarItem(curso);

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> inscricao.adicionarItem(curso));

        assertEquals("Curso já incluído na inscrição", excecao.getMessage());
        assertEquals(1, inscricao.getItens().size());
    }

    @Test
    void naoDeveFinalizarInscricaoSemCursos() {
        Inscricao inscricao = novaInscricao();

        assertThrows(
                IllegalArgumentException.class,
                () -> inscricao.finalizar(AGORA));
        assertEquals(StatusInscricao.ABERTA, inscricao.getStatus());
    }

    @Test
    void deveFinalizarRegistrandoADataDeFinalizacao() {
        Inscricao inscricao = novaInscricao();
        inscricao.adicionarItem(novoCurso(101, "Spring Boot", "100.00"));

        inscricao.finalizar(AGORA);

        assertEquals(StatusInscricao.FINALIZADA, inscricao.getStatus());
        assertEquals(AGORA, inscricao.getDataFinalizacao());
    }

    @Test
    void naoDevePermitirAlteracaoDepoisDeFinalizada() {
        Inscricao inscricao = novaInscricao();
        inscricao.adicionarItem(novoCurso(101, "Spring Boot", "100.00"));
        inscricao.finalizar(AGORA);

        assertThrows(
                IllegalStateException.class,
                () -> inscricao.adicionarItem(novoCurso(102, "Java Básico", "49.90")));
        assertThrows(IllegalStateException.class, inscricao::cancelar);
        assertThrows(IllegalStateException.class, () -> inscricao.finalizar(AGORA));
    }

    @Test
    void deveCancelarInscricaoAbertaENaoAceitarNovosItens() {
        Inscricao inscricao = novaInscricao();

        inscricao.cancelar();

        assertEquals(StatusInscricao.CANCELADA, inscricao.getStatus());
        assertThrows(
                IllegalStateException.class,
                () -> inscricao.adicionarItem(novoCurso(101, "Spring Boot", "100.00")));
    }

    @Test
    void naoDeveRemoverItemQueNaoPertenceAInscricao() {
        Inscricao inscricao = novaInscricao();

        assertThrows(
                IllegalArgumentException.class,
                () -> inscricao.removerItem(999L));
    }

    private Inscricao novaInscricao() {
        Pessoa pessoaAluno = new Pessoa("Ana", "ana@exemplo.com", "52998224725", DIA);
        Pessoa pessoaAtendente = new Pessoa(
                "Bruno", "bruno@exemplo.com", "11144477735", DIA);

        Aluno aluno = new Aluno(pessoaAluno, "11999990000");
        Colaborador atendente = new Colaborador(
                pessoaAtendente, "COL-001", FuncaoColaborador.ATENDENTE, DIA);

        return new Inscricao(aluno, atendente, AGORA);
    }

    private Curso novoCurso(int codigo, String descricao, String valor) {
        return new Curso(
                codigo,
                descricao,
                new BigDecimal("40"),
                new BigDecimal(valor),
                DIA,
                3,
                Status.ATIVO);
    }
}