package com.github.rhaera.project.pocketbank.repository;

import com.github.rhaera.project.pocketbank.model.entity.domain.ClientEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientDetailRepository extends JpaRepository<ClientEntity, Long> {
    @Query(value = "SELECT * FROM bank_clients c WHERE c.cpf = :cpf", nativeQuery = true)
    Optional<ClientEntity> findByCpf(@Param("cpf") String cpf);

    @Query(value = "SELECT * FROM bank_clients c WHERE c.username = :username", nativeQuery = true)
    Optional<ClientEntity> findByUsername(@Param("username") String username);
}
