package com.github.rhaera.project.pocketbank.repository;

import com.github.rhaera.project.pocketbank.model.entity.AgenciaEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgenciaRepository extends JpaRepository<AgenciaEntity, Long> {

    @Query(value = "SELECT * FROM agencies u WHERE u.number = :number", nativeQuery = true)
    Optional<AgenciaEntity> findByNumber(@Param(value = "number") String number);

    @Transactional @Modifying @Query(value = "DELETE FROM agencies u WHERE u.number = :number", nativeQuery=true)
    void deleteByNumber(@Param(value = "number") String number);

    @Query(value = "SELECT * FROM agencies u WHERE u.accounts = :accounts", nativeQuery = true)
    List<AgenciaEntity> findByAccounts(@Param(value = "accounts") int accounts);

    @Transactional @Modifying @Query(value = "DELETE FROM agencies u WHERE u.accounts = :accounts", nativeQuery=true)
    void deleteByAccounts(@Param(value = "accounts") int accounts);
}
