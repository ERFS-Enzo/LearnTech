package com.curso.learntech.domain;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "protecao_login")
public class ProtecaoLogin {

    @Id
    @Column(length = 64)
    private String chave;

    @Column(nullable = false)
    private int falhas;

    @Column(name = "janela_iniciada_em")
    private Instant janelaIniciadaEm;

    @Column(name = "bloqueado_ate")
    private Instant bloqueadoAte;

    protected ProtecaoLogin() {
    }

    public ProtecaoLogin(String chave) {
        this.chave = chave;
    }

    public boolean estaBloqueada(Instant agora) {
        return bloqueadoAte != null
                && agora.isBefore(bloqueadoAte);
    }

    public boolean registrarFalha(
            Instant agora,
            int limite,
            Duration janela,
            Duration duracaoBloqueio) {

        if (estaBloqueada(agora)) {
            return false;
        }

        if (janelaIniciadaEm == null
                || !agora.isBefore(
                janelaIniciadaEm.plus(janela))
                || bloqueadoAte != null) {

            falhas = 0;
            janelaIniciadaEm = agora;
            bloqueadoAte = null;
        }

        falhas++;

        if (falhas >= limite) {
            bloqueadoAte = agora.plus(duracaoBloqueio);
            return true;
        }

        return false;
    }

    public void registrarSucesso() {
        falhas = 0;
        janelaIniciadaEm = null;
        bloqueadoAte = null;
    }
}