package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.ApiError;
import com.curso.learntech.api.dto.AutorRequest;
import com.curso.learntech.api.dto.AutorResponse;
import com.curso.learntech.api.mapper.AutorMapper;
import com.curso.learntech.application.AutorService;
import com.curso.learntech.domain.Autor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Autores", description = "Cadastro e consulta de autores de cursos")
@RestController
@RequestMapping("/api/autores")
public class AutorController {
    private final AutorService service;
    private final AutorMapper mapper;

    public AutorController(AutorService service, AutorMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Cadastrar autor",
            description = "Cria um autor ativo. O CPF deve ter 11 números e ser único.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Autor cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<AutorResponse> cadastrar(
            @Valid @RequestBody AutorRequest request) {
        Autor cadastrado = service.cadastrar(mapper.toEntity(request));

        URI location = URI.create("/api/autores/" + cadastrado.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @Operation(summary = "Consultar autor por ID",
            description = "Retorna os dados do autor.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autor encontrado"),
            @ApiResponse(responseCode = "404", description = "Autor não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public AutorResponse buscarPorId(
            @Parameter(description = "Identificador do autor", example = "1")
            @PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Listar autores",
            description = "Retorna todos os autores, sem paginação.")
    @ApiResponse(responseCode = "200", description = "Lista de autores")
    @GetMapping
    public List<AutorResponse> listar() {
        return service.listar().stream().map(mapper::toResponse).toList();
    }
}