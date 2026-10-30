package com.curso.learntech.api.mapper;

import com.curso.learntech.api.dto.ColaboradorResponse;
import com.curso.learntech.domain.Colaborador;
import org.springframework.stereotype.Component;

@Component
public class ColaboradorMapper {

    public ColaboradorResponse toResponse(Colaborador colaborador) {
        return new ColaboradorResponse(
                colaborador.getId(),
                colaborador.getPessoa().getId(),
                colaborador.getPessoa().getNome(),
                colaborador.getMatricula(),
                colaborador.getFuncao(),
                colaborador.getDataAdmissao());
    }
}