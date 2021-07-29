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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;

@Entity
@Table(name="subject")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@JsonRootName(value="Subject")
public class SubjectEntity {
	
	@Id
	@Column(name="subject_id")
	@JsonView(Views.StudentView.class)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer id;
	
	@JsonView(Views.StudentView.class)
	@Column(name="subjet_name")
	public String subjectName;
	
	@JsonView(Views.TeacherView.class)
	@Column(name="weekly_fund")
	public Integer weeklyFund;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "subjects_teachers", joinColumns ={@JoinColumn(name = "subject_id", nullable = false, updatable = false)},
	inverseJoinColumns = {@JoinColumn(name = "teacher_id",nullable = false, updatable = false)})
	private List<TeacherEntity> teacher = new ArrayList<TeacherEntity>();
	
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "subjects_students", joinColumns ={@JoinColumn(name = "subject_id", nullable = false, updatable = false)},
	inverseJoinColumns = {@JoinColumn(name = "student_id",nullable = false, updatable = false)})
	private List<StudentEntity> students = new ArrayList<StudentEntity>();
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "subjects_marks", joinColumns ={@JoinColumn(name = "subject_id", nullable = false, updatable = false)},
	inverseJoinColumns = {@JoinColumn(name = "mark_id",nullable = false, updatable = false)})
	private List<MarkEntity> marks = new ArrayList<MarkEntity>();
	
	

	public SubjectEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Integer getWeeklyFund() {
		return weeklyFund;
	}

	public void setWeeklyFund(Integer weeklyFund) {
		this.weeklyFund = weeklyFund;
	}

	public List<TeacherEntity> getTeacher() {
		return teacher;
	}

	public void setTeacher(List<TeacherEntity> teacher) {
		this.teacher = teacher;
	}

	

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}

	public List<MarkEntity> getMarks() {
		return marks;
	}

	public void setMarks(List<MarkEntity> marks) {
		this.marks = marks;
	}

}
