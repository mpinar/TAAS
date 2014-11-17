package Helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

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


	private void getCoursesFromWorksheet(XSSFSheet coursesExcel) {
		
		Iterator<Row> coursesRowIterator = coursesExcel.iterator();
		Row dummyRow = coursesRowIterator.next();
		while(coursesRowIterator.hasNext()){

			Row row = coursesRowIterator.next();

			Cell codeCell = row.getCell(0);
			Cell numberCell = row.getCell(1);
			Cell capacityCell = row.getCell(2);

			String code = codeCell.getStringCellValue();
			int number = (int) numberCell.getNumericCellValue();
			int capacity = (int) capacityCell.getNumericCellValue();

			Course course = new Course(code, number, capacity);
		}
	}

	public void getInstructorsFromWorksheet(XSSFSheet instructorExcel){
		// TODO hala yapilacak seyler var
		
		Iterator<Row> instructorRowIterator = instructorExcel.iterator();
		Row dummyRow = instructorRowIterator.next();

		while(instructorRowIterator.hasNext()){

			Row row = instructorRowIterator.next();

			Cell nameCell = row.getCell(0);
			Cell surnameCell = row.getCell(1);
			Cell mailCell = row.getCell(2);
			Cell deptCell = row.getCell(3);


			String name = nameCell.getStringCellValue();
			String surname = surnameCell.getStringCellValue();
			String mail = mailCell.getStringCellValue();
			String dept = deptCell.getStringCellValue();

			if(!(name.isEmpty() || surname.isEmpty() || mail.isEmpty() || dept.isEmpty() )){
				Department d = new Department(dept);
				Instructor ins = new Instructor(name, surname, mail, d);
				if(!ins.checkInstructorIsInDB()){ // Instructor is not in the database. Add it.
					//createRandomPassword 
					MailHelper mh = new MailHelper();
					String rp =  mh.generateRandomPassword();
					ins.addToDB(rp);
					mh.sendEmail(ins,rp);
				}
			}else
			{
				System.out.println("Error on entry. \nName : "+name+"\nSurname : "+surname+"\nMail : "+mail+"\nDepartment_ID : "+dept);
				System.out.println();
			}
		}

	}

	public void getAssistantsFromWorksheet(XSSFSheet assistantExcel){


		Iterator<Row> assistantRowIterator = assistantExcel.iterator();
		Row dummyRow = assistantRowIterator.next();
		while(assistantRowIterator.hasNext()){

			Row row= assistantRowIterator.next();

			Cell nameCell = row.getCell(0);
			Cell surnameCell = row.getCell(1);
			Cell mailCell = row.getCell(2);
			Cell deptCell = row.getCell(3);

			String name = nameCell.getStringCellValue();
			String surname = surnameCell.getStringCellValue();
			String mail = mailCell.getStringCellValue();
			String dept = deptCell.getStringCellValue();

			System.out.println(name);

			Department d = new Department(dept);
			Assistant asst = new Assistant(name, surname, mail, d);
		}

	}
}
