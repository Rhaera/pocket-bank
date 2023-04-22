package com.github.rhaera.project.pocketbank.service.implementation;

import com.github.rhaera.project.pocketbank.model.dto.sql.AgenciaDTO;
import com.github.rhaera.project.pocketbank.model.entity.AgenciaEntity;
import com.github.rhaera.project.pocketbank.repository.AgenciaRepository;
import com.github.rhaera.project.pocketbank.service.AgenciaService;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgenciaServiceImpl implements AgenciaService {
    private static final String BRAZIL_REGION_AGENCIES = "src/main/resources/static/agencies.txt";
    private final AgenciaRepository repository;

    public AgenciaServiceImpl(AgenciaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<AgenciaDTO> createAgency(AgenciaEntity entity) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(BRAZIL_REGION_AGENCIES))) {
            if (br.lines()
                    .map(s -> s.substring(s.indexOf("0")).trim())
                    .collect(Collectors.toList())
                    .contains(entity.getNumber().trim()) && getAgencyByNumber(entity.getNumber().trim()).isEmpty()) {
                return Optional.of(repository.save(entity).toDto());
            }
            return getAgencyByNumber(entity.getNumber().trim()).isPresent() ? getAgencyByNumber(entity.getNumber().trim()) : Optional.empty();
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
        return repository.findByNumber(number).isPresent() ? Optional.of(repository.findByNumber(number).get().toDto()) : Optional.empty();
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
        return repository.findByAccounts(accountAmount)
                        .stream()
                        .map(AgenciaEntity::toDto)
                        .collect(Collectors.toList());
    }

    @Override
    public Optional<AgenciaDTO> updateAgency(AgenciaEntity requestEntity) {
        if (getAgencyByNumber(requestEntity.getNumber().trim()).isPresent()) {
            AgenciaDTO existingAgency = getAgencyByNumber(requestEntity.getNumber()
                                                                            .trim())
                                                                            .get();
            existingAgency.setAccounts(requestEntity.getAccounts());
            return Optional.ofNullable(repository.save(existingAgency.toEntity()).toDto());
        }
        return Optional.empty();
    }

    @Override
    public Optional<AgenciaDTO> updateAgencyById(AgenciaEntity requestEntity, long id) {
        if (getAgencyById(id).isEmpty()) return Optional.empty();
        AgenciaDTO existingAgency = getAgencyById(id).get();
        existingAgency.setAccounts(requestEntity.getAccounts());
        return Optional.ofNullable(repository.save(existingAgency.toEntity()).toDto());
    }

    @Override
    public Optional<AgenciaDTO> deleteAgencyById(long id) {
        if (getAgencyById(id).isEmpty()) return Optional.empty();
        AgenciaDTO deletedAgency = getAgencyById(id).get();
        repository.deleteById(id);
        return Optional.of(deletedAgency);
    }

    @Override
    public Optional<AgenciaDTO> deleteAgencyByNumber(String number) {
        if (getAgencyByNumber(number).isEmpty()) return Optional.empty();
        AgenciaDTO deletedAgency = getAgencyByNumber(number).get();
        repository.deleteByNumber(number.trim());
        return Optional.of(deletedAgency);
    }

    @Override
    public List<AgenciaDTO> deleteAgenciesByAccounts(int accountAmount) {
        List<AgenciaDTO> listaAgenciasDeletadas = getAgenciesByAccounts(accountAmount);
        repository.deleteByAccounts(accountAmount);
        return listaAgenciasDeletadas;
    }
}
