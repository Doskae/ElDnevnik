package com.iktpreobuka.elektronskiDnevnik2.entites.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value="Student marks for a subject")
public class StudentMarksForSubjectDto {
	
	@JsonProperty("First name")
	@NotNull(message="First name must be provided")
	@Size(min=2, max=15, message = "First name must be between {min} and {max} characters long.")
	private String firstName;
	
	@JsonProperty("Last name")
	@NotNull(message="Last name must be provided")
	@Size(min=2, max=15, message = "First name must be between {min} and {max} characters long.")
	private String lastName;
	
	@JsonProperty("Subject")
	@NotNull(message="Subject name must be provided")
	@Size(min=2, max=30, message = "First name must be between {min} and {max} characters long.")
	private String subject;
	
	
	@JsonProperty("Mark")
	@Min(value = 1, message="Mark must be at least 1")
	@Max(value=5, message="Mark cant be over 5")
	private Integer subjectMark;

	public Integer getSubjectMark() {
		return subjectMark;
	}

	public void setSubjectMark(Integer subjectMark) {
		this.subjectMark = subjectMark;
	}

	public StudentMarksForSubjectDto() {
		super();
		// TODO Auto-generated constructor stub
	}

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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	

}
