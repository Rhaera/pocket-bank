package com.github.rhaera.project.pocketbank.repository;

import com.github.rhaera.project.pocketbank.model.entity.domain.Agencia;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaRepository extends MongoRepository<Agencia, String> {

}
