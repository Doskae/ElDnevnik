package com.iktpreobuka.elektronskiDnevnik2.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.iktpreobuka.elektronskiDnevnik2.entites.dto.UserRegistrationDTO;

@Component
public class UserRoleCustomValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		
		return UserRegistrationDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserRegistrationDTO user = (UserRegistrationDTO) target;
		
		
	

	}

}
