package com.curso.learntech.api.mapper;

import com.curso.learntech.api.dto.CategoriaCursoRequest;
import com.curso.learntech.api.dto.CategoriaCursoResponse;
import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Curso;
import org.springframework.stereotype.Component;

@Component
public class CategoriaCursoMapper {

    public CategoriaCurso toEntity(CategoriaCursoRequest request){
        return new CategoriaCurso(
                request.nome()
        );
    }

    public CategoriaCursoResponse toResponse(CategoriaCurso categoriaCurso){

        return new CategoriaCursoResponse(
                categoriaCurso.getId(),
                categoriaCurso.getNome(),
                categoriaCurso.getStatus()

        );
    }
}
