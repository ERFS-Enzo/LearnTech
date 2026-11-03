package com.curso.learntech.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SessaoRefreshTokenTest {

    private final Instant agora =
            Instant.parse("2026-10-30T12:00:00Z");

    private SessaoRefreshToken sessao(
            Instant expiraEm,
            Instant absoluta) {

        Pessoa pessoa = new Pessoa(
                "Pessoa",
                "pessoa@exemplo.com",
                "52998224725",
                LocalDate.of(2026, 1, 1));

        ContaAcesso conta = new ContaAcesso(
                pessoa,
                "teste",
                "{bcrypt}hash",
                Set.of(PerfilAcesso.OPERADOR));

        return new SessaoRefreshToken(
                conta,
                UUID.randomUUID(),
                "a".repeat(64),
                expiraEm,
                absoluta);
    }

    @Test
    void deveExpirarExatamenteNaFronteira() {
        var sessao = sessao(
                agora.plusSeconds(60),
                agora.plusSeconds(120));

        assertTrue(sessao.podeRenovar(
                agora.plusSeconds(59)));

        assertFalse(sessao.podeRenovar(
                agora.plusSeconds(60)));
    }

    @Test
    void naoDeveRenovarTokenUtilizado() {
        var sessao = sessao(
                agora.plusSeconds(60),
                agora.plusSeconds(120));

        sessao.marcarUsada();

        assertFalse(sessao.podeRenovar(agora));
    }

    @Test
    void deveRespeitarExpiracaoAbsoluta() {
        var sessao = sessao(
                agora.plusSeconds(60),
                agora.plusSeconds(60));

        assertFalse(sessao.podeRenovar(
                agora.plusSeconds(60)));
    }
}