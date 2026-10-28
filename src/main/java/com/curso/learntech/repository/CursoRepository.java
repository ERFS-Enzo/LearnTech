package com.curso.learntech.repository;

import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends
        JpaRepository<Curso, Long>,
        JpaSpecificationExecutor<Curso> {

    Optional<Curso> findByCodigoCurso(int codigoCurso);
    boolean existsByCodigoCurso(int codigoCurso);
    boolean existsByCategoriaId(Long categoriaId);
    List<Curso> findByCategoriaId(Long categoriaId);
    List<Curso> findByStatus(Status status);

    @Query("""
        SELECT c
        FROM Curso c
        JOIN FETCH c.categoria
        LEFT JOIN FETCH c.autor
        WHERE c.id = :id
    """)
    Optional<Curso> findByIdComCategoria(@Param("id") Long id);

    @Query("""
        SELECT c
        FROM Curso c
        JOIN FETCH c.categoria
        LEFT JOIN FETCH c.autor
    """)
    List<Curso> findAllComCategoriaEAutor();

    @Override
    @EntityGraph(attributePaths = {"categoria", "autor"})
    Page<Curso> findAll(Specification<Curso> specification, Pageable pageable);
}