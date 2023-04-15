package com.github.rhaera.project.pocketbank.model.entity.abstraction;

import com.github.rhaera.project.pocketbank.model.entity.domain.Client;
import com.github.rhaera.project.pocketbank.model.entity.domain.MovimentacaoFinanceira;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class ContaBancaria {

    public enum TipoConta {
        CORRENTE("corrente"),
        POUPANCA("poupança"),
        SALARIO("salário");

        private final String tipo;

        TipoConta(String tipo) {
            this.tipo = tipo;
        }

        @Override
        public String toString() {
            return tipo;
        }
    }

    @NonNull
    private final String agencia;
    @NonNull
    private final String numeroConta;
    @NonNull
    private final Client client;
    @NonNull
    private final LocalDate criacaoDaConta;
    private final List<MovimentacaoFinanceira> extrato;
    private BigDecimal saldo;

    //Builder with enumSet
}
