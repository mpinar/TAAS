package Model;

import java.util.ArrayList;

public class Instructor {

	String name;
	String surname;
	String username;
	String mail;
	Department department;
	ArrayList<Request> requests;
	ArrayList<Course> teaches;
	
	
	public Instructor(String n, String s, String m, Department d) {
		name = n;
		surname = s;
		mail = m;
		department = d;
	}

  // GETTERS
	
	public String getName() {
		return name;
	}


	public String getSurname() {
		return surname;
	}


	public String getUsername() {
		return username;
	}


	public String getMail() {
		return mail;
	}


	public Department getDepartment() {
		return department;
	}


	public ArrayList<Request> getRequests() {
		return requests;
	}


	public ArrayList<Course> getTeaches() {
		return teaches;
	}


	// SETTERS
	
	public void setName(String name) {
		this.name = name;
	}


	public void setSurname(String surname) {
		this.surname = surname;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public void setMail(String mail) {
		this.mail = mail;
	}


	public void setDepartment(Department department) {
		this.department = department;
	}


	public void setRequests(ArrayList<Request> requests) {
		this.requests = requests;
	}


	public void setTeaches(ArrayList<Course> teaches) {
		this.teaches = teaches;
	}
}
