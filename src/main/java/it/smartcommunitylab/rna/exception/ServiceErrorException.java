package it.smartcommunitylab.rna.exception;

public class ServiceErrorException extends Exception {

	public ServiceErrorException() {
		super();
	}

	public ServiceErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServiceErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceErrorException(String message) {
		super(message);
	}

	public ServiceErrorException(Throwable cause) {
		super(cause);
	}

}