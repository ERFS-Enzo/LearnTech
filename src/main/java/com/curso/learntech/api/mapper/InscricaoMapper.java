package com.curso.learntech.api.mapper;

import com.curso.learntech.api.dto.InscricaoResponse;
import com.curso.learntech.api.dto.InscricaoResumoResponse;
import com.curso.learntech.api.dto.ItemInscricaoResponse;
import com.curso.learntech.domain.Inscricao;
import com.curso.learntech.domain.ItemInscricao;
import org.springframework.stereotype.Component;

@Component
public class InscricaoMapper {

    public InscricaoResponse toResponse(Inscricao inscricao) {
        return new InscricaoResponse(
                inscricao.getId(),
                inscricao.getAluno().getId(),
                inscricao.getAluno().getPessoa().getNome(),
                inscricao.getAtendente().getId(),
                inscricao.getAtendente().getPessoa().getNome(),
                inscricao.getStatus(),
                inscricao.getDataAbertura(),
                inscricao.getDataFinalizacao(),
                inscricao.calcularTotal(),
                inscricao.getItens().stream().map(this::toItemResponse).toList());
    }

    public InscricaoResumoResponse toResumo(Inscricao inscricao) {
        return new InscricaoResumoResponse(
                inscricao.getId(),
                inscricao.getAluno().getId(),
                inscricao.getAluno().getPessoa().getNome(),
                inscricao.getAtendente().getId(),
                inscricao.getStatus(),
                inscricao.getDataAbertura());
    }

    private ItemInscricaoResponse toItemResponse(ItemInscricao item) {
        return new ItemInscricaoResponse(
                item.getId(),
                item.getCurso().getId(),
                item.getCurso().getCodigoCurso(),
                item.getCurso().getDescricao(),
                item.getValorPraticado());
    }
}