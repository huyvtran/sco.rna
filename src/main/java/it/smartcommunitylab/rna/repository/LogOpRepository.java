package it.smartcommunitylab.rna.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.smartcommunitylab.rna.model.LogOp;

public interface LogOpRepository extends MongoRepository<LogOp, String> {

}
