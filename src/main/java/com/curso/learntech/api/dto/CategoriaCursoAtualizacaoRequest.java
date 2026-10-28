package com.curso.learntech.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados editáveis de uma categoria. O status possui fluxo próprio")
public record CategoriaCursoAtualizacaoRequest(
        @Schema(description = "Novo nome da categoria", example = "Desenvolvimento Web")
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 120, message = "Nome deve possuir no máximo 120 caracteres")
        String nome) {
}