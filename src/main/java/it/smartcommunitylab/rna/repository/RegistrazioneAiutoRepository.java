package it.smartcommunitylab.rna.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.rna.model.RegistrazioneAiuto;

@Repository
public interface RegistrazioneAiutoRepository extends MongoRepository<RegistrazioneAiuto, String> {
	RegistrazioneAiuto findByPraticaId(String praticaId);
	List<RegistrazioneAiuto> findByEsitoRegistrazioneIsNull();
}
