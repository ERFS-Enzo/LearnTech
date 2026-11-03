package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.SegurancaDtos.*;
import com.curso.learntech.application.ContaAcessoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/contas-acesso")
public class ContaAcessoController {

    private final ContaAcessoService service;

    public ContaAcessoController(ContaAcessoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ContaResponse> criar(
            @Valid @RequestBody CriarConta request) {

        ContaResponse criada = service.criar(request);

        return ResponseEntity.created(
                        URI.create("/api/contas-acesso/" + criada.id()))
                .body(criada);
    }

    @GetMapping("/minha")
    public ContaResponse minha(Authentication authentication) {
        return service.minhaConta(authentication.getName());
    }

    @PutMapping("/minha/senha")
    public ResponseEntity<Void> alterarSenha(
            Authentication authentication,
            @Valid @RequestBody AlterarSenha request) {

        service.alterarSenha(
                authentication.getName(),
                request.senhaAtual(),
                request.novaSenha());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/perfis")
    public ContaResponse alterarPerfis(
            @PathVariable Long id,
            @Valid @RequestBody AlterarPerfis request) {

        return service.alterarPerfis(id, request.perfis());
    }

    @PutMapping("/{id}/status")
    public ContaResponse alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AlterarStatus request) {

        return service.alterarStatus(id, request.ativa());
    }
}