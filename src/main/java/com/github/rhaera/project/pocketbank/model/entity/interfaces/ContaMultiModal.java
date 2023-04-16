package com.github.rhaera.project.pocketbank.model.entity.interfaces;

import static com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria.TipoConta;
import static com.github.rhaera.project.pocketbank.model.entity.domain.MovimentacaoFinanceira.TipoTransacao;

import com.github.rhaera.project.pocketbank.model.entity.domain.MovimentacaoFinanceira;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;

@Service
public interface ContaMultiModal {
    boolean definirTiposDaConta(EnumSet<? extends TipoConta> opicoesDeConta);
    default void validarSaldoEValor(BigDecimal saldo, BigDecimal valor) {
        if (saldo.compareTo(valor) < 0) throw new IllegalArgumentException("Saldo insuficiente!");
        if (valor.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Quantia inválida!");
    }
    default MovimentacaoFinanceira saqueNaConta(BigDecimal saldo, BigDecimal saque) {
        validarSaldoEValor(saldo, saque);
        return MovimentacaoFinanceira.getMovimentacao(LocalDateTime.now(), TipoTransacao.SAQUE, String.format("Saque de R$ %.2f realizado.", saque));
    }
    default MovimentacaoFinanceira depositoNaConta(BigDecimal deposito) {
        validarSaldoEValor(deposito, deposito);
        return MovimentacaoFinanceira.getMovimentacao(LocalDateTime.now(), TipoTransacao.DEPOSITO, String.format("Depósito de R$ %.2f realizado.", deposito));
    }
}
