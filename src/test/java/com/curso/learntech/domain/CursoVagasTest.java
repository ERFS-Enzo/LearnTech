package com.curso.learntech.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CursoVagasTest {

    @Test
    void cursoNovoNaoPossuiVagas() {
        assertEquals(0, novoCurso().getVagasDisponiveis());
    }

    @Test
    void deveDefinirEOcuparVagas() {
        Curso curso = novoCurso();

        curso.definirVagas(2);
        curso.ocuparVaga();

        assertEquals(1, curso.getVagasDisponiveis());
    }

    @Test
    void naoDeveOcuparVagaQuandoNaoHaVagas() {
        Curso curso = novoCurso();

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                curso::ocuparVaga);

        assertEquals("Curso sem vagas disponíveis: Spring Boot", excecao.getMessage());
        assertEquals(0, curso.getVagasDisponiveis());
    }

    @Test
    void naoDeveDefinirVagasNegativas() {
        Curso curso = novoCurso();

        assertThrows(IllegalArgumentException.class, () -> curso.definirVagas(-1));
    }

    private Curso novoCurso() {
        return new Curso(
                101,
                "Spring Boot",
                new BigDecimal("40"),
                new BigDecimal("100.00"),
                LocalDate.of(2026, 10, 28),
                3,
                Status.ATIVO);
    }
}