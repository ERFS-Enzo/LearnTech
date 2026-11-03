package com.curso.learntech.application;

import com.curso.learntech.domain.EventoSeguranca;
import com.curso.learntech.repository.EventoSegurancaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
public class ConsultaAuditoriaService {

    private final EventoSegurancaRepository repository;

    public ConsultaAuditoriaService(
            EventoSegurancaRepository repository) {

        this.repository = repository;
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Transactional(readOnly = true)
    public Page<EventoSeguranca> pesquisar(
            String tipo,
            Long alvoId,
            Instant inicio,
            Instant fim,
            Pageable pageable) {

        Set<String> permitidos =
                Set.of("id", "ocorridoEm", "tipo");

        for (var ordem : pageable.getSort()) {
            if (!permitidos.contains(ordem.getProperty())) {
                throw new IllegalArgumentException(
                        "Campo de ordenação inválido");
            }
        }

        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            throw new IllegalArgumentException(
                    "Início não pode ser posterior ao fim");
        }

        Specification<EventoSeguranca> filtros =
                (root, query, cb) -> cb.conjunction();

        if (tipo != null && !tipo.isBlank()) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("tipo"), tipo));
        }

        if (alvoId != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("alvoId"), alvoId));
        }

        if (inicio != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(
                            root.get("ocorridoEm"), inicio));
        }

        if (fim != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(
                            root.get("ocorridoEm"), fim));
        }

        return repository.findAll(filtros, pageable);
    }
}