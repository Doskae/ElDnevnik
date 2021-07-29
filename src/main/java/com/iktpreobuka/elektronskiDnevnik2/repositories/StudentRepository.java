package com.iktpreobuka.elektronskiDnevnik2.repositories;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.iktpreobuka.elektronskiDnevnik2.entites.MarkEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.StudentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.SubjectEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.StudentMarksDto;

public interface StudentRepository extends CrudRepository<StudentEntity, Integer>{
	
	public StudentEntity findByFirstNameAndLastNameIgnoreCase(String firstName, String lastName);
	
	//query za izlisrtavanje svih predmeta i svih ocena ucenika, radi u sql ne radi kad se prebaci u javu
	
	@Query(value="select s.student_id,s.first_name,s.last_name,s.grade,s.parent, s.user,sub.subjet_name,m.mark_value,m.mark_description from student s join subjects_students ss on s.student_id=ss.student_id join subject sub on ss.subject_id=sub.subject_id join subjects_marks sm on sub.subject_id=sm.subject_id join mark m on sm.mark_id=m.mark_id join subjects_teachers st on sub.subject_id=st.subject_id join teacher t on st.teacher_id=t.teacher_id where s.student_id=:id", nativeQuery = true)
	//public List<MarkEntity> getMarks();
	
	//public List<StudentMarksDto> getMarks();
	//public HashMap<StudentEntity,MarkEntity> getMarks();
	public List<StudentEntity> getMarks(Integer id);
	
	@Query(value="select sub.subjet_name,m.mark_description, m.mark_value from student s join subjects_students ss on s.student_id=ss.student_id join subject sub on ss.subject_id=sub.subject_id join subjects_marks sm on sub.subject_id=sm.subject_id join mark m on sm.mark_id=m.mark_id join subjects_teachers st on sub.subject_id=st.subject_id join teacher t on st.teacher_id=t.teacher_id where s.student_id=41", nativeQuery =true)
	public List<SubjectEntity> getMarksV2(Integer id);
	
	@Query(value="select s.first_name,s.last_name, sub.subjet_name,m.mark_value from student s join subjects_students ss on s.student_id=ss.student_id join subject sub on ss.subject_id=sub.subject_id join subjects_marks sm on sub.subject_id=sm.subject_id join mark m on sm.mark_id=m.mark_id join subjects_teachers st on sub.subject_id=st.subject_id join teacher t on st.teacher_id=t.teacher_id where s.student_id=41", nativeQuery = true)
	public List<SubjectEntity> getMarksV3(Integer id);

}
