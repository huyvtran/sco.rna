package it.smartcommunitylab.rna.beans;

public class EsitoRichiesta {
	private int code;
	private String message;
	private boolean success;
	private Long richiestaId;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Long getRichiestaId() {
		return richiestaId;
	}
	public void setRichiestaId(Long richiestaId) {
		this.richiestaId = richiestaId;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
