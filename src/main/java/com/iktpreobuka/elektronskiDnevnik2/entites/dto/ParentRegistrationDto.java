package com.iktpreobuka.elektronskiDnevnik2.entites.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iktpreobuka.elektronskiDnevnik2.entites.StudentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.UserEntity;

public class ParentRegistrationDto {
	
	@JsonProperty("First name")
	@NotNull(message="First name must be provided")
	@Size(min=2, max=15, message = "First name must be between {min} and {max} characters long.")
	private String firstName;
	
	@JsonProperty("Last name")
	@NotNull(message="Last name must be provided")
	@Size(min=2, max=25, message = "Last name must be between {min} and {max} characters long.")
	private String lastName;
	
	
	@JsonProperty("Children")
	private List<StudentEntity> students = new ArrayList<StudentEntity>();
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("User ID")
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public ParentRegistrationDto() {
		super();
		// TODO Auto-generated constructor stub
	}

}
