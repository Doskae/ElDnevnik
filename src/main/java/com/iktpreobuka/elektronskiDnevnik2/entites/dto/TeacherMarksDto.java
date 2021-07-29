package com.iktpreobuka.elektronskiDnevnik2.entites.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonPropertyOrder({ "First name", "Last name","Subject","Mark" })
@JsonRootName(value="Teacher's marks")
public class TeacherMarksDto {
	
	@JsonProperty("Subject")
	private String subjectName;
	@JsonProperty("Mark")
	private String mark;
	@JsonProperty("First name")
	private String firstName;
	@JsonProperty("Last name")
	private String lastName;
	public TeacherMarksDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TeacherMarksDto(String subjectName, String mark, String firstName, String lastName) {
		super();
		this.subjectName = subjectName;
		this.mark = mark;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
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
	

}
