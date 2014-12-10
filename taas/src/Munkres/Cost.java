package Munkres;

import java.util.ArrayList;

import Helper.DatabaseHelper;
import Model.*;

public class Cost {

	public DatabaseHelper dbh = new DatabaseHelper();

	ArrayList<Assistant> allAssistants;
	ArrayList<Course> allCourses; 
	int asstSize= 0,coursesSize = 0;
	double[][] costMatrix;

	public Cost(ArrayList<Assistant> allAssistants, ArrayList<Course> allCourses) {

		this.allAssistants = allAssistants;
		this.allCourses = allCourses;
		asstSize = allAssistants.size();
		coursesSize = allCourses.size();
		costMatrix = new double[asstSize][coursesSize];
	}




	public double[][] calculateCost(){

		int asstCount = 0;
		int courseCount = 0;
		for (Assistant assistant : allAssistants) {

			for (Course course : allCourses) {

					course.setID();
					int cost = 0;

					cost += checkDepartment(assistant, course);
					cost += checkTeachingBackground(assistant, course);
					cost += checkAdvisor(assistant, course); //TODO advisor olayini cozmek lazim
					cost += checkAcademicBackground(assistant, course);
					cost += checkInstructorRequest(assistant, course);


					costMatrix[asstCount][courseCount] = cost;
					courseCount++;
				}
			
			asstCount++;
			courseCount = 0;
		}

		printCostMatrix();
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
		ArrayList<String> background = dbh.getTeachingBackgroundofAssistant(assistant);

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

		ArrayList<String> background = dbh.getBackgroundofAssistant(assistant);
		ArrayList<String> requests = dbh.getBackgroundRequestFromCourse(course);

		if(requests != null){
			for (int i = 0; i < background.size(); i++) {
				for (int j = 0; j < requests.size(); j++) {

					if (background.get(i).equalsIgnoreCase(requests.get(j))) {
						result -= 2;	
					}
				}
			}
		}
		return result;
	}

	public int checkInstructorRequest(Assistant assistant, Course course){
		int result =0;
		ArrayList<Instructor>  teachers = course.getTeachingInstructor();
		ArrayList<Request> requests  = new ArrayList<Request>();
		for (Instructor instructor : teachers) {
		
			requests.addAll(course.getRequests(instructor));
		}
		
		
		if (!requests.isEmpty()){
			
			for (Request r : requests) {
				
				if (r.assistantID == assistant.id){
					result -= 15;
				}
			}
			
		}
		return result;
		
	}
	
	private void printCostMatrix(){
		
		for (int i = 0; i < costMatrix.length; i++) {
			System.out.println(allAssistants.get(i));
			for (int j = 0; j < costMatrix[i].length; j++) {
				
				System.out.print(costMatrix[i][j] + "  ");
				System.out.println(allCourses.get(j));
			}
			System.out.println();
			
		}
	}
}
