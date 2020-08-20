package it.smartcommunitylab.rna.beans;

import java.time.LocalDate;

public class ConfermaConcessione {
	private Integer cor;
	private LocalDate dataConcessione;
	private String attoConcessione;
	
	public Integer getCor() {
		return cor;
	}
	public void setCor(Integer cor) {
		this.cor = cor;
	}
	public LocalDate getDataConcessione() {
		return dataConcessione;
	}
	public void setDataConcessione(LocalDate dataConcessione) {
		this.dataConcessione = dataConcessione;
	}
	public String getAttoConcessione() {
		return attoConcessione;
	}
	public void setAttoConcessione(String attoConcessione) {
		this.attoConcessione = attoConcessione;
	}
}
