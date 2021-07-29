package com.iktpreobuka.elektronskiDnevnik2.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.text.View;
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
import com.iktpreobuka.elektronskiDnevnik2.entites.ParentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.RoleEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.StudentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.TeacherEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.UserEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.EmailDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.UserRegistrationDTO;
import com.iktpreobuka.elektronskiDnevnik2.repositories.ParentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.StudentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.TeacherRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.UserRepository;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;
import com.iktpreobuka.elektronskiDnevnik2.services.UserDao;
import com.iktpreobuka.elektronskiDnevnik2.util.RESTError;
import com.iktpreobuka.elektronskiDnevnik2.util.UserRoleCustomValidator;

@RestController
@RequestMapping(path = "/api/v1/users/admin")
public class UserController {

	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserRepository userRepository;

	@Autowired
	TeacherRepository teacherRepository;

	@Autowired
	ParentRepository parentRepository;

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	UserRoleCustomValidator userValidator;

	@Autowired
	UserDao userDao;

	/**
	 * prikaz svih korisnika, samo admin može da izlista sve korisnike
	 * 
	 * @return vraća se lista svih korisnika u vidu entiteta, za sada
	 */

	@Secured("ROLE_ADMIN")
	@JsonView(Views.AdminView.class)
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAllUsers() {

		logger.info("Admin listed all users");

		List<UserEntity> list = new ArrayList<UserEntity>();
		list = (List<UserEntity>) userRepository.findAll();
		if (list.isEmpty()) {
			return new ResponseEntity<String>("List of users is empty", HttpStatus.OK);
		}
		return new ResponseEntity<List<UserEntity>>((List<UserEntity>) userRepository.findAll(), HttpStatus.OK);

	}

	/**
	 * dodavanje korisnika, bez uloge, napravio userValidator za role, ali ne radi
	 * kako treba
	 * 
	 * @param user
	 * @param result
	 * @return
	 */

	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addNewUser(@Valid @RequestBody UserRegistrationDTO user, BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}

		UserEntity newUser = new UserEntity();
		newUser.setEmail(user.getEmail());
		newUser.setPassword(user.getPassword());
		newUser.setUsername(user.getUsername());

