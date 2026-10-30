package com.curso.learntech.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(
        name = "pessoa",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pessoa_cpf",
                columnNames = "cpf"))
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    protected Pessoa() {
    }

    public Pessoa(String nome, String email, String cpf, LocalDate dataCadastro) {
        this.nome = validarNome(nome);
        this.email = validarEmail(email);
        this.cpf = validarCpf(cpf);
        this.dataCadastro = Objects.requireNonNull(
                dataCadastro,
                "Data de cadastro é obrigatória");
        this.status = Status.ATIVO;
    }

    public void alterarDados(String novoNome, String novoEmail) {
        this.nome = validarNome(novoNome);
        this.email = validarEmail(novoEmail);
    }

    public void ativar() {
        this.status = Status.ATIVO;
    }

    public void inativar() {
        this.status = Status.INATIVO;
    }

    public static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public Status getStatus() {
        return status;
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        return nome.trim();
    }

    private static String validarEmail(String email) {
        String normalizado = normalizarEmail(email);
        if (normalizado == null
                || !normalizado.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("E-mail inválido");
        }
        return normalizado;
    }

    private static String validarCpf(String cpf) {
        if (!DocumentoFiscal.cpfValido(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        return cpf;
    }
}