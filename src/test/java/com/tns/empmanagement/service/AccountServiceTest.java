package com.tns.empmanagement.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tns.empmanagement.entity.Account;
import com.tns.empmanagement.exception.BusinessException;
import com.tns.empmanagement.exception.ResourceNotFoundException;
import com.tns.empmanagement.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

	@Mock
	private AccountRepository accountRepository;
	
	@InjectMocks
	private AccountService accountService;
	
	private Account account;
	
	@BeforeEach
	void setUp() {
		account = new Account();
		account.setId(1L);
		account.setAccountHolder("Vamsi");
		account.setBalance(2500000.0);
	}
	
	@Test
	@DisplayName("Should deposit amount successfully")
	void depositShouldDepositAmount() {
		
		Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
		Mockito.when(accountRepository.save(ArgumentMatchers.any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		Account result = accountService.deposit(1L, 200000.0);
		
		Assertions.assertEquals(2700000.0, result.getBalance());
		
		Mockito.verify(accountRepository).save(ArgumentMatchers.any(Account.class));
	}
	
	@Test
	@DisplayName("Should throw exception when deposit amount is invalid")
	void depositShouldThrowBusinessException() {
		
		Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
		
		Assertions.assertThrows(BusinessException.class, () -> accountService.deposit(1L, -100000.0));
	}
	
	@Test
	@DisplayName("Should throw exception when account not found during deposit")
	void  depositShouldThrowResourceNotFoundException() {
		
		Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> accountService.deposit(1L, 100000.0));
	}
	
	@Test
	@DisplayName("Should withdraw amount successfully")
	void withdrawShouldWithdrawAmount() {
		
		Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
		Mockito.when(accountRepository.save(ArgumentMatchers.any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		Account result = accountService.withdraw(1L, 100000.0);
		
		Assertions.assertEquals(2400000.0, result.getBalance());
		
		Mockito.verify(accountRepository).save(ArgumentMatchers.any(Account.class));
	}
	
	@Test
	@DisplayName("Should throw exception when withdraw amount is invalid")
	void withdrawShouldThrowBussinessException() {
		
		Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
		
		Assertions.assertThrows(BusinessException.class, () -> accountService.withdraw(1L, -100000.0));
	}
	
	@Test
	@DisplayName("Should throw exception when balance is insufficient")
	void withdrawShouldThrowIllegalArgumentException() {
		Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> accountService.withdraw(1L, 3000000.0));
	}
	
	@Test
	@DisplayName("Should throw exception when account not found during withdrawl")
	void withdrawShouldThrowResourceNotFoundException() {
		
		Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> accountService.withdraw(1L, 100000.0));
	}
	
	@Test
	@DisplayName("Should return account by ID")
	void getAccountShouldReturnAccount() {
		Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
		
		Account result = accountService.getAccount(1L);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals("Vamsi", result.getAccountHolder());
		
		Mockito.verify(accountRepository).findById(1L);
	}
	
	@Test
	@DisplayName("Should throw exception when account not found")
	void getAccountShouldThrowException() {
		Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> accountService.getAccount(1L));
	}
}
