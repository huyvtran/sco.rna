package it.smartcommunitylab.rna.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;
import it.smartcommunitylab.rna.beans.LocalDateDeserializer;

@Document
public class RegistrazioneAiuto {
	
	public static enum Stato {
		ko, ko_ripetibile, ok
	};

	@Id
	private String id;
	
	private String praticaId;
	private String cf;
	private Long registrazioneId;
	private EsitoRichiesta esitoRegistrazione;
	private Stato statoRichiesta;
	
	private Long confermaId;
	private EsitoRichiesta esitoConferma;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataConferma;
	
	private Long annullamentoId;
	private EsitoRichiesta esitoAnnullamento;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataAnnullamento;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPraticaId() {
		return praticaId;
	}
	public void setPraticaId(String praticaId) {
		this.praticaId = praticaId;
	}
	public String getCf() {
		return cf;
	}
	public void setCf(String cf) {
		this.cf = cf;
	}
	public LocalDate getDataConferma() {
		return dataConferma;
	}
	public void setDataConferma(LocalDate dataConferma) {
		this.dataConferma = dataConferma;
	}
	public Long getRegistrazioneId() {
		return registrazioneId;
	}
	public void setRegistrazioneId(Long registrazioneId) {
		this.registrazioneId = registrazioneId;
	}
	public EsitoRichiesta getEsitoRegistrazione() {
		return esitoRegistrazione;
	}
	public void setEsitoRegistrazione(EsitoRichiesta esitoRegistrazione) {
		this.esitoRegistrazione = esitoRegistrazione;
	}
	public Stato getStatoRichiesta() {
		return statoRichiesta;
	}
	public void setStatoRichiesta(Stato statoRichiesta) {
		this.statoRichiesta = statoRichiesta;
	}
	public Long getConfermaId() {
		return confermaId;
	}
	public void setConfermaId(Long confermaId) {
		this.confermaId = confermaId;
	}
	public EsitoRichiesta getEsitoConferma() {
		return esitoConferma;
	}
	public void setEsitoConferma(EsitoRichiesta esitoConferma) {
		this.esitoConferma = esitoConferma;
	}
	public Long getAnnullamentoId() {
		return annullamentoId;
	}
	public void setAnnullamentoId(Long annullamentoId) {
		this.annullamentoId = annullamentoId;
	}
	public EsitoRichiesta getEsitoAnnullamento() {
		return esitoAnnullamento;
	}
	public void setEsitoAnnullamento(EsitoRichiesta esitoAnnullamento) {
		this.esitoAnnullamento = esitoAnnullamento;
	}
	public LocalDate getDataAnnullamento() {
		return dataAnnullamento;
	}
	public void setDataAnnullamento(LocalDate dataAnnullamento) {
		this.dataAnnullamento = dataAnnullamento;
	}
	
}
