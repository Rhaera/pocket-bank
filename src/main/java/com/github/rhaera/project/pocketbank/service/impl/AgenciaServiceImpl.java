package com.github.rhaera.project.pocketbank.service.impl;

import com.github.rhaera.project.pocketbank.model.dto.sql.AgenciaDTO;
import com.github.rhaera.project.pocketbank.model.entity.AgenciaEntity;
import com.github.rhaera.project.pocketbank.repository.AgenciaRepository;
import com.github.rhaera.project.pocketbank.service.AgenciaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgenciaServiceImpl implements AgenciaService {

    private final String BRASIL_REGION_AGENCIES = "src/main/resources/static/agencies.txt";
    @Autowired private AgenciaRepository repository;

    @Override
    public Optional<AgenciaDTO> createAgency(AgenciaEntity entity) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(BRASIL_REGION_AGENCIES))) {
            if (br.lines()
                    .reduce("", String::concat)
                    .contains(entity.getNumber().trim()) && getAgencyByNumber(entity.getNumber().trim()).isEmpty()) {
                return Optional.of(repository.save(entity).toDto());
            }
            return getAgencyByNumber(entity.getNumber().trim());
        } catch (IOException e) {
            throw new IOException("Falha na leitura do arquivo chave com as agẽncias existentes.");
        }
    }

    @Override
    public Optional<AgenciaDTO> getAgencyById(long id) {
        return repository.findById(id).isPresent() ? Optional.of(repository.findById(id).get().toDto()) : Optional.empty();
    }

    @Override
    public Optional<AgenciaDTO> getAgencyByNumber(String number) {
        return repository.findByNumber(number);
    }

    @Override
    public List<AgenciaDTO> getAllAgencies() {
        return repository.findAll()
                .stream()
                .map(AgenciaEntity::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgenciaDTO> getAgenciesByAccounts(int accountAmount) {
        return repository.findByAccounts(accountAmount);
    }

    @Override
    public Optional<AgenciaDTO> updateAgency(AgenciaEntity requestEntity) {
        if (repository.findByNumber(requestEntity.getNumber().trim()).isPresent()) {
            AgenciaDTO existingAgency = repository.findByNumber(requestEntity.getNumber().trim()).get();
            existingAgency.setAccounts(requestEntity.getAccounts());
            return Optional.ofNullable(repository.save(existingAgency.toEntity()).toDto());
        }
        return Optional.empty();
    }

    @Override
    public Optional<AgenciaDTO> updateAgencyById(AgenciaEntity requestEntity, long id) {
        if (repository.findById(id).isEmpty()) return Optional.empty();
        AgenciaDTO existingAgency = repository.findById(id).get().toDto();
        existingAgency.setAccounts(requestEntity.getAccounts());
        return Optional.ofNullable(repository.save(existingAgency.toEntity()).toDto());
    }

    @Override
    public Optional<AgenciaDTO> deleteAgencyById(long id) {
        if (repository.findById(id).isEmpty()) return Optional.empty();
        AgenciaDTO deletedAgency = repository.findById(id).get().toDto();
        repository.deleteById(id);
        return Optional.ofNullable(deletedAgency);
    }

    @Override
    public Optional<AgenciaDTO> deleteAgencyByNumber(String number) {
        if (repository.findByNumber(number).isEmpty()) return Optional.empty();
        AgenciaDTO deletedAgency = repository.findByNumber(number).get();
        repository.deleteByNumber(number);
        return Optional.of(deletedAgency);
    }

    @Override
    public List<AgenciaDTO> deleteAgenciesByAccounts(int accountAmount) {
        return repository.deleteByAccounts(accountAmount);
    }
}
