package com.github.rhaera.project.pocketbank.model.entity.domain.implementation;

import com.github.rhaera.project.pocketbank.model.entity.domain.Client;
import com.github.rhaera.project.pocketbank.model.entity.interfaces.ContaTripla;
import com.github.rhaera.project.pocketbank.service.domain.Pix;

import static com.github.rhaera.project.pocketbank.service.abstraction.implementation.ChavesPix.TipoChave;

import lombok.Getter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.UUID;

@Getter
public class ContaCorrente extends ContaBancaria implements ContaTripla {
    private boolean portabilidade;
    private boolean cartaoCredito;
    private String chaveAleatoria;
    private final Pix chavesPix;

    public static class Builder extends ContaBancaria.Builder<Builder> {
        private boolean portabilidade = false;
        private boolean cartaoCredito = false;
        private String chaveAleatoria = "";
        private final Pix chavesPix = new Pix(EnumSet.noneOf(TipoChave.class));

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

        public Builder ativarPortabilidade() {
            adicionarTipoConta(TipoConta.SALARIO);
            portabilidade = true;
            return self();
        }

        public Builder requisitarCartaoCredito() {
            cartaoCredito = true;
            return self();
        }

        public Builder adicionarChavePixCPF() {
            chavesPix.add(TipoChave.CPF);
            return self();
        }

        public Builder adicionarChavePixCelular() {
            chavesPix.add(TipoChave.CELULAR);
            return self();
        }

        public Builder adicionarChavePixEmail() {
            chavesPix.add(TipoChave.EMAIL);
            return self();
        }

        public Builder adicionarChavePixAleatoria() {
            chavesPix.add(TipoChave.ALEATORIA);
            chaveAleatoria = TipoChave.ALEATORIA.apply(UUID.randomUUID().toString().split("-")[0]);
            return self();
        }
    }

    private ContaCorrente(Builder builder) {
        super(builder);
        portabilidade  = builder.portabilidade;
        cartaoCredito  = builder.cartaoCredito;
        chaveAleatoria = builder.chaveAleatoria;
        chavesPix      = builder.chavesPix;
    }
    @Override
    public BigDecimal saque(BigDecimal saque) {
        this.getExtrato().add(saqueNaConta(this.getSaldo(), saque));
        this.setSaldo(this.getSaldo().subtract(saque));
        return this.getSaldo();
    }
    @Override
    public BigDecimal deposito(BigDecimal deposito) {
        this.getExtrato().add(depositoNaConta(deposito));
        this.setSaldo(this.getSaldo().add(deposito));
        return this.getSaldo();
    }
    @Override
    public void definirPortabilidade() {
        if (portabilidade) {
            this.getTiposDaConta().remove(TipoConta.SALARIO);
            portabilidade = false;
            return;
        }
        this.getTiposDaConta().add(TipoConta.SALARIO);
        portabilidade = true;
    }
    public void solicitarOuBloquearCartaoDeCredito() {
        if (cartaoCredito) {
            cartaoCredito = false;
            return;
        }
        cartaoCredito = true;
    }
    public BigDecimal definirSaldoParaPoupanca(BigDecimal saldoParaPoupanca) {
        this.getExtrato().add(this.definirSaldoDestinadoAPoupanca(this.getSaldo(), saldoParaPoupanca));
        this.getTiposDaConta().add(TipoConta.POUPANCA);
        this.setSaldo(this.getSaldo().subtract(saldoParaPoupanca));
        return this.getSaldo();
    }
    public boolean apagarChaveCPF() {
        return chavesPix.remove(TipoChave.CPF);
    }
    public boolean cadastrarChaveCPF() {
        return chavesPix.add(TipoChave.CPF);
    }
    public boolean apagarChaveCelular() {
        return chavesPix.remove(TipoChave.CELULAR);
    }
    public boolean cadastrarChaveCelular() {
        return chavesPix.add(TipoChave.CELULAR);
    }
    public boolean apagarChaveEmail() {
        return chavesPix.remove(TipoChave.EMAIL);
    }
    public boolean cadastrarChaveEmail() {
        return chavesPix.add(TipoChave.EMAIL);
    }
    public boolean apagarChaveAleatoria() {
        if (chavesPix.contains(TipoChave.ALEATORIA)) chaveAleatoria = "";
        return chavesPix.remove(TipoChave.ALEATORIA);
    }
    public String cadastrarOuRedefinirChaveAleatoria() {
        if (chavesPix.contains(TipoChave.ALEATORIA)) {
            chaveAleatoria = TipoChave.ALEATORIA.apply(chaveAleatoria);
            return chaveAleatoria;
        }
        chavesPix.add(TipoChave.ALEATORIA);
        chaveAleatoria = TipoChave.ALEATORIA.apply(UUID.randomUUID().toString().split("-")[0]);
        return chaveAleatoria;
    }
}
