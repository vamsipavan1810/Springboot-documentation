package com.tns.empmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="Account")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="account_holder")
	private String accountHolder;
	
	private Double balance;
}