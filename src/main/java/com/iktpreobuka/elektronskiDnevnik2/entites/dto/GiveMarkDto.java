package com.iktpreobuka.elektronskiDnevnik2.entites.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GiveMarkDto {
	
	@JsonProperty("First name")
	@NotNull(message="First name must be provided")
	@Size(min=2, max=15, message = "First name must be between {min} and {max} characters long.")
	private String studentFirstName;
	

	@JsonProperty("Last name")
	@NotNull(message="Last name must be provided")
	@Size(min=2, max=25, message = "Last name must be between {min} and {max} characters long.")
	private String studentLastName;
	

	@JsonProperty("Subject")
	@NotNull(message="Last name must be provided")
	@Size(min=2, max=25, message = "Last name must be between {min} and {max} characters long.")
	private String subjectName;
	
	@JsonProperty("Mark")
	@Min(value = 1, message="Mark must be at least 1")
	@Max(value=5, message="Mark cant be over 5")
	private Integer mark;

	public GiveMarkDto() {
		super();
		
	}

	public String getStudentFirstName() {
		return studentFirstName;
	}

	public void setStudentFirstName(String studentFirstName) {
		this.studentFirstName = studentFirstName;
	}

	public String getStudentLastName() {
		return studentLastName;
	}

	public void setStudentLastName(String studentLastName) {
		this.studentLastName = studentLastName;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}

	@Override
	public String toString() {
		return "GiveMarkDto [studentFirstName=" + studentFirstName + ", studentLastName=" + studentLastName
				+ ", subjectName=" + subjectName + ", mark=" + mark + "]";
	}
	
	
	

}
