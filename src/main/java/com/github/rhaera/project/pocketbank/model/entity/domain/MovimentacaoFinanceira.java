package com.github.rhaera.project.pocketbank.model.entity.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class MovimentacaoFinanceira implements Cloneable {

    @NonNull
    private final LocalDateTime dataTransacao;
    @NonNull
    private final TipoTransacao tipoTransacao;
    @NonNull
    private final String numeroContaOrigem;
    private final String numeroContaDestino;
    private String descricao;
    private BigDecimal taxaTransaction;

    public String formatarParaExtrato() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(this.dataTransacao) + " - " + tipoTransacao + ": " + descricao;
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

    private enum TipoTransacao {
        SAQUE("saque"),
        DEPOSITO("depóito"),
        PIX("pix"),
        TRANSFERENCIA("transferência");

        private final String tipo;

        TipoTransacao(String tipo) {
            this.tipo = tipo;
        }

        @Override
        public String toString() {
            return tipo;
        }
    }

    // builder
}
