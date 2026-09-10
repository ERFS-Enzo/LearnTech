package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.CategoriaCursoRequest;
import com.curso.learntech.api.dto.CategoriaCursoResponse;
import com.curso.learntech.api.dto.CursoRequest;
import com.curso.learntech.api.dto.CursoResponse;
import com.curso.learntech.api.mapper.CategoriaCursoMapper;
import com.curso.learntech.application.CategoriaCursoService;
import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Curso;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
            @Valid @RequestBody CategoriaCursoRequest request){
        CategoriaCurso categoriaCurso = mapper.toEntity(request);
        CategoriaCurso cadastrado = service.cadastrar(categoriaCurso.getNome());

        URI location = URI.create("/api/categorias/" + cadastrado.getNome());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @GetMapping("/{id}")
    public CategoriaCursoResponse buscarPorId(@PathVariable Long id){
        return mapper.toResponse(service.buscarPorId(id));
    }

    @GetMapping
    public List<CategoriaCursoResponse> listar(){
        return service.listar().stream().map(mapper::toResponse).toList();
    }

}
