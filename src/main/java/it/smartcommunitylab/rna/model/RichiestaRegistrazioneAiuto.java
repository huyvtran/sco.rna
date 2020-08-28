package it.smartcommunitylab.rna.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;

@Document
public class RichiestaRegistrazioneAiuto {
	
	@Id
	private String id;
	
	private Long codiceBando;
	private Long richiestaId;
	private EsitoRichiesta esitoRegistrazione;
	private EsitoRichiesta esitoRisposta;
	private List<String> concessioneGestoreIdList = new ArrayList<>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public EsitoRichiesta getEsitoRegistrazione() {
		return esitoRegistrazione;
	}
	public void setEsitoRegistrazione(EsitoRichiesta esitoRegistrazione) {
		this.esitoRegistrazione = esitoRegistrazione;
	}
	public Long getCodiceBando() {
		return codiceBando;
	}
	public void setCodiceBando(Long codiceBando) {
		this.codiceBando = codiceBando;
	}
	public EsitoRichiesta getEsitoRisposta() {
		return esitoRisposta;
	}
	public void setEsitoRisposta(EsitoRichiesta esitoRisposta) {
		this.esitoRisposta = esitoRisposta;
	}
	public Long getRichiestaId() {
		return richiestaId;
	}
	public void setRichiestaId(Long richiestaId) {
		this.richiestaId = richiestaId;
	}
	public List<String> getConcessioneGestoreIdList() {
		return concessioneGestoreIdList;
	}
	public void setConcessioneGestoreIdList(List<String> concessioneGestoreIdList) {
		this.concessioneGestoreIdList = concessioneGestoreIdList;
	}
}
