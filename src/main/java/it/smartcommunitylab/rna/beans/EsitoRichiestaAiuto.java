package it.smartcommunitylab.rna.beans;

import java.time.LocalDate;

import it.smartcommunitylab.rna.model.RegistrazioneAiuto.Stato;

public class EsitoRichiestaAiuto {
	private Integer cor;
	private String concessioneGestoreId;
	private LocalDate dataEsito;
	private String codiceEsito;
	private String descrizione;
	private Stato stato;
	
	public Integer getCor() {
		return cor;
	}
	public void setCor(Integer cor) {
		this.cor = cor;
	}
	public String getConcessioneGestoreId() {
		return concessioneGestoreId;
	}
	public void setConcessioneGestoreId(String concessioneGestoreId) {
		this.concessioneGestoreId = concessioneGestoreId;
	}
	public LocalDate getDataEsito() {
		return dataEsito;
	}
	public void setDataEsito(LocalDate dataEsito) {
		this.dataEsito = dataEsito;
	}
	public String getCodiceEsito() {
		return codiceEsito;
	}
	public void setCodiceEsito(String codiceEsito) {
		this.codiceEsito = codiceEsito;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public Stato getStato() {
		return stato;
	}
	public void setStato(Stato stato) {
		this.stato = stato;
	}
	
	
}
