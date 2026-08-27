package com.curso.learntech.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoriaCursoTest {

    @Test
    void deveAdicionarCursoEManejarOsDoisLadosDaAssociacao() {
        CategoriaCurso categoria = new CategoriaCurso("Programação");
        Curso curso = novoCurso(1);

        categoria.adicionarCategoria(curso);

        assertEquals(1, categoria.getCursos().size());
        assertSame(curso, categoria.getCursos().get(0));
        assertSame(categoria, curso.getCategoria());
    }

    @Test
    void naoDeveAdicionarCursoNulo() {
        CategoriaCurso categoria = new CategoriaCurso("Programação");

        assertThrows(
                NullPointerException.class,
                () -> categoria.adicionarCategoria(null));
    }

    @Test
    void naoDeveAdicionarDoisCursosComOMesmoCodigo() {
        CategoriaCurso categoria = new CategoriaCurso("Programação");
        categoria.adicionarCategoria(novoCurso(1));

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> categoria.adicionarCategoria(novoCurso(1)));

        assertEquals(
                "Codigo do curso ja utilizado!",
                excecao.getMessage());
    }

    @Test
    void naoDevePermitirQueCursoPertençaADuasCategorias() {
        CategoriaCurso programacao = new CategoriaCurso("Programação");
        CategoriaCurso desenvolvimentoWeb = new CategoriaCurso("Desenvolvimento Web");
        Curso curso = novoCurso(1);

        programacao.adicionarCategoria(curso);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> desenvolvimentoWeb.adicionarCategoria(curso));

        assertEquals(
                "O curso já pertence a outra categoria",
                excecao.getMessage());
    }

    @Test
    void naoDeveExporUmaListaInternaModificavel() {
        CategoriaCurso categoria = new CategoriaCurso("Programação");
        Curso curso = novoCurso(1);

        categoria.adicionarCategoria(curso);

        assertThrows(
                UnsupportedOperationException.class,
                () -> categoria.getCursos().add(novoCurso(2)));
    }

    private Curso novoCurso(int codigoCurso) {
        return new Curso(
                codigoCurso,
                "C# e .NET para Iniciantes",
                new java.math.BigDecimal("40"),
                new java.math.BigDecimal("149.90"),
                java.time.LocalDate.of(2026, 8, 20),
                Status.ATIVO);
    }
}