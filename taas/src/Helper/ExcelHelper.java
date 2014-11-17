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

import Model.Course;
import Model.Department;
import Model.Instructor;


public class ExcelHelper {
	static String chosenFile = "TAAS EXCEL.xlsx";


	public static void main(String []args){
		/**
		 * Gets the excel and parses it
		 * @param choosenfile
		 */


		//public void getDataFromExcelFile(String chosenFile){
		try {
			FileInputStream file = new FileInputStream(chosenFile);
			XSSFWorkbook workbook = new XSSFWorkbook(file);


			XSSFSheet instructorExcel = workbook.getSheet("Instructors");
			XSSFSheet assistantExcel = workbook.getSheet("Assistants");

			ExcelHelper.getInstructorsFromWorksheet(instructorExcel);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}



	private void getCoursesFromWorksheet(XSSFWorkbook workbook) {
		XSSFSheet coursesExcel = workbook.getSheet("Courses");
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

	public static void getInstructorsFromWorksheet(XSSFSheet instructorExcel){

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

}
//}
