package com.curso.learntech.domain;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ContaAcessoTest {

    private Pessoa pessoa() {
        return new Pessoa(
                "Enzo11",
                "enzoenzo@gmail.com",
                "52998224725",
                LocalDate.of(2026, 1, 1));
    }

    @Test
    void deveNormalizarLoginEProtegerColecaoDePerfis() {
        ContaAcesso conta = new ContaAcesso(
                pessoa(),
                "  ENZO  ",
                "{bcrypt}hash",
                Set.of(PerfilAcesso.OPERADOR));

        assertEquals("enzo", conta.getLogin());

        assertThrows(
                UnsupportedOperationException.class,
                () -> conta.getPerfis().add(
                        PerfilAcesso.ADMINISTRADOR));
    }

    @Test
    void naoDeveAutenticarContaInativaOuPessoaInativa() {
        Pessoa pessoa = pessoa();

        ContaAcesso conta = new ContaAcesso(
                pessoa,
                "enzo",
                "{bcrypt}hash",
                Set.of(PerfilAcesso.OPERADOR));

        assertTrue(conta.podeAutenticar());

        conta.alterarStatus(false);
        assertFalse(conta.podeAutenticar());

        conta.alterarStatus(true);
        pessoa.inativar();
        assertFalse(conta.podeAutenticar());
    }

    @Test
    void deveRejeitarPerfisVazios() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ContaAcesso(
                        pessoa(),
                        "enzo",
                        "{bcrypt}hash",
                        Set.of()));
    }

    @Test
    void deveGerarHashesDiferentesParaAMesmaSenha() {
        var encoder =
                PasswordEncoderFactories.createDelegatingPasswordEncoder();

        String primeiro = encoder.encode("SenhaTeste123!");
        String segundo = encoder.encode("SenhaTeste123!");

        assertNotEquals(primeiro, segundo);
        assertTrue(encoder.matches("SenhaTeste123!", primeiro));
        assertFalse(encoder.matches("SenhaErrada123!", primeiro));
    }
}