package com.iktpreobuka.elektronskiDnevnik2.entites.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iktpreobuka.elektronskiDnevnik2.entites.SubjectEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.UserEntity;

public class TeacherRegistrationDto {
	
	@JsonProperty("First name")
	@NotNull(message="First name must be provided")
	@Size(min=2, max=15, message = "First name must be between {min} and {max} characters long.")
	private String firstName;
	
	@JsonProperty("Last name")
	@NotNull(message="Last name must be provided")
	@Size(min=2, max=15, message = "Last name must be between {min} and {max} characters long.")
	private String lastName;
	
	private UserEntity user;
	
	@JsonProperty("Subjects")
	private List<SubjectEntity> subjects = new ArrayList<>();

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

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public List<SubjectEntity> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<SubjectEntity> subjects) {
		this.subjects = subjects;
	}

	public TeacherRegistrationDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	

}
