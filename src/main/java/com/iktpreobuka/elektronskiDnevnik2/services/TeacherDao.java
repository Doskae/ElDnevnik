package com.iktpreobuka.elektronskiDnevnik2.services;

import org.springframework.http.ResponseEntity;

import com.iktpreobuka.elektronskiDnevnik2.entites.dto.GiveMarkDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.StudentMarksForSubjectDto;
import com.iktpreobuka.elektronskiDnevnik2.entites.dto.TeacherMarksDto;

public interface TeacherDao {

	public ResponseEntity<?> setSubjectToTeacher(Integer id,Integer subjectId);
	
	public ResponseEntity<?> removeSubjectFromTeacher(Integer id, Integer subjectId);
	
	public ResponseEntity<?> giveMarkToStudent(Integer id, GiveMarkDto mark);
	
	public ResponseEntity<?> seeMarksOfStudent(Integer id, StudentMarksForSubjectDto marks);
	
	public ResponseEntity<?> deleteMarkOfStudent(Integer id, StudentMarksForSubjectDto mark);
	
	public ResponseEntity<?> changeMarkOfStudent(Integer id, StudentMarksForSubjectDto mark, Integer newMark);
	
	public ResponseEntity<?> listAllMarksOfTeacher(Integer id);
 	
}
