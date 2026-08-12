package com.tns.empmanagement.exception;

public class BusinessException extends RuntimeException {
	public BusinessException() {
		super("Bussiness exception");
	}
	public BusinessException(String message) {
		super(message);
	}
}