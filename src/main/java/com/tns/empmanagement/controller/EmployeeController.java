package com.tns.empmanagement.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tns.empmanagement.entity.Employee;
import com.tns.empmanagement.exception.DuplicateResourceException;
import com.tns.empmanagement.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;
	
	@GetMapping
	public ResponseEntity<Page<Employee>> getAllEmployees(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Page<Employee> employees = employeeService.getAllEmployees(PageRequest.of(page, size));
		return ResponseEntity.ok(employees);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
		return ResponseEntity.ok(employeeService.getEmployeeById(id));
	}
	
	@PostMapping
	public ResponseEntity<?> saveEmployee(@Valid @RequestBody Employee employee) {
		if(employeeService.existsByEmail(employee.getEmail())) {
			throw new DuplicateResourceException("Email already exists");
		}
		Employee savedEmployee = employeeService.saveEmployee(employee);
		return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employee) {
		Employee updatedEmployee =  employeeService.updateEmployee(id, employee);
		return ResponseEntity.ok(updatedEmployee);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}
}
