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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import com.iktpreobuka.elektronskiDnevnik2.entites.RoleEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.StudentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.SubjectEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.TeacherEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.UserEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.GiveMarkDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.StudentMarksForSubjectDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.TeacherRegistrationDto;
import com.iktpreobuka.elektronskiDnevnik2.models.EmailObject;
import com.iktpreobuka.elektronskiDnevnik2.repositories.StudentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.SubjectRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.TeacherRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.UserRepository;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;
import com.iktpreobuka.elektronskiDnevnik2.services.EmailService;
import com.iktpreobuka.elektronskiDnevnik2.services.TeacherDao;
import com.iktpreobuka.elektronskiDnevnik2.services.UserDao;
import com.iktpreobuka.elektronskiDnevnik2.util.RESTError;

@RestController
@RequestMapping(path = "/api/v1/teacher/")
public class TeacherController {

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}

	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	SubjectRepository subjectRepository;

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	TeacherRepository teacherRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TeacherDao teacherDao;

	@Autowired
	UserDao userDao;

	@Autowired
	private EmailService emailService;

	/**
	 * admin izlistava sve nastavnike
	 * 
	 * @return listu nastavnika ako ih ima sa http status ok, ako je prazna vraća
	 *         response entity da je lista prazama i http status ok
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/admin")
	public ResponseEntity<?> getAllTeachers() {
		logger.info("Admin listed all teachers");
		List<TeacherEntity> list = new ArrayList<TeacherEntity>();
		list = (List<TeacherEntity>) teacherRepository.findAll();
		if (list.isEmpty()) {
			return new ResponseEntity<String>("The list is of parents is empty", HttpStatus.OK);
		}
		return new ResponseEntity<List<TeacherEntity>>((List<TeacherEntity>) teacherRepository.findAll(),
				HttpStatus.OK);

	}

	/**
	 * Admin dodaje novog nastavnika, ubacije se preko TeacherRegistrationDto, ako
	 * ima grešku u validaciji javlja gresku ubacuje se ime i prezime, ima opcija i
	 * za predmete, loguje ukoliko nije validan dto
	 * 
	 * @param newTeach TeacherRegistrationDto
	 * @param result   za proveru validnossti podataka preko json
	 * @return vraca TEacherRegistrationDto i http status ako je sve ok, loguje se
	 *         da je admin dodao natavnika
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/admin")
	public ResponseEntity<?> addNewTeacher(@Valid @RequestBody TeacherRegistrationDto newTeach, BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request " + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		UserEntity newUser = new UserEntity();
		RoleEntity role = new RoleEntity();
		role.setId(2);
		TeacherEntity teacher = new TeacherEntity();
		teacher.setUser(newUser);
		teacher.getUser().setRole(role);
		teacher.setFirstName(newTeach.getFirstName());
		teacher.setLastName(newTeach.getLastName());
		userRepository.save(newUser);
		teacherRepository.save(teacher);
		logger.info("Admin added new teacher");
		return new ResponseEntity<TeacherRegistrationDto>(newTeach, HttpStatus.OK);
	}

	/**
	 * admin brise teacher preko id, ukliko ga ne nadje preko id loguje se graska da
	 * nije nadjen i ispisuke se custom resterror da tacher not found sa http.status
	 * not found, ukolko je nadjen ipsiuje se da je nastavnik obirsan i loguje info
	 * da je admin obrisao.
	 * 
	 * @param id
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/{id}")
	public ResponseEntity<?> deleteTeacherById(@PathVariable Integer id) {
		TeacherEntity retVal = new TeacherEntity();
		Optional<TeacherEntity> op = teacherRepository.findById(id);
		if (op.isPresent()) {
			retVal = teacherRepository.findById(id).get();
			teacherRepository.delete(retVal);
			logger.info("Admin deleted a teacher ");
			return new ResponseEntity<>("Teacher deleted", HttpStatus.OK);
		} else {
			logger.error("Teacher not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "Teacher not found"), HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * Menja se ime i prezime nastavnika preko id, ukoliko ima pri validaciji greska
	 * loguje se bad request i daje se resposne entity sa greskom
	 * 
	 * @param teach  TeacherREgistrationDto
	 * @param id     Integer id nastavnika
	 * @param result za ispisivanje greske prilikom validacije
	 * @return vraca response entity ako je nastvnik promenjen, i loguje sta je
	 *         promenjeno, vraca response entity ukoliko nastavnik nije pronadjen i
	 *         loguje warn da nije pronadjen
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/admin/{id}/changename")
	public ResponseEntity<?> changeTecher(@Valid @RequestBody TeacherRegistrationDto teach, @PathVariable Integer id,
			BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request " + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		TeacherEntity retVal = new TeacherEntity();
		Optional<TeacherEntity> op = teacherRepository.findById(id);
		if (op.isPresent()) {
			retVal = teacherRepository.findById(id).get();
			if (teach.getFirstName() != null) {
				retVal.setFirstName(teach.getFirstName());
				logger.info("Admin changed first name of the teacher");
			}
			if (teach.getLastName() != null) {
				retVal.setLastName(teach.getLastName());
				logger.info("Admin changed last name of the teacher");
			}
			teacherRepository.save(retVal);
			return new ResponseEntity<>("Teacher changed", HttpStatus.OK);
		} else {
			logger.warn("Teacher not found");
			return new ResponseEntity<RESTError>(new RESTError(1, "Parent not found"), HttpStatus.NOT_FOUND);

		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/admin/{teacherId}/user")
	public UserEntity getUserByTeacherId(@PathVariable Integer teacherId) {
		logger.info("Admin found user by teacher id");
		return userDao.findUserByTeacherId(teacherId);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/admin/{id}/subject/subjectId")
	public ResponseEntity<?> addSubjectToTeacher(@PathVariable Integer id, @RequestParam Integer subjectId) {
		logger.info("Amin added subject to a teacher");
		return teacherDao.setSubjectToTeacher(id, subjectId);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/{id}/subject/subjectId")
	public ResponseEntity<?> deleteSubjectFromTeacher(@PathVariable Integer id, @RequestParam Integer subjectId) {

		logger.info("Admin removed subject from a teacher");
		return teacherDao.removeSubjectFromTeacher(id, subjectId);
	}

	/**
	 * daje ocennu uceniku i nakon uspesnog ocenjivanja salje email sa podacima o
	 * uceniku, oceni i nastavniku na mail roditelja
	 * 
	 * @param teacherId tipa Integer za identifikaciju nastavnika
	 * @param mark      tipa GiveMarkDto ukoliko nije validan zahtev ispisuje gresku
	 * @param result    u slucaju greske validacije trazi gresku
	 * @return vraca response entity u zavisanosti od ishoda zahteva
	 */
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.PUT, value = "/{teacherId}/giveMark")
	public ResponseEntity<?> giveMark(@PathVariable Integer teacherId, @Valid @RequestBody GiveMarkDto mark,
			BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}

		logger.info("Teacher gave a mark to a student");
		return teacherDao.giveMarkToStudent(teacherId, mark);

	}

	/**
	 * Izlistava spisak svih ocena ucenika iz jednog predmeta koji mu predaje jedan
	 * nastavnik
	 * 
	 * @param teacherId tipa Integer za identifiakciju nastavnika
	 * @param marks     tipa StudentMarksForSubjectDto koji ima polja ime, prezime,
	 *                  predmet koji su obavezni i polje ocena koje nije obavezno
	 * @param result    za validaciju dto
	 * @return vraca response entity ako je sve ok sa statusom da su ocene
	 *         izlistane, ako ne onda da nije pronadjen nastavnik, predemt ili
	 *         ucenik
	 */
	@Secured({ "ROLE_TEACHER", "ROLE_ADMIN" })
	@RequestMapping(method = RequestMethod.GET, value = "/{teacherId}/subject/student/marks")
	public ResponseEntity<?> getStudentsMarksForSubject(@PathVariable Integer teacherId,
			@Valid @RequestBody StudentMarksForSubjectDto marks, BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		logger.info("Teacher listed  marks of subject of the student");
		return teacherDao.seeMarksOfStudent(teacherId, marks);
	}

	/**
	 * brise ocenu uceniku iz predmeta
	 * @param teacherId za identifikaciju nastavnika
	 * @param marks tipa studentmarksforsubjectdto za identifikaciju predmeta i ocena
	 * @param result tipa bindingresult za validaciju dto
	 * @return response ebntity u skladu sa odgovarajuceim rezultatom
	 */
	@Secured({ "ROLE_TEACHER", "ROLE_ADMIN" })
	@RequestMapping(method = RequestMethod.DELETE, value = "/{teacherId}/subject/student/removeMark")
	public ResponseEntity<?> deleteStudentMarkForSubject(@PathVariable Integer teacherId,
			@Valid @RequestBody StudentMarksForSubjectDto marks, BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		logger.info("Teacher deleted  mark of subject of the student");
		return teacherDao.deleteMarkOfStudent(teacherId, marks);
	}

	/**
	 * menja ocenu uceniku iz predmeta
	 * @param teacherId tipa Integer za identifikaciju nastavnika
	 * @param marks tipa StudentMarksForSubjectDto za upisivanje nove ocene 
	 * @param newMark tipa Integer vrednost nove ocene
	 * @param result tipa BindingResult za ispitivanje validnosti dto
	 * @return vraca response entity da li je validan dto ako jeste vraca response entity u skladu sa
	 * izlazom meode teacherDao.changeMarkOfStudent
	 */
	@Secured({ "ROLE_TEACHER", "ROLE_ADMIN" })
	@RequestMapping(method = RequestMethod.PUT, value = "/{teacherId}/subject/student/changeMark/newMark")
	public ResponseEntity<?> changeStudentMarkForSubject(@PathVariable Integer teacherId,
			@Valid @RequestBody StudentMarksForSubjectDto marks, @RequestParam Integer newMark, BindingResult result) {
		if (result.hasErrors()) {
			logger.error("Error, bad request" + result.toString());
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}

		logger.info("Teacher changed mark of subject of the student");
		return teacherDao.changeMarkOfStudent(teacherId, marks, newMark);
	}
	/**
	 * Izlistava sve ocene koje je dao nastavnik
	 * @param id tipa Integer za identifikaciju nastavnikan
	 * @return vraca resposne entity u skladu sa izlazom metode teacherDao.listAllMarksOfTeacher ili
	 * repsonse entity da natavnik nije pronadjen
	 */
	@Secured({ "ROLE_TEACHER", "ROLE_ADMIN" })
	@RequestMapping(method=RequestMethod.GET, value="/{id}/allmarks")
	public ResponseEntity<?> listAllTeacherMarks(@PathVariable Integer id){
		TeacherEntity teacher= new TeacherEntity();
		Optional<TeacherEntity> op= teacherRepository.findById(id);
		if(op.isPresent()) {
			logger.info("Teacher or admin listed all marks of a teacher");
			return teacherDao.listAllMarksOfTeacher(id);
			
		}
		else 
			logger.warn("Teacher not found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Teacher not found. Check Id"),
				HttpStatus.NOT_FOUND);
	}

}