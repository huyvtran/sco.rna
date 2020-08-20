package it.smartcommunitylab.rna.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;

@Document
public class RichiestaRegistrazioneAiuto {
	
	@Id
	private String id;
	
	private Integer codiceBando;
	private Long richiestaId;
	private EsitoRichiesta esitoRegistrazione;
	private EsitoRichiesta esitoRisposta;
	
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
	public Integer getCodiceBando() {
		return codiceBando;
	}
	public void setCodiceBando(Integer codiceBando) {
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
}
