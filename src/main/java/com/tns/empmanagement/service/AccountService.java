package com.tns.empmanagement.service;

import org.springframework.stereotype.Service;

import com.tns.empmanagement.entity.Account;
import com.tns.empmanagement.exception.BusinessException;
import com.tns.empmanagement.exception.ResourceNotFoundException;
import com.tns.empmanagement.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	
	public Account deposit (Long id, Double amount) {
		Account account = accountRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
		
		if(amount <= 0) {
			throw new BusinessException("Amount should be greater than 0.");
		}
		account.setBalance(account.getBalance() + amount);
		return accountRepository.save(account);
	}
	
	public Account withdraw(Long id, Double amount) {
		Account account = accountRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
		
		if(amount <= 0) {
			throw new BusinessException("Amount should be greater than 0.");
		}
		if(account.getBalance() < amount) {
			throw new IllegalArgumentException("Insufficient balance.");
		}
		account.setBalance(account.getBalance() - amount);
		
		return accountRepository.save(account);
	}
	
	public Account getAccount(Long id) {
		return accountRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
	}
}
