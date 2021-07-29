package com.iktpreobuka.elektronskiDnevnik2.services;

import org.springframework.http.ResponseEntity;

import com.iktpreobuka.elektronskiDnevnik2.entites.dto.StudentMarksForSubjectDto;

public interface ParentDao {
	
	public ResponseEntity<?> setStudentToParent(Integer id, Integer studentId);
	public ResponseEntity<?> removeStudentFromParent(Integer id, Integer studentId);
	public ResponseEntity<?> seeMarksForChiled(Integer id, StudentMarksForSubjectDto marks );

}
