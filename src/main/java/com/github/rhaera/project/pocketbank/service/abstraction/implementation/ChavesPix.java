package com.github.rhaera.project.pocketbank.service.abstraction.implementation;

import com.github.rhaera.project.pocketbank.model.utility.UtilFormatacoes;

import lombok.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

public class ChavesPix<TipoChave> implements Set<ChavesPix.TipoChave> {

    private final Set<ChavesPix.TipoChave> chavesPix;

    public ChavesPix(Set<ChavesPix.TipoChave> chaves) {
        this.chavesPix = chaves;
    }

    @Override
    public int size() {
        return chavesPix.size();
    }

    @Override
    public boolean isEmpty() {
        return chavesPix.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return chavesPix.contains(o);
    }

    @Override
    public Iterator<ChavesPix.TipoChave> iterator() {
        return chavesPix.iterator();
    }

    @Override
    public Object[] toArray() {
        return chavesPix.toArray();
    }

    @Override
    public <T> T[] toArray(T @NonNull [] a) {
        return chavesPix.toArray(a);
    }

    @Override
    public boolean add(ChavesPix.TipoChave tipoChave) {
        return chavesPix.add(tipoChave);
    }

    @Override
    public boolean remove(Object o) {
        return chavesPix.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return chavesPix.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends ChavesPix.TipoChave> c) {
        return chavesPix.addAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return chavesPix.retainAll(c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return chavesPix.removeAll(c);
    }

    @Override
    public void clear() {
        chavesPix.clear();
    }

    public enum TipoChave {

        CPF(1, UtilFormatacoes::formatarCPF),
        CELULAR(2, UtilFormatacoes::formatarCelular),
        EMAIL(3, UtilFormatacoes::validarFormatacaoEmail),
        ALEATORIA(4, UtilFormatacoes::formatarEGerarChavePixAleatoria);

        private final int tipoChave;
        private final Function<String, String> operator;

        TipoChave(int tipo, Function<String, String> operator) {
            this.tipoChave = tipo;
            this.operator  = operator;
        }

        @Override
        public String toString() {
            return tipoChave == 1 ? "cpf" :
                    (tipoChave == 2 ? "celular" :
                    (tipoChave == 3 ? "email" :
                    "chave aleatória"));
        }

        public <T> String apply(T chave) {
            return operator.apply(String.valueOf(chave).trim());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return chavesPix.equals(o);
    }

    @Override
    public int hashCode() {
        return chavesPix.hashCode();
    }

    @Override
    public String toString() {
        return chavesPix.toString();
    }
}
