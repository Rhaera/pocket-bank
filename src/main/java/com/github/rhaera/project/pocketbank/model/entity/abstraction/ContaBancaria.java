package com.github.rhaera.project.pocketbank.model.entity.abstraction;

import com.github.rhaera.project.pocketbank.controller.exception.TipoContaIlegalException;
import com.github.rhaera.project.pocketbank.model.entity.domain.Client;
import com.github.rhaera.project.pocketbank.model.entity.domain.MovimentacaoFinanceira;
import com.github.rhaera.project.pocketbank.model.utility.UtilLocalizacao;
import com.github.rhaera.project.pocketbank.service.abstraction.ContaMultiModal;

import static com.github.rhaera.project.pocketbank.model.entity.domain.MovimentacaoFinanceira.TipoTransacao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ContaBancaria implements ContaMultiModal {

    @NonNull
    private final Set<TipoConta> tiposDaConta;
    public enum TipoConta {
        CORRENTE(3),
        POUPANCA(2),
        SALARIO(1);

        private final int tipoPredominante;

        TipoConta(int predominancia) {
            if (predominancia < 1 || predominancia > 3) throw new TipoContaIlegalException("Tipo de conta inválido e/ou inexistente!");
            this.tipoPredominante = predominancia;
        }

        @Override
        public String toString() {
            return this.tipoPredominante == 3 ? "Conta corrente" : (this.tipoPredominante == 2 ? "Conta poupança" : "Conta salário");
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

    public abstract static class Builder<T extends Builder<T>> {
        private final EnumSet<TipoConta> tipos = EnumSet.noneOf(TipoConta.class);
        private final String agencia;
        private final String numeroConta;
        private final Client client;
        private LocalDate criacaoDaConta = LocalDate.now();
        private final List<MovimentacaoFinanceira> extrato = new ArrayList<>();
        private BigDecimal saldo = BigDecimal.ZERO;

        public Builder(String numeroConta, Client client) throws IOException {
            this.agencia = UtilLocalizacao.agenciaMaisProxima(client.getCep());
            this.numeroConta = numeroConta;
            this.client = client;
        }

        public abstract ContaBancaria build();

        public abstract T self();

        public T adicionarTipoConta(TipoConta tipoConta) {
            tipos.add(Objects.requireNonNull(tipoConta));
            return self();
        }

        public T programarCriacaoDaConta(LocalDate dataProgramada) {
            criacaoDaConta = dataProgramada;
            return self();
        }

        public T adicionarSaldoDeAbertura(BigDecimal saldoDeAbertura) {
            saldo = saldo.add(saldoDeAbertura);
            extrato.add(MovimentacaoFinanceira.getMovimentacao(LocalDateTime.of(criacaoDaConta, LocalTime.from(LocalDateTime.now().plusSeconds(5L))),
                                                                TipoTransacao.DEPOSITO,
                                                                String.format("PARABÉNS! VOCÊ POSSUI %.2f COMO SALDO DE ABERTURA", saldo)));
            return self();
        }
    }

    public ContaBancaria(Builder<?> builder) {
        tiposDaConta   = builder.tipos.clone();
        agencia        = builder.agencia;
        numeroConta    = builder.numeroConta;
        client         = builder.client;
        criacaoDaConta = builder.criacaoDaConta;
        extrato        = builder.extrato;
        saldo          = builder.saldo;
    }

    public abstract BigDecimal saque(BigDecimal saque);

    public abstract BigDecimal deposito(BigDecimal deposito);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ContaBancaria conta = (ContaBancaria) obj;
        return this.numeroConta.equals(conta.numeroConta) &&
                this.agencia.equals(conta.agencia) &&
                this.client.getCpf().equals(conta.client.getCpf());
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencia.concat(agencia), numeroConta.concat(agencia));
    }

    @Override
    public boolean definirModoDeUsor(EnumSet<? extends TipoConta> options) {
        return this.tiposDaConta.addAll(options);
    }
}
