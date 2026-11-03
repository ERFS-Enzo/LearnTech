package com.curso.learntech.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sessao_refresh_token")
public class SessaoRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private ContaAcesso conta;

    @Column(nullable = false)
    private UUID familia;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(name = "expiracao_absoluta", nullable = false)
    private Instant expiracaoAbsoluta;

    @Column(nullable = false)
    private boolean usada;

    @Column(nullable = false)
    private boolean revogada;

    protected SessaoRefreshToken() {
    }

    public SessaoRefreshToken(
            ContaAcesso conta,
            UUID familia,
            String tokenHash,
            Instant expiraEm,
            Instant expiracaoAbsoluta) {

        this.conta = conta;
        this.familia = familia;
        this.tokenHash = tokenHash;
        this.expiraEm = expiraEm;
        this.expiracaoAbsoluta = expiracaoAbsoluta;
    }

    public boolean podeRenovar(Instant agora) {
        return !usada
                && !revogada
                && agora.isBefore(expiraEm)
                && agora.isBefore(expiracaoAbsoluta);
    }

    public void marcarUsada() {
        this.usada = true;
    }

    public ContaAcesso getConta() {
        return conta;
    }

    public UUID getFamilia() {
        return familia;
    }

    public Instant getExpiracaoAbsoluta() {
        return expiracaoAbsoluta;
    }
}