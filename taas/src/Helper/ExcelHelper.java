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

					//handle teaching information of the instructor.
					String[] t = teaches.split(",");
					ArrayList<Course> coursesTeaching = new ArrayList<Course>();
					for(int i = 0; i<t.length; i++){
						Course c = getCourseInformationForInstructor(t[i]);
						coursesTeaching.add(c);
					}
					ins.teaches = coursesTeaching;
					ins.addTeachingInfoToDB();
					mh.sendEmail(ins,rp);
				}
			}else
			{
				//				System.out.println("Error on entry. \nName : "+name+"\nSurname : "+surname+"\nMail : "+mail+"\nDepartment_ID : "+deptCode);
				//				System.out.println();
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
				List<String> bgl = Arrays.asList(background.split(","));
				String[] arr = teachingBack.split(",");
				ArrayList<String> tb = new ArrayList<String>();
				for(int i = 0; i<arr.length; i++){
					String s = parseString(arr[i]);
					tb.add(s);
				}
				
				ArrayList<String> bg = new ArrayList<String>();
				for (Iterator iterator = bgl.iterator(); iterator.hasNext();) {
					String s = (String) iterator.next();
					bg.add(s);
					
				}
				asst.background = bg;
				asst.teachingBackground = tb;
				asst.isActive = true;
				asst.rawBG = background;
				asst.rawTB = teachingBack;
				asst.addAsstToDB();
				asst.setAdvisorFromMail(advMail);
			}
		}

	}

	private String parseString(String in){
		String s ="";
		String[] arr = in.split(" ",2);
		if(arr == null)
		{
			s = in;
		}
		s = arr[0] +" "+arr[1];
		return s;
	}
	private Course getCourseInformationForInstructor(String input){

		Course c = null;

		String[] arr = input.split(" ",2);

		String code = arr[0];
		int number = Integer.parseInt(arr[1]);
		c = new Course(code, number);


		return c;

	}
}
