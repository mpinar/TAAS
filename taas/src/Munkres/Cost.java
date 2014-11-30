package Munkres;

import java.util.ArrayList;

import Model.Assistant;
import Model.Course;

public class Cost {

	ArrayList<Assistant> allAssistants;
	ArrayList<Course> allCourses; 
	
	double[][] costMatrix;
	
	public Cost(ArrayList<Assistant> allAssistants, ArrayList<Course> allCourses) {

		this.allAssistants = allAssistants;
		this.allCourses = allCourses;
	}

	
	public double[][] calculateCost(){
		
		for (Assistant assistant : allAssistants) {
			for (Course course : allCourses) {
				
			}
		}
		
		return costMatrix;
	}
}
