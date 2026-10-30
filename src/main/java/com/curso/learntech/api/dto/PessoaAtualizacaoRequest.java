package com.curso.learntech.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PessoaAtualizacaoRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve possuir no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 150, message = "E-mail deve possuir no máximo 150 caracteres")
        String email) {
}