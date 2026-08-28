package com.curso.learntech.application;

import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Curso;
import com.curso.learntech.repository.CategoriaCursoRepository;
import com.curso.learntech.repository.CursoRepository;
import com.curso.learntech.repository.RecursoDuplicadoException;
import com.curso.learntech.repository.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CursoService {
    private final CursoRepository cursoRepository;
    private final CategoriaCursoRepository categoriaRepository;

    public CursoService(CursoRepository cursoRepository, CategoriaCursoRepository categoriaRepository) {
        this.cursoRepository = cursoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Curso cadastrar(Curso curso, Long categoriaId){
        if (cursoRepository.existsByCodigoCurso(
                curso.getCodigoCurso())) {
            throw new RecursoDuplicadoException(
                    "Código do curso já cadastrado");
        }

        CategoriaCurso categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de curso não encontrado"));

        categoria.adicionarCurso(curso);
        return cursoRepository.save(curso);
    }
}
