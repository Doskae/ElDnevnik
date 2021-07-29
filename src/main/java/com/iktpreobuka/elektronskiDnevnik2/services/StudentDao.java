package com.iktpreobuka.elektronskiDnevnik2.services;

import org.springframework.http.ResponseEntity;

public interface StudentDao {
	
	public ResponseEntity<?> setSubjectToStudent(Integer id, Integer subjectId);
	public ResponseEntity<?> removeSubjecFromStudent(Integer id, Integer subjectId);
	public ResponseEntity<?> removeParentFromStudent(Integer id, Integer parentId);
	public ResponseEntity<?> getMarksForSubject(Integer id, String subject);
	public ResponseEntity<?> getMarksFoAllSubjects(Integer id);
	

}
