package com.tns.empmanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class TransactionRequest {
	@Positive(message = "Amount must be greater than zero")
	@NotNull(message = "Amount is required")
	private Double amount;
}
