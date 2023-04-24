package com.github.rhaera.project.pocketbank.model.entity.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MovimentacaoFinanceira implements Cloneable, Serializable {

    @NonNull
    private final LocalDateTime dataTransacao;
    @NonNull
    private final TipoTransacao tipoTransacao;
    private final String numeroContaOrigem;
    private final String numeroContaDestino;
    private String descricao;
    private BigDecimal taxaTransaction;

    public String formatarParaExtrato() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(dataTransacao) + " - " + tipoTransacao + ": " + descricao;
    }

    @Override
    public MovimentacaoFinanceira clone() throws CloneNotSupportedException {
        try {
            super.clone();
            return this;
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new CloneNotSupportedException("Ops! Não foi possível realizar a transação. Por favor, tente novamente mais tarde.");
        }
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public enum TipoTransacao {
        @JsonProperty("saque")
        SAQUE("saque"),
        @JsonProperty("depósito")
        DEPOSITO("depóito"),
        @JsonProperty("pix")
        PIX("pix"),
        @JsonProperty("transferência")
        TRANSFERENCIA("transferência"),
        @JsonProperty("empréstimo")
        EMPRESTIMO("empréstimo");

        private final String tipo;

        TipoTransacao(String tipo) {
            this.tipo = tipo;
        }

        @Override
        public String toString() {
            return tipo;
        }
    }

    public static class Builder {
        private final LocalDateTime data;
        private final TipoTransacao transacao;
        private String contaOrigem = "";
        private String contaDestino = "";
        private String descricao = "";
        private BigDecimal taxa = BigDecimal.ZERO;

        public Builder(LocalDateTime data, TipoTransacao transacao) {
            this.data      = data;
            this.transacao = transacao;
        }

        public Builder definirContaOrigem(ContaBancaria conta) {
            this.contaOrigem = conta.getNumeroConta();
            return this;
        }

        public Builder definirContaDestino(ContaBancaria conta) {
            this.contaDestino = conta.getNumeroConta();
            return this;
        }

        public Builder definirDescricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public Builder definirTaxa(BigDecimal taxa) {
            this.taxa = taxa;
            return this;
        }

        public MovimentacaoFinanceira build() {
            return new MovimentacaoFinanceira(this);
        }
    }

    private MovimentacaoFinanceira(Builder builder) {
        this.dataTransacao      = builder.data;
        this.tipoTransacao      = builder.transacao;
        this.numeroContaOrigem  = builder.contaOrigem;
        this.numeroContaDestino = builder.contaDestino;
        this.descricao          = builder.descricao;
        this.taxaTransaction    = builder.taxa;
    }

    public static MovimentacaoFinanceira getMovimentacao(LocalDateTime dataEHora, TipoTransacao tipo, String descricao) {
        return new Builder(dataEHora, tipo).definirDescricao(descricao).build();
    }
}
