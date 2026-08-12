package com.tns.empmanagement.exception;

public class DuplicateResourceException extends RuntimeException {
	public DuplicateResourceException() {
		super("Duplicate resource exception");
	}
	public DuplicateResourceException(String message) {
		super(message);
	}
}