package com.tns.empmanagement.service;

import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.tns.empmanagement.entity.Employee;
import com.tns.empmanagement.exception.ResourceNotFoundException;
import com.tns.empmanagement.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

	@Mock
	private EmployeeRepository employeeRepository;
	
	@InjectMocks
	private EmployeeService employeeService;
	
	private Employee employee;
	
	@BeforeEach
	void setUp() {
		employee = new Employee();
		employee.setId(1L);
		employee.setFirstName("Vamsi");
		employee.setLastName("Yalla");
		employee.setEmail("vamsi@gmail.com");
		employee.setSalary(127000.0);
		employee.setDepartment("Backend Development");
	}
	
	@Test
	@DisplayName("Should return all employees")
	void getAllEmployeesShouldReturnEmployees() {
		Pageable pageable = PageRequest.of(0, 5);
		Page<Employee> page = new PageImpl<>(List.of(employee));
		
		Mockito.when(employeeRepository.findAll(pageable)).thenReturn(page);
		
		Page<Employee> result = employeeService.getAllEmployees(pageable);
		
		Assertions.assertEquals(1, result.getTotalElements());
		
		Mockito.verify(employeeRepository).findAll(pageable);
	}
	
	@Test
	@DisplayName("Should return employee by ID")
	void getEmployeeByIdShouldReturnEmployee() {
		Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
		
		Employee result = employeeService.getEmployeeById(1L);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals("Vamsi", result.getFirstName());
		
		Mockito.verify(employeeRepository).findById(1L);
	}
	
	@Test
	@DisplayName("Should throw exception when employee not found")
	void getEmployeeByIdShouldThrowException() {
		
		Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(1L));
		
		Mockito.verify(employeeRepository).findById(1L);
	}
	
	@Test
	@DisplayName("Should save employee successfully")
	void saveEmployeeShouldSaveEmployee() {
		
		Mockito.when(employeeRepository.save(employee)).thenReturn(employee);
		
		Employee result = employeeService.saveEmployee(employee);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(employee.getEmail(), result.getEmail());
		
		Mockito.verify(employeeRepository).save(employee);
	}
	
	@Test
	@DisplayName("should update employee successfully")
	void updateEmployeeShouldUpdateEmployee() {
		Employee updated = new Employee();
		updated.setFirstName("Vamsi");
		updated.setLastName("Yalla");
		updated.setEmail("vamsi@gmail.com");
		updated.setSalary(379000.0);
		updated.setDepartment("System Architect");
		
		Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
		Mockito.when(employeeRepository.save(ArgumentMatchers.any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		Employee result = employeeService.updateEmployee(1L, updated);
		
		Assertions.assertEquals("Vamsi", result.getFirstName());
		Assertions.assertEquals("System Architect", result.getDepartment());
		
		Mockito.verify(employeeRepository).findById(1L);
		Mockito.verify(employeeRepository).save(ArgumentMatchers.any(Employee.class));
	}
	
	@Test
	@DisplayName("Should throw exception while updating non existing employee")
	void updateEmployeeShouldThrowException() {
		
		Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(1L, employee));
		
		Mockito.verify(employeeRepository).findById(1L);
	}
	
	@Test
	@DisplayName("Should delete employee successfully")
	void deleteEmployeeShouldDeleteEmployee() {
		
		Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
		
		boolean result = employeeService.deleteEmployee(1L);
		
		Assertions.assertTrue(result);
		
		Mockito.verify(employeeRepository).delete(employee);
	}
	
	@Test
	@DisplayName("Should throw exception while deleting non existing employee")
	void deleteEmployeeShouldThrowException() {
		
		Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(1L));
		
		Mockito.verify(employeeRepository).findById(1L);
	}
	
	@Test
	@DisplayName("Should return true when email exists")
	void existsByEmailShouldReturnTrue() {
		
		Mockito.when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(true);
		
		boolean result = employeeService.existsByEmail(employee.getEmail());
		
		Assertions.assertTrue(result);
		
		Mockito.verify(employeeRepository).existsByEmail(employee.getEmail());
	}
	
	@Test
	@DisplayName("Should return false when email does not exists")
	void existsByEmailShouldReturnFalse() {
		
		Mockito.when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(false);
		
		boolean result = employeeService.existsByEmail(employee.getEmail());
		
		Assertions.assertFalse(result);
		
		Mockito.verify(employeeRepository).existsByEmail(employee.getEmail());
	}
}
