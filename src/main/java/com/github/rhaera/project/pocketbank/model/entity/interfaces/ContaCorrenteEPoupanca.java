package com.github.rhaera.project.pocketbank.model.entity.interfaces;

import com.github.rhaera.project.pocketbank.model.entity.domain.MovimentacaoFinanceira;

import static com.github.rhaera.project.pocketbank.model.entity.domain.MovimentacaoFinanceira.TipoTransacao;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public interface ContaCorrenteEPoupanca extends ContaMultiModal {
    default MovimentacaoFinanceira definirSaldoDestinadoAPoupanca(BigDecimal saldoTotal, BigDecimal saldoParaPoupanca) {
        validarSaldoEValor(saldoTotal, saldoTotal);
        return
        MovimentacaoFinanceira.getMovimentacao(LocalDateTime.now(), TipoTransacao.DEPOSITO, String.format("Depósito na poupança de %.2f realizado.", saldoParaPoupanca));
    }
}
