package com.iktpreobuka.elektronskiDnevnik2.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.elektronskiDnevnik2.entites.MarkEntity;

public interface MarkRepository extends CrudRepository<MarkEntity, Integer> {

	public MarkEntity findMarkByValue(Integer value);
	
}
