package com.curso.learntech.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import com.curso.learntech.api.dto.AutenticacaoDtos.*;
import com.curso.learntech.application.AutenticacaoService;
import com.curso.learntech.application.SessaoAutenticacaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {

    private final AutenticacaoService service;
    private final SessaoAutenticacaoService sessaoService;

    public AutenticacaoController(
            AutenticacaoService service,
            SessaoAutenticacaoService sessaoService) {

        this.service = service;
        this.sessaoService = sessaoService;
    }

    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest) {

        return tokens(service.autenticar(
                request.login(),
                request.senha(),
                servletRequest.getRemoteAddr()));
    }

    @SecurityRequirements
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody RefreshRequest request) {

        return tokens(
                sessaoService.renovar(request.refreshToken()));
    }

    @SecurityRequirements
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshRequest request) {

        sessaoService.encerrar(request.refreshToken());

        return ResponseEntity.noContent()
                .cacheControl(CacheControl.noStore())
                .build();
    }

    @PostMapping("/logout-todas")
    public ResponseEntity<Void> logoutTodas(
            Authentication authentication) {

        sessaoService.encerrarTodas(authentication.getName());

        return ResponseEntity.noContent()
                .cacheControl(CacheControl.noStore())
                .build();
    }

    private ResponseEntity<TokenResponse> tokens(
            TokenResponse response) {

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header("Pragma", "no-cache")
                .body(response);
    }
}