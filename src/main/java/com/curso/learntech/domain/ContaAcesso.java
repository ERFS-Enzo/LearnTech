package com.curso.learntech.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "conta_acesso")
public class ContaAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false, unique = true)
    private Pessoa pessoa;

    @Column(nullable = false, unique = true, length = 80)
    private String login;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Column(nullable = false)
    private boolean ativa = true;

    @ElementCollection
    @CollectionTable(
            name = "conta_acesso_perfil",
            joinColumns = @JoinColumn(name = "conta_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 30)
    private Set<PerfilAcesso> perfis = new HashSet<>();

    protected ContaAcesso() {
    }

    public ContaAcesso(
            Pessoa pessoa,
            String login,
            String senhaHash,
            Set<PerfilAcesso> perfis) {

        this.pessoa = Objects.requireNonNull(
                pessoa, "Pessoa é obrigatória");

        this.login = normalizarLogin(login);
        alterarSenhaHash(senhaHash);
        alterarPerfis(perfis);
    }

    public static String normalizarLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login é obrigatório");
        }

        String normalizado = login.trim().toLowerCase(Locale.ROOT);

        if (normalizado.length() > 80) {
            throw new IllegalArgumentException(
                    "Login deve possuir no máximo 80 caracteres");
        }

        return normalizado;
    }

    public void alterarSenhaHash(String senhaHash) {
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new IllegalArgumentException("Hash é obrigatório");
        }

        this.senhaHash = senhaHash;
    }

    public void alterarPerfis(Set<PerfilAcesso> perfis) {
        if (perfis == null
                || perfis.isEmpty()
                || perfis.stream().anyMatch(Objects::isNull)) {

            throw new IllegalArgumentException(
                    "Informe pelo menos um perfil válido");
        }

        this.perfis.clear();
        this.perfis.addAll(perfis);
    }

    public void alterarStatus(boolean ativa) {
        this.ativa = ativa;
    }

    public boolean podeAutenticar() {
        return ativa && pessoa.getStatus() == Status.ATIVO;
    }

    public Long getId() {
        return id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public String getLogin() {
        return login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public Set<PerfilAcesso> getPerfis() {
        return Set.copyOf(perfis);
    }
}