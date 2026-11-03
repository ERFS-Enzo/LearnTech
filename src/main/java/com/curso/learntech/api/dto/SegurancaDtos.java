package com.curso.learntech.api.dto;

import com.curso.learntech.domain.PerfilAcesso;
import jakarta.validation.constraints.*;

import java.util.Set;

public final class SegurancaDtos {

    private SegurancaDtos() {
    }

    public record CriarConta(
            @NotNull @Positive Long pessoaId,
            @NotBlank @Size(max = 80) String login,
            @NotBlank @Size(min = 8, max = 72) String senha,
            @NotEmpty Set<@NotNull PerfilAcesso> perfis) {
    }

    public record ContaResponse(
            Long id,
            Long pessoaId,
            String login,
            boolean ativa,
            Set<PerfilAcesso> perfis) {
    }

    public record AlterarSenha(
            @NotBlank String senhaAtual,
            @NotBlank @Size(min = 8, max = 72) String novaSenha) {
    }

    public record AlterarPerfis(
            @NotEmpty Set<@NotNull PerfilAcesso> perfis) {
    }

    public record AlterarStatus(
            @NotNull Boolean ativa) {
    }
}