package com.iktpreobuka.elektronskiDnevnik2.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.elektronskiDnevnik2.entites.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
	
	public UserEntity findByEmail(String email);

}
