package com.github.rhaera.project.pocketbank.model.entity.domain;

import com.github.rhaera.project.pocketbank.model.entity.abstraction.ContaBancaria;

import java.io.IOException;
import java.math.BigDecimal;

public class ContaPoupanca extends ContaBancaria {

    public static class Builder extends ContaBancaria.Builder<Builder> {

        public Builder(String numeroConta, Client client) throws IOException {
            super(numeroConta, client);
        }

        @Override
        public ContaPoupanca build() {
            return new ContaPoupanca(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }

    private ContaPoupanca(Builder builder) {
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
