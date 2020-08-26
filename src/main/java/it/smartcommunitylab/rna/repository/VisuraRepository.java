package it.smartcommunitylab.rna.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.rna.model.Visura;

@Repository
public interface VisuraRepository extends MongoRepository<Visura, String> {
	Visura findByCf(String cf);
	@Query(value= "{$or:[{$and:[{richiestaVisuraAiutiId:{$ne : null}}, {esitoDownloadVisuraAiuti:null}]}, "
			+ "{$and:[{richiestaVisuraDeggendorfId:{$ne : null}}, {esitoDownloadVisuraDeggendorf:null}]}]}")
	List<Visura> findVisureToDownload();
}
