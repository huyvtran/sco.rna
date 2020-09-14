package it.smartcommunitylab.rna.model;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;

@Document
public class Visura {
	
	@Id
	private String id;
	
	private String cf;
	private Long richiestaVisuraAiutiId;
	private EsitoRichiesta esitoVisuraAiuti;
	private EsitoRichiesta esitoDownloadVisuraAiuti;
	private Long richiestaVisuraDeggendorfId;
	private EsitoRichiesta esitoVisuraDeggendorf;
	private EsitoRichiesta esitoDownloadVisuraDeggendorf;
	private Map<String, Object> visuraAiuti;
	private Map<String, Object> visuraDeggendorf;
	
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
	public Long getRichiestaVisuraAiutiId() {
		return richiestaVisuraAiutiId;
	}
	public void setRichiestaVisuraAiutiId(Long richiestaVisuraAiutiId) {
		this.richiestaVisuraAiutiId = richiestaVisuraAiutiId;
	}
	public EsitoRichiesta getEsitoVisuraAiuti() {
		return esitoVisuraAiuti;
	}
	public void setEsitoVisuraAiuti(EsitoRichiesta esitoVisuraAiuti) {
		this.esitoVisuraAiuti = esitoVisuraAiuti;
	}
	public EsitoRichiesta getEsitoDownloadVisuraAiuti() {
		return esitoDownloadVisuraAiuti;
	}
	public void setEsitoDownloadVisuraAiuti(EsitoRichiesta esitoDownloadVisuraAiuti) {
		this.esitoDownloadVisuraAiuti = esitoDownloadVisuraAiuti;
	}
	public Long getRichiestaVisuraDeggendorfId() {
		return richiestaVisuraDeggendorfId;
	}
	public void setRichiestaVisuraDeggendorfId(Long richiestaVisuraDeggendorfId) {
		this.richiestaVisuraDeggendorfId = richiestaVisuraDeggendorfId;
	}
	public EsitoRichiesta getEsitoVisuraDeggendorf() {
		return esitoVisuraDeggendorf;
	}
	public void setEsitoVisuraDeggendorf(EsitoRichiesta esitoVisuraDeggendorf) {
		this.esitoVisuraDeggendorf = esitoVisuraDeggendorf;
	}
	public EsitoRichiesta getEsitoDownloadVisuraDeggendorf() {
		return esitoDownloadVisuraDeggendorf;
	}
	public void setEsitoDownloadVisuraDeggendorf(EsitoRichiesta esitoDownloadVisuraDeggendorf) {
		this.esitoDownloadVisuraDeggendorf = esitoDownloadVisuraDeggendorf;
	}
	public Map<String, Object> getVisuraAiuti() {
		return visuraAiuti;
	}
	public void setVisuraAiuti(Map<String, Object> visuraAiuti) {
		this.visuraAiuti = visuraAiuti;
	}
	public Map<String, Object> getVisuraDeggendorf() {
		return visuraDeggendorf;
	}
	public void setVisuraDeggendorf(Map<String, Object> visuraDeggendorf) {
		this.visuraDeggendorf = visuraDeggendorf;
	}
}
