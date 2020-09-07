package it.smartcommunitylab.rna.model;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;
import it.smartcommunitylab.rna.beans.EsitoRichiestaAiuto;
import it.smartcommunitylab.rna.beans.LocalDateDeserializer;

@Document
public class RegistrazioneAiuto {
	
	public static enum Stato {
		in_attesa, ko, ko_reiterabile, ok, confermato, annullato
	};

	@Id
	private String id;
	
	private String cf;
	private String concessioneGestoreId;
	private Long codiceBando;
	private Long cor;
	private Date dataConcessione;
	private String attoConcessione;
	
	private EsitoRichiesta esitoRegistrazione;
	private EsitoRichiestaAiuto esitoAiuto;
	
	private RequestAttachData attach;
	
	private EsitoRichiesta esitoConferma;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataConferma;
	
	private EsitoRichiesta esitoAnnullamento;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataAnnullamento;
	
	private Stato stato;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public EsitoRichiesta getEsitoRegistrazione() {
		return esitoRegistrazione;
	}
	public void setEsitoRegistrazione(EsitoRichiesta esitoRegistrazione) {
		this.esitoRegistrazione = esitoRegistrazione;
	}
	public EsitoRichiesta getEsitoConferma() {
		return esitoConferma;
	}
	public void setEsitoConferma(EsitoRichiesta esitoConferma) {
		this.esitoConferma = esitoConferma;
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
	public EsitoRichiestaAiuto getEsitoAiuto() {
		return esitoAiuto;
	}
	public void setEsitoAiuto(EsitoRichiestaAiuto esitoAiuto) {
		this.esitoAiuto = esitoAiuto;
	}
	public String getConcessioneGestoreId() {
		return concessioneGestoreId;
	}
	public void setConcessioneGestoreId(String concessioneGestoreId) {
		this.concessioneGestoreId = concessioneGestoreId;
	}
	public Long getCor() {
		return cor;
	}
	public void setCor(Long cor) {
		this.cor = cor;
	}
	public Date getDataConcessione() {
		return dataConcessione;
	}
	public void setDataConcessione(Date dataConcessione) {
		this.dataConcessione = dataConcessione;
	}
	public String getAttoConcessione() {
		return attoConcessione;
	}
	public void setAttoConcessione(String attoConcessione) {
		this.attoConcessione = attoConcessione;
	}
	public Long getCodiceBando() {
		return codiceBando;
	}
	public void setCodiceBando(Long codiceBando) {
		this.codiceBando = codiceBando;
	}
	public Stato getStato() {
		return stato;
	}
	public void setStato(Stato stato) {
		this.stato = stato;
	}
	public RequestAttachData getAttach() {
		return attach;
	}
	public void setAttach(RequestAttachData attach) {
		this.attach = attach;
	}

	
}
