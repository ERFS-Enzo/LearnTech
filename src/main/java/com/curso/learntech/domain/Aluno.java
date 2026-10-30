package com.curso.learntech.domain;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "aluno")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "pessoa_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_aluno_pessoa"))
    private Pessoa pessoa;

    @Column(length = 30)
    private String telefone;

    protected Aluno() {
    }

    public Aluno(Pessoa pessoa, String telefone) {
        this.pessoa = Objects.requireNonNull(pessoa, "Pessoa é obrigatória");
        this.telefone = normalizarTelefone(telefone);
    }

    public void alterarTelefone(String novoTelefone) {
        this.telefone = normalizarTelefone(novoTelefone);
    }

    public Long getId() {
        return id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public String getTelefone() {
        return telefone;
    }

    private static String normalizarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }
        String normalizado = telefone.trim();
        if (normalizado.length() > 30) {
            throw new IllegalArgumentException(
                    "Telefone deve possuir no máximo 30 caracteres");
        }
        return normalizado;
    }
}