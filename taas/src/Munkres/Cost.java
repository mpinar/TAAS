package Munkres;

import java.util.ArrayList;

import Helper.DatabaseHelper;
import Model.Assistant;
import Model.Course;
import Model.Instructor;

public class Cost {
	
	public DatabaseHelper dbh = new DatabaseHelper();

	ArrayList<Assistant> allAssistants;
	ArrayList<Course> allCourses; 

	double[][] costMatrix = new double[9999][9999];

	public Cost(ArrayList<Assistant> allAssistants, ArrayList<Course> allCourses) {

		this.allAssistants = allAssistants;
		this.allCourses = allCourses;
	}


	public double[][] calculateCost(){
		
		int asstCount = 0;
		int courseCount = 0;
		for (Assistant assistant : allAssistants) {
			
			for (Course course : allCourses) {

				int cost = 0;
				
				cost += checkDepartment(assistant, course);
				cost += checkTeachingBackground(assistant, course);
				//cost += checkAdvisor(assistant, course); //TODO advisor olayini cozmek lazim
				cost += checkAcademicBackground(assistant, course);
				
				
				costMatrix[asstCount][courseCount] = cost;
				courseCount++;
			}
			asstCount++;
		}

		return costMatrix;
	}

	public int checkDepartment(Assistant assistant, Course course){
		String assistantDept = assistant.department.code;
		String courseDept = course.code;
		int result = 0;

		switch (courseDept) {
		case "COMP":
			switch (assistantDept) {
			case "COMP":
				result = 0;
				break;

			case "ELEC":
				result = 2;
				break;

			case "INDR":
				result = 4;
				break;

			case "CHBI":
				result = 5;
				break;

			case "MECH":
				result = 3;
				break;

			default:
				break;
			}

			break;

		case "ELEC":
			
			switch (assistantDept) {
			case "COMP":
				result = 3;
				break;

			case "ELEC":
				result = 0;
				break;

			case "INDR":
				result = 4;
				break;

			case "CHBI":
				result = 5;
				break;

			case "MECH":
				result = 3;
				break;

			default:
				break;
			}

			break;

		case "INDR":
			
			switch (assistantDept) {
			case "COMP":
				result = 2;
				break;

			case "ELEC":
				result = 3;
				break;

			case "INDR":
				result = 0;
				break;

			case "CHBI":
				result = 3;
				break;

			case "MECH":
				result = 3;
				break;

			default:
				break;
			}

			break;

		case "CHBI":
			
			switch (assistantDept) {
			case "COMP":
				result = 4;
				break;

			case "ELEC":
				result = 5;
				break;

			case "INDR":
				result = 4;
				break;

			case "CHBI":
				result = 0;
				break;

			case "MECH":
				result = 3;
				break;

			default:
				break;
			}

			break;

		case "MECH":
			
			switch (assistantDept) {
			case "COMP":
				result = 5;
				break;

			case "ELEC":
				result = 3;
				break;

			case "INDR":
				result = 4;
				break;

			case "CHBI":
				result = 3;
				break;

			case "MECH":
				result = 0;
				break;

			default:
				break;
			}

			break;

		default:
			break;
		}

		return result;

	}
	
	public int checkTeachingBackground(Assistant assistant, Course course){
		
		int result = 0;
		ArrayList<String> background = assistant.teachingBackground;
		
		String fullCourseName = course.code + " " + course.number;
		boolean isAssisted = false;
		if(background.size()>0){
			result -= 2;
		}else if(background.size() == 0){
			result += 5;
		}
		for (int i = 0; i < background.size(); i++) {
			String c = background.get(i);
			if (fullCourseName.equalsIgnoreCase(c)) {
				isAssisted = true;
			}
		}
		if(isAssisted){
			result -= 3;
		}else{
			result += 3;
		}
		
		return result;
		
	}

	public int checkAdvisor(Assistant assistant, Course course){
		
		int cost = 0;
		String advMail = assistant.advisor.mail;
		Instructor ins = dbh.getInstructorFromCourseID(course);
		String insMail = ins.mail;
		
		if (advMail.equalsIgnoreCase(insMail)) {
			cost = -2;
		}
		
		return cost;
		
	}

	public int checkAcademicBackground(Assistant assistant, Course course){
		
		int result = 0;
		
		ArrayList<String> background = assistant.background;
		ArrayList<String> requests = dbh.getBackgroundRequestFromCourse(course);
		
		for (int i = 0; i < background.size(); i++) {
			for (int j = 0; j < requests.size(); j++) {
				
				if (background.get(i).equalsIgnoreCase(requests.get(j))) {
					result -= 2;	
				}else{
					result += 5;
				}
			}
		}
		
		return result;
	}
}
