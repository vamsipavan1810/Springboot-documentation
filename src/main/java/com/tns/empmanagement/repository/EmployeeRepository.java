package com.tns.empmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tns.empmanagement.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	Optional<Employee> findByEmail(String email);
	
	boolean existsByEmail(String email);
}
