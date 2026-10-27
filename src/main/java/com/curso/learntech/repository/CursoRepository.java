package com.curso.learntech.repository;

import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    Optional<Curso> findByCodigoCurso(int codigoCurso);
    boolean existsByCodigoCurso(int codigoCurso);
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

}
