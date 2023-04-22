package com.github.rhaera.project.pocketbank.model.dto.mongodb;

import com.github.rhaera.project.pocketbank.repository.ContaRepository;
import com.github.rhaera.project.pocketbank.service.domain.Pix;

import static com.github.rhaera.project.pocketbank.service.abstraction.implementation.ChavesPix.TipoChave;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.EnumSet;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequisicaoCadastrarPix implements Serializable {

    private String numeroConta;
    private final Pix pix = new Pix(EnumSet.noneOf(TipoChave.class));
    private ContaRepository repository;
    private static RequisicaoCadastrarPix NOVO_PIX = null;

    private RequisicaoCadastrarPix(String numero, Pix novoPix, ContaRepository repositoryContas) {
        this.numeroConta = numero;
        this.pix.addAll(novoPix);
        this.repository = repositoryContas;
    }

    public static RequisicaoCadastrarPix requisicao(String numero, Pix novoPix, ContaRepository repositoryContas) {
        if (NOVO_PIX == null) NOVO_PIX = new RequisicaoCadastrarPix(numero, novoPix, repositoryContas);
        return NOVO_PIX;
    }
}
