package it.smartcommunitylab.rna.manager;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;
import it.smartcommunitylab.rna.exception.ServiceErrorException;
import it.smartcommunitylab.rna.model.Visura;
import it.smartcommunitylab.rna.repository.VisuraRepository;

@Component
public class RnaVisureManager extends RnaManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RnaVisureManager.class);
	
	@Autowired
	private VisuraRepository visuraRepository;
	
	public Visura getVisura(String cf) {
		return visuraRepository.findByCf(cf);
	}
	
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
			if(visura.getEsitoDownloadVisuraAiuti() == null) {
				boolean visuraAiutiReady = false;
				try {
					Map<String, Object> contextMap = new HashMap<>();
					contextMap.put("idRichiesta", visura.getRichiestaVisuraAiutiId());
					String contentString = velocityParser("templates/stato-richiesta.xml", contextMap);
					String risposta = postRequest(contentString, "StatoRichiesta");
					EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
					if(!esito.isSuccess() || (esito.getCode() > 0)) {
						logger.warn(String.format("downloadVisure: errore elaborazione stato richiesta %s - %s - %s", 
								visura.getRichiestaVisuraAiutiId(), esito.getCode(), esito.getMessage()));
						continue;
					}
					if(isRichiestaCompletata(esito)) {
						visuraAiutiReady = true;
					}
				} catch (Exception e) {
					logger.warn(String.format("downloadVisure: errore compilazione stato richiesta %s - %s", 
							visura.getRichiestaVisuraAiutiId(), e.getMessage()));
				}
				if(visuraAiutiReady) {
					try {
						Map<String, Object> contextMap = new HashMap<>(); 
						contextMap.put("idRichiesta", visura.getRichiestaVisuraAiutiId());
						contextMap.put("outputVisura", "XML");
						String contentString = velocityParser("templates/scarica-visura.xml", contextMap);
						String risposta = postRequest(contentString, "ScaricaVisura");
						EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
						if(esito.isSuccess()) {
							visura.setEsitoDownloadVisuraAiuti(esito);
							if(esito.getCode() <= 0) {
								visura.setXmlVisuraAiuti(getVisuraXml(risposta));
							} else {
								logger.warn(String.format("downloadVisura: errore esecuzione scarica visura aiuti  %s - %s - %s", 
										visura.getCf(), esito.getCode(), esito.getMessage()));
							}
							visuraRepository.save(visura);
						} else {
							logger.warn(String.format("downloadVisura: errore invio scarica visura aiuti  %s - %s - s%", 
									visura.getCf(), esito.getCode(), esito.getMessage()));
						}												
					} catch (Exception e) {
						logger.warn(String.format("downloadVisure: errore compilazione scarica visura aiuti %s - %s", 
								visura.getRichiestaVisuraAiutiId(), e.getMessage()));
					}
				}
			}
			if(visura.getEsitoDownloadVisuraDeggendorf() == null) {
				boolean visuraDeggendorfReady = false;
				try {
					Map<String, Object> contextMap = new HashMap<>();
					contextMap.put("idRichiesta", visura.getRichiestaVisuraDeggendorfId());
					String contentString = velocityParser("templates/stato-richiesta.xml", contextMap);
					String risposta = postRequest(contentString, "StatoRichiesta");
					EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
					if(!esito.isSuccess() || (esito.getCode() > 0)) {
						logger.warn(String.format("downloadVisure: errore elaborazione stato richiesta %s - %s - %s", 
								visura.getRichiestaVisuraDeggendorfId(), esito.getCode(), esito.getMessage()));
						continue;
					}
					if(isRichiestaCompletata(esito)) {
						visuraDeggendorfReady = true;
					}
				} catch (Exception e) {
					logger.warn(String.format("downloadVisure: errore compilazione stato richiesta %s - %s", 
							visura.getRichiestaVisuraDeggendorfId(), e.getMessage()));
				}				
				if(visuraDeggendorfReady) {
					try {
						Map<String, Object> contextMap = new HashMap<>(); 
						contextMap.put("idRichiesta", visura.getRichiestaVisuraDeggendorfId());
						contextMap.put("outputVisura", "XML");
						String contentString = velocityParser("templates/scarica-visura.xml", contextMap);
						String risposta = postRequest(contentString, "ScaricaVisura");
						EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
						if(esito.isSuccess()) {
							visura.setEsitoDownloadVisuraDeggendorf(esito);
							if(esito.getCode() <= 0) {
								visura.setXmlVisuraDeggendorf(getVisuraXml(risposta));
							} else {
								logger.warn(String.format("downloadVisura: errore esecuzione scarica visura deggendorf  %s - %s - %s", 
										visura.getCf(), esito.getCode(), esito.getMessage()));
							}
							visuraRepository.save(visura);
						} else {
							logger.warn(String.format("downloadVisuraAiuto: errore invio scarica visura deggendorf  %s - %s - s%", 
									visura.getCf(), esito.getCode(), esito.getMessage()));
						}							
					} catch (Exception e) {
						logger.warn(String.format("downloadVisure: errore compilazione scarica visura deggendorf %s - %s", 
								visura.getRichiestaVisuraDeggendorfId(), e.getMessage()));
					}
				}
			}
		}
	}
	
	private String getVisuraXml(String content) throws Exception {
		Document doc = getDocument(content);
		NodeList nodeList = doc.getElementsByTagNameNS("*", "visura");
		if(nodeList.getLength() > 0) {
			Element visuraElement = (Element) nodeList.item(0);
			String visuraContentEnc = getStringDataFromElement(visuraElement);
			String visuraContent = new String(Base64.getDecoder().decode(visuraContentEnc));
			return visuraContent;
		}
		return null;
	}
		
}
