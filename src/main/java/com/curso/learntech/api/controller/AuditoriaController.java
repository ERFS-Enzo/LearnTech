package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.PaginaResponse;
import com.curso.learntech.application.ConsultaAuditoriaService;
import com.curso.learntech.domain.EventoSeguranca;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/auditoria-seguranca")
public class AuditoriaController {

    private final ConsultaAuditoriaService service;

    public AuditoriaController(ConsultaAuditoriaService service) {
        this.service = service;
    }

    @GetMapping
    public PaginaResponse<EventoResponse> pesquisar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long alvoId,
            @RequestParam(required = false) Instant inicio,
            @RequestParam(required = false) Instant fim,
            @ParameterObject
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return PaginaResponse.de(
                service.pesquisar(
                        tipo, alvoId, inicio, fim, pageable),
                EventoResponse::de);
    }

    public record EventoResponse(
            Long id,
            Instant ocorridoEm,
            String tipo,
            Long atorId,
            Long alvoId,
            String chaveLogin,
            String origem) {

        public static EventoResponse de(EventoSeguranca evento) {
            return new EventoResponse(
                    evento.getId(),
                    evento.getOcorridoEm(),
                    evento.getTipo(),
                    evento.getAtorId(),
                    evento.getAlvoId(),
                    evento.getChaveLogin(),
                    evento.getOrigem());
        }
    }
}