package it.smartcommunitylab.rna.manager;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
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

import it.smartcommunitylab.rna.beans.ConfermaConcessione;
import it.smartcommunitylab.rna.beans.EsitoRichiesta;
import it.smartcommunitylab.rna.beans.EsitoRichiestaAiuto;
import it.smartcommunitylab.rna.exception.ServiceErrorException;
import it.smartcommunitylab.rna.model.RegistrazioneAiuto;
import it.smartcommunitylab.rna.model.RichiestaRegistrazioneAiuto;
import it.smartcommunitylab.rna.repository.RegistrazioneAiutoRepository;
import it.smartcommunitylab.rna.repository.RichiestaRegistrazioneAiutoRepository;

@Component
public class RnaAiutiManager extends RnaManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RnaAiutiManager.class);
	
	@Autowired
	private RegistrazioneAiutoRepository repository;
	@Autowired
	private RichiestaRegistrazioneAiutoRepository richiestaRepository;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	
	public List<RegistrazioneAiuto> getRegistrazioneAiuto(List<String> praticheIds) {
		List<RegistrazioneAiuto> pratiche = repository.findByPraticheIds(praticheIds);
		logger.info(String.format("getRegistrazioneAiuto: %s", pratiche.size()));
		return pratiche;
	}
	
	public void addRegistrazioneAiuto(List<RegistrazioneAiuto> pratiche, Long codiceBando) throws Exception {
		List<RegistrazioneAiuto> nuovePratiche = new ArrayList<>();
		for(RegistrazioneAiuto pratica : pratiche) {
			RegistrazioneAiuto praticaDb = repository.findByPraticaId(pratica.getPraticaId());
			if(praticaDb != null) {
				if(praticaDb.getEsitoRegistrazione() != null) {
					logger.warn(String.format("addRegistrazioneAiuto: praticaId già inviata %s", pratica.getPraticaId()));
				} else {
					praticaDb.setCodiceBando(codiceBando);
					repository.save(praticaDb);
					nuovePratiche.add(praticaDb);
				}
			} else {
				pratica.setCodiceBando(codiceBando);
				repository.save(pratica);
				nuovePratiche.add(pratica);
			}
		}
		inviaRichiesteAiuto(nuovePratiche, codiceBando);
	}
	
	private void inviaRichiesteAiuto(List<RegistrazioneAiuto> pratiche, Long codiceBando) throws Exception {
		RichiestaRegistrazioneAiuto richiesta = new RichiestaRegistrazioneAiuto();
		richiesta.setCodiceBando(codiceBando);
		try {
			Map<String, Object> contextMap = new HashMap<>(); 
			contextMap.put("pratiche", pratiche);
			contextMap.put("codiceBando", codiceBando);
			String attachString = velocityParser("templates/registra-aiuto-attach.xml", contextMap);
			contextMap.put("attach", Base64.getEncoder().encodeToString(attachString.getBytes()));
			String contentString = velocityParser("templates/registra-aiuto.xml", contextMap);
			String risposta = postRequest(contentString, "RegistraAiuto");
			EsitoRichiesta esito = getEsitoRichiesta(risposta, "Result");
			if(esito.isSuccess()) {
				richiesta.setEsitoRegistrazione(esito);
				if(esito.getCode() <= 0) {
					richiesta.setRichiestaId(esito.getRichiestaId());						
				}
				richiestaRepository.save(richiesta);
			} else {
				logger.warn(String.format("inviaRichiesteAiuto: errore invio richiesta  %s - %s", 
						esito.getCode(), esito.getMessage()));
				throw new ServiceErrorException(esito.getCode() + " - " + esito.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("inviaRichiesteAiuto: errore compilazione richiesta %s", e.getMessage()));
			throw new ServiceErrorException(e.getMessage());
		}
	}	
	
	public void confermaAiuto(List<ConfermaConcessione> concessioni) {
		for(ConfermaConcessione concessione : concessioni) {
			RegistrazioneAiuto pratica = repository.findByCor(concessione.getCor());
			pratica.setAttoConcessione(concessione.getAttoConcessione());
			pratica.setDataConcessione(concessione.getDataConcessione());
			repository.save(pratica);
			try {
				Map<String, Object> contextMap = new HashMap<>(); 
				contextMap.put("pratica", pratica);
				String contentString = velocityParser("templates/conferma-concessione.xml", contextMap);
				String risposta = postRequest(contentString, "ConfermaConcessione");
				EsitoRichiesta esito = getEsitoRichiesta(risposta, "???");
				if(esito.isSuccess()) {
					pratica.setEsitoConferma(esito);
					pratica.setDataConferma(LocalDate.now());
					repository.save(pratica);
				} else {
					logger.warn(String.format("confermaAiuto: errore invio richiesta  %s - %s - %s", 
							pratica.getPraticaId(), esito.getCode(), esito.getMessage()));					
				}
			} catch (Exception e) {
				logger.error(String.format("confermaAiuto: errore compilazione richiesta %s - %s", pratica.getPraticaId(), e.getMessage()));
			}			
		}
	}
	
	public void annullaAiuto(List<String> praticheIds) {
		List<RegistrazioneAiuto> pratiche = repository.findByPraticheIds(praticheIds);
		for(RegistrazioneAiuto pratica : pratiche) {
			try {
				Map<String, Object> contextMap = new HashMap<>(); 
				contextMap.put("Cor", pratica.getCor());
				contextMap.put("Notifica", "NO");
				String contentString = velocityParser("templates/annulla-concessione.xml", contextMap);
				String risposta = postRequest(contentString, "AnnullaConcessione");
				EsitoRichiesta esito = getEsitoRichiesta(risposta, "???");
				if(esito.isSuccess()) {
					pratica.setEsitoAnnullamento(esito);
					pratica.setDataAnnullamento(LocalDate.now());
					repository.save(pratica);
				} else {
					logger.warn(String.format("annullaAiuto: errore invio richiesta  %s - %s - %s", 
							pratica.getPraticaId(), esito.getCode(), esito.getMessage()));					
				}
			} catch (Exception e) {
				logger.error(String.format("annullaAiuto: errore compilazione richiesta %s - %s", pratica.getPraticaId(), e.getMessage()));
			}			
		}
	}
	
	@Scheduled(cron = "0,30 * * * * ?")
	public void inviaEsitoRichesta() {
		logger.info("inviaEsitoRichesta: init");
		List<RichiestaRegistrazioneAiuto> list = richiestaRepository.findByEsitoRispostaIsNull();
		for(RichiestaRegistrazioneAiuto richiesta : list) {
			try {
				Map<String, Object> contextMap = new HashMap<>();
				contextMap.put("idRichiesta", richiesta.getRichiestaId());
				String contentString = velocityParser("templates/stato-richiesta.xml", contextMap);
				String risposta = postRequest(contentString, "StatoRichiesta");
				EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
				if(!esito.isSuccess() || (esito.getCode() > 0)) {
					logger.warn(String.format("inviaEsitoRichesta: errore elaborazione stato richiesta %s - %s - %s", 
							richiesta.getRichiestaId(), esito.getCode(), esito.getMessage()));
					continue;
				}
				if(!isRichiestaCompletata(esito)) {
					continue;
				}
			} catch (Exception e) {
				logger.warn(String.format("inviaEsitoRichesta: errore invio stato richiesta %s - %s", richiesta.getRichiestaId(), e.getMessage()));
			}
			try {
				Map<String, Object> contextMap = new HashMap<>();
				contextMap.put("idRichiesta", richiesta.getRichiestaId());
				String contentString = velocityParser("templates/scarica-esito-richiesta.xml", contextMap);
				String risposta = postRequest(contentString, "ScaricaEsitoRichiesta");
				EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
				if(esito.isSuccess()) {
					richiesta.setEsitoRisposta(esito);
					richiestaRepository.save(richiesta);
					List<EsitoRichiestaAiuto> esitiAiuto = getEsiti(risposta);
					for(EsitoRichiestaAiuto esitoAiuto : esitiAiuto) {
						RegistrazioneAiuto aiuto = repository.findByConcessioneGestoreId(esitoAiuto.getConcessioneGestoreId());
						if(aiuto != null) {
							aiuto.setEsitoAiuto(esitoAiuto);
							aiuto.setCor(esitoAiuto.getCor());
							repository.save(aiuto);
						} else {
							logger.warn(String.format("inviaEsitoRichesta: aiuto non trovato  %s", esitoAiuto.getConcessioneGestoreId()));										
						}
					}					
				} else {
					logger.warn(String.format("inviaEsitoRichesta: errore invio richiesta  %s - %s - %s", 
							richiesta.getRichiestaId(), esito.getCode(), esito.getMessage()));										
				}
			} catch (Exception e) {
				logger.error(String.format("inviaEsitoRichesta: errore %s - %s", richiesta.getRichiestaId(), e.getMessage()));
			}			
		}
	}
	
	private List<EsitoRichiestaAiuto> getEsiti(String content) throws Exception {
		Document document = getDocument(content);
		NodeList nodeList = document.getElementsByTagNameNS("*", "esito");
		if(nodeList.getLength() > 0) {
			List<EsitoRichiestaAiuto> result = new ArrayList<>();
			Element esitoElement = (Element) nodeList.item(0);
			String esitoContentEnc = getStringDataFromElement(esitoElement);
			String esitoContent = new String(Base64.getDecoder().decode(esitoContentEnc));
			Document docEsito = getDocument(esitoContent);
			NodeList nodeEsitoList = docEsito.getElementsByTagNameNS("*", "ESITO_RICH_CONCESSIONE");
			for(int i=0; i<nodeEsitoList.getLength(); i++) {
				EsitoRichiestaAiuto era = new EsitoRichiestaAiuto();
				Element elementConcessione = (Element) nodeEsitoList.item(i);
				String xml = getStringFromElement(elementConcessione);
				String codiceEsito = getStringDataFromTag(elementConcessione, "CODICE");
				String descrizione = getStringDataFromTag(elementConcessione, "DESCRIZIONE");
				era.setCodiceEsito(codiceEsito);
				era.setDescrizione(descrizione);
				era.setMsgOriginario(xml);
				//TODO get stato esito
				if(codiceEsito.equals("0")) {
					String cor = getStringDataFromTag(elementConcessione, "COR");
					String dataEsito = getStringDataFromTag(elementConcessione, "DATA_ESITO");
					String concessioneGestoreId = getStringDataFromTag(elementConcessione, "ID_CONCESSIONE_GEST");
					era.setCor(Long.valueOf(cor));
					era.setConcessioneGestoreId(concessioneGestoreId);
					era.setDataEsito(sdf.parse(dataEsito));
				}
				result.add(era);
			}
			return result;
		}
		throw new ServiceErrorException("ESITO not found");
	}
	
	private boolean isRichiestaCompletata(EsitoRichiesta esito) {
		return esito.getMessage().equalsIgnoreCase("Completata");
	}
	
}
