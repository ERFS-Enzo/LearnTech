package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.ColaboradorAtualizacaoRequest;
import com.curso.learntech.api.dto.ColaboradorRequest;
import com.curso.learntech.api.dto.ColaboradorResponse;
import com.curso.learntech.api.dto.PaginaResponse;
import com.curso.learntech.api.mapper.ColaboradorMapper;
import com.curso.learntech.application.ColaboradorService;
import com.curso.learntech.domain.Colaborador;
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

@Tag(name = "Colaboradores",
        description = "Vínculo profissional de uma pessoa. A função não é perfil de acesso")
@RestController
@RequestMapping("/api/colaboradores")
public class ColaboradorController {

    private final ColaboradorService service;
    private final ColaboradorMapper mapper;

    public ColaboradorController(ColaboradorService service, ColaboradorMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Criar vínculo de colaborador",
            description = "A pessoa deve existir, estar ativa e ainda não ser colaboradora. "
                    + "Só ATENDENTE e COORDENADOR podem registrar inscrições.")
    @PostMapping
    public ResponseEntity<ColaboradorResponse> cadastrar(
            @Valid @RequestBody ColaboradorRequest request) {
        Colaborador cadastrado = service.cadastrar(
                request.pessoaId(),
                request.matricula(),
                request.funcao(),
                request.dataAdmissao());

        URI location = URI.create("/api/colaboradores/" + cadastrado.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @Operation(summary = "Consultar colaborador por ID")
    @GetMapping("/{id}")
    public ColaboradorResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Listar colaboradores", description = "Lista paginada.")
    @GetMapping
    public PaginaResponse<ColaboradorResponse> listar(
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return PaginaResponse.de(service.listar(pageable), mapper::toResponse);
    }

    @Operation(summary = "Alterar matrícula e função do colaborador")
    @PutMapping("/{id}")
    public ColaboradorResponse alterar(
            @PathVariable Long id,
            @Valid @RequestBody ColaboradorAtualizacaoRequest request) {
        return mapper.toResponse(
                service.alterar(id, request.matricula(), request.funcao()));
    }
}