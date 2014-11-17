package Model;

import Helper.DatabaseHelper;

public class Course {

	
	public String code;
	public int number;
	public int capacity;
	public int maxAssistantNumber;
	DatabaseHelper dbh; 
	
	public Course(String c, int n, int capacity) { 
		code = c;
		number = n;
		this.capacity = capacity;
		setMaxAssistantNumber(capacity);
		dbh = new DatabaseHelper();
	}
	
	
	private void setMaxAssistantNumber(int c) {
		
		double d = c;
	
		maxAssistantNumber = (int) Math.ceil(d / 20.0);
		
		
	}


	public void addCourseToDatabase() {
		// TODO Auto-generated method stub
		dbh.addCourseToDatabase(this);
	}
}
