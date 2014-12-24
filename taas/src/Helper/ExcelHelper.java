package Helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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


	XSSFSheet instructors;
	XSSFSheet assistants;
	XSSFSheet courses;

	public int year;
	public String semester;


	public ArrayList<Assistant> allAssistants = new ArrayList<Assistant>();
	public ArrayList<Course> allCourses = new ArrayList<Course>();

	public ExcelHelper(String file){
		try {
			FileInputStream excelFile = new FileInputStream(file);
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);


			instructors = workbook.getSheet("Instructors");
			assistants = workbook.getSheet("Assistants");
			courses = workbook.getSheet("Courses");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public ExcelHelper(){

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
				Cell capacityCell = row.getCell(1);

				String code = codeCell.getStringCellValue();
				String[] coursecodenumber = code.split(" ");
				String ccode = coursecodenumber[0];
				int number = Integer.parseInt(coursecodenumber[1]); // TODO numarasiz derslerin chek olayi
				int capacity = (int) capacityCell.getNumericCellValue();


				if(!code.isEmpty()){
					Course course = new Course(ccode, number, capacity);
					course.addCourseToDatabase();
					allCourses.add(course);
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
			Cell mailCell = row.getCell(1);
			Cell deptCell = row.getCell(2);
			Cell coursesCell = row.getCell(3);

			String wholeName = nameCell.getStringCellValue();

			String[] splitted = wholeName.split(" ");
			String name = "";
			for (int i = 0; i < splitted.length - 1; i++) {
				String s = splitted[i];
				name +=s+" ";
			}
			String surname = splitted[splitted.length - 1];
			String mail = mailCell.getStringCellValue();
			String deptCode = deptCell.getStringCellValue();
			String teaches = coursesCell.getStringCellValue();


			if(!(name.isEmpty() || surname.isEmpty() || mail.isEmpty() || deptCode.isEmpty() )){
				Department d = new Department(deptCode);
				Instructor ins = new Instructor(name, surname, mail, d);
				if(!ins.checkInstructorIsInDB()){ // Instructor is not in the database. Add it.
					//createRandomPassword 
					MailHelper mh = new MailHelper();

					String rp = EncryptionHelper.generatePass("test");
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
					//		mh.sendEmail(ins,rp);
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
			Cell mailCell = row.getCell(1);
			Cell deptCell = row.getCell(2);
			Cell advisorCell = row.getCell(3);
			Cell backgroundCell = row.getCell(4);
			Cell teachingBackCell = row.getCell(5);
			Cell kusisIDCell = row.getCell(6);

			String wholeName = nameCell.getStringCellValue();

			String[] splitted = wholeName.split(" ");
			String name = "";
			for (int i = 0; i < splitted.length - 1; i++) {
				String s = splitted[i];
				name +=s+" ";
			}

			String surname = splitted[splitted.length - 1];
			String mail = mailCell.getStringCellValue();
			String dept = deptCell.getStringCellValue();
			String advMail = advisorCell.getStringCellValue();
			String background = backgroundCell.getStringCellValue();
			String teachingBack = teachingBackCell.getStringCellValue();
			int kusisID = Integer.parseInt(kusisIDCell.getStringCellValue());


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
				if (!advMail.isEmpty()){
					advMail = advMail.replaceAll("; ", ",");

					String[] advisors = advMail.split(",");
					if(advisors.length > 1){
						for (int i = 0; i < advisors.length; i++) {
							asst.setAdvisorFromMail(advisors[i]);
						}

					}else if(advisors.length == 1){
						asst.setAdvisorFromMail(advMail);
					}
				}
				allAssistants.add(asst);

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
		if(arr[1].length() == 3){
			int number = Integer.parseInt(arr[1]);
			c = new Course(code, number);
		}else{ // course bi garip bunlarin kodu 999 olsun
			int number = 999;
			c = new Course(code, number);
		}

		return c;

	}

	public void createOutputExcel(ArrayList<Course> course, ArrayList<Assistant> assistant, int[][] assignment){

		XSSFWorkbook output = new XSSFWorkbook();
		XSSFSheet outSheet = output.createSheet("Result");

		Row infoRow = outSheet.createRow(0);
		Cell infoCell = infoRow.createCell(0);
		Cell infoCell2 = infoRow.createCell(1);
		Cell infoCell3 = infoRow.createCell(2);

		infoCell.setCellValue("Name");
		infoCell2.setCellValue("Mail");
		infoCell3.setCellValue("Class");
		int rowNum =1;
		System.out.println("number of assg: "+assignment.length);
		for (int i=0; i<assignment.length; i++)
		{
			int courseIndex=0;
			int asstIndex =0;
			if(course.size() < assistant.size()){
				courseIndex = assignment[i][0];
				asstIndex = assignment[i][1];
			}else{
				asstIndex = assignment[i][0];
				courseIndex = assignment[i][1];
			}
			System.out.println("AsstIndex :" +asstIndex +"\nCourseindex: "+courseIndex);

			Assistant asst = assistant.get(asstIndex);
			Course cour = course.get(courseIndex);

			Row outRow = outSheet.createRow(rowNum);
			Cell nameCell = outRow.createCell(0);
			Cell mailCell = outRow.createCell(1);
			Cell classCell = outRow.createCell(2);

			String firstName = asst.name;
			String surname = asst.surname;
			String name = firstName + " " + surname;
			String mail = asst.mail;

			String courseName = cour.code;
			int courseNumber = cour.number;
			String className = courseName + " " + courseNumber;

			nameCell.setCellValue(name);
			mailCell.setCellValue(mail);
			classCell.setCellValue(className);
			System.out.println(name+"("+mail+")"+ "---->"+ className);
			rowNum++;

		}
		FileOutputStream out;
		try {
			out = new FileOutputStream(new File("Assistants.xlsx"));
			output.write(out);
			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}
