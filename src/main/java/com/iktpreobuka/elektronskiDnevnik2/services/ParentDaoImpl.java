package com.iktpreobuka.elektronskiDnevnik2.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.entites.MarkEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.ParentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.StudentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.SubjectEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.StudentMarksForSubjectDto;
import com.iktpreobuka.elektronskiDnevnik2.repositories.ParentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.StudentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.SubjectRepository;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;
import com.iktpreobuka.elektronskiDnevnik2.util.RESTError;

@Service
public class ParentDaoImpl implements ParentDao {
	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	ParentRepository parentRepository;

	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	SubjectRepository subjectRepository;

	/**
	 * Postavlja ucenika kod roditelja, ukoliko je sve ok, loguje da je admin dodao
	 * ucenika roditelju i vraca response entity da je ucenik dodat roditelju param
	 * id tipa Integer za identifikaciju roditelja param studentId za identifikaciju
	 * ucenika return response entity da je sve ok ako je uradjena transkackija sa
	 * http ok, ili response entity sa custom resterrror da nije pronadjen roditelj
	 * ili ucenik
	 */
	@Override
	public ResponseEntity<?> setStudentToParent(Integer id, Integer studentId) {
		ParentEntity retVal = new ParentEntity();
		StudentEntity student = new StudentEntity();
		List<StudentEntity> listOfStudents = new ArrayList<StudentEntity>();

		Optional<ParentEntity> op = parentRepository.findById(id);
		Optional<StudentEntity> op1 = studentRepository.findById(studentId);
		if ((op.isPresent()) && (op1.isPresent())) {
			retVal = parentRepository.findById(id).get();
			student = studentRepository.findById(studentId).get();
			listOfStudents = retVal.getStudents();

			listOfStudents.add(student);
			retVal.setStudents(listOfStudents);
			student.setParent(retVal);
			studentRepository.save(student);
			parentRepository.save(retVal);

			logger.info("Admin added student to parent");
			return new ResponseEntity<>("Student added to a parent", HttpStatus.OK);
		}

		logger.warn("Parent or student not found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Parent or student not found. Check Id"),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * Brise ucenika iz roditelja, i loguje promenu, kao i gresku ukoliko nisu dobri
	 * id param id tipa Integer za identifikaciju roditelja param studentId za
	 * identifikaciju učenika return response entity sa porukom da je ucenik
	 * izbrisan iz roditelja i htttp status ok response entity da sa custom proukom
	 * da je doslo do greske i da ucenik ili roditelj nije nadjen
	 */
	@Override
	public ResponseEntity<?> removeStudentFromParent(Integer id, Integer studentId) {
		ParentEntity retVal = new ParentEntity();
		StudentEntity student = new StudentEntity();
		List<StudentEntity> listOfStudents = new ArrayList<StudentEntity>();
		Optional<ParentEntity> op = parentRepository.findById(id);
		Optional<StudentEntity> op1 = studentRepository.findById(studentId);
		if ((op.isPresent()) && (op1.isPresent())) {
			retVal = parentRepository.findById(id).get();
			student = studentRepository.findById(studentId).get();
			listOfStudents = retVal.getStudents();
			
			for(Iterator<StudentEntity> itr = listOfStudents.iterator();itr.hasNext();) {
				StudentEntity stu=itr.next();
				if(itr.equals(student)) {
					itr.remove();
				}
			}
			retVal.setStudents(listOfStudents);
			parentRepository.save(retVal);
		}
		logger.warn("Student or parent not found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Parent or student not found. Check Ids"),
				HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<?> seeMarksForChiled(Integer id, StudentMarksForSubjectDto marks) {
		
		logger.info("Admin or a parent tried get marks of a student for a subject");
		//pronaci roditelja preko id, ucenika i predmet preko dto podataka
		ParentEntity parent = new ParentEntity();
		List<StudentEntity> children= new ArrayList<>();
		StudentEntity child = new StudentEntity();
		List<SubjectEntity> listOfChildSubject= new ArrayList<>();
		SubjectEntity subject= new SubjectEntity();
		List<MarkEntity> listOfMarks= new ArrayList<>();
		
		Optional<ParentEntity> op= parentRepository.findById(id);
		Optional<StudentEntity> op1= Optional.ofNullable(studentRepository.findByFirstNameAndLastNameIgnoreCase(marks.getFirstName(),
				marks.getLastName()));
		Optional<SubjectEntity> op2= Optional.ofNullable(subjectRepository.findBySubjectNameIgnoreCase(marks.getSubject()));
		
		if(op.isPresent()&& op1.isPresent() && op2.isPresent()) {
			parent=parentRepository.findById(id).get();
			child=studentRepository.findByFirstNameAndLastNameIgnoreCase(marks.getFirstName(), marks.getLastName());
			subject=subjectRepository.findBySubjectNameIgnoreCase(marks.getSubject());
			
			//ako postoje pronaci da li ucenik pripada tom roditelju
			children=parent.getStudents();
			for (StudentEntity studentEntity : children) {
				if(studentEntity==child) {
					//ako pripada ubaciti predmete u listu
					listOfChildSubject=child.getSubjects();
					
					// proveriti da li ucenik slusa taj predmet
					for (SubjectEntity subjectEntity : listOfChildSubject) {
						if(subjectEntity==subject) {
							listOfMarks=subject.getMarks();
							logger.info("Students marks listed");
							return new ResponseEntity<List<MarkEntity>>(listOfMarks,HttpStatus.OK);
						}
					}
				}
			}
			
			
		}
		logger.warn("Student or subject not found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Student or subject not found. Check Ids"),
				HttpStatus.NOT_FOUND);
		
	}

	
}