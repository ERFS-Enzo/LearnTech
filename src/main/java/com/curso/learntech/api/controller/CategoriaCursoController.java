package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.ApiError;
import com.curso.learntech.api.dto.CategoriaCursoAtualizacaoRequest;
import com.curso.learntech.api.dto.CategoriaCursoRequest;
import com.curso.learntech.api.dto.CategoriaCursoResponse;
import com.curso.learntech.api.dto.PaginaResponse;
import com.curso.learntech.api.dto.StatusRequest;
import com.curso.learntech.api.mapper.CategoriaCursoMapper;
import com.curso.learntech.application.CategoriaCursoService;
import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Categorias de cursos",
        description = "Cadastro, consulta e organização de cursos por categoria")
@RestController
@RequestMapping("/api/categorias")
public class CategoriaCursoController {
    private final CategoriaCursoService service;
    private final CategoriaCursoMapper mapper;

    public CategoriaCursoController(CategoriaCursoService service, CategoriaCursoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Cadastrar categoria",
            description = "Cria uma categoria ativa. O nome deve ser único, "
                    + "sem diferenciar maiúsculas de minúsculas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria cadastrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Nome de categoria já cadastrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<CategoriaCursoResponse> cadastrar(
            @Valid @RequestBody CategoriaCursoRequest request) {
        CategoriaCurso cadastrado = service.cadastrar(request.nome());

        URI location = URI.create("/api/categorias/" + cadastrado.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @Operation(summary = "Consultar categoria por ID",
            description = "Retorna a representação usada na visualização e na tela de alteração.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public CategoriaCursoResponse buscarPorId(
            @Parameter(description = "Identificador da categoria", example = "1")
            @PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Pesquisar categorias",
            description = "Filtros combináveis com paginação. Use page (começa em 0), "
                    + "size (máximo 100) e sort (por exemplo, nome,asc). "
                    + "Campos de ordenação permitidos: id, nome e status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de categorias"),
            @ApiResponse(responseCode = "400", description = "Paginação ou ordenação inválida",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public PaginaResponse<CategoriaCursoResponse> pesquisar(
            @Parameter(description = "Trecho do nome, sem diferenciar maiúsculas de minúsculas",
                    example = "program")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Situação da categoria", example = "ATIVO")
            @RequestParam(required = false) Status status,
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return PaginaResponse.de(
                service.pesquisar(nome, status, pageable),
                mapper::toResponse);
    }

    @Operation(summary = "Alterar categoria",
            description = "Substitui o nome da categoria. O status possui fluxo próprio.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria alterada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Nome de categoria já cadastrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public CategoriaCursoResponse alterar(
            @Parameter(description = "Identificador da categoria", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CategoriaCursoAtualizacaoRequest request) {
        return mapper.toResponse(service.alterar(id, request.nome()));
    }

    @Operation(summary = "Ativar ou inativar categoria",
            description = "Altera somente o status. Inativar não exclui o registro.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status alterado"),
            @ApiResponse(responseCode = "400", description = "Status inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/status")
    public CategoriaCursoResponse alterarStatus(
            @Parameter(description = "Identificador da categoria", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }

    @Operation(summary = "Excluir categoria",
            description = "Exclui fisicamente somente quando nenhum curso utiliza a categoria. "
                    + "Se houver cursos, inative a categoria.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria excluída"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Categoria possui cursos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "Identificador da categoria", example = "1")
            @PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}