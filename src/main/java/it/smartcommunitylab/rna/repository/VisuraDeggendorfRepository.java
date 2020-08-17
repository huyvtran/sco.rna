package it.smartcommunitylab.rna.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.rna.model.VisuraDeggendorf;

@Repository
public interface VisuraDeggendorfRepository extends MongoRepository<VisuraDeggendorf, String> {

}
