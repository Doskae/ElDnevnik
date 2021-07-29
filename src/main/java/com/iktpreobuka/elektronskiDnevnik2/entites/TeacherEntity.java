package com.iktpreobuka.elektronskiDnevnik2.entites;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;

@Entity
@Table(name="teacher")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@JsonRootName(value="Teacher")
public class TeacherEntity {
	

	@Id
	@Column(name="teacher_id")
	@JsonView(Views.TeacherView.class)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@JsonView(Views.StudentView.class)
	@Column(name="first_name")
	private String firstName;
	
	@JsonView(Views.StudentView.class)
	@Column(name="last_name")
	private String lastName;
	
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "user")
	private UserEntity user;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "subjects_teachers", joinColumns ={@JoinColumn(name = "teacher_id", nullable = false, updatable = false)},
	inverseJoinColumns = {@JoinColumn(name = "subject_id",nullable = false, updatable = false)})
	private List<SubjectEntity> subject = new ArrayList<SubjectEntity>();
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public List<SubjectEntity> getSubject() {
		return subject;
	}

	public void setSubject(List<SubjectEntity> subject) {
		this.subject = subject;
	}

	public TeacherEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
