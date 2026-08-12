package com.tns.empmanagement.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
	LocalDateTime timestamp,
	int status,
	String message,
	List<String> errors,
	String path
) {
}
