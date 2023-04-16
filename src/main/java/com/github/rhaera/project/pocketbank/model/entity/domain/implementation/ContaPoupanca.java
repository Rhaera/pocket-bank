package com.github.rhaera.project.pocketbank.model.entity.domain.implementation;

import com.github.rhaera.project.pocketbank.model.entity.domain.Client;
import com.github.rhaera.project.pocketbank.model.entity.interfaces.ContaCorrenteEPoupanca;

import lombok.Getter;

import java.io.IOException;
import java.math.BigDecimal;

@Getter
public class ContaPoupanca extends ContaBancaria implements ContaCorrenteEPoupanca {
    private BigDecimal saldoPoupanca = BigDecimal.ZERO;

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
        if (this.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            this.saldoPoupanca = this.getSaldo();
            this.setSaldo(BigDecimal.ZERO);
        }
    }

    @Override
    public BigDecimal saque(BigDecimal saque) {
        this.getExtrato().add(saqueNaConta(this.saldoPoupanca, saque));
        this.saldoPoupanca = this.saldoPoupanca.subtract(saque);
        return this.getSaldo();
    }

    @Override
    public BigDecimal deposito(BigDecimal deposito) {
        if (this.getTiposDaConta().contains(TipoConta.CORRENTE)) {
            this.getExtrato().add(depositoNaConta(deposito));
            this.setSaldo(this.getSaldo().add(deposito));
            return this.getSaldo();
        }
        this.getExtrato().add(depositoNaConta(deposito));
        this.saldoPoupanca = this.saldoPoupanca.add(deposito);
        return this.saldoPoupanca;
    }

    public BigDecimal definirSaldoParaPoupanca(BigDecimal saldoParaPoupanca) {
        if (!this.getTiposDaConta().contains(TipoConta.CORRENTE)) return saldoPoupanca;
        this.getExtrato().add(this.definirSaldoDestinadoAPoupanca(this.getSaldo(), saldoParaPoupanca));
        this.setSaldo(this.getSaldo().subtract(saldoParaPoupanca));
        return saldoPoupanca;
    }
}
