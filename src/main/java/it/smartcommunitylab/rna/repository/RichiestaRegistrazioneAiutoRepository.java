package it.smartcommunitylab.rna.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.rna.model.RichiestaRegistrazioneAiuto;

@Repository
public interface RichiestaRegistrazioneAiutoRepository extends MongoRepository<RichiestaRegistrazioneAiuto, String> {
	
	List<RichiestaRegistrazioneAiuto> findByEsitoRispostaIsNull();
	
	@Query(value="{concessioneGestoreIdList:?0, codiceBando: ?1}")
	RichiestaRegistrazioneAiuto findByConcessioneGestoreIdAndCodiceBando(String concessioneGestoreId, Long codiceBando);
	
	RichiestaRegistrazioneAiuto findByRichiestaId(Long richiestaId);
}
