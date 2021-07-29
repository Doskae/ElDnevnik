package com.iktpreobuka.elektronskiDnevnik2.entites.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.iktpreobuka.elektronskiDnevnik2.entites.MarkEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.SubjectEntity;

@JsonRootName(value="Student marks")
public class StudentMarksDto {
	
	public StudentMarksDto(String firstName, String lastName, String subjectName, String marks) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.subjectName = subjectName;
		this.marks = marks;
	}
	private String firstName;
	private String lastName;
	private String subjectName;
	private String marks;
	public StudentMarksDto() {
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
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getMarks() {
		return marks;
	}
	public void setMarks(String marks) {
		this.marks = marks;
	}
	
	
	

}
