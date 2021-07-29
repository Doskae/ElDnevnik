package com.iktpreobuka.elektronskiDnevnik2.entites.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iktpreobuka.elektronskiDnevnik2.entites.ParentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.UserEntity;

public class StudentRegistrationDto {
	
	@JsonProperty("First name")
	@NotNull(message="First name must be provided")
	@Size(min=2, max=15, message = "First name must be between {min} and {max} characters long.")
	private String firstName;
	
	@JsonProperty("Last name")
	@NotNull(message="Last name must be provided")
	@Size(min=2, max=25, message = "Last name must be between {min} and {max} characters long.")
	private String lastName;
	
	@JsonProperty("Grade")
	@NotNull(message="Grade must be provided")
	@Min(value=1, message="Minimal grade must be 1")
	@Max(value=8, message="Maximal grade must be 8")
	private Integer grade;
	
	@JsonProperty("Parent")
	private ParentEntity parent;
	
	
	private UserEntity user;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public ParentEntity getParent() {
		return parent;
	}

	public void setParent(ParentEntity parent) {
		this.parent = parent;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public StudentRegistrationDto() {
		super();
		// TODO Auto-generated constructor stub
	}

}
