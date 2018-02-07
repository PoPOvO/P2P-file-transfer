package com.mec.deal_mapping.model;

public class StudentModel {
	private String id;
	private String password;
	
	public StudentModel() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "id=" + id + "\n" + "password=" + password;
	}
	
}
