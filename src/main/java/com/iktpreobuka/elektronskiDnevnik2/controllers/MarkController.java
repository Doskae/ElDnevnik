package com.iktpreobuka.elektronskiDnevnik2.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.entites.MarkEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.SubjectEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.MarkRegistrationDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.SubjectDto;
import com.iktpreobuka.elektronskiDnevnik2.repositories.MarkRepository;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;
import com.iktpreobuka.elektronskiDnevnik2.util.RESTError;

@RestController
@RequestMapping(path = "/api/v1/marks")
public class MarkController {

	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}

	@Autowired
	MarkRepository markRepository;

	/**
	 * izlistavanje svih korisnika
	 * 
	 * @return response entity da li su ilzlistani ili je lista prazna
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/admin")
	public ResponseEntity<?> getAllMarks() {
		logger.info("Admin listed all users");
		List<MarkEntity> list = new ArrayList<MarkEntity>();
		list = (List<MarkEntity>) markRepository.findAll();
		if (list.isEmpty()) {
			return new ResponseEntity<String>("List of marks is empty", HttpStatus.OK);
		}
		return new ResponseEntity<List<MarkEntity>>((List<MarkEntity>) markRepository.findAll(), HttpStatus.OK);
	}

	/**
	 * Dodavanje ocene
	 * 
	 * @param newMark MarkRegistrationDto za ubacianje ocene
	 * @param result  tipa BindingResult za validaciju dto body
	 * @return response entity dodata ocena ili greska
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/admin")
	public ResponseEntity<?> addMark(@Valid @RequestBody MarkRegistrationDto newMark, BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		MarkEntity mark = new MarkEntity();
		mark.setValue(newMark.getValue());
		mark.setDescription(newMark.getDescription());
		markRepository.save(mark);
		logger.info("Admin added new mark");
		return new ResponseEntity<MarkRegistrationDto>(newMark, HttpStatus.OK);
	}

	/**
	 * biranje ocene
	 * 
	 * @param id tipa Integer za identifikaciju ocene
	 * @return ResponseEntity da li obrisana ili nije nadjena
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/{id}")
	public ResponseEntity<?> deleteMark(@PathVariable Integer id) {
		MarkEntity mark = new MarkEntity();
		Optional<MarkEntity> op = markRepository.findById(id);
		if (op.isPresent()) {
			mark = markRepository.findById(id).get();
			markRepository.delete(mark);
			logger.info("Admin deleted mark");
			return new ResponseEntity<>("Mark deleted", HttpStatus.OK);
		} else {
			logger.error("Mark not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "Mark not found"), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * 
	 * @param mk     tipa MarkRegistrationDto
	 * @param id     tipa Integer za identifikaciju ocene
	 * @param result tipa BindingResult za validaciju dto
	 * @return response entity da li je MarkEntity izmenjen ili ne
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/admin/{id}")
	public ResponseEntity<?> changeMark(@Valid @RequestBody MarkRegistrationDto mk, @PathVariable Integer id,
			BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		MarkEntity mark = new MarkEntity();
		Optional<MarkEntity> op = markRepository.findById(id);
		if (op.isPresent()) {
			mark = markRepository.findById(id).get();
			if (mk.getDescription() != null) {
				mark.setDescription(mk.getDescription());

				logger.info("Admin changed description of the mark");
			}
			if (mk.getValue() != null) {
				mark.setValue(mk.getValue());
				logger.info("Admin changed value of the mark");
			}
			markRepository.save(mark);
			return new ResponseEntity<>("Mark changed", HttpStatus.OK);
		} else {
			logger.error("Mark not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "Subject not found"), HttpStatus.NOT_FOUND);
		}
	}

}
