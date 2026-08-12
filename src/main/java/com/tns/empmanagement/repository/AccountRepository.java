package com.tns.empmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tns.empmanagement.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}