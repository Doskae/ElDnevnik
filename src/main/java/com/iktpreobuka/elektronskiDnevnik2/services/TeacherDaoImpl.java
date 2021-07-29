package com.iktpreobuka.elektronskiDnevnik2.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.entites.MarkEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.ParentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.StudentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.SubjectEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.TeacherEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.GiveMarkDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.StudentMarksDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.StudentMarksForSubjectDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.TeacherMarksDto;
import com.iktpreobuka.elektronskiDnevnik2.models.EmailObject;
import com.iktpreobuka.elektronskiDnevnik2.repositories.MarkRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.StudentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.SubjectRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.TeacherRepository;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;
import com.iktpreobuka.elektronskiDnevnik2.util.RESTError;

@Service
public class TeacherDaoImpl implements TeacherDao {

	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	TeacherRepository teacherRepository;

	@Autowired
	SubjectRepository subjectRepository;

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	MarkRepository markRepository;

	@Autowired
	EmailService emailService;
	
	@PersistenceContext
	private EntityManager em;

	/**
	 * dodavanje predmeta nastavniku preko id nastavnika u id predmeta. nema provere
	 * da li je predmet vec dodat moglo bi se dopuniti sa tim, eventualno metod sa
	 * imenom predmeta kao parametrom
	 * 
	 * @param id        path variable za id nastavnika
	 * @param subjectId id request parameter za subject id
	 * @return vraca poruku da je predmet dodat, a ako je los bilo koji id kaze da
	 *         nastavnik ili predmet nisu dodati
	 */
	@Override
	public ResponseEntity<?> setSubjectToTeacher(Integer id, Integer subjectId) {
		TeacherEntity retVal = new TeacherEntity();
		SubjectEntity subject = new SubjectEntity();
		List<SubjectEntity> listOfSubjects = new ArrayList<SubjectEntity>();

		Optional<TeacherEntity> op = teacherRepository.findById(id);
		Optional<SubjectEntity> op1 = subjectRepository.findById(subjectId);
		if ((op.isPresent()) && (op1.isPresent())) {
			retVal = teacherRepository.findById(id).get();
			subject = subjectRepository.findById(subjectId).get();
			listOfSubjects = retVal.getSubject();

			listOfSubjects.add(subject);
			retVal.setSubject(listOfSubjects);
			teacherRepository.save(retVal);

			logger.info("Admin changed teacher subjects");
			return new ResponseEntity<>("Subjcet added", HttpStatus.OK);
		}
		logger.warn("Teacher or subject not found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Teacher or subject not found. Check Ids"),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * Brise predmet kod nastavnika, proverava da li je predmet kod nastavnika u
	 * listi ako jeste brise ga i snima novu listu predmeta kod nastavnika.
	 * 
	 * @param id        tipa Integer za identifikaciju nastavnika
	 * @param subjectId tipa Integer za identifikaciju predmeta
	 * @return vraca response entity da je predmet obrisan sa htttp ok ako je sve u
	 *         redu vraca custm errro ako nije nadjen predmet ili nastavnik sa
	 *         porukom da se proveri id i http not found
	 */
	@Override
	public ResponseEntity<?> removeSubjectFromTeacher(Integer id, Integer subjectId) {
		TeacherEntity retVal = new TeacherEntity();
		SubjectEntity subject = new SubjectEntity();
		List<SubjectEntity> listOfSubjects = new ArrayList<SubjectEntity>();
		Optional<TeacherEntity> op = teacherRepository.findById(id);
		Optional<SubjectEntity> op1 = subjectRepository.findById(subjectId);
		if ((op.isPresent()) && (op1.isPresent())) {
			retVal = teacherRepository.findById(id).get();
			subject = subjectRepository.findById(subjectId).get();
			listOfSubjects = retVal.getSubject();
			if (listOfSubjects.contains(subject)) {
				listOfSubjects.remove(subject);
				retVal.setSubject(listOfSubjects);
				teacherRepository.save(retVal);
				logger.info("Admin deleted subject from teacher");
				return new ResponseEntity<>("Subject deleted", HttpStatus.OK);
			}
		}
		logger.warn("Student found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Teacher or subject not found. Check Ids"),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * daje ocenu uceniku preko id nastavanika
	 */
	@Override
	public ResponseEntity<?> giveMarkToStudent(Integer id, GiveMarkDto mark) {
		logger.info("Teacher is giving mark");
		StudentEntity student = new StudentEntity();
		SubjectEntity subject = new SubjectEntity();
		MarkEntity grade = new MarkEntity();
		TeacherEntity teacher = new TeacherEntity();
		ParentEntity parent = new ParentEntity();
		List<SubjectEntity> listOfStudentSubjects = new ArrayList<>();
		List<MarkEntity> listOfSubjectMarks = new ArrayList<>();
		List<SubjectEntity> listofTeacherSubjects = new ArrayList<>();

		// pronaci da li postoje trazeni nastavnik, ucenik, predmet i ocena
		Optional<TeacherEntity> op3 = teacherRepository.findById(id);
		Optional<StudentEntity> op = Optional.ofNullable(studentRepository
				.findByFirstNameAndLastNameIgnoreCase(mark.getStudentFirstName(), mark.getStudentLastName()));
		Optional<SubjectEntity> op1 = Optional
				.ofNullable(subjectRepository.findBySubjectNameIgnoreCase(mark.getSubjectName()));
		Optional<MarkEntity> op2 = Optional.ofNullable(markRepository.findMarkByValue(mark.getMark()));

		if (op.isPresent() && op1.isPresent() && op2.isPresent() && op3.isPresent()) {
			teacher = teacherRepository.findById(id).get();
			student = studentRepository.findByFirstNameAndLastNameIgnoreCase(mark.getStudentFirstName(),
					mark.getStudentLastName());
			subject = subjectRepository.findBySubjectNameIgnoreCase(mark.getSubjectName());
			grade = markRepository.findMarkByValue(mark.getMark());

			// proveriti da li nastavnik predaje taj predmet

			// ubacuje listu predmeta nastavnika
			listofTeacherSubjects = teacher.getSubject();

			// ubacuje listu predmeta kod ucenika
			listOfStudentSubjects = student.getSubjects();

			// proverava da li je predmet kod nastavnika
			for (SubjectEntity subjectEntity : listofTeacherSubjects) {
				if (subjectEntity == subject) {
					// ako jeste proverava da li je predmet kod ucenika
					for (SubjectEntity studentSubject : listOfStudentSubjects) {
						if (studentSubject == subject) {
							// ako jeste ucitava listu ocena

							listOfSubjectMarks = subject.getMarks();
							listOfSubjectMarks.add(grade);
							subject.setMarks(listOfSubjectMarks);
							studentRepository.save(student);
							logger.info("Teacher has given a mark");

							// poslati email roditelju
							EmailObject email = new EmailObject();
							parent = student.getParent();

							email.setTo(parent.getEmail());
							email.setSubject("New mark for your child");
							email.setText(mark.toString() + " " + "teacher: " + teacher.getFirstName() + " "
									+ teacher.getLastName());
							emailService.sendSimpleMessage(email);
							logger.info("Email with new sent to students parent");
							return new ResponseEntity<>("Mark given", HttpStatus.OK);
						}

					}
				}
			}

		}
		logger.warn("Mark has not been given. Check student, subject or mark");
		return new ResponseEntity<RESTError>(new RESTError(2, "Mark not given.  Check subject, student or mark"),
				HttpStatus.NOT_FOUND);

	}

	@Override
	public ResponseEntity<?> seeMarksOfStudent(Integer id, StudentMarksForSubjectDto marks) {
		// find teacher, subject and student u bazi
		TeacherEntity teacher = new TeacherEntity();
		StudentEntity student = new StudentEntity();
		SubjectEntity subject = new SubjectEntity();
		List<SubjectEntity> listOfTeacherSubjects = new ArrayList<>();
		List<SubjectEntity> listOfStudentSubject = new ArrayList<>();
		List<MarkEntity> listOfSubjectMarks = new ArrayList<>();
		Optional<TeacherEntity> op = teacherRepository.findById(id);
		Optional<StudentEntity> op1 = Optional.ofNullable(
				studentRepository.findByFirstNameAndLastNameIgnoreCase(marks.getFirstName(), marks.getLastName()));
		Optional<SubjectEntity> op2 = Optional
				.ofNullable(subjectRepository.findBySubjectNameIgnoreCase(marks.getSubject()));

		// ukoliko su pronadjeni dodati nastavniku listu predmeta, uceniku listu
		// predmeta
		if (op.isPresent() && op1.isPresent() && op2.isPresent()) {
			teacher = teacherRepository.findById(id).get();

			student = studentRepository.findByFirstNameAndLastNameIgnoreCase(marks.getFirstName(), marks.getLastName());
			subject = subjectRepository.findBySubjectNameIgnoreCase(marks.getSubject());
			listOfTeacherSubjects = teacher.getSubject();
			listOfStudentSubject = student.getSubjects();

			// proveriti da li je nastavnik predaje predmet uceniku
			for (SubjectEntity subjectEntity : listOfTeacherSubjects) {
				if (subjectEntity == subject) {
					for (SubjectEntity studentStubjecs : listOfStudentSubject) {
						if (studentStubjecs == subject) {
							// ako predaje ubaciti listu ocena iz tog predmeta
							listOfSubjectMarks = subject.getMarks();
							logger.info("Students marks listed");
							return new ResponseEntity<List<MarkEntity>>(listOfSubjectMarks, HttpStatus.OK);
						}
					}
				}

			}

		}
		logger.warn("Student, subject or teacher not found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Student, subject or teacher not found. Check Ids"),
				HttpStatus.NOT_FOUND);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ResponseEntity<?> deleteMarkOfStudent(Integer id, StudentMarksForSubjectDto mark) {
		StudentEntity student = new StudentEntity();
		TeacherEntity teacher = new TeacherEntity();
		SubjectEntity subject = new SubjectEntity();
		List<SubjectEntity> listOfTeacherSubjects = new ArrayList<>();
		List<SubjectEntity> listOfStudentSubject = new ArrayList<>();
		List<MarkEntity> listOfSubjectMarks = new ArrayList<>();

		Optional<TeacherEntity> op = teacherRepository.findById(id);
		Optional<StudentEntity> op1 = Optional.ofNullable(
				studentRepository.findByFirstNameAndLastNameIgnoreCase(mark.getFirstName(), mark.getLastName()));
		Optional<SubjectEntity> op2 = Optional
				.ofNullable(subjectRepository.findBySubjectNameIgnoreCase(mark.getSubject()));

		// ukoliko su pronadjeni dodati nastavniku listu predmeta, uceniku listu
		// predmeta
		if (op.isPresent() && op1.isPresent() && op2.isPresent()) {
			teacher = teacherRepository.findById(id).get();
			student = studentRepository.findByFirstNameAndLastNameIgnoreCase(mark.getFirstName(), mark.getLastName());
			subject = subjectRepository.findBySubjectNameIgnoreCase(mark.getSubject());
			listOfTeacherSubjects = teacher.getSubject();
			listOfStudentSubject = student.getSubjects();
			for (SubjectEntity subjectEntity : listOfTeacherSubjects) {
				if (subjectEntity == subject) {
					for (SubjectEntity studentStubjecs : listOfStudentSubject) {
						if (studentStubjecs == subject) {
							// ako predaje ubaciti listu ocena iz tog predmeta
							listOfSubjectMarks = subject.getMarks();
							// pronaci vrednost trazene ocene i obrisati je
							Iterator<MarkEntity> itr = listOfSubjectMarks.iterator();
							while (itr.hasNext()) {
								MarkEntity mk = itr.next();
								if (mk.getValue() == mark.getSubjectMark()) {
									itr.remove();
									break;
								}
							}

							// kad obrise update ucenika
							listOfStudentSubject.remove(subject);
							subject.setMarks(listOfSubjectMarks);
							listOfStudentSubject.add(subject);
							student.setSubjects(listOfStudentSubject);
							studentRepository.save(student);
							logger.info("Students mark deleted");
							return new ResponseEntity<List<MarkEntity>>(listOfSubjectMarks, HttpStatus.OK);
						}
					}
				}

			}
		}
		logger.warn("Student, subject or teacher not found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Student, subject or teacher not found. Check Ids"),
				HttpStatus.NOT_FOUND);

	}

	@Override
	public ResponseEntity<?> changeMarkOfStudent(Integer id, StudentMarksForSubjectDto mark, Integer newMark) {
		StudentEntity student = new StudentEntity();
		TeacherEntity teacher = new TeacherEntity();
		SubjectEntity subject = new SubjectEntity();
		List<SubjectEntity> listOfTeacherSubjects = new ArrayList<>();
		List<SubjectEntity> listOfStudentSubject = new ArrayList<>();
		List<MarkEntity> listOfSubjectMarks = new ArrayList<>();
		List<MarkEntity> tempList = new ArrayList<>();
		MarkEntity mk = new MarkEntity();

		MarkEntity retMk = new MarkEntity();
		retMk.setValue(mark.getSubjectMark());
		Optional<TeacherEntity> op = teacherRepository.findById(id);
		Optional<StudentEntity> op1 = Optional.ofNullable(
				studentRepository.findByFirstNameAndLastNameIgnoreCase(mark.getFirstName(), mark.getLastName()));
		Optional<SubjectEntity> op2 = Optional
				.ofNullable(subjectRepository.findBySubjectNameIgnoreCase(mark.getSubject()));
		if (op.isPresent() && op1.isPresent() && op2.isPresent()) {
			teacher = teacherRepository.findById(id).get();
			student = studentRepository.findByFirstNameAndLastNameIgnoreCase(mark.getFirstName(), mark.getLastName());
			subject = subjectRepository.findBySubjectNameIgnoreCase(mark.getSubject());
			listOfTeacherSubjects = teacher.getSubject();
			listOfStudentSubject = student.getSubjects();
			for (SubjectEntity subjectEntity : listOfTeacherSubjects) {
				if (subjectEntity == subject) {
					for (SubjectEntity studentStubjecs : listOfStudentSubject) {
						if (studentStubjecs == subject) {
							// ako predaje ubaciti listu ocena iz tog predmeta
							listOfSubjectMarks = subject.getMarks();
							mk = markRepository.findMarkByValue(mark.getSubjectMark());
							retMk = markRepository.findMarkByValue(newMark);
							Iterator<MarkEntity> itr = listOfSubjectMarks.iterator();
							while (itr.hasNext()) {
								MarkEntity remMk = itr.next();
								if (remMk == markRepository.findMarkByValue(mark.getSubjectMark())) {
									itr.remove();
									break;
								}
							}
							listOfSubjectMarks.add(retMk);
							listOfStudentSubject.remove(subject);
							subject.setMarks(listOfSubjectMarks);
							listOfStudentSubject.add(subject);
							student.setSubjects(listOfStudentSubject);
							studentRepository.save(student);
							logger.info("Students mark changed");
							return new ResponseEntity<List<MarkEntity>>(listOfSubjectMarks, HttpStatus.OK);

						}

					}
				}
			}
		}
		logger.warn("Student, subject or teacher not found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Student, subject or teacher not found. Check Ids"),
				HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<?> listAllMarksOfTeacher(Integer id) {

		String sql="select sub.subjet_name, m.mark_value, s.first_name, s.last_name from teacher t join subjects_teachers st on st.teacher_id=t.teacher_id join subject sub on st.subject_id=sub.subject_id join subjects_marks sm on sub.subject_id=sm.subject_id join subjects_students ss on sm.subject_id=ss.subject_id join student s on  ss.student_id=s.student_id join mark m on sm.mark_id=m.mark_id where t.teacher_id=:id";
		Query q =em.createNativeQuery(sql);
		q.setParameter("id", id);
		List<TeacherMarksDto> marks=q.getResultList();
		return new ResponseEntity<List<TeacherMarksDto>>(marks,HttpStatus.OK) ;
		
	}
}
