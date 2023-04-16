package com.github.rhaera.project.pocketbank.model.entity.domain.implementation;

import com.github.rhaera.project.pocketbank.model.entity.domain.Client;
import com.github.rhaera.project.pocketbank.model.entity.interfaces.ContaCorrenteESalario;

import lombok.Getter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class ContaSalario extends ContaBancaria implements ContaCorrenteESalario {

    private int limiteSaquesMensais;
    private int numeroDeSaquesMensais;

    public static class Builder extends ContaBancaria.Builder<Builder> {
        private int limiteSaquesMensais   = 5;
        private final int numeroDeSaquesMensais = 0;

        public Builder(String numeroConta, Client client) throws IOException {
            super(numeroConta, client);
        }

        @Override
        public ContaSalario build() {
            return new ContaSalario(this);
        }

        @Override
        public Builder self() {
            return this;
        }

        public Builder solicitarAumentoSaquesMensais(int novoLimite) {
            if (limiteSaquesMensais >= novoLimite) return self();
            // Analise de Credito;
            limiteSaquesMensais = novoLimite;
            return self();
        }
    }

    private ContaSalario(Builder builder) {
        super(builder);
        limiteSaquesMensais   = builder.limiteSaquesMensais;
        numeroDeSaquesMensais = builder.numeroDeSaquesMensais;
    }

    @Override
    public BigDecimal saque(BigDecimal saque) {
        if (limiteSaquesMensais == numeroDeSaquesMensais)
            throw new IllegalStateException("Limite de saques mensal atingido para esse mês, por favor, use seu cartão de débito físico ou virtual");
        numeroDeSaquesMensais += 1;
        this.getExtrato().add(saqueNaConta(this.getSaldo(), saque));
        this.setSaldo(this.getSaldo().subtract(saque));
        return this.getSaldo();
    }

    @Override
    public BigDecimal deposito(BigDecimal deposito) {
        if (!this.getTiposDaConta().contains(TipoConta.CORRENTE))
            throw new IllegalStateException("Conta salário simples não pode fazer essa operação (abra uma conta corrente).");
        this.getExtrato().add(depositoNaConta(deposito));
        this.setSaldo(this.getSaldo().add(deposito));
        return this.getSaldo();
    }

    @Override
    public void definirPortabilidade() {
        this.getTiposDaConta().add(TipoConta.CORRENTE);
    }

    public int recarregarSaques() {
        if (LocalDate.now().getDayOfMonth() == 1 && numeroDeSaquesMensais < limiteSaquesMensais) numeroDeSaquesMensais = limiteSaquesMensais;
        return numeroDeSaquesMensais;
    }

    public int pedirAumentoDoLimiteDeSaquesMensais(int novoLimite) {
        if (limiteSaquesMensais >= novoLimite) return limiteSaquesMensais;
        limiteSaquesMensais = novoLimite;
        return limiteSaquesMensais;
    }
}
