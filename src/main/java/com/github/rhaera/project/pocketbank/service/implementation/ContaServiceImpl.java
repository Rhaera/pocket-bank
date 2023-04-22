package com.github.rhaera.project.pocketbank.service.implementation;

import com.github.rhaera.project.pocketbank.model.dto.sql.AgenciaDTO;
import com.github.rhaera.project.pocketbank.model.entity.domain.Agencia;
import com.github.rhaera.project.pocketbank.model.entity.domain.Client;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;
import com.github.rhaera.project.pocketbank.model.utility.UtilFormatacoes;
import com.github.rhaera.project.pocketbank.repository.ContaRepository;
import com.github.rhaera.project.pocketbank.service.AgenciaService;
import com.github.rhaera.project.pocketbank.service.ContaService;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContaServiceImpl implements ContaService {

    private final ContaRepository repository;

    private final AgenciaService service;

    public ContaServiceImpl(ContaRepository repository, AgenciaService service) {
        this.repository = repository;
        this.service    = service;
    }

    @Override
    public Optional<Agencia> addAgency(Agencia agency) { // trim
        return service.getAllAgencies()
                        .stream()
                        .map(AgenciaDTO::getNumber)
                        .collect(Collectors.toList())
                        .contains(agency.getCodigoAgencia().trim()) ? Optional.of(repository.save(agency))
                        : Optional.empty();
    }

    @Override
    public Optional<ContaBancaria> createAccount(ContaBancaria accountRequest) {
        if (getAgencyById(accountRequest.getAgencia().trim()).isEmpty() || verifyClient(accountRequest.getClient().getCpf())) return Optional.empty();
        Agencia agencia = getAgencyById(accountRequest.getAgencia().trim()).get();
        agencia.getContasAtivas().add(accountRequest);
        repository.save(agencia);
        return Optional.of(accountRequest);
    }

    @Override
    public Optional<Agencia> getAgencyById(String agencyNumber) { // trim
        return repository.findById(agencyNumber.trim()).isPresent() ? Optional.of(repository.findById(agencyNumber.trim()).get()) : Optional.empty();
    }

    @Override
    public Optional<ContaBancaria> getBankAccountByAgency(String agencyNumber, String accountNumber) {
        return getAgencyById(agencyNumber).isPresent() ?
                (getAgencyById(agencyNumber).get()
                                            .getContasAtivas()
                                            .stream()
                                            .anyMatch(account -> account.getNumeroConta()
                                                                        .equals(accountNumber)) ?
                getAgencyById(agencyNumber).get()
                                            .getContasAtivas()
                                            .stream()
                                            .filter(account -> account.getNumeroConta()
                                                                        .equals(accountNumber))
                                            .findAny() : Optional.empty()) :
                Optional.empty();
    }

    @Override
    public List<Agencia> getAllAgencies() {
        return repository.findAll();
    }

    @Override
    public Map<String, List<ContaBancaria>> getAllAccountsGroupingByAgency() {
        Map<String, List<ContaBancaria>> accountListMapper = new HashMap<>(); // challenge
        getAllAgencies().forEach(agencia -> accountListMapper.put(agencia.getCodigoAgencia(), agencia.getContasAtivas()));
        return accountListMapper;
    }

    @Override
    public List<ContaBancaria> getAllAccountsByAgency(String number) {
        return repository.findById(number).isPresent() ? repository.findById(number)
                                                                    .get()
                                                                    .getContasAtivas() : Collections.emptyList();
    }

    @Override
    public Optional<ContaBancaria> updateAccountByAgency(String agencyNumber, ContaBancaria requestedAccount) throws IOException {
        if (verifyAgencyAndAccount(agencyNumber, requestedAccount.getNumeroConta())) return Optional.empty();
        ContaBancaria existingAccount = getBankAccountByAgency(agencyNumber, requestedAccount.getNumeroConta()
                                                                                            .trim())
                                                                                            .orElseThrow();
        existingAccount = existingAccount.equalizadorDosTiposDaContaEDadosDoCliente(requestedAccount);
        Agencia agency = getAgencyById(agencyNumber).orElseThrow();
        agency.getContasAtivas().set(agency.getContasAtivas().indexOf(requestedAccount), existingAccount);
        repository.save(agency);
        return Optional.ofNullable(existingAccount);
    }

    @Override
    public Optional<ContaBancaria> updateAccountByClient(String cpf) throws IOException {
        if (verifyClient(cpf)) {
            ContaBancaria existingAccount = searchAccountByClient(cpf);
            return updateAccountByAgency(existingAccount.getAgencia(), existingAccount);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Agencia> deleteAgencyById(String agencyNumber) {
        if (service.deleteAgencyByNumber(agencyNumber.trim()).isEmpty()) {
            Agencia agency = getAgencyById(agencyNumber).orElseThrow();
            repository.deleteById(agencyNumber.trim());
            return Optional.of(agency);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ContaBancaria> deleteAccountByAgency(String agencyNumber, String accountNumber) {
        if (verifyAgencyAndAccount(agencyNumber, accountNumber)) return Optional.empty();
        Agencia agency = getAgencyById(agencyNumber).orElseThrow();
        ContaBancaria deletedAccount = getBankAccountByAgency(agencyNumber, accountNumber).orElseThrow();
        agency.getContasAtivas().remove(deletedAccount);
        repository.save(agency);
        return Optional.of(deletedAccount);
    }

    @Override
    public Optional<ContaBancaria> deleteAccountByClient(String cpf) {
        if (!verifyClient(cpf)) return Optional.empty();
        ContaBancaria deletedAccount = searchAccountByClient(cpf);
        Agencia agency = getAgencyById(deletedAccount.getAgencia()).orElseThrow();
        agency.getContasAtivas().remove(deletedAccount);
        repository.save(agency);
        return Optional.of(deletedAccount);
    }

    private boolean verifyAgencyAndAccount(String agencyNumber, String accountNumber) {
        return getAgencyById(agencyNumber).isEmpty() || getBankAccountByAgency(agencyNumber.trim(), accountNumber.trim()).isEmpty();
    }

    private boolean verifyClient(String cpf) {
        String formatedCPF = UtilFormatacoes.formatarCPF(cpf).trim();
        return getAllAgencies().stream()
                                .map(Agencia::getContasAtivas)
                                .anyMatch(accountList -> accountList.stream()
                                                                    .map(ContaBancaria::getClient)
                                                                    .anyMatch(client -> client.getCpf()
                                                                                                .equals(formatedCPF)));
    }

    private ContaBancaria searchAccountByClient(String cpf) {
        String formatedCPF = UtilFormatacoes.formatarCPF(cpf).trim();
        return getAllAccountsGroupingByAgency().values()
                                .stream()
                                .filter(list -> list.stream()
                                        .map(ContaBancaria::getClient)
                                        .map(Client::getCpf)
                                        .collect(Collectors.toList())
                                        .contains(formatedCPF))
                                .collect(Collectors.toList())
                                .get(0)
                                .stream()
                                .filter(account -> account.getClient()
                                        .getCpf()
                                        .equals(formatedCPF))
                                .collect(Collectors.toList())
                                .get(0);
    }
}
