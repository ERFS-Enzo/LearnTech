package com.curso.learntech.api.mapper;

import com.curso.learntech.api.dto.AlunoResponse;
import com.curso.learntech.domain.Aluno;
import org.springframework.stereotype.Component;

@Component
public class AlunoMapper {

    public AlunoResponse toResponse(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getPessoa().getId(),
                aluno.getPessoa().getNome(),
                aluno.getTelefone());
    }
}