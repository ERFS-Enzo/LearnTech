package com.curso.learntech.api.mapper;

import com.curso.learntech.api.dto.AutorRequest;
import com.curso.learntech.api.dto.AutorResponse;
import com.curso.learntech.domain.Autor;
import org.springframework.stereotype.Component;

@Component
public class AutorMapper {
    public Autor toEntity(AutorRequest request){
        return new Autor(
                request.nomeAutor(),
                request.cpf()
        );
    }

    public AutorResponse toResponse(Autor autor){
        return new AutorResponse(
                autor.getId(),
                autor.getNomeAutor(),
                autor.getCpf(),
                autor.getStatus()
        );
    }
}
