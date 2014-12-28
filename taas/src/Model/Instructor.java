package Model;

import java.util.ArrayList;

import Helper.DatabaseHelper;

public class Instructor {
	
	public int id;
	public String name;
	public String surname;
	public String mail;
	public Department department;
	public ArrayList<Request> requests;
	public ArrayList<Course> teaches;
	DatabaseHelper dbh = new DatabaseHelper();
	
	public Instructor(String n, String s, String m, Department d) {
		name = n;
		surname = s;
		mail = m;
		department = d;
		teaches = new ArrayList<Course>(); 
		id = setIDFromDBUsingMail();
		
	}

  // GETTERS
	
	public String getName() {
		return name;
	}


	public String getSurname() {
		return surname;
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

	// METHODS
	
	public String toString(){
		String s =	"Instructor\nName:"+ name + "\nSurname: "+surname ;
				
		return s;
	}
	
	
	public boolean checkInstructorIsInDB(){
		
		ArrayList<Instructor> instructors = dbh.getAllInstructors(); 
		boolean result = false;
		
		for(int i=0; i<instructors.size(); i++){
			
			if(this.mail.compareToIgnoreCase(instructors.get(i).mail) == 0){
				result = true;
				break;
				// update teaching information
			}
		}
		return result;
	}

	public void addToDB(String randomPassword) {
	
		dbh.addInstructorToDatabase(this, randomPassword);
		
	}
	
	public ArrayList<Course> getTeachingInformationFromDB(){
		// TODO
		return dbh.getTeachingInformationFromInsID(this.id);
	}
	
	public void addTeachingInfoToDB(){
		dbh.addTeachingInformationForInstructor(this);
	}
	
	private int setIDFromDBUsingMail(){
		int id = dbh.getInstructorIDfromMail(this.mail);
		return id;
		
	}

	public boolean setAdditionalRequestForCourse(Course c, String additionalReq) {
		// TODO Auto-generated method stub
		return dbh.saveAdditionalRequestForCourse(c, this.id, additionalReq);
		
	}

	public boolean makeAssistantRequest(Course selectedCourse, Assistant selectedAssistant) {
		// TODO Auto-generated method stub
		//selectedAssistant.setAssistantID();
		return dbh.insertAssistantRequest(this, selectedCourse, selectedAssistant);
		
	}
}
