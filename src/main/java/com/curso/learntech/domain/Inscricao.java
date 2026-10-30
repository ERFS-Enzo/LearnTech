package com.curso.learntech.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "inscricao")
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "aluno_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_inscricao_aluno"))
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "atendente_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_inscricao_atendente"))
    private Colaborador atendente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusInscricao status;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_finalizacao")
    private LocalDateTime dataFinalizacao;

    @OneToMany(mappedBy = "inscricao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private final List<ItemInscricao> itens = new ArrayList<>();

    protected Inscricao() {
    }

    public Inscricao(Aluno aluno, Colaborador atendente, LocalDateTime dataAbertura) {
        this.aluno = Objects.requireNonNull(aluno, "Aluno é obrigatório");
        this.atendente = Objects.requireNonNull(atendente, "Atendente é obrigatório");
        this.dataAbertura = Objects.requireNonNull(
                dataAbertura,
                "Data de abertura é obrigatória");
        this.status = StatusInscricao.ABERTA;
    }

    public ItemInscricao adicionarItem(Curso curso) {
        validarAberta();
        Objects.requireNonNull(curso, "Curso é obrigatório");

        boolean repetido = itens.stream()
                .anyMatch(item -> mesmoCurso(item.getCurso(), curso));
        if (repetido) {
            throw new IllegalArgumentException("Curso já incluído na inscrição");
        }

        ItemInscricao item = new ItemInscricao(this, curso, curso.getValorCurso());
        itens.add(item);
        return item;
    }

    public boolean possuiItem(Long itemId) {
        return itens.stream().anyMatch(item -> Objects.equals(item.getId(), itemId));
    }

    public void removerItem(Long itemId) {
        validarAberta();

        boolean removido = itens.removeIf(item -> Objects.equals(item.getId(), itemId));
        if (!removido) {
            throw new IllegalArgumentException("Item não encontrado na inscrição");
        }
    }

    public void finalizar(LocalDateTime dataFinalizacao) {
        validarAberta();
        Objects.requireNonNull(dataFinalizacao, "Data de finalização é obrigatória");

        if (itens.isEmpty()) {
            throw new IllegalArgumentException(
                    "Inscrição sem cursos não pode ser finalizada");
        }

        this.status = StatusInscricao.FINALIZADA;
        this.dataFinalizacao = dataFinalizacao;
    }

    public void cancelar() {
        validarAberta();
        this.status = StatusInscricao.CANCELADA;
    }

    public BigDecimal calcularTotal() {
        return itens.stream()
                .map(ItemInscricao::getValorPraticado)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Colaborador getAtendente() {
        return atendente;
    }

    public StatusInscricao getStatus() {
        return status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public List<ItemInscricao> getItens() {
        return Collections.unmodifiableList(itens);
    }

    private void validarAberta() {
        if (status != StatusInscricao.ABERTA) {
            throw new IllegalStateException("Inscrição não está aberta");
        }
    }

    private static boolean mesmoCurso(Curso existente, Curso novo) {
        return existente == novo
                || (existente.getId() != null
                && Objects.equals(existente.getId(), novo.getId()));
    }
}