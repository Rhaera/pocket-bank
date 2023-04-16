package com.github.rhaera.project.pocketbank.service.domain;

import static com.github.rhaera.project.pocketbank.service.abstraction.implementation.ChavesPix.TipoChave;

import com.github.rhaera.project.pocketbank.controller.exception.MaisDeTresChavesPixException;
import com.github.rhaera.project.pocketbank.service.abstraction.implementation.ChavesPix;

import lombok.NonNull;

import java.util.Collection;
import java.util.EnumSet;

public class Pix extends ChavesPix<TipoChave> {

    private final int MAX_NUM_CHAVES_PIX = 3;

    public Pix(EnumSet<TipoChave> chaves) {
        super(chaves);
        if (this.size() > MAX_NUM_CHAVES_PIX) {
            this.clear();
            throw new MaisDeTresChavesPixException("Número máximo de chaves atingido!");
        }
    }

    @Override public boolean add(TipoChave tipoChave) {
        validarNumeroMaximoDeChavesPix();
        return super.add(tipoChave);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends TipoChave> c) {
        validarNumeroMaximoDeChavesPix();
        return super.addAll(c);
    }

    private void validarNumeroMaximoDeChavesPix() {
        if (this.size() == MAX_NUM_CHAVES_PIX) throw new MaisDeTresChavesPixException("Número máximo de chaves atingido!");
    }
}
