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

				XSSFSheet coursesExcel = workbook.getSheet("Courses");
				XSSFSheet instructorExcel = workbook.getSheet("Instructors");
				XSSFSheet assistantExcel = workbook.getSheet("Assistants");

				Iterator<Row> coursesRowIterator = coursesExcel.iterator();
				Iterator<Row> instructorRowIterator = instructorExcel.iterator();
				Iterator<Row> assistantRowIterator = assistantExcel.iterator();


				Row bosaRow = instructorRowIterator.next();
				while(instructorRowIterator.hasNext()){

					Row row = instructorRowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();

					Cell nameCell = row.getCell(0);
					Cell surnameCell = row.getCell(1);
					Cell mailCell = row.getCell(2);
					Cell deptCell = row.getCell(3);


					String name = nameCell.getStringCellValue();
					String surname = surnameCell.getStringCellValue();
					String mail = mailCell.getStringCellValue();
					String dept = deptCell.getStringCellValue();
					
					System.out.println(name + "\n");
					Department d = new Department(dept);
					Instructor ins = new Instructor(name, surname, mail, d);


				}


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
//}