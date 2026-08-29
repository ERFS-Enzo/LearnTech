package com.curso.learntech.domain;

import jakarta.persistence.*;

@Entity
@Table(
        name = "autor",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_autor_cpf",
                columnNames = "cpf"))
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_autor", nullable = false, length = 150)
    private String nomeAutor;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    protected Autor() {
    }

    public Autor(String razaoSocial, String cnpj) {
        this.nomeAutor = validarTextoObrigatorio(nomeAutor);
        this.cpf = validarCpf(cpf);
        this.status = Status.ATIVO;
    }

    private String validarTextoObrigatorio(String texto){
        if(texto == null || texto.isBlank()){
            throw new IllegalArgumentException("Nome do Autor é obrigatório");
        }

        return texto;
    }

    private String validarCpf(String cpf){
        if(cpf == null || cpf.isBlank()){
            throw new IllegalArgumentException("Cpf é obrigatório");
        }

        return cpf;
    }

    public Long getId() {
        return id;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public String getCpf() {
        return cpf;
    }

    public Status getStatus() {
        return status;
    }
}
