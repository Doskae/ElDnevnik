package com.iktpreobuka.elektronskiDnevnik2.controllers;

import java.util.ArrayList;
import java.util.HashSet;
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
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.ParentRegistrationDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.StudentRegistrationDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.TeacherRegistrationDto;
import com.iktpreobuka.elektronskiDnevnik2.repositories.ParentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.StudentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.SubjectRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.UserRepository;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;
import com.iktpreobuka.elektronskiDnevnik2.services.StudentDao;
import com.iktpreobuka.elektronskiDnevnik2.services.UserDao;
import com.iktpreobuka.elektronskiDnevnik2.util.RESTError;

@RestController
@RequestMapping(path = "/api/v1/student/")
public class StudentController {

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
	SubjectRepository subjectRepository;

	@Autowired
	StudentDao studentDao;

	@Autowired
	UserDao userDao;

	/**
	 * izlistava sve ucenike, ako nema ucenika ispisuje da je lista prazna, loguje
	 * da je admin izlistao sve ucenike
	 * 
	 * @return vraca spisak ucenika sa porukom kao response enitity sa statusom ok
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/admin")
	public ResponseEntity<?> getAllStudents() {
		logger.info("Admin listed all students");
		List<StudentEntity> list = new ArrayList<StudentEntity>();
		list = (List<StudentEntity>) studentRepository.findAll();
		if (list.isEmpty()) {
			return new ResponseEntity<String>("The list is of students is empty", HttpStatus.OK);
		}
		return new ResponseEntity<List<StudentEntity>>((List<StudentEntity>) studentRepository.findAll(),
				HttpStatus.OK);

	}

	/**
	 * Admin dodaje novog ucenika, ako ima greske u unosu ispisuje gresku, na kraju
	 * ispisuje da je dodat ucenik dodeljuje mu se novi user id i dodeljuje mu se
	 * role_student. nema dodavanja predmeta i roditelja
	 * 
	 * @param newStudent ulazni parametar je StudentRegistrationDto za unos imena,
	 *                   prezimena i razreda
	 * @param result     ispitivanje validacije ulaznog tela
	 * @return vraca gresku ukoliko nije validan request body, vraća
	 *         StudentRegistrationDto i status ok, ako je sve ok
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/admin")
	public ResponseEntity<?> addNewParent(@Valid @RequestBody StudentRegistrationDto newStudent, BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		UserEntity newUser = new UserEntity();
		RoleEntity role = new RoleEntity();
		role.setId(4);
		StudentEntity student = new StudentEntity();
		student.setUser(newUser);
		student.getUser().setRole(role);
		student.setFirstName(newStudent.getFirstName());
		student.setLastName(newStudent.getLastName());
		student.setGrade(newStudent.getGrade());
		userRepository.save(newUser);
		studentRepository.save(student);
		logger.info("Admin added new student");
		return new ResponseEntity<StudentRegistrationDto>(newStudent, HttpStatus.OK);

	}

	/**
	 * Brise ucenika po id, ostaje user ne obrisan u user tabeli
	 * 
	 * @param id ulazni parametar je id
	 * @return ukoliko ne nadje ucenika izbacuje gresku da ucenik nije nadjen sa
	 *         http not found, i loguje gresku da nnije nadjen ukoliko je sve ok,
	 *         loguje ingo da je studen obrisan i vraca poruku studen obrisan http
	 *         ok
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/{id}")
	public ResponseEntity<?> deleteStudentById(@PathVariable Integer id) {
		StudentEntity retVal = new StudentEntity();
		Optional<StudentEntity> op = studentRepository.findById(id);
		if (op.isPresent()) {
			retVal = studentRepository.findById(id).get();
			studentRepository.delete(retVal);
			logger.info("Admin deleted a student");
			return new ResponseEntity<>("Student deleted", HttpStatus.OK);
		} else {
			logger.error("Student not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "Student not found"), HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * Promena podataka o uceniku, ide preko studentregistrationdto pa mora da se
	 * ubaci i ime i prezime i razred, evenutalno napraviti novi to
	 * 
	 * @param student tipa StudentRegistrationDto moraju se ubaciti polja ime,
	 *                prezime i razred inace ne prolazi validaciju
	 * @param id      tipa Integer za pronalazenje ucenika u bazi
	 * @param result  tipa BindingREsult za validaciju ulaznih parametara
	 * @return vraca response entity da je ucenik promenje, ili custom RESTError
	 *         student not found
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/admin/{id}/changeStudent")
	public ResponseEntity<?> changeParent(@Valid @RequestBody StudentRegistrationDto student, @PathVariable Integer id,
			BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		StudentEntity retVal = new StudentEntity();
		Optional<StudentEntity> op = studentRepository.findById(id);
		if (op.isPresent()) {
			retVal = studentRepository.findById(id).get();
			if (student.getFirstName() != null) {
				retVal.setFirstName(student.getFirstName());
				logger.info("Admin changed first name of the student");
			}
			if (student.getLastName() != null) {
				retVal.setLastName(student.getLastName());
				logger.info("Admin changed last name of the teacher");
			}
			studentRepository.save(retVal);
			return new ResponseEntity<>("Student changed", HttpStatus.OK);
		} else {
			logger.warn("Student not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "Student not found"), HttpStatus.NOT_FOUND);

		}

	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/admin/{id}/subject/subjectId")
	public ResponseEntity<?> addSubjectToStudent(@PathVariable Integer id, @RequestParam Integer subjectId) {
		logger.info("Admin added subject to student");
		return studentDao.setSubjectToStudent(id, subjectId);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/{id}/subject/subjectId")
	public ResponseEntity<?> deleteSubjectFromStudent(@PathVariable Integer id, @RequestParam Integer subjectId) {
		logger.info("Admin removed subject from student");
		return studentDao.removeSubjecFromStudent(id, subjectId);
	}

	// ne radi nece da brise
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/admin/{id}/parent/parentId")
	public ResponseEntity<?> deleteStudentFromParent(@PathVariable Integer id, @RequestParam Integer parentId) {
		logger.info("Admin removed parent from student");
		return studentDao.removeParentFromStudent(id, parentId);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/admin/find-by-firstname-and-lastname")
	public ResponseEntity<?> findStudentByNameAndLastName(@RequestParam("First name") String firstName,
			@RequestParam("Last name") String lastName) {
		logger.info("Admin searched for student by first and last name");
		StudentEntity student = new StudentEntity();
		student = studentRepository.findByFirstNameAndLastNameIgnoreCase(firstName, lastName);
		if (student == null) {
			logger.warn("Student not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "Student not found"), HttpStatus.NOT_FOUND);
		}
		logger.info("Student found");
		return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);

	}

	/**
	 * PRonalazi korisnika preko id studenta
	 * @param studentId za identifikaciju studenta
	 * @return vraća userentity prema student id
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/admin/find-user/{studentId}")
	public UserEntity findUserByStudentId(@PathVariable Integer studentId) {
		logger.info("Admin searched for the user by student id");
		return userDao.findUserByStudentId(studentId);

	}
	
	/**
	 * Prikazuje listu ocena za trazeni predmet, Mogu da urade ucenik i administrator loguje na pocetku
	 * da je neko pokusao da izlista ocene i loguje da li je uspesno uradjen zahev ili ne
	 * @param id tipa Integer za identifikaciju predmeta 
	 * @param subject tip String za unos imena predmeta za koji se zele ispisati ocene
	 * @return vraca response entitiy iz student.Dao.getMarksForSubject u zavisnosti od toga
	 * da li je zahtev uspesan ili ne
	 */
	@Secured({"ROLE_STUDENT","ROLE_ADMIN"})
	@RequestMapping(method=RequestMethod.GET, value="/{id}/getMarks/subject")
	public ResponseEntity<?> getMarksForSub(@PathVariable Integer id, @RequestParam("subject") String subject ){
		logger.info("Admin or student got marks for subject");
		return studentDao.getMarksForSubject(id, subject);
		
	}

}
