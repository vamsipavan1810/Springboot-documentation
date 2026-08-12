package com.tns.empmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Employees")
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "First name is required")
	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@NotBlank(message = "Last name is required")
	@Column(name = "last_name", nullable = false)
	private String lastName;
	
	@Email(message = "Invalid email")
	@NotBlank(message = "Email is required")
	@Column(nullable = false, unique = true)
	private String email;
	
	@Positive(message = "Salary must be greater than zero")
	@Column(nullable = false)
	private Double salary;
	
	@NotBlank(message = "Department is required")
	@Column(nullable = false)
	private String department;
}
