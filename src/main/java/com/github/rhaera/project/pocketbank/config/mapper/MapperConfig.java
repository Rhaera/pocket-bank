package com.github.rhaera.project.pocketbank.config.mapper;

import com.github.rhaera.project.pocketbank.model.dto.mongodb.RequisicaoCadastrarConta;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;
import com.github.rhaera.project.pocketbank.model.mapper.ContaMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ContaMapper mapper() {
        return new ContaMapper() {
            @Override
            public ContaBancaria toEntity(RequisicaoCadastrarConta cadastrarConta) {
                return null;
            }

            @Override
            public RequisicaoCadastrarConta toDTO(ContaBancaria contaBancaria) {
                return null;
            }

            @Override
            public ContaBancaria partialUpdate(RequisicaoCadastrarConta cadastrarConta, ContaBancaria contaBancaria) {
                return null;
            }
        };
    }
}
