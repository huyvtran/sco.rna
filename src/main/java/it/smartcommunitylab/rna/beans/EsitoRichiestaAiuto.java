package it.smartcommunitylab.rna.beans;

import java.util.Date;

import it.smartcommunitylab.rna.model.RegistrazioneAiuto.Stato;

public class EsitoRichiestaAiuto {
	private Long cor;
	private String concessioneGestoreId;
	private Date dataEsito;
	private String codiceEsito;
	private String descrizione;
	private Stato stato;
	private String msgOriginario;
	
	public Long getCor() {
		return cor;
	}
	public void setCor(Long cor) {
		this.cor = cor;
	}
	public String getConcessioneGestoreId() {
		return concessioneGestoreId;
	}
	public void setConcessioneGestoreId(String concessioneGestoreId) {
		this.concessioneGestoreId = concessioneGestoreId;
	}
	public Date getDataEsito() {
		return dataEsito;
	}
	public void setDataEsito(Date dataEsito) {
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
	public String getMsgOriginario() {
		return msgOriginario;
	}
	public void setMsgOriginario(String msgOriginario) {
		this.msgOriginario = msgOriginario;
	}
	
	
}
