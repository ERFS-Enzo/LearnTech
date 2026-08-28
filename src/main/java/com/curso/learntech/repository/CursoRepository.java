package com.curso.learntech.repository;

import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    Optional<Curso> findByCodigoCurso(int codigoCurso);
    boolean existsByCodigoCurso(int codigoCurso);
    List<Curso> findByCategoriaId(Long categoriaId);
    List<Curso> findByStatus(Status status);
}
