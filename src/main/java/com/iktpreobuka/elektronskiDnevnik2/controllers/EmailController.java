package com.iktpreobuka.elektronskiDnevnik2.controllers;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.models.EmailObject;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;
import com.iktpreobuka.elektronskiDnevnik2.services.EmailService;
import com.iktpreobuka.elektronskiDnevnik2.util.RESTError;

@RestController
@RequestMapping(path = "/sendEmail")
public class EmailController {

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}

	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	private static String PATH_TO_ATTACHMENT = "F:\\workspace\\elektronskiDnevnik2\\logs\\spring-boot-logging.log";
	@Autowired
	private EmailService emailService;

	/**
	 * Salje obican email
	 * 
	 * @param object tipa Email mora da sadrzi validne podatke email objekta
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/simple")
	public ResponseEntity<?> sendSimpleMail(@Valid @RequestBody EmailObject object, BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, email not valid");
			logger.error(createErrorMessage(result));
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			emailService.sendSimpleMessage(object);
			logger.error("Simple email sent");
			return new ResponseEntity<>("Simple email sent", HttpStatus.OK);
		} catch (Exception e) {
			logger.warn("Email not sent" + e.toString());
			e.printStackTrace();
			return new ResponseEntity<>("Email not sent " + e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/emailLog")
	public ResponseEntity<?> sendLog(@Valid @RequestBody EmailObject object, BindingResult result) throws Exception {
		if (result.hasErrors()) {
			logger.error("Error, email not valid");
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			emailService.sendMessageWithAttachment(object, PATH_TO_ATTACHMENT);
			logger.info("Log sent as email ");
			return new ResponseEntity<>("Email  with log sent", HttpStatus.OK);
		} catch (Exception e) {
			logger.warn("Email not sent" + e.toString());
			e.printStackTrace();
			return new ResponseEntity<>("Email not sent " + e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
}