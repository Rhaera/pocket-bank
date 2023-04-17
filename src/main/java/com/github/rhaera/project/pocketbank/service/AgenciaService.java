package com.github.rhaera.project.pocketbank.service;

import com.github.rhaera.project.pocketbank.model.dto.sql.AgenciaDTO;
import com.github.rhaera.project.pocketbank.model.entity.AgenciaEntity;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AgenciaService {
    @Transactional
    Optional<AgenciaDTO> createAgency(AgenciaEntity entity) throws IOException;
    Optional<AgenciaDTO> getAgencyById(long id);
    Optional<AgenciaDTO> getAgencyByNumber(String number);
    List<AgenciaDTO> getAllAgencies();
    List<AgenciaDTO> getAgenciesByAccounts(int accountAmount);
    Optional<AgenciaDTO> updateAgency(AgenciaEntity entity);
    Optional<AgenciaDTO> updateAgencyById(AgenciaEntity entity, long id);
    Optional<AgenciaDTO> deleteAgencyById(long id);
    Optional<AgenciaDTO> deleteAgencyByNumber(String number);
    List<AgenciaDTO> deleteAgenciesByAccounts(int accountAmount);
}
