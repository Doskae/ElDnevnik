package com.iktpreobuka.elektronskiDnevnik2.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.entites.MarkEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.ParentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.RoleEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.StudentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.SubjectEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.TeacherEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.UserEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.EmailDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.ParentRegistrationDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.StudentMarksDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.StudentMarksForSubjectDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.UserRegistrationDTO;
import com.iktpreobuka.elektronskiDnevnik2.repositories.ParentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.StudentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.UserRepository;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;
import com.iktpreobuka.elektronskiDnevnik2.services.ParentDao;
import com.iktpreobuka.elektronskiDnevnik2.services.StudentDao;
import com.iktpreobuka.elektronskiDnevnik2.util.RESTError;

@RestController
@RequestMapping(path = "/api/v1/parent/")
public class ParentController {

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}

	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	ParentRepository parentRepository;

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ParentDao parentDao;

	@Autowired
	StudentDao studentDao;

	/**
	 * Izlistavanje liste roditelja koristi parent repository
	 * 
	 * @return ako je lista prazna vraća poruku da je lista prazna, ako je lista
	 *         popunjena vraća listu parent
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/admin")
	public ResponseEntity<?> getAllParents() {
		logger.info("Admin listed all parents");
		List<ParentEntity> list = new ArrayList<ParentEntity>();
		list = (List<ParentEntity>) parentRepository.findAll();
		if (list.isEmpty()) {
			return new ResponseEntity<String>("The list is of parents is empty", HttpStatus.OK);
		}
		return new ResponseEntity<List<ParentEntity>>((List<ParentEntity>) parentRepository.findAll(), HttpStatus.OK);

	}

	/**
	 * Ubacuje parent entity u tabelu parent, dodaje user id, u user tabeli ubacuje
	 * novog korisnika sa null vrednostima polja, kao role entity dodeljuje 3. Samo
	 * admin ima pravo
	 * 
	 * @param newParent ParentRegistrationDto tip podatka, koji sadrži First name,
	 *                  Last name, metod ne ubacuje decu
	 * @param result    ukoliko nije validan zahtev izbacuje poruku o gresci
	 * @return vraća response entity sa First name i Last name poljima preko
	 *         ParentRegistrationDto
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/admin")
	public ResponseEntity<?> addNewParent(@Valid @RequestBody ParentRegistrationDto newParent, BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		UserEntity newUser = new UserEntity();
		RoleEntity role = new RoleEntity();
		role.setId(3);
		ParentEntity parent = new ParentEntity();
		parent.setUser(newUser);
		parent.getUser().setRole(role);
		parent.setFirstName(newParent.getFirstName());
		parent.setLastName(newParent.getLastName());
		userRepository.save(newUser);
		parentRepository.save(parent);
		logger.info("Admin added new parent");
		return new ResponseEntity<ParentRegistrationDto>(newParent, HttpStatus.OK);

	}

	/**
	 * brise parent is parent table preko id, ali ostaju povezani user id i role id,
	 * naci kako da se to resi
	 * 
	 * @param id
	 * @return ukoliko je los id vraca da parent nije pronadjen, ukoliko je sve
	 *         uradu vraca poruku da je parent obrisan
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/{id}")
	public ResponseEntity<?> deleteParentById(@PathVariable Integer id) {
		ParentEntity retVal = new ParentEntity();
		Optional<ParentEntity> op = parentRepository.findById(id);
		if (op.isPresent()) {
			retVal = parentRepository.findById(id).get();
			parentRepository.delete(retVal);
			logger.info("Admin deleted a parent");
			return new ResponseEntity<>("Parent deleted", HttpStatus.OK);
		} else {
			logger.error("Parent not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "Parent not found"), HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * menja se ime i prezime roditelja, samo admin može, loguje sve promene na
	 * trazenom entitetu
	 * 
	 * @param par    ulazni podatak tipa parentregistrationdto
	 * @param id     path variable id, za pronalazenje roditelja
	 * @param result kontrola greske ulaznih promenljivih
	 * @return vaca gresku o poruci ako nisu validni ulazni podaci, vraca poruku da
	 *         je promenjen parent, ako nije nadjen vraca poruku da nije nadjen
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/admin/{id}/changename")
	public ResponseEntity<?> changeParent(@Valid @RequestBody ParentRegistrationDto par, @PathVariable Integer id,
			BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		ParentEntity retVal = new ParentEntity();
		Optional<ParentEntity> op = parentRepository.findById(id);
		if (op.isPresent()) {
			retVal = parentRepository.findById(id).get();
			if (par.getFirstName() != null) {
				retVal.setFirstName(par.getFirstName());
				logger.info("Admin changed first name of the parent");
			}
			if (par.getLastName() != null) {
				retVal.setLastName(par.getLastName());
				logger.info("Admin changed last name of the parent");
			}
			parentRepository.save(retVal);
			return new ResponseEntity<>("Parent changed", HttpStatus.OK);
		} else {
			logger.error("Parent not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "Parent not found"), HttpStatus.NOT_FOUND);

		}
	}

	/**
	 * dodaje email roditelju, preko EmailDto zbog validacije
	 * 
	 * @param id     tipa Integer za identifikaciju roditelja
	 * @param email  tipa emaildto koji ima string email za postavljanje email
	 * @param result za ispitivanje greske za email
	 * @return respose entites, da je promenjen roditelj i dodat email ako je sve ok
	 *         sa http ok response entiti da roditelj nije pronadjen ako nije dobar
	 *         id response entiti sa greskom ako nije dobar email
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/admin/{id}/email")
	public ResponseEntity<?> addEmail(@PathVariable Integer id, @Valid @RequestBody EmailDto email,
			BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request. Email not valid" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		ParentEntity retVal = new ParentEntity();
		Optional<ParentEntity> op = parentRepository.findById(id);
		if (op.isPresent()) {
			retVal.setEmail(email.getEmail());
			logger.info("Admin added email of the parent");
			parentRepository.save(retVal);
			return new ResponseEntity<>("Parent changed. Email added", HttpStatus.OK);
		} else {
			logger.error("Parent not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "Parent not found"), HttpStatus.NOT_FOUND);

		}
	}

	/**
	 * Dodavanje ucenika roditelju
	 * 
	 * @param id        tipa Integer za identifikaciju roditelja
	 * @param studentId tipa Integer za identifikaciju ucenika
	 * @return response entity da je obirsan ucneik od roditelj i http status ok
	 *         response entity sa custom greskom da nije pronadjen ucenik ili
	 *         roditelj
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/admin/{id}/student/studentId")
	public ResponseEntity<?> addStudentToParent(@PathVariable Integer id, @RequestParam Integer studentId) {
		logger.info("Admin added student to parent");

		return parentDao.setStudentToParent(id, studentId);
	}

	/**
	 * 
	 * @param id        tipa Integer za identifikaciju roditelja
	 * @param studentId tipa Integer za identifikaciju ucenika
	 * @return repsonse entity u skladu sa rezultatom metode
	 *         parentDao.removeStudentFromParent
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/{id}/student/studentId")
	public ResponseEntity<?> deleteStudentFromParent(@PathVariable Integer id, @RequestParam Integer studentId) {
		logger.info("Admin removed student from the parent");

		return parentDao.removeStudentFromParent(id, studentId);
	}

	/**
	 * 
	 * @param id     tipa Integer id za identifikaciju roditljea
	 * @param marks  tipa StudenMarksForSubjectDto za identifikaciju predmeta
	 * @param result tipa BindingResult za validaciju dto
	 * @return repsonse entity u skladu sa validacijom i izvrsenjem metode
	 *         parentDao.seeMarksForChiled
	 */
	@Secured({ "ROLE_PARENT", "ROLE_ADMIN" })
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/ChildMarksFromSubject")
	public ResponseEntity<?> getMarksFromSubjectForChild(@PathVariable Integer id,
			@Valid @RequestBody StudentMarksForSubjectDto marks, BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request. Subject, child or parent not found" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}

		logger.info("Parent or admin get marks from one subject from a student");
		return parentDao.seeMarksForChiled(id, marks);
	}

	/**
	 * Ipsisuje roditljeu sve ocene deteta
	 * 
	 * @param id        tipa Integer za identifikaciju roditelja
	 * @param studentId tipa Integer za identifikaciju ucenika
	 * @return response entity u skladu sa ima roditelja i rezultatom metode
	 *         studentDao.getMarksForAllSubjects
	 */
	@Secured({ "ROLE_PARENT", "ROLE_ADMIN" })
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/child")
	public ResponseEntity<?> getAllMarksFromSubjectForChild(@PathVariable Integer id, @RequestParam Integer studentId) {
		ParentEntity parent = new ParentEntity();
		Optional<ParentEntity> op = parentRepository.findById(id);
		if (op.isPresent()) {
			logger.info("Parent or admin listed all marks of a student");
			return studentDao.getMarksFoAllSubjects(studentId);
		} else
			logger.error("Parent not found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Parent not found"), HttpStatus.NOT_FOUND);

	}

}