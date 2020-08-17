package it.smartcommunitylab.rna.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;
import it.smartcommunitylab.rna.model.VisuraAiuto;
import it.smartcommunitylab.rna.repository.VisuraAiutoRepository;
import it.smartcommunitylab.rna.repository.VisuraDeggendorfRepository;

public class RnaVisureManager extends RnaManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RnaVisureManager.class);
	
	@Autowired
	private VisuraAiutoRepository visuraAiutoRepository;
	@Autowired
	private VisuraDeggendorfRepository visuraDeggendorfRepository;
	
	public VisuraAiuto addVisuraAiuto(String cf) throws Exception {
		VisuraAiuto visuraDb = visuraAiutoRepository.findByCf(cf);
		if(visuraDb == null) {
			VisuraAiuto visura = new VisuraAiuto();
			visura.setCf(cf);
			visuraAiutoRepository.save(visura);
			return visura;
		}
		return visuraDb;
	}
	
	@Scheduled(cron = "0 00 01 * * ?")
	public void inviaRichiesteVisuraAiuti() {
		List<VisuraAiuto> listVisure = visuraAiutoRepository.findByEsitoIsNull();
		try {
			Map<String, Object> contextMap = new HashMap<>(); 
			contextMap.put("visure", listVisure);
			String contentString = velocityParser("templates/richiedi-visura-aiuti.xml", contextMap);
			String risposta = postRequest(contentString, "RichiediVisureMassivo");
			List<EsitoRichiesta> listEsito = getEsitoRichiesta(risposta);
			for(EsitoRichiesta esito : listEsito) {
				if(esito.isSuccess()) {
					VisuraAiuto visuraDb = visuraAiutoRepository.findByCf(esito.getEntityId());
					if(visuraDb!= null) {
						visuraDb.setEsito(esito);
						if(esito.getCode() <= 0) {
							visuraDb.setRichiestaId(esito.getRichiestaId());
						}
						visuraAiutoRepository.save(visuraDb);
					} else {
						logger.warn(String.format("inviaRichiesteVisuraAiuti: cf richiesta non trovato %s", esito.getEntityId()));
					}					
				} else {
					logger.warn(String.format("inviaRichiesteVisuraAiuti: errore esecuzione richiesta  %s - %s", esito.getEntityId(), esito.getMessage()));
				}
			}
		} catch (Exception e) {
			logger.warn(String.format("inviaRichiesteVisuraAiuti: errore   %s", e.getMessage()));
		}
	}
	
}
