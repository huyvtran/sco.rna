package it.smartcommunitylab.rna.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;
import it.smartcommunitylab.rna.beans.EsitoRichiestaAiuto;
import it.smartcommunitylab.rna.model.RegistrazioneAiuto;
import it.smartcommunitylab.rna.repository.RegistrazioneAiutoRepository;

public class RnaAiutiManager extends RnaManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RnaAiutiManager.class);
	
	@Autowired
	private RegistrazioneAiutoRepository repository;
	
	public void addRegistrazioneAiuto(List<RegistrazioneAiuto> pratiche, Integer codiceBando) throws Exception {
		List<RegistrazioneAiuto> nuovePratiche = new ArrayList<>();
		for(RegistrazioneAiuto pratica : pratiche) {
			RegistrazioneAiuto praticaDb = repository.findByPraticaId(pratica.getPraticaId());
			if(praticaDb != null) {
				if(praticaDb.getEsitoRegistrazione() != null) {
					logger.warn(String.format("addRegistrazioneAiuto: praticaId già inviata %s", pratica.getPraticaId()));
				} else {
					nuovePratiche.add(praticaDb);
				}
			} else {
				repository.save(pratica);
				nuovePratiche.add(pratica);
			}
		}
		inviaRichiesteAiuto(nuovePratiche, codiceBando);
	}
	
	private void inviaRichiesteAiuto(List<RegistrazioneAiuto> pratiche, Integer codiceBando) {
		for(RegistrazioneAiuto pratica : pratiche) {
			try {
				Map<String, Object> contextMap = new HashMap<>(); 
				contextMap.put("pratica", pratica);
				contextMap.put("codiceBando", codiceBando);
				String contentString = velocityParser("templates/registra-aiuto.xml", contextMap);
				String risposta = postRequest(contentString, "RegistraAiuto");
				EsitoRichiesta esito = getEsitoRichiesta(risposta);
				if(esito.isSuccess()) {
					pratica.setEsitoRegistrazione(esito);
					if(esito.getCode() <= 0) {
						pratica.setRegistrazioneId(esito.getRichiestaId());						
					}
					repository.save(pratica);
				} else {
					logger.warn(String.format("inviaRichiesteAiuto: errore invio richiesta  %s - %s - %s", 
							pratica.getPraticaId(), esito.getCode(), esito.getMessage()));
				}
			} catch (Exception e) {
				logger.warn(String.format("inviaRichiesteAiuto: errore compilazione richiesta %s - %s", pratica.getPraticaId(), e.getMessage()));
			}
		}		
	}	
	public void confermaAiuto(List<String> praticheIds, Integer codiceBando) {
		List<RegistrazioneAiuto> pratiche = repository.findByPraticheIds(praticheIds);
		for(RegistrazioneAiuto pratica : pratiche) {
			try {
				Map<String, Object> contextMap = new HashMap<>(); 
				contextMap.put("pratica", pratica);
				contextMap.put("codiceBando", codiceBando);
				String contentString = velocityParser("templates/conferma-concessione.xml", contextMap);
				String risposta = postRequest(contentString, "ConfermaConcessione");
				EsitoRichiesta esito = getEsitoRichiesta(risposta);
				if(esito.isSuccess()) {
					pratica.setEsitoConferma(esito);
					pratica.setDataConferma(LocalDate.now());
					repository.save(pratica);
				} else {
					logger.warn(String.format("confermaAiuto: errore invio richiesta  %s - %s - %s", 
							pratica.getPraticaId(), esito.getCode(), esito.getMessage()));					
				}
			} catch (Exception e) {
				logger.warn(String.format("confermaAiuto: errore compilazione richiesta %s - %s", pratica.getPraticaId(), e.getMessage()));
			}			
		}
	}
	
	public void annullaAiuto(List<String> praticheIds, Integer codiceBando) {
		List<RegistrazioneAiuto> pratiche = repository.findByPraticheIds(praticheIds);
		for(RegistrazioneAiuto pratica : pratiche) {
			try {
				Map<String, Object> contextMap = new HashMap<>(); 
				contextMap.put("pratica", pratica);
				contextMap.put("Notifica", "NO");
				String contentString = velocityParser("templates/annulla-concessione.xml", contextMap);
				String risposta = postRequest(contentString, "AnnullaConcessione");
				EsitoRichiesta esito = getEsitoRichiesta(risposta);
				if(esito.isSuccess()) {
					pratica.setEsitoAnnullamento(esito);
					pratica.setDataAnnullamento(LocalDate.now());
					repository.save(pratica);
				} else {
					logger.warn(String.format("annullaAiuto: errore invio richiesta  %s - %s - %s", 
							pratica.getPraticaId(), esito.getCode(), esito.getMessage()));					
				}
			} catch (Exception e) {
				logger.warn(String.format("annullaAiuto: errore compilazione richiesta %s - %s", pratica.getPraticaId(), e.getMessage()));
			}			
		}
	}
	
	@Scheduled(cron = "0 00 03 * * ?")
	public void inviaEsitoRichesta() {
		List<RegistrazioneAiuto> pratiche = repository.findByRegistrazioneIdIsNotNullAndEsitoAiutoIsNull();
		for(RegistrazioneAiuto pratica : pratiche) {
			try {
				Map<String, Object> contextMap = new HashMap<>();
				contextMap.put("idRichiesta", pratica.getRegistrazioneId());
				String contentString = velocityParser("templates/scarica-esito-richiesta.xml", contextMap);
				String risposta = postRequest(contentString, "ScaricaEsitoRichiesta");
				EsitoRichiestaAiuto esito = getEsito(risposta);
				if(esito.isSuccess()) {
					pratica.setEsitoAiuto(esito);
				} else {
					logger.warn(String.format("inviaEsitoRichesta: errore invio richiesta  %s - %s - %s", 
							pratica.getPraticaId(), esito.getCode(), esito.getMessage()));										
				}
				repository.save(pratica);
			} catch (Exception e) {
				logger.warn(String.format("inviaEsitoRichesta: errore %s - %s", pratica.getPraticaId(), e.getMessage()));
			}
		}
	}
	
	private EsitoRichiestaAiuto getEsito(String content) {
		//TODO estrazione esito richiesta aiuto
		return null;
	}
}
