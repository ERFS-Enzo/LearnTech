package com.curso.learntech.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PessoaTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 10, 28);

    @Test
    void deveCriarPessoaAtivaComDadosNormalizados() {
        Pessoa pessoa = new Pessoa(
                "  Ana Souza ", " ANA@Exemplo.COM ", "52998224725", HOJE);

        assertEquals("Ana Souza", pessoa.getNome());
        assertEquals("ana@exemplo.com", pessoa.getEmail());
        assertEquals("52998224725", pessoa.getCpf());
        assertEquals(Status.ATIVO, pessoa.getStatus());
        assertEquals(HOJE, pessoa.getDataCadastro());
    }

    @Test
    void naoDeveCriarPessoaComCpfInvalido() {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> new Pessoa("Ana", "ana@exemplo.com", "52998224724", HOJE));

        assertEquals("CPF inválido", excecao.getMessage());
    }

    @Test
    void naoDeveCriarPessoaComEmailInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Pessoa("Ana", "ana-sem-arroba", "52998224725", HOJE));
    }

    @Test
    void naoDeveCriarPessoaSemNome() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Pessoa("  ", "ana@exemplo.com", "52998224725", HOJE));
    }

    @Test
    void deveAlterarDadosEInativarSemApagarIdentidade() {
        Pessoa pessoa = new Pessoa("Ana", "ana@exemplo.com", "52998224725", HOJE);

        pessoa.alterarDados("Ana Maria", "ana.maria@exemplo.com");
        pessoa.inativar();

        assertEquals("Ana Maria", pessoa.getNome());
        assertEquals("ana.maria@exemplo.com", pessoa.getEmail());
        assertEquals("52998224725", pessoa.getCpf());
        assertEquals(Status.INATIVO, pessoa.getStatus());
    }
}