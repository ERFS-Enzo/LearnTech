package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.AlunoAtualizacaoRequest;
import com.curso.learntech.api.dto.AlunoRequest;
import com.curso.learntech.api.dto.AlunoResponse;
import com.curso.learntech.api.dto.PaginaResponse;
import com.curso.learntech.api.mapper.AlunoMapper;
import com.curso.learntech.application.AlunoService;
import com.curso.learntech.domain.Aluno;
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

@Tag(name = "Alunos", description = "Papel de aluno associado a uma pessoa")
@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService service;
    private final AlunoMapper mapper;

    public AlunoController(AlunoService service, AlunoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Associar papel de aluno a uma pessoa",
            description = "A pessoa deve existir, estar ativa e ainda não ser aluno.")
    @PostMapping
    public ResponseEntity<AlunoResponse> cadastrar(@Valid @RequestBody AlunoRequest request) {
        Aluno cadastrado = service.cadastrar(request.pessoaId(), request.telefone());

        URI location = URI.create("/api/alunos/" + cadastrado.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @Operation(summary = "Consultar aluno por ID")
    @GetMapping("/{id}")
    public AlunoResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Listar alunos", description = "Lista paginada.")
    @GetMapping
    public PaginaResponse<AlunoResponse> listar(
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return PaginaResponse.de(service.listar(pageable), mapper::toResponse);
    }

    @Operation(summary = "Alterar telefone do aluno")
    @PutMapping("/{id}")
    public AlunoResponse alterar(
            @PathVariable Long id,
            @Valid @RequestBody AlunoAtualizacaoRequest request) {
        return mapper.toResponse(service.alterarTelefone(id, request.telefone()));
    }
}