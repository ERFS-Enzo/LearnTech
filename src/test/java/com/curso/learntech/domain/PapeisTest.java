package com.curso.learntech.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PapeisTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 10, 28);

    @Test
    void mesmaPessoaPodeSerAlunoEColaboradorSemHeranca() {
        Pessoa pessoa = new Pessoa("Ana", "ana@exemplo.com", "52998224725", HOJE);

        Aluno aluno = new Aluno(pessoa, "11999990000");
        Colaborador colaborador = new Colaborador(
                pessoa, "COL-001", FuncaoColaborador.ATENDENTE, HOJE);

        assertSame(pessoa, aluno.getPessoa());
        assertSame(pessoa, colaborador.getPessoa());
    }

    @Test
    void deveNormalizarTelefoneVazioParaNulo() {
        Pessoa pessoa = new Pessoa("Ana", "ana@exemplo.com", "52998224725", HOJE);

        Aluno aluno = new Aluno(pessoa, "   ");
        assertNull(aluno.getTelefone());

        aluno.alterarTelefone(" 11988887777 ");
        assertEquals("11988887777", aluno.getTelefone());
    }

    @Test
    void somenteAtendenteECoordenadorPodemRegistrarInscricoes() {
        Pessoa pessoa = new Pessoa("Ana", "ana@exemplo.com", "52998224725", HOJE);

        assertTrue(novoColaborador(pessoa, FuncaoColaborador.ATENDENTE)
                .podeRegistrarInscricoes());
        assertTrue(novoColaborador(pessoa, FuncaoColaborador.COORDENADOR)
                .podeRegistrarInscricoes());
        assertFalse(novoColaborador(pessoa, FuncaoColaborador.SUPORTE)
                .podeRegistrarInscricoes());
        assertFalse(novoColaborador(pessoa, FuncaoColaborador.FINANCEIRO)
                .podeRegistrarInscricoes());
    }

    @Test
    void naoDeveCriarColaboradorSemMatricula() {
        Pessoa pessoa = new Pessoa("Ana", "ana@exemplo.com", "52998224725", HOJE);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Colaborador(pessoa, " ", FuncaoColaborador.ATENDENTE, HOJE));
    }

    private Colaborador novoColaborador(Pessoa pessoa, FuncaoColaborador funcao) {
        return new Colaborador(pessoa, "COL-001", funcao, HOJE);
    }
}