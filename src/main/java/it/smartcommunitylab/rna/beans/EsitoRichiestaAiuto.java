package it.smartcommunitylab.rna.beans;

import it.smartcommunitylab.rna.model.RegistrazioneAiuto.Stato;

public class EsitoRichiestaAiuto {
	private int code;
	private String message;
	private boolean success;	
	private Long richiestaId;
	private Stato stato;
	
	public Long getRichiestaId() {
		return richiestaId;
	}
	public void setRichiestaId(Long richiestaId) {
		this.richiestaId = richiestaId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Stato getStato() {
		return stato;
	}
	public void setStato(Stato stato) {
		this.stato = stato;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
