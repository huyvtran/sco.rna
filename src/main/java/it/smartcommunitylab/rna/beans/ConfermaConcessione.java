package it.smartcommunitylab.rna.beans;

import java.util.Date;

public class ConfermaConcessione {
	private Long cor;
	private Date dataConcessione;
	private String attoConcessione;
	
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
}
