package com.curso.learntech.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "categoria_curso")
public class CategoriaCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private final List<Curso> cursos = new ArrayList<>();

    protected CategoriaCurso(){
    }

    public CategoriaCurso(String nome){
        this.nome = validarTextoObrigatorio(nome, "Nome é campo obrigatório!");
        this.status = status.ATIVO;
    }

    public void adicionarCategoria(Curso curso){
        Objects.requireNonNull(curso, "curso é obrigatória!");

        boolean codigoJaUtilizado = cursos.stream()
                                    .anyMatch(item -> item != curso
                                    && item.getCodigoCurso() == curso.getCodigoCurso());

        if(codigoJaUtilizado){
            throw new IllegalArgumentException("Codigo do curso ja utilizado!");
        }

        curso.associaAo(this);

        if(!cursos.contains(curso)){
            cursos.add(curso);
        }
    }

    public void ativar(){
        this.status = Status.ATIVO;
    }

    public void inativar() {
        this.status = Status.INATIVO;
    }

    public Long getId(){
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Status getStatus() {
        return status;
    }

    public List<Curso> getCursos(){
        return List.copyOf(cursos);
    }

    private static String validarTextoObrigatorio(String texto, String mensagem){
        if(texto == null || texto.isBlank()){
            throw new IllegalArgumentException(mensagem);
        }
        return texto;
    }

}
