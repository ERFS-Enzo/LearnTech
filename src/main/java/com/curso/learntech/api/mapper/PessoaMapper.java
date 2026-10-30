package com.curso.learntech.api.mapper;

import com.curso.learntech.api.dto.PessoaRequest;
import com.curso.learntech.api.dto.PessoaResponse;
import com.curso.learntech.domain.Pessoa;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PessoaMapper {

    public Pessoa toEntity(PessoaRequest request) {
        return new Pessoa(
                request.nome(),
                request.email(),
                request.cpf(),
                LocalDate.now());
    }

    public PessoaResponse toResponse(Pessoa pessoa) {
        return new PessoaResponse(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getEmail(),
                pessoa.getCpf(),
                pessoa.getDataCadastro(),
                pessoa.getStatus());
    }
}