package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.CursoAtualizacaoRequest;
import com.curso.learntech.api.dto.CursoRequest;
import com.curso.learntech.api.dto.CursoResponse;
import com.curso.learntech.api.dto.MovimentacaoCargaHorariaRequest;
import com.curso.learntech.api.dto.PaginaResponse;
import com.curso.learntech.api.dto.StatusRequest;
import com.curso.learntech.api.mapper.CursoMapper;
import com.curso.learntech.application.CursoService;
import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.Status;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService service;
    private final CursoMapper mapper;

    public CursoController(CursoService service, CursoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CursoResponse> cadastrar(
            @Valid @RequestBody CursoRequest request) {

        Curso curso = mapper.toEntity(request);
        Curso cadastrado = service.cadastrar(
                curso,
                request.categoriaId(),
                request.autorId());
        URI location = URI.create("/api/cursos/" + cadastrado.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @GetMapping("/{id}")
    public CursoResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @GetMapping
    public PaginaResponse<CursoResponse> pesquisar(
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long autorId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return PaginaResponse.de(
                service.pesquisar(descricao, status, categoriaId, autorId, pageable),
                mapper::toResponse);
    }

    @PutMapping("/{id}")
    public CursoResponse alterar(
            @PathVariable Long id,
            @Valid @RequestBody CursoAtualizacaoRequest request) {
        return mapper.toResponse(service.alterar(
                id,
                request.descricao(),
                request.valorCurso(),
                request.quantidadeModulos(),
                request.categoriaId(),
                request.autorId()));
    }

    @PutMapping("/{id}/status")
    public CursoResponse alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }

    @PostMapping("/{id}/carga-horaria/acrescimos")
    public CursoResponse acrescentarCargaHoraria(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoCargaHorariaRequest request) {
        return mapper.toResponse(
                service.acrescentarCargaHoraria(id, request.quantidade()));
    }

    @PostMapping("/{id}/carga-horaria/reducoes")
    public CursoResponse reduzirCargaHoraria(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoCargaHorariaRequest request) {
        return mapper.toResponse(
                service.reduzirCargaHoraria(id, request.quantidade()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}