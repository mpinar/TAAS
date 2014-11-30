package Helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Model.Assistant;
import Model.Course;
import Model.Department;
import Model.Instructor;


public class ExcelHelper {
	static String chosenFile = "TAAS EXCEL.xlsx";

	XSSFSheet instructors;
	XSSFSheet assistants;
	XSSFSheet courses;

	public int year;
	public String semester;

	public ExcelHelper(String file){
		try {
			FileInputStream excelFile = new FileInputStream(chosenFile);
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);


			instructors = workbook.getSheet("Instructors");
			assistants = workbook.getSheet("Assistants");
			courses = workbook.getSheet("Courses");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	public void setYearAndSemester(int y, String s){
		year = y;
		semester = s;

	}

	public void getCoursesFromWorksheet() {

		if(courses != null){
			Iterator<Row> coursesRowIterator = courses.iterator();
			Row dummyRow = coursesRowIterator.next();
			while(coursesRowIterator.hasNext()){

				Row row = coursesRowIterator.next();

				Cell codeCell = row.getCell(0);
				Cell numberCell = row.getCell(1);
				Cell capacityCell = row.getCell(2);

				String code = codeCell.getStringCellValue();
				int number = (int) numberCell.getNumericCellValue();
				int capacity = (int) capacityCell.getNumericCellValue();

				if(!code.isEmpty()){
					Course course = new Course(code, number, capacity);
					course.addCourseToDatabase();
					
				}

			}
		}
	}
	
	public void getInstructorsFromWorksheet(){
		// TODO hala yapilacak seyler var

		Iterator<Row> instructorRowIterator = instructors.iterator();
		Row dummyRow = instructorRowIterator.next();

		while(instructorRowIterator.hasNext()){

			Row row = instructorRowIterator.next();

			Cell nameCell = row.getCell(0);
			Cell surnameCell = row.getCell(1);
			Cell mailCell = row.getCell(2);
			Cell deptCell = row.getCell(3);
			Cell coursesCell = row.getCell(4);

			String name = nameCell.getStringCellValue();
			String surname = surnameCell.getStringCellValue();
			String mail = mailCell.getStringCellValue();
			String deptCode = deptCell.getStringCellValue();
			String teaches = coursesCell.getStringCellValue();

			if(!(name.isEmpty() || surname.isEmpty() || mail.isEmpty() || deptCode.isEmpty() )){
				Department d = new Department(deptCode);
				Instructor ins = new Instructor(name, surname, mail, d);
				if(!ins.checkInstructorIsInDB()){ // Instructor is not in the database. Add it.
					//createRandomPassword 
					MailHelper mh = new MailHelper();
					String rp =  mh.generateRandomPassword();
					ins.addToDB(rp);

					// course handling might change
					//handle teaching information of the instructor. 
					teaches = teaches.replaceAll(", ",",");
					String[] t = teaches.split(",");

				
					for (String s : t) {
						Course c = createCourseFromString(s);
						ins.teaches.add(c);
					}
			
					
					ins.addTeachingInfoToDB();
					mh.sendEmail(ins,rp);
				}else{
					 // The instructor is in the database
					// update teaches and request information
//
//					ins.teaches.add(parseCourseName(teaches));
//					ins.addTeachingInfoToDB();
				}
			}else{
				
			}
		}

	}

	public void getAssistantsFromWorksheet(){


		Iterator<Row> assistantRowIterator = assistants.iterator();
		Row dummyRow = assistantRowIterator.next();
		while(assistantRowIterator.hasNext()){

			Row row= assistantRowIterator.next();

			Cell nameCell = row.getCell(0);
			Cell surnameCell = row.getCell(1);
			Cell mailCell = row.getCell(2);
			Cell deptCell = row.getCell(3);
			Cell advisorCell = row.getCell(4);
			Cell backgroundCell = row.getCell(5);
			Cell teachingBackCell = row.getCell(6);

			String name = nameCell.getStringCellValue();
			String surname = surnameCell.getStringCellValue();
			String mail = mailCell.getStringCellValue();
			String dept = deptCell.getStringCellValue();
			String advMail = advisorCell.getStringCellValue();
			String background = backgroundCell.getStringCellValue();
			String teachingBack = teachingBackCell.getStringCellValue();

			if(!mail.isEmpty()){
				Department d = new Department(dept);
				Assistant asst = new Assistant(name, surname, mail, d);
			
				// handle background

				background = background.replaceAll(", ",",");
				asst.setRawBG(background);
				asst.background  = convertBackgroundToList(background);

				teachingBack = teachingBack.replace(", ",",");
				asst.setRawTBG(teachingBack);
				asst.teachingBackground = convertBackgroundToList(teachingBack);
				
				asst.isActive = true;
				asst.addAsstToDB();
				asst.setAdvisorFromMail(advMail);
			}
		}

	}

	private ArrayList<String> convertBackgroundToList(String in){
		ArrayList<String> al = new ArrayList<String>();
		String[] arr = in.split(",");
		for (int i = 0; i < arr.length; i++) {
			String s = arr[i];
			al.add(s);
		}
		
		return al;

	}
	
	private Course createCourseFromString(String input){


		Course c = null;

		String[] arr = input.split(" ",2);

		String code = arr[0];
		int number = Integer.parseInt(arr[1]);
		c = new Course(code, number);
		return c;

	}
}
