package com.iktpreobuka.elektronskiDnevnik2.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.entites.MarkEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.ParentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.StudentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.SubjectEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.StudentMarksDto;
import com.iktpreobuka.elektronskiDnevnik2.repositories.ParentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.StudentRepository;
import com.iktpreobuka.elektronskiDnevnik2.repositories.SubjectRepository;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;
import com.iktpreobuka.elektronskiDnevnik2.util.RESTError;

import jdk.internal.org.jline.utils.Log;

@Service
public class StudentDaoImpl implements StudentDao {
	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	@PersistenceContext
	private EntityManager em;

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	SubjectRepository subjectRepository;

	@Autowired
	ParentRepository parentRepository;

	/**
	 * Dodaje predmet uceniku vidi se u relacionoj tabeli studen_subject. samo admin
	 * ima pravo
	 * 
	 * @param id        tipa Integer za identifikaciju ucenika
	 * @param subjectId tipa Integer za identifikaciju predmeta
	 * @return vraca Response entity da je dodat predmet sa http statusom ok, ako je
	 *         sve u redu ako nije vraca custom resterro da ucenik ili predmet nisu
	 *         pronad
	 */
	@Override
	public ResponseEntity<?> setSubjectToStudent(Integer id, Integer subjectId) {
		StudentEntity retVal = new StudentEntity();
		SubjectEntity subject = new SubjectEntity();
		List<SubjectEntity> listOfSubjects = new ArrayList<SubjectEntity>();

		Optional<StudentEntity> op = studentRepository.findById(id);
		Optional<SubjectEntity> op1 = subjectRepository.findById(subjectId);
		if ((op.isPresent()) && (op1.isPresent())) {
			retVal = studentRepository.findById(id).get();
			subject = subjectRepository.findById(subjectId).get();
			listOfSubjects = retVal.getSubjects();

			listOfSubjects.add(subject);
			retVal.setSubjects(listOfSubjects);
			studentRepository.save(retVal);

			logger.info("Admin changed students subjects");
			return new ResponseEntity<>("Subject added", HttpStatus.OK);
		}
		logger.warn("Student found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Student or subject not found. Check Ids"),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * Brise predmet iz liste kod ucenika
	 * 
	 * @param id        tipa Integer za identifikaciju ucenika, kao path variable
	 * @param subjectId tipa Integer za identifikaciju predmeta
	 * @return vraca response entiti da je predmet obrisan sa statusom ok, ako je sv
	 *         ok, ako nije nadjen ucenik ili ocena vraca vustom response error.
	 */
	@Override
	public ResponseEntity<?> removeSubjecFromStudent(Integer id, Integer subjectId) {
		StudentEntity retVal = new StudentEntity();
		SubjectEntity subject = new SubjectEntity();
		List<SubjectEntity> listOfSubjects = new ArrayList<SubjectEntity>();
		Optional<StudentEntity> op = studentRepository.findById(id);
		Optional<SubjectEntity> op1 = subjectRepository.findById(subjectId);
		if ((op.isPresent()) && (op1.isPresent())) {
			retVal = studentRepository.findById(id).get();
			subject = subjectRepository.findById(subjectId).get();
			listOfSubjects = retVal.getSubjects();
			if (listOfSubjects.contains(subject)) {
				listOfSubjects.remove(subject);
				retVal.setSubjects(listOfSubjects);
				studentRepository.save(retVal);
				logger.info("Admin deleted subject from student");
				return new ResponseEntity<>("Subject deleted", HttpStatus.OK);
			}
		}
		logger.warn("Student found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Student or subject not found. Check Ids"),
				HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<?> removeParentFromStudent(Integer id, Integer parentId) {
		ParentEntity parent = new ParentEntity();
		StudentEntity student = new StudentEntity();
		List<StudentEntity> children = new ArrayList<>();
		Optional<ParentEntity> op = parentRepository.findById(parentId);
		Optional<StudentEntity> op1 = studentRepository.findById(id);
		if ((op.isPresent()) && (op1.isPresent())) {
			parent = parentRepository.findById(parentId).get();
			student = studentRepository.findById(id).get();
			student.setParent(null);
			logger.info("Admin removed a studend from parent");
			return new ResponseEntity<>("Student deleted", HttpStatus.OK);
		}
		/*	
		 *
			children = parent.getStudents();

			Iterator<StudentEntity> itr = children.iterator();

			while (itr.hasNext()) {

				StudentEntity stu = itr.next();
				if (stu == student) {
					itr.remove();
					parent.setStudents(children);
					parentRepository.save(parent);

					logger.info("Admin removed a studend from parent");
					return new ResponseEntity<>("Student deleted", HttpStatus.OK);
				}

			}
*/
			
		
		logger.warn("Student or parent found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Student or parent not found. Check Ids"),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * ispisuje listu svih ocena za odredjeni predmet ako taj predmet pripada uceniku
	 * ako je sve ok izbacuje response entity sa listom ocena i porukom da je ok
	 * ako nije sve u redu izbacuje response entiy sa resterror da nije pronadjeno
	 * loguje obe opecije u skladu sa izvrsenjem
	 */
	@Override
	public ResponseEntity<?> getMarksForSubject(Integer id, String subject) {
		StudentEntity student= new StudentEntity();
		SubjectEntity sub= new SubjectEntity();
		List<SubjectEntity> listOfStudentSubject = new ArrayList<>();
		List<MarkEntity> listOfSubjectMarks=new ArrayList<>();
		Optional<StudentEntity> op= studentRepository.findById(id);
		Optional<SubjectEntity> op1= Optional.ofNullable(subjectRepository.findBySubjectNameIgnoreCase(subject));
		if(op.isPresent()&& op1.isPresent()) {
			student=studentRepository.findById(id).get();
			sub=subjectRepository.findBySubjectNameIgnoreCase(subject);
			listOfStudentSubject=student.getSubjects();
			for (SubjectEntity subjectEntity : listOfStudentSubject) {
				if(subjectEntity==sub) {
					listOfSubjectMarks=subjectEntity.getMarks();
					logger.info("Admin or student got marks for subject");
					return new ResponseEntity<List<MarkEntity>>(listOfSubjectMarks,HttpStatus.OK);
				}
			}
		}
		logger.warn("Student or subject found");
		return new ResponseEntity<RESTError>(new RESTError(1, "Student or subject not found. Check Ids"),
				HttpStatus.NOT_FOUND);
		
		
	}

	@Override
	public ResponseEntity<?> getMarksFoAllSubjects(Integer id) {
		
		String sql="select s.first_name,s.last_name, sub.subjet_name,m.mark_value from student s join subjects_students ss on s.student_id=ss.student_id join subject sub on ss.subject_id=sub.subject_id join subjects_marks sm on sub.subject_id=sm.subject_id join mark m on sm.mark_id=m.mark_id join subjects_teachers st on sub.subject_id=st.subject_id join teacher t on st.teacher_id=t.teacher_id where s.student_id=:id";
		Query q =em.createNativeQuery(sql);
		q.setParameter("id", id);
		List<StudentMarksDto> marks=q.getResultList();
		return new ResponseEntity<List<StudentMarksDto>>(marks,HttpStatus.OK);
	}

}
