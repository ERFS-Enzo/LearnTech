package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.AutorRequest;
import com.curso.learntech.api.dto.AutorResponse;
import com.curso.learntech.api.mapper.AutorMapper;
import com.curso.learntech.application.AutorService;
import com.curso.learntech.domain.Autor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/autores")
public class AutorController {
    private final AutorService service;
    private final AutorMapper mapper;

    public AutorController(AutorService service, AutorMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<AutorResponse> cadastrar(
            @Valid @RequestBody AutorRequest request) {
        Autor cadastrado = service.cadastrar(mapper.toEntity(request));

        URI location = URI.create("/api/autores/" + cadastrado.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @GetMapping("/{id}")
    public AutorResponse buscarPorId(@PathVariable Long id){
        return mapper.toResponse(service.buscarPorId(id));
    }

    @GetMapping
    public List<AutorResponse> listar(){
        return service.listar().stream().map(mapper::toResponse).toList();
    }

}
