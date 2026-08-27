package com.curso.learntech.domain;

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

    private Curso novoCurso(String cargaHoraria, String preco) {
        return new Curso(
                1,
                "C# e .NET para Iniciantes",
                new BigDecimal(cargaHoraria),
                new BigDecimal(preco),
                LocalDate.of(2026, 8, 20),
                Status.ATIVO);
    }
}