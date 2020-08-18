package it.smartcommunitylab.rna.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;
import it.smartcommunitylab.rna.model.VisuraAiuto;
import it.smartcommunitylab.rna.model.VisuraDeggendorf;
import it.smartcommunitylab.rna.repository.VisuraAiutoRepository;
import it.smartcommunitylab.rna.repository.VisuraDeggendorfRepository;

public class RnaVisureManager extends RnaManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RnaVisureManager.class);
	
	@Autowired
	private VisuraAiutoRepository visuraAiutoRepository;
	@Autowired
	private VisuraDeggendorfRepository visuraDeggendorfRepository;
	
	public VisuraAiuto addRichiestaVisuraAiuto(String cf) throws Exception {
		VisuraAiuto visuraDb = visuraAiutoRepository.findByCf(cf);
		if(visuraDb == null) {
			VisuraAiuto visura = new VisuraAiuto();
			visura.setCf(cf);
			visuraAiutoRepository.save(visura);
			return visura;
		}
		return visuraDb;
	}
	
	public VisuraDeggendorf addRichiestaVisuraDeggendorf(String cf) throws Exception {
		VisuraDeggendorf visuraDb = visuraDeggendorfRepository.findByCf(cf);
		if(visuraDb == null) {
			VisuraDeggendorf visura = new VisuraDeggendorf();
			visura.setCf(cf);
			visuraDeggendorfRepository.save(visura);
			return visura;
		}
		return visuraDb;
	}
	
	@Scheduled(cron = "0 00 01 * * ?")
	public void inviaRichiesteVisuraAiuto() {
		List<VisuraAiuto> listVisure = visuraAiutoRepository.findByEsitoIsNull();
		try {
			Map<String, Object> contextMap = new HashMap<>(); 
			contextMap.put("visure", listVisure);
			String contentString = velocityParser("templates/richiedi-visura-aiuti.xml", contextMap);
			String risposta = postRequest(contentString, "RichiediVisureMassivo");
			List<EsitoRichiesta> listEsito = getEsitoRichieste(risposta);
			for(EsitoRichiesta esito : listEsito) {
				if(esito.isSuccess()) {
					VisuraAiuto visuraDb = visuraAiutoRepository.findByCf(esito.getEntityId());
					if(visuraDb != null) {
						visuraDb.setEsito(esito);
						if(esito.getCode() <= 0) {
							visuraDb.setRichiestaId(esito.getRichiestaId());
						}
						visuraAiutoRepository.save(visuraDb);
					} else {
						logger.warn(String.format("inviaRichiesteVisuraAiuto: cf richiesta non trovato %s", esito.getEntityId()));
					}					
				} else {
					logger.warn(String.format("inviaRichiesteVisuraAiuto: errore invio richiesta  %s - %s", esito.getEntityId(), esito.getMessage()));
				}
			}
		} catch (Exception e) {
			logger.warn(String.format("inviaRichiesteVisuraAiuto: errore   %s", e.getMessage()));
		}
	}
	
	@Scheduled(cron = "0 30 01 * * ?")
	public void inviaRichiesteVisuraDeggendorf() {
		List<VisuraDeggendorf> listVisure = visuraDeggendorfRepository.findByEsitoIsNull();
		try {
			Map<String, Object> contextMap = new HashMap<>(); 
			contextMap.put("visure", listVisure);
			String contentString = velocityParser("templates/richiedi-visura-deggendorf.xml", contextMap);
			String risposta = postRequest(contentString, "RichiediVisureMassivo");
			List<EsitoRichiesta> listEsito = getEsitoRichieste(risposta);
			for(EsitoRichiesta esito : listEsito) {
				if(esito.isSuccess()) {
					VisuraDeggendorf visuraDb = visuraDeggendorfRepository.findByCf(esito.getEntityId());
					if(visuraDb!= null) {
						visuraDb.setEsito(esito);
						if(esito.getCode() <= 0) {
							visuraDb.setRichiestaId(esito.getRichiestaId());
						}
						visuraDeggendorfRepository.save(visuraDb);
					} else {
						logger.warn(String.format("inviaRichiesteVisuraDeggendorf: cf richiesta non trovato %s", esito.getEntityId()));
					}					
				} else {
					logger.warn(String.format("inviaRichiesteVisuraDeggendorf: errore invio richiesta  %s - %s", esito.getEntityId(), esito.getMessage()));
				}
			}
		} catch (Exception e) {
			logger.warn(String.format("inviaRichiesteVisuraDeggendorf: errore   %s", e.getMessage()));
		}
	}
	
	@Scheduled(cron = "0 00 02 * * ?")
	public void downloadVisuraAiuto() {
		List<VisuraAiuto> listVisure = visuraAiutoRepository.findByRichiestaIdIsNotNullAndFileIsNull();
		for(VisuraAiuto visura : listVisure) {
			try {
				Map<String, Object> contextMap = new HashMap<>(); 
				contextMap.put("idRichiesta", visura.getRichiestaId());
				contextMap.put("outputVisura", "PDF");
				String contentString = velocityParser("templates/scarica-visura.xml", contextMap);
				String risposta = postRequest(contentString, "ScaricaVisura");
				EsitoRichiesta esito = getEsitoRichiesta(risposta);
				if(esito.isSuccess()) {
					VisuraAiuto visuraDb = visuraAiutoRepository.findByCf(esito.getEntityId());
					if(visuraDb!= null) {
						if(esito.getCode() <= 0) {
							Binary file = getFile(risposta, "Visura");
							visuraDb.setFile(file);
							visuraDb.setMimeType("application/pdf");
							visuraAiutoRepository.save(visuraDb);
						} else {
							logger.warn(String.format("downloadVisuraAiuto: errore esecuzione richiesta  %s - %s - %s", 
									esito.getEntityId(), esito.getCode(), esito.getMessage()));
						}
					} else {
						logger.warn(String.format("downloadVisuraAiuto: cf richiesta non trovato %s", esito.getEntityId()));
					}					
				} else {
					logger.warn(String.format("downloadVisuraAiuto: errore invio richiesta  %s - %s", esito.getEntityId(), esito.getMessage()));
				}
			} catch (Exception e) {
				logger.warn(String.format("downloadVisuraAiuto: errore %s - %s", visura.getCf(), e.getMessage()));
			}			
		}
	}
	
	@Scheduled(cron = "0 30 02 * * ?")
	public void downloadVisuraDeggendorf() {
		List<VisuraDeggendorf> listVisure = visuraDeggendorfRepository.findByRichiestaIdIsNotNullAndFileIsNull();
		for(VisuraDeggendorf visura : listVisure) {
			try {
				Map<String, Object> contextMap = new HashMap<>(); 
				contextMap.put("idRichiesta", visura.getRichiestaId());
				contextMap.put("outputVisura", "PDF");
				String contentString = velocityParser("templates/scarica-visura.xml", contextMap);
				String risposta = postRequest(contentString, "ScaricaVisura");
				EsitoRichiesta esito = getEsitoRichiesta(risposta);
				if(esito.isSuccess()) {
					VisuraDeggendorf visuraDb = visuraDeggendorfRepository.findByCf(esito.getEntityId());
					if(visuraDb!= null) {
						if(esito.getCode() <= 0) {
							Binary file = getFile(risposta, "Visura");
							visuraDb.setFile(file);
							visuraDb.setMimeType("application/pdf");
							visuraDeggendorfRepository.save(visuraDb);
						} else {
							logger.warn(String.format("downloadVisuraDeggendorf: errore esecuzione richiesta  %s - %s - %s", 
									esito.getEntityId(), esito.getCode(), esito.getMessage()));
						}
					} else {
						logger.warn(String.format("downloadVisuraDeggendorf: cf richiesta non trovato %s", esito.getEntityId()));
					}					
				} else {
					logger.warn(String.format("downloadVisuraDeggendorf: errore invio richiesta  %s - %s", esito.getEntityId(), esito.getMessage()));
				}
			} catch (Exception e) {
				logger.warn(String.format("downloadVisuraDeggendorf: errore %s - %s", visura.getCf(), e.getMessage()));
			}			
		}
	}
	
}
