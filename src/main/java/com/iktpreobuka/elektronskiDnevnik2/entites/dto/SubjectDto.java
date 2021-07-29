package com.iktpreobuka.elektronskiDnevnik2.entites.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubjectDto {
	
	
	@JsonProperty("Subject")
	@Size(min=5, max=30, message = "Subject name must be between {min} and {max} characters long.")
	public String subjectName;
	
	
	@JsonProperty("Fund")
	@Min(value = 1, message="Weekly fund must be at least 1")
	@Max(value=5, message="Weekly fund must cant be over 5")
	public Integer weeklyFund;
	
	

	public SubjectDto() {
		super();
		
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Integer getWeeklyFund() {
		return weeklyFund;
	}

	public void setWeeklyFund(Integer weeklyFund) {
		this.weeklyFund = weeklyFund;
	}

}
