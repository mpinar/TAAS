package Model;

import Helper.DatabaseHelper;

public class Course {

	public int id;
	public String code;
	public int number;
	public int capacity;
	public int maxAssistantNumber;
	public String instrReq;
	DatabaseHelper dbh; 

	public Course(String code, int num){
		this.code = code;
		number = num;
		dbh = new DatabaseHelper();
		capacity = getCapacityFromDB();
		setID();
		setMaxAssistantNumber(capacity);
		updateMaxAssistantCount();
	}
	public Course(String c, int n, int capacity) { 
		code = c;
		number = n;
		this.capacity = capacity;
		setID();
		setMaxAssistantNumber(capacity);
		updateMaxAssistantCount();
		dbh = new DatabaseHelper();
	}


	public Course(String code, int number, int capacity, int maxasst) {
		// TODO Auto-generated constructor stub
		this.code = code;
		this.number = number;
		this.capacity = capacity;
		this.maxAssistantNumber = maxasst;
		
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
	public void setID() {
		// TODO Auto-generated method stub
		id = dbh.getCourseID(this);
	}
	public void updateMaxAssistantCount() {
		// TODO get max asst count subtract request number
		
		int max = this.maxAssistantNumber;
		int reqCount = dbh.getRequestCountForCourse(this);
		this.maxAssistantNumber = max - reqCount;
		
	}
	
	//sadsadsa
}
