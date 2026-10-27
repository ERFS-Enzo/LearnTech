package com.curso.learntech.application;

import com.curso.learntech.domain.Autor;
import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Curso;
import com.curso.learntech.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CursoService {
    private final CursoRepository cursoRepository;
    private final CategoriaCursoRepository categoriaRepository;
    private final AutorRepository autorRepository;

    public CursoService(CursoRepository cursoRepository,
                        CategoriaCursoRepository categoriaRepository,
                        AutorRepository autorRepository) {
        this.cursoRepository = cursoRepository;
        this.categoriaRepository = categoriaRepository;
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Curso cadastrar(Curso curso, Long categoriaId, Long autorId) {
        if (cursoRepository.existsByCodigoCurso(curso.getCodigoCurso())) {
            throw new RecursoDuplicadoException("Código do curso já cadastrado");
        }

        CategoriaCurso categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de curso não encontrado"));

        if (autorId != null) {
            Autor autor = autorRepository.findById(autorId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Autor não encontrado"));
            curso.atribuirAutor(autor);
        }

        categoria.adicionarCurso(curso);
        return cursoRepository.save(curso);
    }

    @Transactional(readOnly = true)
    public Curso buscarPorId(Long id) {
        return cursoRepository.findByIdComCategoria(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return cursoRepository.findAllComCategoriaEAutor();
    }
}