package com.iktpreobuka.elektronskiDnevnik2.services;



import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import com.iktpreobuka.elektronskiDnevnik2.entites.UserEntity;

@Service
public class UserDaoImpl implements UserDao {

	@PersistenceContext
	private EntityManager em;
	@Override
	public UserEntity findUserByTeacherId(Integer id) {
			
		String sql="select u from UserEntity u left join fetch  u.teachers t where t.id=:id";
		Query query = em.createQuery(sql);
		query.setParameter("id", id);
		UserEntity result= new UserEntity();
		result=(UserEntity) query.getSingleResult();
		return result;
	}
	@Override
	public UserEntity findUserByStudentId(Integer id) {
		
		String sql="select u from UserEntity u left join fetch  u.students s where s.id=:id";
		Query query = em.createQuery(sql);
		query.setParameter("id", id);
		UserEntity result= new UserEntity();
		result=(UserEntity) query.getSingleResult();
		return result;
	}
	@Override
	public UserEntity findUserByParentId(Integer id) {
		String sql="select u from UserEntity u left join fetch  u.parents p where p.id=:id";
		Query query = em.createQuery(sql);
		query.setParameter("id", id);
		UserEntity result= new UserEntity();
		result=(UserEntity) query.getSingleResult();
		return result;
	}

	
		
	}


