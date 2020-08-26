package it.smartcommunitylab.rna.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;
import it.smartcommunitylab.rna.exception.ServiceErrorException;
import it.smartcommunitylab.rna.model.Visura;
import it.smartcommunitylab.rna.repository.VisuraRepository;

@Component
public class RnaVisureManager extends RnaManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RnaVisureManager.class);
	
	@Autowired
	private VisuraRepository visuraRepository;
	
	public Visura addRichiestaVisuraAiuto(String cf) throws Exception {
		Visura visuraDb = visuraRepository.findByCf(cf);
		if(visuraDb == null) {
			Visura visura = new Visura();
			visura.setCf(cf);
			return inviaRichiesteVisure(visura);
		}
		return inviaRichiesteVisure(visuraDb);
	}
	
	public Visura inviaRichiesteVisure(Visura visura) throws Exception {
		try {
			Map<String, Object> contextMap = new HashMap<>(); 
			contextMap.put("codiceFiscale", visura.getCf());
			if(visura.getEsitoVisuraAiuti() == null) {
				String contentString = velocityParser("templates/richiedi-visura-aiuti.xml", contextMap);
				String risposta = postRequest(contentString, "RichiediVisuraAiuti");
				EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
				if(esito.isSuccess()) {
					visura.setEsitoVisuraAiuti(esito);
					if(esito.getCode() <= 0) {
						visura.setRichiestaVisuraAiutiId(esito.getRichiestaId());
					}
					visuraRepository.save(visura);
				} else {
					String msg = String.format("errore invio richiesta visura aiuti  %s - %s - %s", 
							visura.getCf(), esito.getCode(), esito.getMessage()); 
					logger.warn("inviaRichiesteVisuraAiuto: " + msg);
					throw new ServiceErrorException(msg);
				}				
			}
			if(visura.getEsitoVisuraDeggendorf() == null) {
				String contentString = velocityParser("templates/richiedi-visura-deggendorf.xml", contextMap);
				String risposta = postRequest(contentString, "RichiediVisuraDeggendorf");
				EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
				if(esito.isSuccess()) {
					visura.setEsitoVisuraDeggendorf(esito);
					if(esito.getCode() <= 0) {
						visura.setRichiestaVisuraDeggendorfId(esito.getRichiestaId());
					}
					visuraRepository.save(visura);
				} else {
					String msg = String.format("errore invio richiesta visura deggendorf %s - %s - %s", 
							visura.getCf(), esito.getCode(), esito.getMessage()); 
					logger.warn("inviaRichiesteVisuraAiuto: " + msg);
					throw new ServiceErrorException(msg);
				}				
			}
		} catch (Exception e) {
			String msg = String.format("errore compilazione richiesta %s - %s", visura.getCf(), e.getMessage());
			logger.warn("inviaRichiesteVisuraAiuto: " + msg);
			throw new ServiceErrorException(msg);
		}
		return visura;
	}
	
	@Scheduled(cron = "0,30 * * * * ?")
	public void downloadVisure() {
		List<Visura> listVisure = visuraRepository.findVisureToDownload();
		for(Visura visura : listVisure) {
			try {
				if(visura.getEsitoDownloadVisuraAiuti() == null) {
					Map<String, Object> contextMap = new HashMap<>(); 
					contextMap.put("idRichiesta", visura.getRichiestaVisuraAiutiId());
					contextMap.put("outputVisura", "XML");
					String contentString = velocityParser("templates/scarica-visura.xml", contextMap);
					String risposta = postRequest(contentString, "ScaricaVisura");
					EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
					if(esito.isSuccess()) {
						visura.setEsitoDownloadVisuraAiuti(esito);
						if(esito.getCode() <= 0) {
							//TODO extrazione xml
							visura.setXmlVisuraAiuti("");
						} else {
							logger.warn(String.format("downloadVisuraAiuto: errore esecuzione scarica visura aiuti  %s - %s - %s", 
									visura.getCf(), esito.getCode(), esito.getMessage()));
						}
						visuraRepository.save(visura);
					} else {
						logger.warn(String.format("downloadVisuraAiuto: errore invio scarica visura aiuti  %s - %s - s%", 
								visura.getCf(), esito.getCode(), esito.getMessage()));
					}					
				}
				if(visura.getEsitoDownloadVisuraDeggendorf() == null) {
					//TODO
				}
			} catch (Exception e) {
				logger.warn(String.format("downloadVisuraAiuto: errore %s - %s", visura.getCf(), e.getMessage()));
			}			
		}
	}
		
}
