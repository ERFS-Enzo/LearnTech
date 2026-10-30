package com.curso.learntech.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(
        name = "item_inscricao",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_item_inscricao_curso",
                columnNames = {"inscricao_id", "curso_id"}))
public class ItemInscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "inscricao_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_item_inscricao_inscricao"))
    private Inscricao inscricao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "curso_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_item_inscricao_curso"))
    private Curso curso;

    // Cópia do valor do curso no momento da inclusão (preço histórico).
    @Column(name = "valor_praticado", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorPraticado;

    protected ItemInscricao() {
    }

    ItemInscricao(Inscricao inscricao, Curso curso, BigDecimal valorPraticado) {
        this.inscricao = Objects.requireNonNull(inscricao, "Inscrição é obrigatória");
        this.curso = Objects.requireNonNull(curso, "Curso é obrigatório");
        Objects.requireNonNull(valorPraticado, "Valor praticado é obrigatório");
        if (valorPraticado.signum() < 0) {
            throw new IllegalArgumentException("Valor praticado não pode ser negativo");
        }
        this.valorPraticado = valorPraticado;
    }

    public Long getId() {
        return id;
    }

    public Inscricao getInscricao() {
        return inscricao;
    }

    public Curso getCurso() {
        return curso;
    }

    public BigDecimal getValorPraticado() {
        return valorPraticado;
    }
}