package com.curso.learntech.api.dto;

import com.curso.learntech.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representação de um autor")
public record AutorResponse(
        @Schema(description = "Identificador gerado pelo servidor", example = "1")
        Long id,

        @Schema(description = "Nome do autor", example = "Maria Souza")
        String nomeAutor,

        @Schema(description = "CPF fictício do autor", example = "12345678901")
        String cpf,

        @Schema(description = "Situação do autor", example = "ATIVO")
        Status status
) {
}