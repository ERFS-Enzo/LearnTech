package com.curso.learntech.domain;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CursoTest {

    @Test
    void deveCriarCursoAtivoComDadosValidos() {
        Curso curso = novoCurso("40", "149.90");

        assertEquals(1, curso.getCodigoCurso());
        assertEquals("C# e .NET para Iniciantes", curso.getDescricao());
        assertEquals(
                0,
                new BigDecimal("40").compareTo(curso.getCargaHoraria()));
        assertEquals(
                0,
                new BigDecimal("149.90").compareTo(curso.getValorCurso()));
        assertEquals(Status.ATIVO, curso.getStatus());
        assertEquals(
                LocalDate.of(2026, 8, 20),
                curso.getDataCadastro());
    }

    @Test
    void naoDeveCriarCursoComCodigoEmBranco() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Curso(
                        -1,
                        "C# e .NET para Iniciantes",
                        new BigDecimal("40"),
                        new BigDecimal("149.90"),
                        LocalDate.of(2026, 8, 20),
                        5,
                        Status.ATIVO));
    }

    @Test
    void naoDeveCriarCursoComCargaHorariaNegativa() {
        assertThrows(
                IllegalArgumentException.class,
                () -> novoCurso("-1", "149.90"));
    }

    @Test
    void naoDeveCriarCursoComPrecoNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> novoCurso("40", "-10.00"));
    }

    @Test
    void deveAlterarDescricao() {
        Curso curso = novoCurso("40", "149.90");

        curso.alterarDescricao("ASP.NET Core para APIs");

        assertEquals(
                "ASP.NET Core para APIs",
                curso.getDescricao());
    }

    @Test
    void deveAlterarCargaHoraria() {
        Curso curso = novoCurso("40", "149.90");

        curso.alterarCargaHoraria(new BigDecimal("60"));

        assertEquals(
                0,
                new BigDecimal("60")
                        .compareTo(curso.getCargaHoraria()));
    }

    @Test
    void deveAlterarPreco() {
        Curso curso = novoCurso("40", "149.90");

        curso.alterarValorCurso(new BigDecimal("199.90"));

        assertEquals(
                0,
                new BigDecimal("199.90")
                        .compareTo(curso.getValorCurso()));
    }

    @Test
    void deveAlterarOStatusPorComportamentoExplicito() {
        Curso curso = novoCurso("40", "149.90");

        curso.inativar();
        assertEquals(Status.INATIVO, curso.getStatus());

        curso.ativar();
        assertEquals(Status.ATIVO, curso.getStatus());
    }

    @Test
    void deveCalcularValorPorHora() {
        Curso curso = novoCurso("40", "149.90");

        BigDecimal valorPorHora = curso.calcularValorPorHora();

        assertEquals(
                0,
                new BigDecimal("3.75").compareTo(valorPorHora));
    }

    @Test
    void naoDeveCriarCursoComCargaHorariaZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> novoCurso("0", "149.90"));
    }

    @Test
    void deveAlterarQuantidadeDeModulos() {
        Curso curso = novoCurso("40", "149.90");

        curso.alterarQuantidadeModulos(12);

        assertEquals(12, curso.getQuantidadeModulos());
    }

    @Test
    void naoDeveAlterarQuantidadeDeModulosParaValorNegativo() {
        Curso curso = novoCurso("40", "149.90");

        assertThrows(
                IllegalArgumentException.class,
                () -> curso.alterarQuantidadeModulos(-1));
        assertEquals(9, curso.getQuantidadeModulos());
    }

    @Test
    void deveAtribuirERemoverAutor() {
        Curso curso = novoCurso("40", "149.90");
        Autor autor = new Autor("Autor Teste", "11111111111");

        curso.alterarAutor(autor);
        assertSame(autor, curso.getAutor());

        curso.alterarAutor(null);
        assertNull(curso.getAutor());
    }

    @Test
    void deveAcrescentarCargaHoraria() {
        Curso curso = novoCurso("40", "149.90");

        curso.acrescentarCargaHoraria(new BigDecimal("10.5"));

        assertEquals(
                0,
                new BigDecimal("50.5").compareTo(curso.getCargaHoraria()));
    }

    @Test
    void deveReduzirCargaHoraria() {
        Curso curso = novoCurso("40", "149.90");

        curso.reduzirCargaHoraria(new BigDecimal("15"));

        assertEquals(
                0,
                new BigDecimal("25").compareTo(curso.getCargaHoraria()));
    }

    @Test
    void naoDeveReduzirCargaHorariaAteZerarOuNegativar() {
        Curso curso = novoCurso("40", "149.90");

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> curso.reduzirCargaHoraria(new BigDecimal("40")));

        assertEquals("Carga horária insuficiente", excecao.getMessage());
        assertEquals(
                0,
                new BigDecimal("40").compareTo(curso.getCargaHoraria()));
    }

    @Test
    void naoDeveMovimentarCargaHorariaComQuantidadeNaoPositiva() {
        Curso curso = novoCurso("40", "149.90");

        assertThrows(
                IllegalArgumentException.class,
                () -> curso.acrescentarCargaHoraria(BigDecimal.ZERO));
        assertThrows(
                IllegalArgumentException.class,
                () -> curso.reduzirCargaHoraria(new BigDecimal("-1")));
    }

    private Curso novoCurso(String cargaHoraria, String preco) {
        return new Curso(
                1,
                "C# e .NET para Iniciantes",
                new BigDecimal(cargaHoraria),
                new BigDecimal(preco),
                LocalDate.of(2026, 8, 20),
                9,
                Status.ATIVO);
    }
}