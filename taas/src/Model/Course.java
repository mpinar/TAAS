package Model;

public class Course {

	
	String code;
	int number;
	int capacity;
	int maxAssistantNumber;
	
	public Course(String c, int n, int capacity) { 
		code = c;
		number = n;
		this.capacity = capacity;
		setMaxAssistantNumber(capacity);
	}
	
	
	private void setMaxAssistantNumber(int c) {
		
		maxAssistantNumber = c % 20;
		
		
	}
}
