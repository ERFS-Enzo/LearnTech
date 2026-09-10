package com.curso.learntech.api.mapper;

import com.curso.learntech.api.dto.CursoRequest;
import com.curso.learntech.api.dto.CursoResponse;
import com.curso.learntech.domain.Autor;
import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CursoMapper {

    public Curso toEntity(CursoRequest request){
        return new Curso(
                request.codigoCurso(),
                request.descricao(),
                request.cargaHoraria(),
                request.valorCurso(),
                LocalDate.now(),
                request.quantidadeModulos(),
                Status.ATIVO
        );
    }

    public CursoResponse toResponse(Curso curso){
        Autor autor = curso.getAutor();

        return new CursoResponse(
                curso.getId(),
                curso.getCodigoCurso(),
                curso.getDescricao(),
                curso.getCargaHoraria(),
                curso.getValorCurso(),
                curso.getQuantidadeModulos(),
                curso.getDataCadastro(),
                curso.getStatus(),
                curso.getCategoria().getId(),
                curso.getCategoria().getNome(),
                autor == null ? null : autor.getId(),
                autor == null ? null : autor.getNomeAutor()
        );
    }
}
