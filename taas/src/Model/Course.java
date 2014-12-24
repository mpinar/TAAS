package Model;

import java.util.ArrayList;

import com.sun.org.apache.xpath.internal.operations.Equals;

import Helper.DatabaseHelper;

public class Course {

	public int id;
	public String code;
	public int number;
	public int capacity;
	public int maxAssistantNumber;
	public String instrReq;
	DatabaseHelper dbh = new DatabaseHelper();

	public Course(String code, int num){
		this.code = code;
		number = num;
		dbh = new DatabaseHelper();
		capacity = getCapacityFromDB();
		setID();
		setMaxAssistantNumber(capacity);
		updateMaxAssistantCount();
	}

	// use this only adding course to db
	public Course(String c, int n, int capacity) { 
		code = c;
		number = n;
		this.capacity = capacity;
		setMaxAssistantNumber(capacity);
		dbh = new DatabaseHelper();
		setID();
	}


	public Course(String code, int number, int capacity, int maxasst) {
		// TODO Auto-generated constructor stub
		this.code = code;
		this.number = number;
		this.capacity = capacity;
		this.maxAssistantNumber = maxasst;
		setID();

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

		int max = dbh.getMaxAssistantCountForCourse(this);
		int reqCount = dbh.getRequestCountForCourse(this);
		this.maxAssistantNumber = max - reqCount;

	}

	public ArrayList<Request> getRequests(Instructor instructor) {
		// TODO Auto-generated method stub

		return dbh.getRequests(this,instructor);

	}

	public ArrayList<Instructor>  getTeachingInstructor() {
		// TODO Auto-generated method stub
		return dbh.getInstructorForCourse(this);
	}
	@Override
	public boolean equals(Object o){
		if (o == null) return false;
		if (!(o instanceof Course)) return false;

		Course c1 = (Course) o;

		return c1.code.equalsIgnoreCase(this.code) && (c1.number == this.number);

	}
	@Override
	public int hashCode(){
		
		return (int) this.id;
	}
}
