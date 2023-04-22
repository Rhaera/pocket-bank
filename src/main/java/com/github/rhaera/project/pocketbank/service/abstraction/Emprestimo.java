package com.github.rhaera.project.pocketbank.service.abstraction;

import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaCorrente;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.stream.Stream;

public abstract class Emprestimo implements AnaliseDeCredito {

    private BigDecimal escoreAvaliado = BigDecimal.ZERO;

    @Override
    public BigDecimal definirLimiteEmprestimo(ContaBancaria conta) {
        int tempoDeExistenciaDaContaEmAnos = LocalDateTime.now().getYear() - conta.getCriacaoDaConta().getYear();
        ContaCorrente corrente = (ContaCorrente) conta;
        BigDecimal creditCardIncrement = BigDecimal.ONE;
        if (conta.getClass().equals(ContaCorrente.class) && corrente.getDataDeFabricacaoCartaoCredito() != null)
            creditCardIncrement = BigDecimal.valueOf(2.50d);
        numerosDeMersenneProbabilisticos()
                .map(numeroPrimo -> BigInteger.TWO.pow(numeroPrimo.intValueExact())
                                                    .subtract(BigInteger.ONE))
                .filter(numeroMersenne ->
                        numeroMersenne.isProbablePrime(5 + conta.getSaldo().intValue() / 100 + conta.getExtrato().size() + tempoDeExistenciaDaContaEmAnos))
                .limit(Math.max(BigDecimal.valueOf(2L)
                                            .multiply(BigDecimal.valueOf(conta.getExtrato().size()))
                                            .divide(BigDecimal.valueOf(5L), 0, RoundingMode.HALF_UP)
                                            .add(creditCardIncrement.multiply(BigDecimal.valueOf(tempoDeExistenciaDaContaEmAnos)))
                                            .intValueExact(), 2))
                .forEach(mersenne ->
                    escoreAvaliado = escoreAvaliado.add(BigDecimal.valueOf(2L)
                                                                .divide(BigDecimal.valueOf(3L)
                                                                .subtract(BigDecimal.valueOf(1L)
                                                                .divide(BigDecimal.valueOf(mersenne.intValueExact()), 0, RoundingMode.HALF_UP)),
                                                                        0,
                                                                        RoundingMode.HALF_UP))
                );
        return escoreAvaliado;
    }

    public abstract boolean definirSePossuiOfertaEmprestimo(ContaBancaria conta);

    private static Stream<BigInteger> numerosDeMersenneProbabilisticos() {
        return Stream.iterate(BigInteger.TWO, BigInteger::nextProbablePrime);
    }
}
