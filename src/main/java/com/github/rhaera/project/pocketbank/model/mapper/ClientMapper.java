package com.github.rhaera.project.pocketbank.model.mapper;

import com.github.rhaera.project.pocketbank.model.dto.RequisicaoCadastrarConta;
import com.github.rhaera.project.pocketbank.model.entity.abstraction.ContaBancaria;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {
    ContaBancaria toEntity(RequisicaoCadastrarConta cadastrarConta);
    RequisicaoCadastrarConta toDTO(ContaBancaria contaBancaria);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ContaBancaria partialUpdate(RequisicaoCadastrarConta cadastrarConta, @MappingTarget ContaBancaria contaBancaria);
}
