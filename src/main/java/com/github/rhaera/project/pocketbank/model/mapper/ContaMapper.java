package com.github.rhaera.project.pocketbank.model.mapper;

import com.github.rhaera.project.pocketbank.model.dto.mongodb.RequisicaoCadastrarConta;
import com.github.rhaera.project.pocketbank.model.dto.mongodb.ContaObject;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaCorrente;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaPoupanca;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaSalario;

import static com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria.TipoConta;

import org.mapstruct.*;

import java.io.IOException;
import java.util.EnumSet;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContaMapper {
    ContaObject toDto(RequisicaoCadastrarConta cadastrarConta) throws IOException;

    RequisicaoCadastrarConta toRequest(ContaObject contaBancaria);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ContaObject partialUpdate(RequisicaoCadastrarConta cadastrarConta, @MappingTarget ContaBancaria contaBancaria) throws IOException;

    static ContaBancaria accountTypeCheckingAndSalaryFilter(RequisicaoCadastrarConta cadastrarConta) throws IOException {
        if (cadastrarConta.getTipos().contains(TipoConta.SALARIO) && cadastrarConta.getTipos().size() == 1)
            return new ContaSalario.Builder(cadastrarConta.getNumeroConta(), cadastrarConta.getClient()).adicionarTipoConta(TipoConta.SALARIO).build();
        if (cadastrarConta.getTipos().containsAll(EnumSet.of(TipoConta.CORRENTE, TipoConta.SALARIO)))
            return new ContaCorrente.Builder(cadastrarConta.getNumeroConta(), cadastrarConta.getClient())
                                    .adicionarTiposConta(EnumSet.of(TipoConta.CORRENTE, TipoConta.SALARIO))
                                    .build();
        return new ContaCorrente.Builder(cadastrarConta.getNumeroConta(), cadastrarConta.getClient())
                .adicionarTipoConta(TipoConta.CORRENTE)
                .build();
    }

    static ContaBancaria accountTypeCheckingAndSavingFilter(RequisicaoCadastrarConta cadastrarConta) throws IOException {
        if (cadastrarConta.getTipos().contains(TipoConta.POUPANCA) && cadastrarConta.getTipos().size() == 1)
            return new ContaPoupanca.Builder(cadastrarConta.getNumeroConta(), cadastrarConta.getClient()).adicionarTipoConta(TipoConta.POUPANCA).build();
        if (cadastrarConta.getTipos().containsAll(EnumSet.of(TipoConta.CORRENTE, TipoConta.POUPANCA)))
            return new ContaCorrente.Builder(cadastrarConta.getNumeroConta(), cadastrarConta.getClient())
                                    .adicionarTiposConta(EnumSet.of(TipoConta.CORRENTE, TipoConta.POUPANCA))
                                    .build();
        return new ContaCorrente.Builder(cadastrarConta.getNumeroConta(), cadastrarConta.getClient())
                                .adicionarTipoConta(TipoConta.CORRENTE)
                                .build();
    }
}
