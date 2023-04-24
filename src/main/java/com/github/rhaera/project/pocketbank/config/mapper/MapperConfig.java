package com.github.rhaera.project.pocketbank.config.mapper;

import com.github.rhaera.project.pocketbank.model.dto.mongodb.RequisicaoCadastrarConta;
import com.github.rhaera.project.pocketbank.model.dto.mongodb.ContaObject;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaPoupanca;
import com.github.rhaera.project.pocketbank.model.mapper.ContaMapper;

import static com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria.TipoConta;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.stream.Collectors;

@Configuration
public class MapperConfig {
    @Bean
    public ContaMapper mapper() {
        return new ContaMapper() {
            @Override
            public ContaObject toDto(RequisicaoCadastrarConta cadastrarConta) throws IOException {
                if (cadastrarConta.getTipos().size() == 1)
                    return ContaMapper.accountTypeCheckingAndSavingFilter(cadastrarConta).getClass().equals(ContaPoupanca.class) ?
                            new ContaObject().toDto(ContaMapper.accountTypeCheckingAndSavingFilter(cadastrarConta)) :
                            new ContaObject().toDto(ContaMapper.accountTypeCheckingAndSalaryFilter(cadastrarConta));
                return cadastrarConta.getTipos().contains(TipoConta.SALARIO) ?
                        new ContaObject().toDto(ContaMapper.accountTypeCheckingAndSalaryFilter(cadastrarConta)) :
                        new ContaObject().toDto(ContaMapper.accountTypeCheckingAndSavingFilter(cadastrarConta));
            }
            @Override
            public RequisicaoCadastrarConta toRequest(ContaObject contaBancaria) {
                return RequisicaoCadastrarConta.getInstance(contaBancaria.getCliente(), contaBancaria.getNumConta(), contaBancaria.getNumAgencia());
            }
            @Override
            public ContaObject partialUpdate(RequisicaoCadastrarConta cadastrarConta, ContaBancaria contaBancaria) {
                ContaObject contaAtualizada = new ContaObject().toDto(contaBancaria);
                if (cadastrarConta.validacaoNumeroContaECep()) {
                    cadastrarConta.requisicaoValidada();
                    contaAtualizada.setNumConta(cadastrarConta.getNumeroConta());
                }
                if (!cadastrarConta.getTipos().equals(contaBancaria.getTiposDaConta()) || !cadastrarConta.getClient().equals(contaBancaria.getClient())) {
                    contaAtualizada.setCliente(cadastrarConta.getClient());
                    contaAtualizada.setTipos(cadastrarConta.getTipos()
                                                            .stream()
                                                            .map(TipoConta::toString)
                                                            .collect(Collectors.toSet()));
                }
                return contaAtualizada;
            }
        };
    }
}
