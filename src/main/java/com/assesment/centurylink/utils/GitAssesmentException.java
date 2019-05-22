package com.assesment.centurylink.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;

@SuppressWarnings("serial")
public class GitAssesmentException extends RuntimeException {

	@SuppressWarnings("rawtypes")
	@Getter
	private final transient ResponseEntity responseEntity;

	public GitAssesmentException(final HttpStatus status, final Object json) {
		this.responseEntity = new ResponseEntity<>(json, status);
	}

	@SuppressWarnings("rawtypes")
	public GitAssesmentException(final HttpStatus status) {
		this.responseEntity = new ResponseEntity(status);
	}
}
