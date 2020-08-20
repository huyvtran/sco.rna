package it.smartcommunitylab.rna.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.rna.model.RegistrazioneAiuto;

@Repository
public interface RegistrazioneAiutoRepository extends MongoRepository<RegistrazioneAiuto, String> {
	RegistrazioneAiuto findByPraticaId(String praticaId);
	RegistrazioneAiuto findByConcessioneGestoreId(String concessioneGestoreId);
	RegistrazioneAiuto findByCor(Integer cor);
	List<RegistrazioneAiuto> findByEsitoRegistrazioneIsNull();
	@Query(value="{praticaId:{$in:?0}}")
	List<RegistrazioneAiuto> findByPraticheIds(List<String> praticheIds);
}
