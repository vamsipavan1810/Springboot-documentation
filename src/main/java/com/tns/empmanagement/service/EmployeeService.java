package com.tns.empmanagement.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tns.empmanagement.entity.Employee;
import com.tns.empmanagement.exception.ResourceNotFoundException;
import com.tns.empmanagement.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
	
	private final EmployeeRepository employeeRepository;
	
	public Page<Employee> getAllEmployees(Pageable pageable) {
		return employeeRepository.findAll(pageable);
	}
	
	@Cacheable(value = "employees", key = "#id")
	public Employee getEmployeeById(Long id) {
		return employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
	}
	
	public Employee saveEmployee(Employee employee) {
		return employeeRepository.save(employee);
	}
	
	@CachePut(value = "employees", key = "#id")
	public Employee updateEmployee(Long id, Employee employee) {
		Employee emp = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
		
		emp.setFirstName(employee.getFirstName());
		emp.setLastName(employee.getLastName());
		emp.setEmail(employee.getEmail());
		emp.setSalary(employee.getSalary());
		emp.setDepartment(employee.getDepartment());
		
		Employee updatedEmployee = employeeRepository.save(emp);
		return updatedEmployee;
	}
	
	@CacheEvict(value = "employees", key = "#id")
	public boolean deleteEmployee(Long id) {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " +  id));
		
		employeeRepository.delete(employee);
		return true;
	}
	
	public boolean existsByEmail(String email) {
		return employeeRepository.existsByEmail(email);
	}
}
