package com.assesment.centurylink.dto;

public class ExceptionPayload {

	private String message;

	public ExceptionPayload(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
