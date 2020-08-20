package it.smartcommunitylab.rna.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.rna.model.LogOp;

@Repository
public interface LogOpRepository extends MongoRepository<LogOp, String> {

}
