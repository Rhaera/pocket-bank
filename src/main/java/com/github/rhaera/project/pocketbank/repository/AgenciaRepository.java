package com.github.rhaera.project.pocketbank.repository;

import com.github.rhaera.project.pocketbank.model.dto.sql.AgenciaDTO;
import com.github.rhaera.project.pocketbank.model.entity.AgenciaEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgenciaRepository extends JpaRepository<AgenciaEntity, Long> {

    @Query(value = "select from agencies A where A.number = :number")
    Optional<AgenciaDTO> findByNumber(@Param(value = "number") String number);

    @Modifying @Query(value = "delete from agencies A where A.number = :number")
    Optional<AgenciaDTO> deleteByNumber(@Param(value = "number") String number);

    @Query(value = "select from agencies A where A.accounts = :accounts")
    List<AgenciaDTO> findByAccounts(@Param(value = "accounts") int accounts);

    @Modifying @Query(value = "delete from agencies A where A.accounts = :accounts")
    List<AgenciaDTO> deleteByAccounts(@Param(value = "accounts") int accounts);
}
