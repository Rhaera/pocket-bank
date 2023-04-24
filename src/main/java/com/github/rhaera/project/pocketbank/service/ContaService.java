package com.github.rhaera.project.pocketbank.service;

import com.github.rhaera.project.pocketbank.model.dto.mongodb.ContaObject;
import com.github.rhaera.project.pocketbank.model.entity.domain.Agencia;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ContaService {
    Optional<Agencia> addAgency(Agencia agency);
    Optional<ContaObject> createAccount(ContaObject accountRequest);
    Optional<Agencia> getAgencyById(String agencyNumber);
    Optional<ContaObject> getBankAccountByAgency(String agencyNumber, String accountNumber);
    List<Agencia> getAllAgencies();
    Map<String, List<ContaObject>> getAllAccountsGroupingByAgency();
    List<ContaObject> getAllAccountsByAgency(String number);
    Optional<ContaObject> updateAccountByAgency(String agencyNumber, ContaObject requestedAccount) throws IOException;
    Optional<ContaObject> updateAccountByClient(String cpf) throws IOException;
    Optional<Agencia> deleteAgencyById(String agencyNumber);
    Optional<ContaObject> deleteAccountByAgency(String agencyNumber, String accountNumber);
    Optional<ContaObject> deleteAccountByClient(String cpf);
}
