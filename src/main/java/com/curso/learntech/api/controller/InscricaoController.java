package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.InscricaoRequest;
import com.curso.learntech.api.dto.InscricaoResponse;
import com.curso.learntech.api.dto.InscricaoResumoResponse;
import com.curso.learntech.api.dto.ItemInscricaoRequest;
import com.curso.learntech.api.dto.PaginaResponse;
import com.curso.learntech.api.mapper.InscricaoMapper;
import com.curso.learntech.application.InscricaoService;
import com.curso.learntech.domain.Inscricao;
import com.curso.learntech.domain.StatusInscricao;
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

@Tag(name = "Inscrições",
        description = "Inscrição de um aluno em cursos, com preço histórico e baixa de vagas")
@RestController
@RequestMapping("/api/inscricoes")
public class InscricaoController {

    private final InscricaoService service;
    private final InscricaoMapper mapper;

    public InscricaoController(InscricaoService service, InscricaoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Abrir inscrição",
            description = "Aluno e atendente devem estar ativos, e o atendente deve ter "
                    + "função ATENDENTE ou COORDENADOR.")
    @PostMapping
    public ResponseEntity<InscricaoResponse> abrir(@Valid @RequestBody InscricaoRequest request) {
        Inscricao aberta = service.abrir(request.alunoId(), request.atendenteId());

        URI location = URI.create("/api/inscricoes/" + aberta.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(aberta));
    }

    @Operation(summary = "Consultar inscrição por ID",
            description = "Devolve itens, valores praticados e total.")
    @GetMapping("/{id}")
    public InscricaoResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Pesquisar inscrições",
            description = "Devolve resumos, sem itens, com filtros e paginação.")
    @GetMapping
    public PaginaResponse<InscricaoResumoResponse> pesquisar(
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) Long atendenteId,
            @RequestParam(required = false) StatusInscricao status,
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return PaginaResponse.de(
                service.pesquisar(alunoId, atendenteId, status, pageable),
                mapper::toResumo);
    }

    @Operation(summary = "Adicionar curso à inscrição",
            description = "Copia o valor atual do curso para o item (preço histórico).")
    @PostMapping("/{id}/itens")
    public ResponseEntity<InscricaoResponse> adicionarItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemInscricaoRequest request) {
        Inscricao inscricao = service.adicionarItem(id, request.cursoId());

        URI location = URI.create("/api/inscricoes/" + inscricao.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(inscricao));
    }

    @Operation(summary = "Remover item da inscrição",
            description = "Somente em inscrição ABERTA.")
    @DeleteMapping("/{inscricaoId}/itens/{itemId}")
    public InscricaoResponse removerItem(
            @PathVariable Long inscricaoId,
            @PathVariable Long itemId) {
        return mapper.toResponse(service.removerItem(inscricaoId, itemId));
    }

    @Operation(summary = "Finalizar inscrição",
            description = "Finaliza a inscrição e baixa uma vaga de cada curso na mesma "
                    + "transação. Se algum curso estiver inativo ou sem vagas, nada é gravado.")
    @PostMapping("/{id}/finalizacao")
    public InscricaoResponse finalizar(@PathVariable Long id) {
        return mapper.toResponse(service.finalizar(id));
    }

    @Operation(summary = "Cancelar inscrição",
            description = "Somente em inscrição ABERTA. Não afeta as vagas.")
    @PostMapping("/{id}/cancelamento")
    public InscricaoResponse cancelar(@PathVariable Long id) {
        return mapper.toResponse(service.cancelar(id));
    }
}