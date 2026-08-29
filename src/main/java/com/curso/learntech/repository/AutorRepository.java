package com.curso.learntech.repository;

import com.curso.learntech.domain.Autor;
import com.curso.learntech.domain.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutorRepository  extends JpaRepository<Autor, Long> {
    boolean existsByCpf(String cpf);
}
