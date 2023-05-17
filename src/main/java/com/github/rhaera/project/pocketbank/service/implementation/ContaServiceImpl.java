package com.github.rhaera.project.pocketbank.service.implementation;

import com.github.rhaera.project.pocketbank.model.dto.sql.AgenciaDTO;
import com.github.rhaera.project.pocketbank.model.dto.mongodb.ContaObject;
import com.github.rhaera.project.pocketbank.model.entity.domain.Agencia;
import com.github.rhaera.project.pocketbank.model.entity.domain.ClientEntity;
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
    public Optional<ContaObject> createAccount(ContaObject accountRequest) { //trim
        if (getAgencyById(accountRequest.getNumAgencia().trim()).isEmpty()) return Optional.empty(); //|| verifyClient(accountRequest.getCliente().getNonNullCpf())) return Optional.empty();
        Agencia agencia = getAgencyById(accountRequest.getNumAgencia().trim()).get();
        agencia.getContasAtivas().add(accountRequest);
        repository.save(agencia);
        return Optional.of(accountRequest);
    }

    @Override
    public Optional<Agencia> getAgencyById(String agencyNumber) { // trim
        return repository.findById(agencyNumber.trim()).isPresent() ? Optional.of(repository.findById(agencyNumber.trim()).get()) : Optional.empty();
    }

    @Override
    public Optional<ContaObject> getBankAccountByAgency(String agencyNumber, String accountNumber) {
        return getAgencyById(agencyNumber).isPresent() ?
                (getAgencyById(agencyNumber).get()
                                            .getContasAtivas()
                                            .stream()
                                            .anyMatch(account -> account.getNumConta()
                                                                        .equals(accountNumber)) ?
                getAgencyById(agencyNumber).get()
                                            .getContasAtivas()
                                            .stream()
                                            .filter(account -> account.getNumConta()
                                                                        .equals(accountNumber))
                                            .findAny() : Optional.empty()) :
                Optional.empty();
    }

    @Override
    public List<Agencia> getAllAgencies() {
        return repository.findAll();
    }

    @Override
    public Map<String, List<ContaObject>> getAllAccountsGroupingByAgency() {
        Map<String, List<ContaObject>> accountListMapper = new HashMap<>(); // challenge
        getAllAgencies().forEach(agencia -> accountListMapper.put(agencia.getCodigoAgencia(), agencia.getContasAtivas()));
        return accountListMapper;
    }

    @Override
    public List<ContaObject> getAllAccountsByAgency(String number) {
        return repository.findById(number).isPresent() ? repository.findById(number)
                                                                    .get()
                                                                    .getContasAtivas() : Collections.emptyList();
    }

    @Override
    public Optional<ContaObject> updateAccountByAgency(String agencyNumber, ContaObject requestedAccount) throws IOException {
        if (verifyAgencyAndAccount(agencyNumber, requestedAccount.getNumConta())) return Optional.empty();
        ContaObject existingAccount = getBankAccountByAgency(agencyNumber, requestedAccount.getNumConta()
                                                                                            .trim())
                                                                                            .orElseThrow();
        existingAccount = existingAccount.equalizadorDosTiposDaContaEDadosDoCliente(requestedAccount);
        Agencia agency = getAgencyById(agencyNumber).orElseThrow();
        agency.getContasAtivas().set(agency.getContasAtivas().indexOf(requestedAccount), existingAccount);
        repository.save(agency);
        return Optional.ofNullable(existingAccount);
    }

    @Override
    public Optional<ContaObject> updateAccountByClient(String cpf) throws IOException {
        if (verifyClient(cpf)) {
            ContaObject existingAccount = searchAccountByClient(cpf);
            return updateAccountByAgency(existingAccount.getNumAgencia(), existingAccount);
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
    public Optional<ContaObject> deleteAccountByAgency(String agencyNumber, String accountNumber) {
        if (verifyAgencyAndAccount(agencyNumber, accountNumber)) return Optional.empty();
        Agencia agency = getAgencyById(agencyNumber).orElseThrow();
        ContaObject deletedAccount = getBankAccountByAgency(agencyNumber, accountNumber).orElseThrow();
        agency.getContasAtivas().remove(deletedAccount);
        repository.save(agency);
        return Optional.of(deletedAccount);
    }

    @Override
    public Optional<ContaObject> deleteAccountByClient(String cpf) {
        if (!verifyClient(cpf)) return Optional.empty();
        ContaObject deletedAccount = searchAccountByClient(cpf);
        Agencia agency = getAgencyById(deletedAccount.getNumAgencia()).orElseThrow();
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
                .filter(Objects::nonNull).noneMatch(accountList -> accountList.stream()
                                            .map(ContaObject::getCliente)
                                            .filter(Objects::nonNull)
                                            .anyMatch(client -> client.getNonNullCpf()
                                                                        .equals(formatedCPF)));
    }

    private ContaObject searchAccountByClient(String cpf) {
        String formatedCPF = UtilFormatacoes.formatarCPF(cpf).trim();
        return getAllAccountsGroupingByAgency().values()
                                .stream()
                                .filter(list -> list.stream()
                                        .map(ContaObject::getCliente)
                                        .map(ClientEntity::getCpf)
                                        .collect(Collectors.toList())
                                        .contains(formatedCPF))
                                .collect(Collectors.toList())
                                .get(0)
                                .stream()
                                .filter(account -> account.getCliente()
                                        .getCpf()
                                        .equals(formatedCPF))
                                .collect(Collectors.toList())
                                .get(0);
    }
}
