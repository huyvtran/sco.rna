package it.smartcommunitylab.rna.beans;

import java.util.List;

public class GetRichiestaList {

	private List<String> ids;
	private Long codiceBando;
	
	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	public Long getCodiceBando() {
		return codiceBando;
	}
	public void setCodiceBando(Long codiceBando) {
		this.codiceBando = codiceBando;
	}
}
