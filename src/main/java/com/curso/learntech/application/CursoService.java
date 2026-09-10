package com.curso.learntech.application;

import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Curso;
import com.curso.learntech.repository.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService {
    private final CursoRepository cursoRepository;
    private final CategoriaCursoRepository categoriaRepository;

    public CursoService(CursoRepository cursoRepository, CategoriaCursoRepository categoriaRepository) {
        this.cursoRepository = cursoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Curso cadastrar(Curso curso, Long categoriaId, Long autorId){
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

    @Transactional(readOnly = true)
    public Curso buscarPorId(Long id) {
        return cursoRepository.findByIdComCategoria(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado"));
    }

    @Transactional
    public List<Curso> listar(){
        return cursoRepository.findAllComCategoriaEAutor();
    }

}
