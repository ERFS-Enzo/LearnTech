package com.curso.learntech.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public class Curso {
    private final int codigoCurso;
    private String descricao;
    private BigDecimal cargaHoraria;
    private BigDecimal valorCurso;
    private final LocalDate dataCadastro;
    private Status status;
    private CategoriaCurso categoria;

    public Curso(int codigoCurso,
                 String descricao,
                 BigDecimal cargaHoraria,
                 BigDecimal valorCurso,
                 LocalDate dataCadastro,
                 Status status) {
        this.codigoCurso = validarCodigoCurso(
                codigoCurso,
                "Código do curso é obrigatório");
        this.descricao = validarTextoObrigatorio(
                descricao,
                "Descrição é obrigatória");
        this.cargaHoraria = validarNaoNegativo(
                cargaHoraria,
                "Carga horária é obrigatória");
        this.valorCurso = validarNaoNegativo(
                valorCurso,
                "Valor do Curso não pode ser negativo");
        this.dataCadastro = Objects.requireNonNull(
                dataCadastro,
                "Data de cadastro é obrigatória");
        this.status = Status.ATIVO;
    }

    public void alterarDescricao(String novaDescricao){
        this.descricao = validarTextoObrigatorio(
                novaDescricao,
                "Descrição é obrigatória");
    }

    public void alterarCargaHoraria(BigDecimal novaCargaHoraria) {
        this.cargaHoraria = validarNaoNegativo(
                novaCargaHoraria,
                "Carga horária é obrigatória");
    }

    public void alterarValorCurso(BigDecimal novoValorCurso) {
        this.valorCurso = validarNaoNegativo(
                novoValorCurso,
                "O valor do curso não pode ser negativo");
    }

    public void ativar() {
        this.status = Status.ATIVO;
    }

    public void inativar() {
        this.status = Status.INATIVO;
    }

    void associaAo(CategoriaCurso categoria) {
        Objects.requireNonNull(categoria, "Categoria do curso é  obrigatória");

        if (this.categoria != null && this.categoria != categoria) {
            throw new IllegalStateException(
                    "O curso já pertence a outra categoria");
        }

        this.categoria = categoria;
    }

    public int getCodigoCurso() {
        return codigoCurso;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getCargaHoraria() {
        return cargaHoraria;
    }

    public BigDecimal getValorCurso() {
        return valorCurso;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public Status getStatus() {
        return status;
    }

    public CategoriaCurso getCategoria() {
        return categoria;
    }

    private static String validarTextoObrigatorio(String texto, String mensagem) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return texto.trim();
    }

    private static BigDecimal validarNaoNegativo(BigDecimal valor, String mensagem) {
        Objects.requireNonNull(valor, mensagem);
        if (valor.signum() < 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }

    private static void validarPositivo(BigDecimal valor, String mensagem) {
        Objects.requireNonNull(valor, mensagem);
        if (valor.signum() <= 0) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private static int validarCodigoCurso(int valor, String mensagem){
        Objects.requireNonNull(valor, mensagem);
        if(valor < 0){
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }

    public BigDecimal calcularValorPorHora() {
        return valorCurso
                .divide(cargaHoraria, 2, RoundingMode.HALF_UP);
    }

}
