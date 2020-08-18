package it.smartcommunitylab.rna.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;
import it.smartcommunitylab.rna.exception.BadRequestException;
import it.smartcommunitylab.rna.model.RegistrazioneAiuto;
import it.smartcommunitylab.rna.repository.RegistrazioneAiutoRepository;

public class RnaAiutiManager extends RnaManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RnaAiutiManager.class);
	
	@Autowired
	private RegistrazioneAiutoRepository repository;
	
	public void addRegistrazioneAiuto(List<RegistrazioneAiuto> pratiche) throws Exception {
		List<RegistrazioneAiuto> nuovePratiche = new ArrayList<>();
		for(RegistrazioneAiuto pratica : pratiche) {
			RegistrazioneAiuto praticaDb = repository.findByPraticaId(pratica.getPraticaId());
			if(praticaDb != null) {
				logger.warn(String.format("addRegistrazioneAiuto: praticaId esistente %s", pratica.getPraticaId()));
			} else {
				repository.save(pratica);
				nuovePratiche.add(pratica);
			}
		}
		inviaRichiesteAiuto(nuovePratiche);
	}
	
	@Scheduled(cron = "0 00 03 * * ?")
	public void inviaRichiesteAiuto(List<RegistrazioneAiuto> pratiche) {
		if(pratiche == null) {
			pratiche = repository.findByEsitoRegistrazioneIsNull();
		}
		try {
			Map<String, Object> contextMap = new HashMap<>(); 
			contextMap.put("pratiche", pratiche);
			String contentString = velocityParser("templates/registra-aiuto.xml", contextMap);
			String risposta = postRequest(contentString, "RegistraAiuto");
			List<EsitoRichiesta> listEsito = getEsitoRichieste(risposta);
			for(EsitoRichiesta esito : listEsito) {
				if(esito.isSuccess()) {
					RegistrazioneAiuto praticaDb = repository.findByPraticaId(esito.getEntityId());
					if(praticaDb != null) {
						praticaDb.setEsitoRegistrazione(esito);
						if(esito.getCode() <= 0) {
							praticaDb.setRegistrazioneId(esito.getRichiestaId());
						}
						repository.save(praticaDb);
					} else {
						logger.warn(String.format("inviaRichiesteAiuto: praticaId non trovata %s", esito.getEntityId()));
					}
				} else {
					logger.warn(String.format("inviaRichiesteAiuto: errore invio richiesta  %s - %s", esito.getEntityId(), esito.getMessage()));
				}
			}
		} catch (Exception e) {
			logger.warn(String.format("inviaRichiesteAiuto: errore   %s", e.getMessage()));
		}		
	}
}
