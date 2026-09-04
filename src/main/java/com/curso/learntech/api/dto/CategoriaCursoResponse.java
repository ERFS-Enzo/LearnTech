package com.curso.learntech.api.dto;

import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.Status;

import java.util.ArrayList;
import java.util.List;

public record CategoriaCursoResponse(
        Long id,
        String nome,
        Status status,
        List<Curso> cursos
) {
}
