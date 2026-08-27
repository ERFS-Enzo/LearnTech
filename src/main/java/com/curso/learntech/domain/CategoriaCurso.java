package com.curso.learntech.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoriaCurso {
    private final String nome;
    private Status status;
    private final List<Curso> cursos = new ArrayList<>();

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
        return mensagem;
    }

}
