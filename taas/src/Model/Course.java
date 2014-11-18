package Model;

import Helper.DatabaseHelper;

public class Course {


	public String code;
	public int number;
	public int capacity;
	public int maxAssistantNumber;
	
	DatabaseHelper dbh; 

	public Course(String code, int num){
		this.code = code;
		number = num;
		dbh = new DatabaseHelper();
		capacity = getCapacityFromDB();
		setMaxAssistantNumber(capacity);
	}
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
	private int getCapacityFromDB(){
		return dbh.getCourseCapacity(this);
	}
	public void addCourseToDatabase() {
		// TODO Auto-generated method stub
		dbh.addCourseToDatabase(this);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.code + " " + this.number + " [Max Assistant request: "+maxAssistantNumber +"]" ;
	}
}
