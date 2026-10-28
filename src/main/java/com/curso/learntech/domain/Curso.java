package com.curso.learntech.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "curso",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_curso_codigo_curso",
                columnNames = "codigo_curso"
        )
)
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_curso", nullable = false)
    private int codigoCurso;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(name = "carga_horaria", nullable = false, precision = 18, scale = 3)
    private BigDecimal cargaHoraria;

    @Column(name = "valor_curso", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorCurso;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "categoria_curso_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_curso_categoria_curso")
    )
    private CategoriaCurso categoria;

    @Column(name = "quantidade_modulos", nullable = false)
    private int quantidadeModulos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "autor_id",
            foreignKey = @ForeignKey(name = "fk_curso_autor")
    )
    private Autor autor;

    protected Curso() {
    }

    public Curso(int codigoCurso,
                 String descricao,
                 BigDecimal cargaHoraria,
                 BigDecimal valorCurso,
                 LocalDate dataCadastro,
                 int quantidadeModulos,
                 Status status) {
        this.codigoCurso = validarCodigoCurso(
                codigoCurso,
                "Código do curso não pode ser negativo");
        this.descricao = validarTextoObrigatorio(
                descricao,
                "Descrição é obrigatória");
        this.cargaHoraria = validarPositivo(
                cargaHoraria,
                "Carga horária deve ser maior que zero");
        this.valorCurso = validarNaoNegativo(
                valorCurso,
                "Valor do Curso não pode ser negativo");
        this.dataCadastro = Objects.requireNonNull(
                dataCadastro,
                "Data de cadastro é obrigatória");
        this.quantidadeModulos = validarQuantidadeModulos(quantidadeModulos);
        this.status = Objects.requireNonNull(status, "Status é obrigatório");
    }

    public void alterarDescricao(String novaDescricao) {
        this.descricao = validarTextoObrigatorio(
                novaDescricao,
                "Descrição é obrigatória");
    }

    public void alterarCargaHoraria(BigDecimal novaCargaHoraria) {
        this.cargaHoraria = validarPositivo(
                novaCargaHoraria,
                "Carga horária deve ser maior que zero");
    }

    public void alterarValorCurso(BigDecimal novoValorCurso) {
        this.valorCurso = validarNaoNegativo(
                novoValorCurso,
                "O valor do curso não pode ser negativo");
    }

    public void atribuirAutor(Autor autor) {
        this.autor = Objects.requireNonNull(autor, "Autor é obrigatório");
    }

    public void ativar() {
        this.status = Status.ATIVO;
    }

    public void inativar() {
        this.status = Status.INATIVO;
    }

    public void alterarQuantidadeModulos(int novaQuantidade) {
        this.quantidadeModulos = validarQuantidadeModulos(novaQuantidade);
    }

    public void alterarAutor(Autor autor) {
        this.autor = autor; // autor é opcional: aceita null
    }

    public void alterarCategoria(CategoriaCurso novaCategoria) {
        Objects.requireNonNull(novaCategoria, "Categoria do curso é obrigatória");

        if (this.categoria == novaCategoria) {
            return;
        }

        novaCategoria.validarInclusao(this);
        if (this.categoria != null) {
            this.categoria.removerInternamente(this);
        }
        this.categoria = novaCategoria;
        novaCategoria.adicionarInternamente(this);
    }

    public void acrescentarCargaHoraria(BigDecimal quantidade) {
        validarPositivo(quantidade, "Quantidade deve ser maior que zero");
        this.cargaHoraria = this.cargaHoraria.add(quantidade);
    }

    public void reduzirCargaHoraria(BigDecimal quantidade) {
        validarPositivo(quantidade, "Quantidade deve ser maior que zero");
        if (quantidade.compareTo(this.cargaHoraria) >= 0) {
            throw new IllegalArgumentException("Carga horária insuficiente");
        }
        this.cargaHoraria = this.cargaHoraria.subtract(quantidade);
    }

    void associaAo(CategoriaCurso categoria) {
        Objects.requireNonNull(categoria, "Categoria do curso é  obrigatória");

        if (this.categoria != null && this.categoria != categoria) {
            throw new IllegalStateException(
                    "O curso já pertence a outra categoria");
        }

        this.categoria = categoria;
    }

    public Long getId() {
        return id;
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

    public Autor getAutor() {
        return autor;
    }

    public int getQuantidadeModulos() {
        return quantidadeModulos;
    }

    private static String validarTextoObrigatorio(String texto, String mensagem) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return texto;
    }

    private static BigDecimal validarNaoNegativo(BigDecimal valor, String mensagem) {
        Objects.requireNonNull(valor, mensagem);
        if (valor.signum() < 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }

    private static BigDecimal validarPositivo(BigDecimal valor, String mensagem) {
        Objects.requireNonNull(valor, mensagem);
        if (valor.signum() <= 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }

    private static int validarCodigoCurso(int valor, String mensagem) {
        if (valor < 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }

    private static int validarQuantidadeModulos(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException(
                    "Quantidade de módulos não pode ser negativa");
        }
        return valor;
    }

    public BigDecimal calcularValorPorHora() {
        return valorCurso.divide(cargaHoraria, 2, RoundingMode.HALF_UP);
    }
}