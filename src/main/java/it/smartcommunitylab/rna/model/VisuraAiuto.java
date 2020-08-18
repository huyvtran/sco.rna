package it.smartcommunitylab.rna.model;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;

@Document
public class VisuraAiuto {
	
	@Id
	private String id;
	
	private String cf;
	private Long richiestaId;
	private EsitoRichiesta esito;
	private String mimeType;
	private Binary file;
	 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getRichiestaId() {
		return richiestaId;
	}
	public void setRichiestaId(Long richiestaId) {
		this.richiestaId = richiestaId;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public Binary getFile() {
		return file;
	}
	public void setFile(Binary file) {
		this.file = file;
	}
	public String getCf() {
		return cf;
	}
	public void setCf(String cf) {
		this.cf = cf;
	}
	public EsitoRichiesta getEsito() {
		return esito;
	}
	public void setEsito(EsitoRichiesta esito) {
		this.esito = esito;
	}
	
}
