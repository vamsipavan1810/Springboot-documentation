package com.tns.empmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tns.empmanagement.dto.TransactionRequest;
import com.tns.empmanagement.entity.Account;
import com.tns.empmanagement.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;
	
	@PostMapping("/{id}/deposit")
	public ResponseEntity<Account> deposit(@PathVariable Long id,@RequestBody TransactionRequest request) {
		Account account  = accountService.deposit(id, request.getAmount());
		return ResponseEntity.ok(account);
	}
	
	@PostMapping("/{id}/withdraw")
	public ResponseEntity<Account> withdraw(@PathVariable Long id, @RequestBody TransactionRequest request) {
		Account account = accountService.withdraw(id, request.getAmount());
		return ResponseEntity.ok(account);
	}
	
	@GetMapping("/{id}/balance")
	public ResponseEntity<Double> getBalance(@PathVariable Long id) {
		Account account = accountService.getAccount(id);
		return ResponseEntity.ok(account.getBalance());
	}
}
