package it.smartcommunitylab.rna.model;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class VisuraAiuto {
	
	@Id
	private String id;
	
	private String richiestAiutoId;
	private String praticaId;
	private String richiestaId;
	private boolean visuraDisponibile = false;
	private String mimeType;
	private Binary file;
	 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRichiestAiutoId() {
		return richiestAiutoId;
	}
	public void setRichiestAiutoId(String richiestAiutoId) {
		this.richiestAiutoId = richiestAiutoId;
	}
	public String getPraticaId() {
		return praticaId;
	}
	public void setPraticaId(String praticaId) {
		this.praticaId = praticaId;
	}
	public String getRichiestaId() {
		return richiestaId;
	}
	public void setRichiestaId(String richiestaId) {
		this.richiestaId = richiestaId;
	}
	public boolean isVisuraDisponibile() {
		return visuraDisponibile;
	}
	public void setVisuraDisponibile(boolean visuraDisponibile) {
		this.visuraDisponibile = visuraDisponibile;
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
	
}