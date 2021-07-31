package com.iktpreobuka.elektronskiDnevnik2.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.entites.SubjectEntity;

import com.iktpreobuka.elektronskiDnevnik2.entites.dto.SubjectDto;

import com.iktpreobuka.elektronskiDnevnik2.repositories.SubjectRepository;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;
import com.iktpreobuka.elektronskiDnevnik2.util.RESTError;

@RestController
@RequestMapping(path = "/api/v1/subjects/admin")

public class SubjectController {
	
	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}
	
	@Autowired
	SubjectRepository subjectRepository;
	
	/**
	 * Izlistava sve predmete
	 * @return resposne entity u skladu da li ima predmeta ili ne
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAllSubjects() {
		logger.info("Admin listed all subjects");
		List<SubjectEntity> list= new ArrayList<SubjectEntity>();
		list= (List<SubjectEntity>) subjectRepository.findAll();
		if(list.isEmpty()) {
			return new ResponseEntity<String>("List of subject is empty", HttpStatus.OK);
		}
		return new ResponseEntity<List<SubjectEntity>>((List<SubjectEntity>) subjectRepository.findAll(), HttpStatus.OK );

	}
	
	
	/**
	 * dodavanje novog predmeta, samo admin može
	 * @param subject
	 * @param result
	 * @return greške ukoliko se ne prodje validacija dto, i vraća se dto sa imenom predmeta i nedeljnim fondom
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addNewSubject(@Valid @RequestBody SubjectDto subject, BindingResult result){
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		} 
		SubjectEntity newSubject= new SubjectEntity();
		newSubject.setSubjectName(subject.getSubjectName());
		newSubject.setWeeklyFund(subject.getWeeklyFund());
		subjectRepository.save(newSubject);
		logger.info("Admin added new subject");
		return new ResponseEntity<SubjectDto>(subject, HttpStatus.OK);
	}
	
	/**
	 * brisanje predmeta po id, samo admin
	 * kad nema predmeta i kad su losi kredencijali nesto izleti gadno, ali radi
	 * @param id
	 * @return ukoliko je sve ok responeentity da je predmet obrisan, ukoliko je loš id resterror da nije pronadjen
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value="/{id}")
	public ResponseEntity<?> deleteSubjectById(@PathVariable Integer id){
		SubjectEntity subject = new SubjectEntity();
		Optional <SubjectEntity> op = subjectRepository.findById(id);
		if(op.isPresent()) {
			subject=subjectRepository.findById(id).get();
			subjectRepository.delete(subject);
			logger.info("Admin deleted a subject");
			return new ResponseEntity<>("Subject deleted",HttpStatus.OK);
		}else {
			logger.error("Subject not found");
			return  new ResponseEntity<RESTError>(new RESTError(1, "Subject not found"), HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * menja fond prema id predmeta
	 * @param newFund zašto neće da radi validaciju????????
	 * @param id
	 * @return ako ne nadje prema id vraća da nije pronadjen i loguje, ako nadje oavestava da je fond promenjen i loguje
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.PUT, value="/{id}/fund")
	public ResponseEntity<?> changeSubjectFund(@RequestParam ("newfund") @Min(value=1) @Max(value=5) int newFund,
			@PathVariable Integer id){
		
		SubjectEntity subject = new SubjectEntity();
		Optional <SubjectEntity> op = subjectRepository.findById(id);
		if(op.isPresent()) {
			subject=subjectRepository.findById(id).get();
			subject.setWeeklyFund(newFund);
			subjectRepository.save(subject);
			logger.info("Admin changed weekly fund");
			return new ResponseEntity<>("Subject fund changed",HttpStatus.OK);
		}else {
			logger.error("Subject not found");
			return  new ResponseEntity<RESTError>(new RESTError(1, "Subject not found"), HttpStatus.NOT_FOUND);
		}
	}
		/**
		 * promena imena predmetea
		 * @param name tipa String ne sme da bude prazan kako bi se promenilo imena
		 * @param id tipa Integer za identifikaciju predmeta
		 * @return response entity da li je promenjeno ime ili predmet nije nadjen
		 */
		@Secured("ROLE_ADMIN")
		@RequestMapping(method=RequestMethod.PUT, value="/{id}/name")
		public ResponseEntity<?> changeSubjectName(@RequestParam ("name") @NotEmpty String name, @PathVariable Integer id){
		
			SubjectEntity subject = new SubjectEntity();
			Optional <SubjectEntity> op = subjectRepository.findById(id);
			if(op.isPresent()) {
				subject=subjectRepository.findById(id).get();
				subject.setSubjectName(name);
				subjectRepository.save(subject);
				logger.info("Admin changed name of the subject");
				return new ResponseEntity<>("Subject name changed",HttpStatus.OK);
			}else {
				logger.error("Subject not found");
				return  new ResponseEntity<RESTError>(new RESTError(1, "Subject not found"), HttpStatus.NOT_FOUND);
			}
	}
		
		/**
		 * menja predmet prema id i trazi subjectdto ne radi ako stvim integer u ime predmeta
		 * 
		 * @param sub SubjectDto 
		 * @param id
		 * @return response entity ako nije nadjen predmet i poruku da je promenjen predmet ako je nadjen
		 */
		@Secured("ROLE_ADMIN")
		@RequestMapping(method=RequestMethod.PUT, value="/{id}")
		public ResponseEntity<?>changeSubject(@Valid @RequestBody SubjectDto sub, @PathVariable Integer id,
				BindingResult result){
			if (result.hasErrors()) {
				logger.error("Error, bad request" + result.toString());
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			SubjectEntity subject = new SubjectEntity();
			Optional <SubjectEntity> op = subjectRepository.findById(id);
			if(op.isPresent()) {
				subject=subjectRepository.findById(id).get();
				if(sub.getSubjectName()!=null) {
					subject.setSubjectName(sub.getSubjectName());
					
					logger.info("Admin changed name of the subject");
				}
				if(sub.getWeeklyFund()!=null) {
					subject.setWeeklyFund(sub.getWeeklyFund());
					logger.info("Admin changed fund of the subject");
				}
				subjectRepository.save(subject);
				return new ResponseEntity<>("Subject changed",HttpStatus.OK);
			}else {
				logger.error("Subject not found");
				return  new ResponseEntity<RESTError>(new RESTError(1, "Subject not found. Check Ids"), HttpStatus.NOT_FOUND);
			}
		}
		
	

}
	

