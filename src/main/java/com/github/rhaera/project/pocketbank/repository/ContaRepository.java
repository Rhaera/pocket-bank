package com.github.rhaera.project.pocketbank.repository;

import com.github.rhaera.project.pocketbank.model.dto.mongodb.ContaObject;
import com.github.rhaera.project.pocketbank.model.entity.domain.Agencia;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaRepository extends MongoRepository<Agencia, String> {

    @Query(value = "{_id: ?0}")
    List<ContaObject> findAccountListById(String id);

}
