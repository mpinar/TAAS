package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import com.mysql.jdbc.UpdatableResultSet;

import Helper.DatabaseHelper;

public class Assistant {

	public int id;
	public String name;
	public String surname;
	public String mail;
	public Department department;
	public Instructor advisor;
	public ArrayList<String> background;
	public ArrayList<String> teachingBackground;
	private String rawBG, rawTBG;

	public boolean isActive;
	DatabaseHelper dbh;


	public Assistant(String n, String s, String m, Department d){
		name = n;
		surname = s;
		mail = m;
		department = d;
		dbh = new DatabaseHelper();
		background = new ArrayList<String>();
		teachingBackground = new ArrayList<String>();
		setAssistantID();
	}


	public String getRawBG() {
		return rawBG;
	}


	public String getRawTBG() {
		return rawTBG;
	}


	public void setRawBG(String rawBG) {
		this.rawBG = rawBG;
	}


	public void setRawTBG(String rawTBG) {
		this.rawTBG = rawTBG;
	}


	public void setAdvisorFromMail(String mail){
		this.advisor = dbh.getInstructorFromMail(mail);
		setAssistantID();
		insertAdvisorInfoInDB();
		
	}

	private void insertAdvisorInfoInDB(){
		dbh.insertAdvisorInfoForAssistant(this);
	}

	public void setAssistantID(){
		id = dbh.getAssistantID(this);
	}


	public void addAsstToDB() {
		// TODO Auto-generated method stub
		dbh.addAssistantToDB(this);
	}


	public String toString(){
		return this.name + " "+this.surname + " ["+ this.mail + "]"; 
	}


	public HashMap<Course, Integer> getCostsForCourses(int id2) {
		// TODO Auto-generated method stub
		return dbh.getCostsForAssistant(id2);
	}
}
