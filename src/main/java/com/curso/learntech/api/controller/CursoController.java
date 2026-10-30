package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.*;
import com.curso.learntech.api.mapper.CursoMapper;
import com.curso.learntech.application.CursoService;
import com.curso.learntech.domain.Curso;
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

@Tag(name = "Cursos",
        description = "Cadastro, consulta, alteração e ajuste de carga horária de cursos")
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService service;
    private final CursoMapper mapper;

    public CursoController(CursoService service, CursoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Cadastrar curso",
            description = "Cria um curso ativo associado a uma categoria e, "
                    + "opcionalmente, a um autor.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Curso cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Categoria ou autor não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Código do curso já cadastrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
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

    @Operation(summary = "Consultar curso por ID",
            description = "Retorna a representação completa usada na visualização "
                    + "e na tela de alteração.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso encontrado"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public CursoResponse buscarPorId(
            @Parameter(description = "Identificador do curso", example = "1")
            @PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Pesquisar cursos",
            description = "Filtros combináveis com paginação. Use page (começa em 0), "
                    + "size (máximo 100) e sort (por exemplo, descricao,asc). "
                    + "Campos de ordenação permitidos: id, codigoCurso, descricao, "
                    + "cargaHoraria, valorCurso, quantidadeModulos, dataCadastro e status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de cursos"),
            @ApiResponse(responseCode = "400", description = "Paginação ou ordenação inválida",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public PaginaResponse<CursoResponse> pesquisar(
            @Parameter(description = "Trecho da descrição, sem diferenciar maiúsculas de minúsculas",
                    example = "spring")
            @RequestParam(required = false) String descricao,
            @Parameter(description = "Situação do curso", example = "ATIVO")
            @RequestParam(required = false) Status status,
            @Parameter(description = "Identificador exato da categoria", example = "1")
            @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Identificador exato do autor", example = "1")
            @RequestParam(required = false) Long autorId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return PaginaResponse.de(
                service.pesquisar(descricao, status, categoriaId, autorId, pageable),
                mapper::toResponse);
    }

    @Operation(summary = "Alterar curso",
            description = "Substitui descrição, valor, quantidade de módulos, categoria e autor. "
                    + "Código, carga horária, data de cadastro e status possuem fluxos próprios.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso alterado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Curso, categoria ou autor não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public CursoResponse alterar(
            @Parameter(description = "Identificador do curso", example = "1")
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

    @Operation(summary = "Ativar ou inativar curso",
            description = "Altera somente o status. Inativar não exclui o registro.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status alterado"),
            @ApiResponse(responseCode = "400", description = "Status inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/status")
    public CursoResponse alterarStatus(
            @Parameter(description = "Identificador do curso", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }

    @Operation(summary = "Definir vagas do curso",
            description = "Substitui a quantidade de vagas disponíveis. As vagas são baixadas "
                    + "quando uma inscrição é finalizada.")
    @PutMapping("/{id}/vagas")
    public CursoResponse definirVagas(
            @PathVariable Long id,
            @Valid @RequestBody VagasRequest request) {
        return mapper.toResponse(service.definirVagas(id, request.vagas()));
    }

    @Operation(summary = "Acrescentar carga horária",
            description = "Soma uma quantidade positiva de horas à carga horária atual. "
                    + "Repetir a chamada repete o efeito.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carga horária atualizada"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/{id}/carga-horaria/acrescimos")
    public CursoResponse acrescentarCargaHoraria(
            @Parameter(description = "Identificador do curso", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoCargaHorariaRequest request) {
        return mapper.toResponse(
                service.acrescentarCargaHoraria(id, request.quantidade()));
    }

    @Operation(summary = "Reduzir carga horária",
            description = "Subtrai horas da carga horária atual, que deve continuar maior que zero. "
                    + "Repetir a chamada repete o efeito.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carga horária atualizada"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida ou carga horária insuficiente",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/{id}/carga-horaria/reducoes")
    public CursoResponse reduzirCargaHoraria(
            @Parameter(description = "Identificador do curso", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoCargaHorariaRequest request) {
        return mapper.toResponse(
                service.reduzirCargaHoraria(id, request.quantidade()));
    }

    @Operation(summary = "Excluir curso",
            description = "Exclui fisicamente o curso.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Curso excluído"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "Identificador do curso", example = "1")
            @PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}