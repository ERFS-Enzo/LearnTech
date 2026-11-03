package com.curso.learntech.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "evento_seguranca")
public class EventoSeguranca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ocorrido_em", nullable = false)
    private Instant ocorridoEm;

    @Column(nullable = false, length = 60)
    private String tipo;

    @Column(name = "ator_id")
    private Long atorId;

    @Column(name = "alvo_id")
    private Long alvoId;

    @Column(name = "chave_login", length = 64)
    private String chaveLogin;

    @Column(length = 64)
    private String origem;

    protected EventoSeguranca() {
    }

    public EventoSeguranca(
            Instant ocorridoEm,
            String tipo,
            Long atorId,
            Long alvoId,
            String chaveLogin,
            String origem) {

        this.ocorridoEm = ocorridoEm;
        this.tipo = tipo;
        this.atorId = atorId;
        this.alvoId = alvoId;
        this.chaveLogin = chaveLogin;
        this.origem = limpar(origem);
    }

    private String limpar(String valor) {
        if (valor == null) {
            return null;
        }

        String limpo = valor.replaceAll("\\p{Cntrl}", "");
        return limpo.substring(0, Math.min(limpo.length(), 64));
    }

    public Long getId() {
        return id;
    }

    public Instant getOcorridoEm() {
        return ocorridoEm;
    }

    public String getTipo() {
        return tipo;
    }

    public Long getAtorId() {
        return atorId;
    }

    public Long getAlvoId() {
        return alvoId;
    }

    public String getChaveLogin() {
        return chaveLogin;
    }

    public String getOrigem() {
        return origem;
    }
}