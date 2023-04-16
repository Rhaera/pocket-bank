package com.github.rhaera.project.pocketbank.model.entity.domain;

import com.github.rhaera.project.pocketbank.model.entity.abstraction.ContaBancaria;

import java.io.IOException;
import java.math.BigDecimal;

public class ContaCorrente extends ContaBancaria {

    public static class Builder extends ContaBancaria.Builder<Builder> {

        public Builder(String numeroConta, Client client) throws IOException {
            super(numeroConta, client);
        }

        @Override
        public ContaCorrente build() {
            return new ContaCorrente(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }

    private ContaCorrente(Builder builder) {
        super(builder);
    }

    @Override
    public BigDecimal saque(BigDecimal saque) {
        return saque;
    }

    @Override
    public BigDecimal deposito(BigDecimal deposito) {
        return deposito;
    }
}
