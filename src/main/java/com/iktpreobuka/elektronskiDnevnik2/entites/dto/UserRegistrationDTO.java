package com.iktpreobuka.elektronskiDnevnik2.entites.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iktpreobuka.elektronskiDnevnik2.entites.RoleEntity;


public class UserRegistrationDTO {
	
	@NotNull(message="Username must be provided")
	@JsonProperty("username")
	@Size(min=6, max=16, message = "Username must be between {min} and {max} characters long.")
	private String username;
	
	@JsonIgnore
	@NotNull(message="Password must be provided")
	@JsonProperty("password")
	@Size(min=4, max=16, message = "Username must be between {min} and {max} characters long.")
	private String password;
	
	@JsonProperty("email")
	@NotNull(message = "Email must be provided.")
	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
	message="Email is not valid.")
	private String email;
	
	
	public UserRegistrationDTO() {
		super();
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
}
