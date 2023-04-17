package com.github.rhaera.project.pocketbank.model.mapper;

import com.github.rhaera.project.pocketbank.model.dto.mongodb.RequisicaoCadastrarConta;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContaMapper {
    ContaBancaria toEntity(RequisicaoCadastrarConta cadastrarConta);
    RequisicaoCadastrarConta toDTO(ContaBancaria contaBancaria);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ContaBancaria partialUpdate(RequisicaoCadastrarConta cadastrarConta, @MappingTarget ContaBancaria contaBancaria);
}
