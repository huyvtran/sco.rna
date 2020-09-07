package it.smartcommunitylab.rna.beans;

import java.util.List;

import it.smartcommunitylab.rna.model.RegistrazioneAiuto;

public class AddRichiestaList {

	private List<RegistrazioneAiuto> richieste;
	private Long codiceBando;
	
	public List<RegistrazioneAiuto> getRichieste() {
		return richieste;
	}
	public void setRichieste(List<RegistrazioneAiuto> richieste) {
		this.richieste = richieste;
	}
	public Long getCodiceBando() {
		return codiceBando;
	}
	public void setCodiceBando(Long codiceBando) {
		this.codiceBando = codiceBando;
	}
}
