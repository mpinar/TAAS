package Model;

import java.util.ArrayList;

public class Assistant {

	String name;
	String surname;
	String mail;
	Department department;
	Instructor advisor;
	ArrayList<String> background;
	ArrayList<Course> teachingBackground;
	
	
	public Assistant(String n, String s, String m, Department d){
		name = n;
		surname = s;
		mail = m;
		department = d;
	}
}
