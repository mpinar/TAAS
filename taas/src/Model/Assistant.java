package Model;

import java.util.ArrayList;

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
	public String rawBG, rawTB;
	
	public boolean isActive;
	DatabaseHelper dbh;
	
	
	public Assistant(String n, String s, String m, Department d){
		name = n;
		surname = s;
		mail = m;
		department = d;
		dbh = new DatabaseHelper();

	}
	
	
	public void setAdvisorFromMail(String mail){
	advisor = dbh.getInstructorFromMail(mail);
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
}
