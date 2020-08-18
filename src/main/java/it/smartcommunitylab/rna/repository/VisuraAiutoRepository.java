package it.smartcommunitylab.rna.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.rna.model.VisuraAiuto;

@Repository
public interface VisuraAiutoRepository extends MongoRepository<VisuraAiuto, String> {
	VisuraAiuto findByCf(String cf);
	List<VisuraAiuto> findByEsitoIsNull();
	List<VisuraAiuto> findByRichiestaIdIsNotNullAndFileIsNull();
}
