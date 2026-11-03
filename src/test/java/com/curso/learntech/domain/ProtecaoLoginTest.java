package com.curso.learntech.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ProtecaoLoginTest {

    private final Instant agora =
            Instant.parse("2026-10-30T12:00:00Z");

    private final Duration janela = Duration.ofMinutes(15);
    private final Duration bloqueio = Duration.ofMinutes(15);

    @Test
    void deveBloquearNaQuintaFalhaELiberarNaFronteira() {
        var protecao = new ProtecaoLogin("a".repeat(64));

        for (int indice = 0; indice < 4; indice++) {
            assertFalse(protecao.registrarFalha(
                    agora, 5, janela, bloqueio));
        }

        assertTrue(protecao.registrarFalha(
                agora, 5, janela, bloqueio));

        assertTrue(protecao.estaBloqueada(
                agora.plusSeconds(899)));

        assertFalse(protecao.estaBloqueada(
                agora.plusSeconds(900)));
    }

    @Test
    void sucessoDeveLimparFalhas() {
        var protecao = new ProtecaoLogin("a".repeat(64));

        protecao.registrarFalha(
                agora, 2, janela, bloqueio);

        protecao.registrarSucesso();

        assertFalse(protecao.registrarFalha(
                agora, 2, janela, bloqueio));

        assertFalse(protecao.estaBloqueada(agora));
    }

    @Test
    void falhaForaDaJanelaDeveIniciarNovaContagem() {
        var protecao = new ProtecaoLogin("a".repeat(64));

        protecao.registrarFalha(
                agora, 2, janela, bloqueio);

        assertFalse(protecao.registrarFalha(
                agora.plus(janela),
                2,
                janela,
                bloqueio));
    }

    @Test
    void auditoriaDeveRemoverCaracteresDeControleDaOrigem() {
        var evento = new EventoSeguranca(
                agora,
                "LOGIN_FALHA",
                null,
                null,
                "a".repeat(64),
                "127.0.0.1\r\nEVENTO_FALSO");

        assertFalse(evento.getOrigem().contains("\r"));
        assertFalse(evento.getOrigem().contains("\n"));
    }
}