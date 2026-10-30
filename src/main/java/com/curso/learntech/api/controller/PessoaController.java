package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.PaginaResponse;
import com.curso.learntech.api.dto.PessoaAtualizacaoRequest;
import com.curso.learntech.api.dto.PessoaRequest;
import com.curso.learntech.api.dto.PessoaResponse;
import com.curso.learntech.api.dto.StatusRequest;
import com.curso.learntech.api.mapper.PessoaMapper;
import com.curso.learntech.application.PessoaService;
import com.curso.learntech.domain.Pessoa;
import com.curso.learntech.domain.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Pessoas", description = "Identidade civil única, base dos papéis de aluno e colaborador")
@RestController
@RequestMapping("/api/pessoas")
public class PessoaController {

    private final PessoaService service;
    private final PessoaMapper mapper;

    public PessoaController(PessoaService service, PessoaMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Cadastrar pessoa",
            description = "CPF com dígitos verificadores válidos. CPF e e-mail são únicos.")
    @PostMapping
    public ResponseEntity<PessoaResponse> cadastrar(@Valid @RequestBody PessoaRequest request) {
        Pessoa cadastrada = service.cadastrar(mapper.toEntity(request));

        URI location = URI.create("/api/pessoas/" + cadastrada.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrada));
    }

    @Operation(summary = "Consultar pessoa por ID")
    @GetMapping("/{id}")
    public PessoaResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Pesquisar pessoas",
            description = "Filtros por nome e status, com paginação.")
    @GetMapping
    public PaginaResponse<PessoaResponse> pesquisar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Status status,
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return PaginaResponse.de(
                service.pesquisar(nome, status, pageable),
                mapper::toResponse);
    }

    @Operation(summary = "Alterar pessoa",
            description = "Substitui nome e e-mail. O CPF não muda.")
    @PutMapping("/{id}")
    public PessoaResponse alterar(
            @PathVariable Long id,
            @Valid @RequestBody PessoaAtualizacaoRequest request) {
        return mapper.toResponse(service.alterar(id, request.nome(), request.email()));
    }

    @Operation(summary = "Ativar ou inativar pessoa")
    @PutMapping("/{id}/status")
    public PessoaResponse alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }
}