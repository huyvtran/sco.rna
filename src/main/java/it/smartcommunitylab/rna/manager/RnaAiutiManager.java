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
import it.smartcommunitylab.rna.common.Utils;
import it.smartcommunitylab.rna.exception.BadRequestException;
import it.smartcommunitylab.rna.exception.ServiceErrorException;
import it.smartcommunitylab.rna.model.RegistrazioneAiuto;
import it.smartcommunitylab.rna.model.RegistrazioneAiuto.Stato;
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
	
	private SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	private SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-ddXXX");
	
	public RegistrazioneAiuto getRegistrazioneAiuto(String concessioneGestoreId, Long codiceBando) {
		return repository.findByConcessioneGestoreIdAndCodiceBando(concessioneGestoreId, codiceBando);
	}
	
	public List<RegistrazioneAiuto> getRegistrazioneAiuto(List<String> concessioneGestoreIds, Long codiceBando) {
		List<RegistrazioneAiuto> pratiche = new ArrayList<>();
		for(String concessioneGestoreId : concessioneGestoreIds) {
			RegistrazioneAiuto pratica = repository.findByConcessioneGestoreIdAndCodiceBando(concessioneGestoreId, codiceBando);
			if(pratica != null) {
				pratiche.add(pratica);
			}
		}
		logger.info(String.format("getRegistrazioneAiuto: %s", pratiche.size()));
		return pratiche;
	}
	
	public void addRegistrazioneAiuto(List<RegistrazioneAiuto> pratiche, Long codiceBando) throws Exception {
		List<RegistrazioneAiuto> nuovePratiche = new ArrayList<>();
		for(RegistrazioneAiuto pratica : pratiche) {
			RegistrazioneAiuto praticaDb = repository.findByConcessioneGestoreIdAndCodiceBando(pratica.getConcessioneGestoreId(), codiceBando);
			if(praticaDb != null) {
				if(praticaDb.getEsitoRegistrazione() != null) {
					logger.warn(String.format("addRegistrazioneAiuto: concessioneGestoreId già inviata %s", pratica.getConcessioneGestoreId()));
				} else {
					praticaDb.setCodiceBando(codiceBando);
					praticaDb.setStato(Stato.in_attesa);
					repository.save(praticaDb);
					nuovePratiche.add(praticaDb);
				}
			} else {
				pratica.setCodiceBando(codiceBando);
				pratica.setStato(Stato.in_attesa);
				repository.save(pratica);
				nuovePratiche.add(pratica);
			}
		}
		inviaRichiesteAiuto(nuovePratiche, codiceBando);
	}
	
	private void inviaRichiesteAiuto(List<RegistrazioneAiuto> pratiche, Long codiceBando) throws Exception {
		RichiestaRegistrazioneAiuto richiesta = new RichiestaRegistrazioneAiuto();
		richiesta.setCodiceBando(codiceBando);
		for(RegistrazioneAiuto pratica : pratiche) {
			richiesta.getConcessioneGestoreIdList().add(pratica.getConcessioneGestoreId());
		}
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
	
	public RegistrazioneAiuto confermaAiuto(ConfermaConcessione concessione) throws Exception {
		RegistrazioneAiuto pratica = repository.findByCor(concessione.getCor());
		if(pratica == null) {
			throw new BadRequestException("concessioneGestoreId non trovato");
		}
		if(pratica.getStato() != Stato.ok) {
			throw new BadRequestException("stato non compatibile");
		}		
		pratica.setAttoConcessione(concessione.getAttoConcessione());
		pratica.setDataConcessione(concessione.getDataConcessione());
		try {
			Map<String, Object> contextMap = new HashMap<>(); 
			contextMap.put("cor", pratica.getCor());
			contextMap.put("attoConcessione", pratica.getAttoConcessione());
			contextMap.put("dataConcessione", sdfDay.format(pratica.getDataConcessione()));
			contextMap.put("notifica", "NO");
			String contentString = velocityParser("templates/conferma-concessione.xml", contextMap);
			String risposta = postRequest(contentString, "ConfermaConcessione");
			EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
			if(esito.isSuccess()) {
				if(esito.getCode() <= 0) {
					pratica.setEsitoConferma(esito);
					pratica.setDataConferma(LocalDate.now());
					pratica.setStato(Stato.confermato);
					repository.save(pratica);
					return pratica;					
				} else {
					String msg = String.format("errore conferma concessione  %s - %s - %s", 
							pratica.getCor(), esito.getCode(), esito.getMessage());
					logger.warn("confermaAiuto: " + msg);
					throw new ServiceErrorException(msg);
				}
			} else {
				String msg = String.format("errore invio richiesta  %s - %s - %s", 
						pratica.getCor(), esito.getCode(), esito.getMessage());
				logger.warn("confermaAiuto: " + msg);
				throw new ServiceErrorException(msg);
			}
		} catch (Exception e) {
			String msg = String.format("errore compilazione richiesta %s - %s", pratica.getCor(), e.getMessage());
			logger.error("confermaAiuto: " + msg);
			throw new ServiceErrorException(msg);
		}			
	}
	
	public RegistrazioneAiuto annullaAiuto(Long cor) throws Exception {
		RegistrazioneAiuto pratica = repository.findByCor(cor);
		if(pratica == null) {
			throw new BadRequestException("COR non trovato");
		}
		if(pratica.getStato() != Stato.ok) {
			throw new BadRequestException("stato non compatibile");
		}
		try {
			Map<String, Object> contextMap = new HashMap<>(); 
			contextMap.put("cor", pratica.getCor());
			contextMap.put("notifica", "NO");
			String contentString = velocityParser("templates/annulla-concessione.xml", contextMap);
			String risposta = postRequest(contentString, "AnnullaConcessione");
			EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
			if(esito.isSuccess()) {
				if(esito.getCode() <= 0) {
					pratica.setEsitoAnnullamento(esito);
					pratica.setDataAnnullamento(LocalDate.now());
					pratica.setStato(Stato.annullato);
					repository.save(pratica);					
				} else {
					String msg = String.format("errore annullamento concessione  %s - %s - %s", 
							pratica.getCor(), esito.getCode(), esito.getMessage());
					logger.warn("annullaAiuto: " + msg);
					throw new ServiceErrorException(msg);
				}
				return pratica;
			} else {
				String msg = String.format("errore invio richiesta  %s - %s - %s", 
						pratica.getCor(), esito.getCode(), esito.getMessage()); 
				logger.warn("annullaAiuto: " + msg);
				throw new ServiceErrorException(msg);
			}
		} catch (Exception e) {
			String msg = String.format("errore compilazione richiesta %s - %s", pratica.getCor(), e.getMessage());
			logger.error("annullaAiuto: " + msg);
			throw new ServiceErrorException(msg);
		}			
	}
	
	public RegistrazioneAiuto reiteraRegistrazioneAiuto(String concessioneGestoreId, Long codiceBando) throws Exception {
		RegistrazioneAiuto pratica = repository.findByConcessioneGestoreIdAndCodiceBando(concessioneGestoreId, codiceBando);
		if(pratica != null) {
			if(Stato.ko_reiterabile.equals(pratica.getStato())) {
				RichiestaRegistrazioneAiuto registrazioneAiuto = richiestaRepository.findByConcessioneGestoreIdAndCodiceBando(concessioneGestoreId, codiceBando);
				if(registrazioneAiuto == null) {
					throw new BadRequestException(String.format("richiesta non trovata %s - %s", concessioneGestoreId, codiceBando));
				}
				try {
					Map<String, Object> contextMap = new HashMap<>();
					contextMap.put("idRichiesta", registrazioneAiuto.getRichiestaId());
					contextMap.put("idConcessioneGest", concessioneGestoreId);
					String contentString = velocityParser("templates/reitera-stato-richiesta.xml", contextMap);
					String risposta = postRequest(contentString, "ReiteraRegistraAiuto");
					EsitoRichiesta esito = getEsitoRichiesta(risposta, "return");
					if(esito.isSuccess()) {
						if(esito.getCode() <= 0) {
							RichiestaRegistrazioneAiuto ra = new RichiestaRegistrazioneAiuto();
							ra.setCodiceBando(registrazioneAiuto.getCodiceBando());
							ra.setRichiestaId(esito.getRichiestaId());
							ra.setEsitoRegistrazione(esito);
							richiestaRepository.save(ra);
							pratica.setStato(Stato.in_attesa);
							repository.save(pratica);
							return pratica;
						} else {
							String msg = String.format("errore reitera richiesta aiuto  %s - %s - %s", 
									concessioneGestoreId, esito.getCode(), esito.getMessage());
							logger.warn("reiteraRegistrazioneAiuto: " + msg);
							throw new ServiceErrorException(msg);							
						}
					} else {
						String msg = String.format("errore invio richiesta  %s - %s - %s", 
								concessioneGestoreId, esito.getCode(), esito.getMessage()); 
						logger.warn("reiteraRegistrazioneAiuto: " + msg);
						throw new ServiceErrorException(msg);
					}
				} catch (Exception e) {
					String msg = String.format("errore compilazione richiesta %s - %s", concessioneGestoreId, e.getMessage());
					logger.warn("reiteraRegistrazioneAiuto: " + msg);
					throw new ServiceErrorException(msg);
				}
			} else {
				String msg = String.format("stato della pratica non corretto %s", concessioneGestoreId);
				logger.warn("reiteraRegistrazioneAiuto: " + msg);
				throw new BadRequestException(msg);
			}
		} else {
			String msg = String.format("pratica non trovata %s", concessioneGestoreId);
			logger.warn("reiteraRegistrazioneAiuto: " + msg);
			throw new BadRequestException(msg);			
		}
	}
	
	@Scheduled(cron = "0,30 * * * * ?")
	public void checkEsitoRichesta() {
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
						RegistrazioneAiuto aiuto = repository.findByConcessioneGestoreIdAndCodiceBando(esitoAiuto.getConcessioneGestoreId(), richiesta.getCodiceBando());
						if(aiuto != null) {
							aiuto.setEsitoAiuto(esitoAiuto);
							aiuto.setStato(getStatoRichiesta(esitoAiuto));
							if((aiuto.getStato() == Stato.ok)) {
								aiuto.setCor(esitoAiuto.getCor());
							}
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
				String cor = getStringDataFromTag(elementConcessione, "COR");
				String dataEsito = getStringDataFromTag(elementConcessione, "DATA_ESITO");
				String concessioneGestoreId = getStringDataFromTag(elementConcessione, "ID_CONCESSIONE_GEST");
				era.setCodiceEsito(codiceEsito);
				era.setDescrizione(descrizione);
				era.setMsgOriginario(xml);
				if(Utils.isNotEmpty(cor)) {
					era.setCor(Long.valueOf(cor));
				}
				if(Utils.isNotEmpty(concessioneGestoreId)) {
					era.setConcessioneGestoreId(concessioneGestoreId);
				}
				if(Utils.isNotEmpty(dataEsito)) {
					era.setDataEsito(sdfTimestamp.parse(dataEsito));
				}
				result.add(era);
			}
			return result;
		}
		throw new ServiceErrorException("ESITO not found");
	}
	
	private Stato getStatoRichiesta(EsitoRichiestaAiuto era) {
		int codice = Integer.valueOf(era.getCodiceEsito());
		if((codice == 0) || (codice == 111) || (codice == 112)) {
			return Stato.ok;
		}
		if((codice == 114) || (codice == 115)) {
			return Stato.ko_reiterabile;
		}
		return Stato.ko;
	}
	
}
