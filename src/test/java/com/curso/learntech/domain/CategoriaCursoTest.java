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

        categoria.adicionarCurso(curso);

        assertEquals(1, categoria.getCursos().size());
        assertSame(curso, categoria.getCursos().get(0));
        assertSame(categoria, curso.getCategoria());
    }

    @Test
    void naoDeveAdicionarCursoNulo() {
        CategoriaCurso categoria = new CategoriaCurso("Programação");

        assertThrows(
                NullPointerException.class,
                () -> categoria.adicionarCurso(null));
    }

    @Test
    void naoDeveAdicionarDoisCursosComOMesmoCodigo() {
        CategoriaCurso categoria = new CategoriaCurso("Programação");
        categoria.adicionarCurso(novoCurso(1));

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> categoria.adicionarCurso(novoCurso(1)));

        assertEquals(
                "Codigo do curso ja utilizado!",
                excecao.getMessage());
    }

    @Test
    void naoDevePermitirQueCursoPertençaADuasCategorias() {
        CategoriaCurso programacao = new CategoriaCurso("Programação");
        CategoriaCurso desenvolvimentoWeb = new CategoriaCurso("Desenvolvimento Web");
        Curso curso = novoCurso(1);

        programacao.adicionarCurso(curso);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> desenvolvimentoWeb.adicionarCurso(curso));

        assertEquals(
                "O curso já pertence a outra categoria",
                excecao.getMessage());
    }

    @Test
    void naoDeveExporUmaListaInternaModificavel() {
        CategoriaCurso categoria = new CategoriaCurso("Programação");
        Curso curso = novoCurso(1);

        categoria.adicionarCurso(curso);

        assertThrows(
                UnsupportedOperationException.class,
                () -> categoria.getCursos().add(novoCurso(2)));
    }

    @Test
    void deveAlterarONomeDaCategoria() {
        CategoriaCurso categoria = new CategoriaCurso("Programação");

        categoria.alterarNome("Desenvolvimento");

        assertEquals("Desenvolvimento", categoria.getNome());
    }

    @Test
    void naoDeveAlterarONomeParaBranco() {
        CategoriaCurso categoria = new CategoriaCurso("Programação");

        assertThrows(
                IllegalArgumentException.class,
                () -> categoria.alterarNome("   "));
        assertEquals("Programação", categoria.getNome());
    }

    @Test
    void deveTrocarDeCategoriaMantendoOsDoisLadosDaAssociacao() {
        CategoriaCurso origem = new CategoriaCurso("Programação");
        CategoriaCurso destino = new CategoriaCurso("Desenvolvimento Web");
        Curso curso = novoCurso(1);
        origem.adicionarCurso(curso);

        curso.alterarCategoria(destino);

        assertSame(destino, curso.getCategoria());
        assertEquals(0, origem.getCursos().size());
        assertEquals(1, destino.getCursos().size());
        assertSame(curso, destino.getCursos().get(0));
    }

    @Test
    void naoDeveTrocarParaCategoriaQueJaPossuiOMesmoCodigo() {
        CategoriaCurso origem = new CategoriaCurso("Programação");
        CategoriaCurso destino = new CategoriaCurso("Desenvolvimento Web");
        Curso emOrigem = novoCurso(1);
        origem.adicionarCurso(emOrigem);
        destino.adicionarCurso(novoCurso(1));

        assertThrows(
                IllegalArgumentException.class,
                () -> emOrigem.alterarCategoria(destino));

        assertSame(origem, emOrigem.getCategoria());
        assertEquals(1, origem.getCursos().size());
    }

    private Curso novoCurso(int codigoCurso) {
        return new Curso(
                codigoCurso,
                "C# e .NET para Iniciantes",
                new java.math.BigDecimal("40"),
                new java.math.BigDecimal("149.90"),
                java.time.LocalDate.of(2026, 8, 20),
                10,
                Status.ATIVO);
    }
}