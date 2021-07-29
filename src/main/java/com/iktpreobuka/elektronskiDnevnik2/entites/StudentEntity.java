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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;

@Entity
@Table(name="student")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@JsonRootName(value="Student")
public class StudentEntity {
	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="student_id")
	@JsonView(Views.StudentView.class)
	private Integer id;
	
	@JsonView(Views.StudentView.class)
	@Column(name="first_name")
	private String firstName;
	
	@JsonView(Views.StudentView.class)
	@Column(name="last_name")
	private String lastName;
	
	@JsonView(Views.StudentView.class)
	@Column(name="grade")
	private Integer grade;
	
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private ParentEntity parent;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "subjects_students", joinColumns ={@JoinColumn(name = "student_id", nullable = false, updatable = false)},
	inverseJoinColumns = {@JoinColumn(name = "subject_id",nullable = false, updatable = false)})
	private List<SubjectEntity> subjects = new ArrayList<SubjectEntity>();
	
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "user")
	private UserEntity user;
	
	


	public StudentEntity() {
		super();
		// TODO Auto-generated constructor stub
	}


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


	public Integer getGrade() {
		return grade;
	}


	public void setGrade(Integer grade) {
		this.grade = grade;
	}


	public ParentEntity getParent() {
		return parent;
	}


	public void setParent(ParentEntity parent) {
		this.parent = parent;
	}


	public List<SubjectEntity> getSubjects() {
		return subjects;
	}


	public void setSubjects(List<SubjectEntity> subjects) {
		this.subjects = subjects;
	}


	public UserEntity getUser() {
		return user;
	}


	public void setUser(UserEntity user) {
		this.user = user;
	}


	

}
