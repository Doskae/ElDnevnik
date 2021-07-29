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
@Table(name = "Mark")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@JsonRootName(value="Mark")
public class MarkEntity {
	@Id
	@Column(name = "mark_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(Views.AdminView.class)
	private Integer id;

	@Column(name = "mark_value")
	@JsonView(Views.PublicView.class)
	private Integer value;

	@JsonView(Views.PublicView.class)
	@Column(name = "mark_description")
	private String description;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade =CascadeType.REFRESH)
	@JoinTable(name = "subjects_marks", joinColumns ={@JoinColumn(name = "mark_id", nullable = false, updatable = false)},
	inverseJoinColumns = {@JoinColumn(name = "subject_id",nullable = false, updatable = false)})
	private List<SubjectEntity> subjects = new ArrayList<SubjectEntity>();
	
	
	

	public MarkEntity() {
		super();

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SubjectEntity> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<SubjectEntity> subjects) {
		this.subjects = subjects;
	}

	

	

	

}
