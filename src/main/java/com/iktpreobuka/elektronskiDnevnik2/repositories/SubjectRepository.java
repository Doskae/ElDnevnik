package com.iktpreobuka.elektronskiDnevnik2.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.elektronskiDnevnik2.entites.StudentEntity;
import com.iktpreobuka.elektronskiDnevnik2.entites.SubjectEntity;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Integer> {
	
	public SubjectEntity findBySubjectNameIgnoreCase(String subjectname);
	
	

}
