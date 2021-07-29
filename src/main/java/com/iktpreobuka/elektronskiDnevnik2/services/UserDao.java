package com.iktpreobuka.elektronskiDnevnik2.services;



import com.iktpreobuka.elektronskiDnevnik2.entites.UserEntity;

public interface UserDao {
	
	
	public UserEntity findUserByTeacherId(Integer id);
	
	public UserEntity findUserByStudentId(Integer id);
	
	public UserEntity findUserByParentId(Integer id);
	

}
