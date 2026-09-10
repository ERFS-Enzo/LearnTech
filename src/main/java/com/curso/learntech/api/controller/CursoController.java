package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.CursoRequest;
import com.curso.learntech.api.dto.CursoResponse;
import com.curso.learntech.api.mapper.CursoMapper;
import com.curso.learntech.application.CursoService;
import com.curso.learntech.domain.Curso;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
            @Valid @RequestBody CursoRequest request){

        Curso curso = mapper.toEntity(request);
        Curso cadastrado = service.cadastrar(
                curso,
                request.categoriaId(),
                request.autorId());
        URI location = URI.create("/api/cursos/" + cadastrado.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @GetMapping("/{id}")
    public CursoResponse buscarPorId(@PathVariable Long id){
        return mapper.toResponse(service.buscarPorId(id));
    }

    @GetMapping
    public List<CursoResponse> listar(){
        return service.listar()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

}
