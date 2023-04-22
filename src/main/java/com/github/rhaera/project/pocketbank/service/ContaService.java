package com.github.rhaera.project.pocketbank.service;

import com.github.rhaera.project.pocketbank.model.entity.domain.Agencia;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ContaService {
    Optional<Agencia> addAgency(Agencia agency);
    Optional<ContaBancaria> createAccount(ContaBancaria accountRequest);
    Optional<Agencia> getAgencyById(String agencyNumber);
    Optional<ContaBancaria> getBankAccountByAgency(String agencyNumber, String accountNumber);
    List<Agencia> getAllAgencies();
    Map<String, List<ContaBancaria>> getAllAccountsGroupingByAgency();
    List<ContaBancaria> getAllAccountsByAgency(String number);
    Optional<ContaBancaria> updateAccountByAgency(String agencyNumber, ContaBancaria requestedAccount) throws IOException;
    Optional<ContaBancaria> updateAccountByClient(String cpf) throws IOException;
    Optional<Agencia> deleteAgencyById(String agencyNumber);
    Optional<ContaBancaria> deleteAccountByAgency(String agencyNumber, String accountNumber);
    Optional<ContaBancaria> deleteAccountByClient(String cpf);
}
