package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.CategoriaCursoAtualizacaoRequest;
import com.curso.learntech.api.dto.CategoriaCursoRequest;
import com.curso.learntech.api.dto.CategoriaCursoResponse;
import com.curso.learntech.api.dto.PaginaResponse;
import com.curso.learntech.api.dto.StatusRequest;
import com.curso.learntech.api.mapper.CategoriaCursoMapper;
import com.curso.learntech.application.CategoriaCursoService;
import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Status;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaCursoController {
    private final CategoriaCursoService service;
    private final CategoriaCursoMapper mapper;

    public CategoriaCursoController(CategoriaCursoService service, CategoriaCursoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CategoriaCursoResponse> cadastrar(
            @Valid @RequestBody CategoriaCursoRequest request) {
        CategoriaCurso cadastrado = service.cadastrar(request.nome());

        URI location = URI.create("/api/categorias/" + cadastrado.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @GetMapping("/{id}")
    public CategoriaCursoResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @GetMapping
    public PaginaResponse<CategoriaCursoResponse> pesquisar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Status status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return PaginaResponse.de(
                service.pesquisar(nome, status, pageable),
                mapper::toResponse);
    }

    @PutMapping("/{id}")
    public CategoriaCursoResponse alterar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaCursoAtualizacaoRequest request) {
        return mapper.toResponse(service.alterar(id, request.nome()));
    }

    @PutMapping("/{id}/status")
    public CategoriaCursoResponse alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}