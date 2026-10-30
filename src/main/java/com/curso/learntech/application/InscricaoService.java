package com.curso.learntech.application;

import com.curso.learntech.domain.Aluno;
import com.curso.learntech.domain.Colaborador;
import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.Inscricao;
import com.curso.learntech.domain.ItemInscricao;
import com.curso.learntech.domain.Status;
import com.curso.learntech.domain.StatusInscricao;
import com.curso.learntech.repository.AlunoRepository;
import com.curso.learntech.repository.ColaboradorRepository;
import com.curso.learntech.repository.CursoRepository;
import com.curso.learntech.repository.InscricaoRepository;
import com.curso.learntech.repository.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class InscricaoService {

    private final InscricaoRepository repository;
    private final AlunoRepository alunoRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final CursoRepository cursoRepository;

    public InscricaoService(InscricaoRepository repository,
                            AlunoRepository alunoRepository,
                            ColaboradorRepository colaboradorRepository,
                            CursoRepository cursoRepository) {
        this.repository = repository;
        this.alunoRepository = alunoRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public Inscricao abrir(Long alunoId, Long atendenteId) {
        Aluno aluno = alunoRepository.findOneById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado"));
        Colaborador atendente = colaboradorRepository.findOneById(atendenteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador não encontrado"));

        validarParticipantes(aluno, atendente);
        return repository.save(new Inscricao(aluno, atendente, LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public Inscricao buscarPorId(Long id) {
        return buscarEntidade(id);
    }

    @Transactional(readOnly = true)
    public Page<Inscricao> pesquisar(Long alunoId,
                                     Long atendenteId,
                                     StatusInscricao status,
                                     Pageable pageable) {
        validarOrdenacao(pageable, Set.of("id", "status", "dataAbertura", "dataFinalizacao"));

        Specification<Inscricao> filtros = (root, query, cb) -> cb.conjunction();

        if (alunoId != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("aluno").get("id"), alunoId));
        }
        if (atendenteId != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("atendente").get("id"), atendenteId));
        }
        if (status != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }

        return repository.findAll(filtros, pageable);
    }

    @Transactional
    public Inscricao adicionarItem(Long inscricaoId, Long cursoId) {
        Inscricao inscricao = buscarEntidade(inscricaoId);
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado"));

        if (curso.getStatus() != Status.ATIVO) {
            throw new IllegalArgumentException("Curso deve estar ativo");
        }

        inscricao.adicionarItem(curso);
        return inscricao;
    }

    @Transactional
    public Inscricao removerItem(Long inscricaoId, Long itemId) {
        Inscricao inscricao = buscarEntidade(inscricaoId);

        if (!inscricao.possuiItem(itemId)) {
            throw new RecursoNaoEncontradoException("Item não encontrado na inscrição");
        }

        inscricao.removerItem(itemId);
        return inscricao;
    }

    // Dois efeitos coordenados na mesma transação:
    // 1) a inscrição passa a FINALIZADA; 2) cada curso perde uma vaga.
    // Se qualquer curso estiver inativo ou sem vagas, nada é gravado.
    @Transactional
    public Inscricao finalizar(Long id) {
        Inscricao inscricao = buscarEntidade(id);

        inscricao.finalizar(LocalDateTime.now());
        validarParticipantes(inscricao.getAluno(), inscricao.getAtendente());

        for (ItemInscricao item : inscricao.getItens()) {
            Curso curso = item.getCurso();
            if (curso.getStatus() != Status.ATIVO) {
                throw new IllegalArgumentException(
                        "Todos os cursos da inscrição devem estar ativos");
            }
            curso.ocuparVaga();
        }

        return inscricao;
    }

    @Transactional
    public Inscricao cancelar(Long id) {
        Inscricao inscricao = buscarEntidade(id);
        inscricao.cancelar();
        return inscricao;
    }

    private void validarParticipantes(Aluno aluno, Colaborador atendente) {
        if (aluno.getPessoa().getStatus() != Status.ATIVO) {
            throw new IllegalArgumentException("Aluno deve estar ativo");
        }
        if (atendente.getPessoa().getStatus() != Status.ATIVO) {
            throw new IllegalArgumentException("Atendente deve estar ativo");
        }
        if (!atendente.podeRegistrarInscricoes()) {
            throw new IllegalArgumentException(
                    "A função do colaborador não permite registrar inscrições");
        }
    }

    private Inscricao buscarEntidade(Long id) {
        return repository.findOneById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrição não encontrada"));
    }

    private void validarOrdenacao(Pageable pageable, Set<String> camposPermitidos) {
        pageable.getSort().forEach(ordem -> {
            if (!camposPermitidos.contains(ordem.getProperty())) {
                throw new IllegalArgumentException(
                        "Campo de ordenação inválido: " + ordem.getProperty());
            }
        });
    }
}