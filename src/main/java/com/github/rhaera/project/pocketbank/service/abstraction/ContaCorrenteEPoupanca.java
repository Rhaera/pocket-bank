package com.github.rhaera.project.pocketbank.service.abstraction;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface ContaCorrenteEPoupanca extends ContaMultiModal {
    BigDecimal definirSaldoDestinadoAPoupanca(BigDecimal saldoParaPoupanca);
}
