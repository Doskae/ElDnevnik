package com.iktpreobuka.elektronskiDnevnik2.security;

public class Views {
	
	public static class PublicView{}
	public static class StudentView extends PublicView {}
	public static class ParentView extends StudentView {}
	public static class TeacherView extends ParentView{}
	public static class AdminView extends TeacherView{}

}