		userRepository.save(newUser);
		logger.info("Admin added new user");
		return new ResponseEntity<UserRegistrationDTO>(user, HttpStatus.OK);

	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}

	/**
	 * brisanje korisnika po id, admin samo može, vraća response entity za ok i
	 * upisuje logger.info da je obrisan user, ako ne pronadje user vraća
	 * logger.error user not found i custom RESTError
	 * 
	 * @param id
	 * @return
	 */

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteUserById(@PathVariable Integer id) {
		UserEntity retVal = new UserEntity();
		Optional<UserEntity> op = userRepository.findById(id);
		if (op.isPresent()) {
			retVal = userRepository.findById(id).get();
			userRepository.delete(retVal);
			logger.info("Admin deleted user");
			return new ResponseEntity<>("User deleted", HttpStatus.OK);
		} else {
			logger.error("User not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * postavlja korisnika sa ulogom nastavnika
	 * 
	 * @param id
	 * @return response entity da je postavljan kao teacher ili response entity da
	 *         nije pronađen
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/teacher")
	public ResponseEntity<?> setUserAsTeacher(@PathVariable Integer id) {
		UserEntity retVal = new UserEntity();
		RoleEntity teacher = new RoleEntity();
		teacher.setId(2);
		Optional<UserEntity> op = userRepository.findById(id);
		if (op.isPresent()) {
			retVal = userRepository.findById(id).get();
			retVal.setRole(teacher);
			logger.info("Admin - User set as teacher");
			userRepository.save(retVal);
			return new ResponseEntity<>("User set as a teacher", HttpStatus.OK);

		} else {
			logger.error("User not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * postavlja korisnika sa ulogom parent
	 * 
	 * @param id
	 * @return response entity da je postavljan kao parent ili response entity da
	 *         nije pronađen
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/parent")
	public ResponseEntity<?> setUserAsParent(@PathVariable Integer id) {
		UserEntity retVal = new UserEntity();
		RoleEntity parent = new RoleEntity();
		parent.setId(3);
		Optional<UserEntity> op = userRepository.findById(id);
		if (op.isPresent()) {
			retVal = userRepository.findById(id).get();
			retVal.setRole(parent);
			logger.info("Admin - User set as teacher");
			userRepository.save(retVal);
			return new ResponseEntity<>("User set as a parent", HttpStatus.OK);

		} else {
			logger.error("User not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * postavljanje user za student
	 * 
	 * @param id
	 * @return response entity da je postavljan kao parent ili response entity da
	 *         nije pronađen
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/student")
	public ResponseEntity<?> setUserAsStudent(@PathVariable Integer id) {
		UserEntity retVal = new UserEntity();
		RoleEntity student = new RoleEntity();
		student.setId(4);
		Optional<UserEntity> op = userRepository.findById(id);
		if (op.isPresent()) {
			retVal = userRepository.findById(id).get();
			retVal.setRole(student);
			logger.info("Admin - User set as student");
			userRepository.save(retVal);
			return new ResponseEntity<>("User set as a student", HttpStatus.OK);

		} else {
			logger.error("User not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * Postavlja UserEntity parametre za ucenika preko studentId. trazi prvo
	 * studenta preko student id ako nadje onda trazi user preko servisa
	 * userDao.findUserByStudentId ako ne nadje vraća response entity da nije user
	 * pronadje ako pronadje podesava email, postavlja sifru na "pass" postavlja
	 * username kao ime+prezime ucenika
	 * 
	 * @param studentId Tipa Integer za identifikovanje ucenika
	 * @param email     za ubacivanje email, moze i emaildto ali treba menajti
	 *                  getere i setere entity
	 * @return vraca response entites u skladu sa rezultatom,. da je dodat email,
	 *         password i username ako je sve ok ako nije da kroisnik nije pronadjen
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/student/set-user/{studentId}")
	public ResponseEntity<?> setUserParamsOfStudent(@PathVariable Integer studentId, @RequestParam String email) {
		logger.info("Admin set user parameters through student id");
		UserEntity user = new UserEntity();
		StudentEntity student = new StudentEntity();
		Optional<StudentEntity> op = studentRepository.findById(studentId);
		if (op.isPresent()) {
			student = studentRepository.findById(studentId).get();
			user = userDao.findUserByStudentId(studentId);
			if (user == null) {
				logger.error("User not found");
				return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);
			}
			user.setEmail(email);
			user.setPassword("pass");
			user.setUsername(student.getFirstName() + student.getLastName());
			userRepository.save(user);
			logger.info("Admin - Students email, password and username added");
			return new ResponseEntity<>("Students email, password and username added", HttpStatus.OK);
		} else {
			logger.error("User not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);

		}

	}

	/**
	 * Postavlja UserEntity parametre za nastavnika preko teacherId. trazi prvo
	 * nastavnika preko teacherid ako nadje onda trazi user preko servisa
	 * userDao.findUserByTeacherId ako ne nadje vraća response entity da nije user
	 * pronadje ako pronadje podesava email, postavlja sifru na "pass" postavlja
	 * username kao ime+prezime nastavnika
	 * 
	 * @param studentId Tipa Integer za identifikovanje ucenika
	 * @param email     za ubacivanje email, moze i emaildto ali treba menajti
	 *                  getere i setere entity
	 * @return vraca response entites u skladu sa rezultatom,. da je dodat email,
	 *         password i username ako je sve ok ako nije da kroisnik nije pronadjen
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/teacher/set-user/{teacherId}")
	public ResponseEntity<?> setUserParamsOfTeacher(@PathVariable Integer teacherId, @RequestParam String email) {
		logger.info("Admin set user parameters through teacher id");
		UserEntity user = new UserEntity();
		TeacherEntity teacher = new TeacherEntity();
		Optional<TeacherEntity> op = teacherRepository.findById(teacherId);
		if (op.isPresent()) {
			teacher = teacherRepository.findById(teacherId).get();
			user = userDao.findUserByTeacherId(teacherId);
			if (user == null) {
				logger.error("User not found");
				return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);
			}
			user.setEmail(email);
			user.setPassword("pass");
			user.setUsername(teacher.getFirstName() + teacher.getLastName());
			userRepository.save(user);
			logger.info("Admin - Teachers email, password and username added");
			return new ResponseEntity<>("Teachers email, password and username added", HttpStatus.OK);
		} else {
			logger.error("User not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/parent/set-user/{parentId}")
	public ResponseEntity<?> setUserParamsOfParent(@PathVariable Integer parentId, @RequestParam String email) {
		logger.info("Admin set user parameters through parent id");
		UserEntity user = new UserEntity();
		ParentEntity parent = new ParentEntity();
		Optional<ParentEntity> op = parentRepository.findById(parentId);
		if (op.isPresent()) {
			parent = parentRepository.findById(parentId).get();
			user = userDao.findUserByParentId(parentId);
			if (user == null) {
				logger.error("User not found");
				return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);
			}
			user.setEmail(email);
			user.setPassword("pass");
			user.setUsername(parent.getFirstName() + parent.getLastName());
			userRepository.save(user);
			logger.info("Admin - Parent email, password and username added");
			return new ResponseEntity<>("Parent email, password and username added", HttpStatus.OK);
		} else {
			logger.error("User not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);

		}

	}
}
