package com.curso.learntech.repository;

import com.curso.learntech.domain.EventoSeguranca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventoSegurancaRepository
        extends JpaRepository<EventoSeguranca, Long>,
        JpaSpecificationExecutor<EventoSeguranca> {
}