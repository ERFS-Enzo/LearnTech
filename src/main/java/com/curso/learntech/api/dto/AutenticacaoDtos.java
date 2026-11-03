package com.curso.learntech.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AutenticacaoDtos {

    private AutenticacaoDtos() {
    }

    public record LoginRequest(
            @NotBlank @Size(max = 80) String login,
            @NotBlank @Size(max = 200) String senha) {
    }

    public record TokenResponse(
            String accessToken,
            String tokenType,
            long expiresIn) {
    }
}