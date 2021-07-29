package com.iktpreobuka.elektronskiDnevnik2.entites.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarkRegistrationDto {
	
	@JsonProperty("Mark")
	@Min(value = 1, message="Mark must be at least 1")
	@Max(value=5, message="Mark cant be over 5")
	private Integer value;
	
	@JsonProperty("Description")
	@Size(min=4, max=30, message = "Subject name must be between {min} and {max} characters long.")
	private String description;

	public MarkRegistrationDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
