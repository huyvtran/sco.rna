package it.smartcommunitylab.rna.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.rna.beans.LocalDateDeserializer;

@Document
public class RegistrazioneAiuto {
	
	public static enum Esito {
		ko, ko_ripetibile, ok
	};

	@Id
	private String id;
	
	private String praticaId;
	private String cf;
	private String richiestaId;
	private Esito esito;
	
	private boolean concessioneConfermata = false;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataConferma;	
	
	private boolean concessioneAnnullata = false;
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
	public Esito getEsito() {
		return esito;
	}
	public void setEsito(Esito esito) {
		this.esito = esito;
	}
	public boolean isConcessioneConfermata() {
		return concessioneConfermata;
	}
	public void setConcessioneConfermata(boolean concessioneConfermata) {
		this.concessioneConfermata = concessioneConfermata;
	}
	public LocalDate getDataConferma() {
		return dataConferma;
	}
	public void setDataConferma(LocalDate dataConferma) {
		this.dataConferma = dataConferma;
	}
	public boolean isConcessioneAnnullata() {
		return concessioneAnnullata;
	}
	public void setConcessioneAnnullata(boolean concessioneAnnullata) {
		this.concessioneAnnullata = concessioneAnnullata;
	}
	public LocalDate getDataAnnullamento() {
		return dataAnnullamento;
	}
	public void setDataAnnullamento(LocalDate dataAnnullamento) {
		this.dataAnnullamento = dataAnnullamento;
	}
	public String getRichiestaId() {
		return richiestaId;
	}
	public void setRichiestaId(String richiestaId) {
		this.richiestaId = richiestaId;
	}
	
}
