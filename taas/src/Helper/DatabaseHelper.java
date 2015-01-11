/*
 * Created by Deniz Oztreves & Mert Pinar
 * Copyright (c) 2014-2015 Deniz Oztreves & Mert Pinar. All rights reserved
 * 
 */


package Helper;

/**
 * Database connection helper. This class contains SQL connections between the program and the database.
 * It handles all of the necessary queries in the project.
 * @author denizoztreves
 *
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

import Model.*;

public class DatabaseHelper {

	//Database credentials.
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3306/";
	static final String DB_NAME = "TAAS-DB";
	static final String DB_UNAME = "TAAS"; 
	static final String DB_PASS = "SAAT";

	public int year;
	public String semester;
	// Encryption properties
	private EncryptionHelper bcrypt = null;

	public DatabaseHelper(){

		// Initialize the encyrptor object
		bcrypt = new EncryptionHelper();
		year = TimeStorage.year;
		semester = TimeStorage.semester;
	}

	private Connection connectToDatabase() throws InstantiationException, IllegalAccessException{

		Connection conn = null;

		try {
			Class.forName(JDBC_DRIVER).newInstance();

			conn = DriverManager.getConnection(DB_URL+DB_NAME+"?useUnicode=true&characterEncoding=utf8", DB_UNAME, DB_PASS);

		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;

	}



	/**
	 * @category DB Transaction
	 * @param username
	 * @return
	 */

	private String getEncryptedPasswordFromDB(String username){
		Connection c;
		String res = "";
		try {
			c = connectToDatabase();
			String sql = "Select Instructor.*, count(ID) as count from Instructor where mail=?"; // TODO

			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, username);

			ResultSet rs = ps.executeQuery();


			while(rs.next()) {
				if(rs.getInt("count") == 1)
					res = rs.getString("password");
				else
					System.out.println("ERROR: More than 1 user with this email.");
			}
			c.close();
		} catch (InstantiationException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}

		return res;

	}

	/**
	 *  Authirizes the user from login page
	 *  @category Action
	 * @param username
	 * @param plainPassword
	 * @return 
	 */

	public boolean authorizeUser(String username, String plainPassword){
		boolean result = false;
		// get the username from db
		// check if it is equal
		// check passwords
		String encryptedPassFromDB = getEncryptedPasswordFromDB(username);

		if (!username.isEmpty() && !encryptedPassFromDB.isEmpty())
		{	
			if(bcrypt.checkpw(plainPassword, encryptedPassFromDB)){
				result = true;
			}
		}


		return result;
	}


	public Department getDepartmentInformation(String code){

		Connection c;
		Department dept = null;
		try {
			c = connectToDatabase();
			String sql = "Select * from Department where code=?";

			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, code);

			ResultSet result = ps.executeQuery();

			while(result.next()) {
				String n = result.getString("department_name");
				String cc = result.getString("code");
				String f = result.getString("faculty");

				dept = new Department(n, cc, f);
			}

			c.close();
		} catch (InstantiationException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}

		return dept;

	}



	/**
	 * Returns the user after authorization from it's username
	 * @param username
	 * @return
	 */

	public Instructor getAuthorizedInstructor(String username){   // TODO

		Connection c;
		Instructor ins = null;
		try {
			c = connectToDatabase();
			String sql = "select count(id) as count, Instructor.*  from Instructor where mail=?";

			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, username);

			ResultSet rs = ps.executeQuery();

			int count = 1 ; // rs.getInt(1);

			if(count > 1){
				System.err.println("ERROR:Bu mail ile birden fazla kullanici var");
				System.exit(1);
			}else{
				while(rs.next()){

					String fname = rs.getString("name");
					String lname = rs.getString("surname");
					String mail = rs.getString("mail");
					String dcode = rs.getString("department_code");
					Department  d = getDepartmentInformation(dcode);
					ins = new Instructor(fname, lname, mail, d);

				}
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}

		return ins;
	}



	public void updatePassword(int pID, String password){
		Connection c;

		try{
			c = connectToDatabase();
			String sql= "UPDATE Person SET password= '?' where ID = ? ";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, password);
			ps.setInt(2, pID);

			ps.executeQuery();
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}


	}


	public ArrayList<Department> getAllDepartments(){
		Connection c;
		ArrayList<Department> result = new ArrayList<Department>();

		try{
			c = connectToDatabase();
			String sql= "Select * from department";
			PreparedStatement ps = c.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){

				String n = rs.getString("department_name");
				String code = rs.getString("code");
				String f = rs.getString("faculty");

				Department dept = new Department(n, code, f);
				result.add(dept);
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<Assistant> getAllAssistants(){

		ArrayList<Assistant> result = new ArrayList<Assistant>();
		Connection c;
		try{
			c = connectToDatabase();
			String sql= "select * from assistant"; // 
			PreparedStatement ps = c.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				String dept = rs.getString("department_code");
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String mail = rs.getString("mail");
				Department d = getDepartmentInformation(dept);

				Assistant asst = new Assistant(name,surname,mail,d);
				asst.background = getBackgroundofAssistant(asst);
				asst.teachingBackground = getTeachingBackgroundofAssistant(asst);
				asst.advisor = getAdvisorForAssistant(asst);
				result.add(asst);
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return result;
	}

	private Instructor getAdvisorForAssistant(Assistant asst) {
		// TODO Auto-generated method stub
		Connection c;
		Instructor i = null;
		try{

			c= connectToDatabase();

			String sql = "Select *,count(instructor.id) as count from instructor join advisor on instructor_id = instructor.id where assistant_id = ?";

			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, asst.id);		
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				if(rs.getInt("count") == 1){
					String n = rs.getString("name");
					String s = rs.getString("surname");
					String m = rs.getString("mail");
					Department d = getDepartmentInformation(rs.getString("department_code"));

					i = new Instructor(n, s, m, d);
				}
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}
		return i;
	}

	public ArrayList<Instructor> getAllInstructors(){

		Connection c; 
		ArrayList<Instructor> result = new ArrayList<Instructor>();

		try{

			c = connectToDatabase();
			String sql = "Select * from Instructor";
			PreparedStatement ps = c.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){

				String n = rs.getString("name");
				String s = rs.getString("surname");
				String m = rs.getString("mail");
				Department d = getDepartmentInformation(rs.getString("department_code"));

				Instructor i = new Instructor(n, s, m, d);
				result.add(i);
			}
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<Course> getAllCourses(){

		Connection c; 
		ArrayList<Course> result = new ArrayList<Course>();

		try{

			c = connectToDatabase();
			String sql = "Select * from Course";
			PreparedStatement ps = c.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){

				String code = rs.getString("Department_code");
				int n = rs.getInt("number");
				int capacity = rs.getInt("capacity");
				int maxAsst = rs.getInt("maxAssistantNumber");


				Course i = new Course(code, n, capacity, maxAsst);
				result.add(i);
			}
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<String> getMoreDeptInformation(String code) {
		// TODO Auto-generated method stub
		Connection c; 
		ArrayList<String> result = new ArrayList<String>();

		try{

			c = connectToDatabase();
			String sql = "Select count(code) as count,Department.* from Department where code =? ";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, code);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				if(rs.getInt("count") == 1){

					String n = rs.getString("department_name");
					String f = rs.getString("faculty");

					result.add(n);
					result.add(f);
				}
			}
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return result;
	}

	public void addInstructorToDatabase(Instructor instructor, String randomPass) {
		// TODO Auto-generated method stub
		Connection c;

		try{
			c = connectToDatabase();
			String sql = "insert into instructor (name,surname,mail,password,department_code) Values (?,?,?,?,?)";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, instructor.name);
			ps.setString(2, instructor.surname);
			ps.setString(3, instructor.mail);
			//String password = bcrypt.generatePass(randomPass);
			ps.setString(4, randomPass);
			ps.setString(5, instructor.department.code);

			ps.executeUpdate();
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

	}

	public void addCourseToDatabase(Course course) {
		// TODO Auto-generated method stub

		Connection c;
		try{	
			c=connectToDatabase();
			String sql = "insert into course (department_code, number, capacity, maxAssistantNumber,year,semester) values (?,?,?,?,?,?);";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1,course.code);
			ps.setInt(2, course.number);
			ps.setInt(3, course.capacity);
			ps.setInt(4, course.maxAssistantNumber);
			ps.setInt(5, year);
			ps.setString(6, semester);
			ps.executeUpdate();
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

	}

	public void setYearAndSemester(int y, String s){
		year = y;
		semester = s;

	}

	public int getIDofInstructor(Instructor ins){

		Connection c; 
		int id = 0 ;
		try{
			c = connectToDatabase();
			String sql = "Select id from Instructor where mail = ?";

			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, ins.mail);
			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				id = rs.getInt("id");
			}
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return id;

	}

	public void addTeachingInformationForInstructor(Instructor instructor) {
		// TODO Auto-generated method stub
		Connection c; 

		try{
			c = connectToDatabase();
			String sql1 = "select id from course where year = ? and semester= ? and department_code = ? and number = ?"; 
			for(int i=0; i<instructor.teaches.size(); i++){
				PreparedStatement ps = c.prepareStatement(sql1);
				ps.setInt(1, year);
				ps.setString(2, semester);
				ps.setString(3, instructor.teaches.get(i).code);
				ps.setInt(4, instructor.teaches.get(i).number);

				ResultSet rs = ps.executeQuery();

				while(rs.next()){
					int instrID  = getIDofInstructor(instructor);
					String sql2 = "Insert into teaches (Course_ID,Instructor_ID) values (?,?)";
					PreparedStatement ps1 = c.prepareStatement(sql2);
					ps1.setInt(1, rs.getInt("id"));
					ps1.setInt(2, instrID);

					ps1.executeUpdate();
				}

			}
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

	}

	public int getCourseCapacity(Course course) {
		// TODO Auto-generated method stub
		Connection c;
		int capacity = 0;

		try{
			c = connectToDatabase();
			String sql = "Select capacity from course where department_code=? and number=?";
			PreparedStatement ps = c.prepareStatement(sql);

			ps.setString(1, course.code);
			ps.setInt(2, course.number);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				capacity = rs.getInt("capacity");
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return capacity;

	}

	public Instructor getInstructorFromMail(String mail) {
		// TODO Auto-generated method stub
		Instructor i = null;
		Connection c;
		try{
			c = connectToDatabase();
			String sql = "Select * from Instructor where mail =?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, mail);
			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				String n = rs.getString("name");
				String s = rs.getString("surname");
				Department d = getDepartmentInformation(rs.getString("department_code"));
				i = new Instructor(n, s, mail, d);
				i.id = rs.getInt("id");
			}

			c.close();

		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return i;
	}

	public void insertAdvisorInfoForAssistant(Assistant assistant) {
		Connection c;

		try{
			c = connectToDatabase();
			// get assistant id
			String sql="Insert into advisor(assistant_id,instructor_id) values (?,?)";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, assistant.id);
			ps.setInt(2, assistant.advisor.id);

			ps.executeUpdate();
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

	}

	public int getAssistantID(Assistant assistant) {
		// TODO Auto-generated method stub
		int id = 0;
		Connection c;

		try{
			c = connectToDatabase();
			String sql = "Select id from assistant where mail = ?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, assistant.mail);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				id = rs.getInt("id");
			}

			c.close();	
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return id;
	}

	public void addAssistantToDB(Assistant assistant) {
		// TODO Auto-generated method stub
		Connection c;

		try{
			c = connectToDatabase();
			String sql = "Insert into assistant (name,surname,mail,department_code,isActive,background,teachingBackground) values (?,?,?,?,?,?,?)"; 
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, assistant.name);
			ps.setString(2, assistant.surname);
			ps.setString(3, assistant.mail);
			ps.setString(4, assistant.department.code);
			ps.setBoolean(5, assistant.isActive);
			ps.setString(6, assistant.getRawBG());
			ps.setString(7, assistant.getRawTBG());
			ps.executeUpdate();
			c.close();	
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

	}

	public int getInstructorIDfromMail(String mail) {
		// TODO Auto-generated method stub
		Connection c; 
		int id = 0 ;
		try{
			c = connectToDatabase();
			String sql = "Select id from Instructor where mail = ?";
			PreparedStatement ps = c.prepareStatement(sql);

			ps.setString(1, mail);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				id = rs.getInt("id");
			}
			c.close();

		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}


		return id;
	}


	public ArrayList<Course> getTeachingInformationFromInsID(int instrID) {
		// TODO Auto-generated method stub

		Connection c; 
		ArrayList<Course> result = new ArrayList<Course>();
		try{
			c = connectToDatabase();
			String sql = "select * from teaches as t join course as c on t.course_ID = c.ID where instructor_id = ?";

			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, instrID);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){

				String code = rs.getString("department_code");
				int number = rs.getInt("number");
				int capacity =rs.getInt("capacity");
				int maxasst = rs.getInt("maxassistantnumber");

				Course co = new Course(code,number,capacity,maxasst);
				result.add(co);
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return result;

	}

	public boolean saveAdditionalRequestForCourse(Course course,int iID, String additionalReq) {

		// TODO isActive olayi

		Connection c; 

		boolean result = false;
		try{
			c = connectToDatabase();
			String sql = "Select id, count(id) as count from course where department_code=? and number=?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, course.code);
			ps.setInt(2, course.number);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				if(rs.getInt("count")==1){
					int cid = rs.getInt("id");
					sql = "update teaches set backgroundrequest = ? where course_id =? and instructor_id =?";
					ps = c.prepareStatement(sql);
					ps.setString(1, additionalReq);
					ps.setInt(2, cid);
					ps.setInt(3, iID);

					if(1 == ps.executeUpdate()){
						result = true;
					}else{
						result = false;
					}

				}else{
					System.out.println("Error : More than one course with this course"); // isActive olana kadar problem yaratir
				}
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<Assistant> getAssistantsForDepartment(Department department) {
		// TODO Auto-generated method stub

		Connection c;
		ArrayList<Assistant> result = new ArrayList<Assistant>();
		try{
			c= connectToDatabase();
			String sql = "select * from assistant where isActive=? and department_code=?";
			PreparedStatement ps = c.prepareStatement(sql);

			ps.setInt(1, 1);
			ps.setString(2, department.code);
			ResultSet rs = ps.executeQuery();

			while(rs.next()){

				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String mail = rs.getString("mail");
				Department d = new Department(rs.getString("department_code"));
				Assistant a = new Assistant(name, surname, mail, d);
				result.add(a);
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}


		return result;
	}

	public boolean insertAssistantRequest(Instructor instructor,
			Course selectedCourse, Assistant selectedAssistant) {
		// TODO Auto-generated method stub
		boolean result = false;
		Connection c;

		try{
			c = connectToDatabase();

			String sql = "Insert into Request (instructor_id,course_id,assistant_id) values(?,?,?)";

			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, instructor.id);
			ps.setInt(2, selectedCourse.id);
			ps.setInt(3, selectedAssistant.id);

			if(1 ==ps.executeUpdate()){
				result =true;
			}


			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return result;
	}


	public int getCourseID(Course co){

		int result =0;
		Connection c;

		try{
			c = connectToDatabase();

			String sql = "Select id, count(id) as count from course where department_code = ? and number =? and year =? and semester =?"; //TODO isActiv
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, co.code);
			ps.setInt(2, co.number);
			ps.setInt(3,year);
			ps.setString(4, semester);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){

				if(rs.getInt("count") != 1){
					result = 0;

				}else{
					result = rs.getInt("id");
				}
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}
		return result;
	}


	public int getRequestCountForCourse(Course course) {
		// TODO Auto-generated method stub
		Connection c;
		int result = 0;
		try{
			c = connectToDatabase();
			String sql = "Select requestedAsstNumber from Course where id=?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, course.id);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				result = rs.getInt("requestedAsstNumber");
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}
		return result;
	}

	public int getMaxAssistantCountForCourse(Course course) {


		Connection c;
		int result = 0;
		try{
			c = connectToDatabase();
			String sql = "Select maxAssistantNumber from Course where id=?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, course.id);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				result = rs.getInt("maxAssistantNumber");
			}

		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}
		return result;
	}

	public Instructor getInstructorFromCourseID(Course course){

		Connection c;
		Instructor i= null;
		try{
			c = connectToDatabase();
			String sql = "select * from teaches t join instructor i on i.id = t.`Instructor_ID` where Course_ID = ?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, course.id);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				String n = rs.getString("name");
				String s = rs.getString("surname");
				String mail = rs.getString("mail");
				Department d = getDepartmentInformation(rs.getString("department_code"));
				i = new Instructor(n, s, mail, d);
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}
		return i;

	}

	public ArrayList<String> getBackgroundRequestFromCourse(Course course){

		Connection c;
		ArrayList<String> requests = new ArrayList<>();
		try{
			c = connectToDatabase();
			String sql = "select backgroundRequest from teaches where Course_ID = ?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, course.id);

			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				String request = rs.getString("backgroundRequest");

				if(request != null){
					String[] backArr =request.split(", ");
					for (int i = 0; i < backArr.length; i++) {
						requests.add(backArr[i]);
					}
				}else{
					requests = null;
				}
			}

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return requests;

	}

	public ArrayList<String> getBackgroundofAssistant(Assistant assistant){

		Connection c;
		ArrayList<String> requests = new ArrayList<>();
		try{
			c = connectToDatabase();
			String sql = "select background from assistant where id = ?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, assistant.id);

			ResultSet rs = ps.executeQuery();

			String background = null;

			while(rs.next()){
				background = rs.getString("background");


				String[] backArr =background.split(",");
				for (int i = 0; i < backArr.length; i++) {
					requests.add(backArr[i]);
				}


			}
			assistant.setRawBG(background);

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return requests;

	}

	public ArrayList<String> getTeachingBackgroundofAssistant(Assistant assistant){

		Connection c;
		ArrayList<String> requests = new ArrayList<>();
		try{
			c = connectToDatabase();
			String sql = "select teachingBackground from assistant where id = ?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, assistant.id);

			ResultSet rs = ps.executeQuery();

			String background = null;

			while(rs.next()){
				background = rs.getString("teachingBackground");


				String[] backArr =background.split(",");
				for (int i = 0; i < backArr.length; i++) {
					requests.add(backArr[i]);
				}
			}
			assistant.setRawTBG(background);

			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return requests;

	}

	public ArrayList<Request> getRequests(Course course, Instructor instructor) {
		// TODO Auto-generated method stub

		ArrayList<Request> requests = new ArrayList<>();

		Connection c; 

		try{

			c = connectToDatabase();
			String sql = "select distinct assistant_id from request where course_id = ? && Instructor_id =?";

			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, course.id);
			ps.setInt(2, instructor.id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()){

				int asstID = rs.getInt("assistant_id");
				Request r = new Request(instructor.id, asstID, course.id);
				requests.add(r);

			}
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}
		return requests;
	}

	public ArrayList<Instructor> getInstructorForCourse(Course course) {
		// TODO Auto-generated method stub
		ArrayList<Instructor> teachers = new ArrayList<Instructor>();
		Instructor i = null;
		Connection c; 

		try{
			c = connectToDatabase();
			String sql = "Select distinct id,department_code,name,surname,mail from teaches t join instructor i on i.id = t.instructor_id  where course_id = ?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, course.id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){

				String n = rs.getString("name");
				String s = rs.getString("surname");
				String mail = rs.getString("mail");
				Department d = getDepartmentInformation(rs.getString("department_code"));
				i = new Instructor(n, s, mail, d);
				i.id = rs.getInt("id");
				teachers.add(i);
			}
			c.close();

		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}
		return teachers;
	}

	public boolean clearDatabase() {
		// TODO Auto-generated method stub
		boolean result =false;
		Connection c;
		try{
			c = connectToDatabase();
			Statement s = c.createStatement();
			s.addBatch("delete from cost");
			s.addBatch("delete from advisor");
			s.addBatch("delete from teaches");
			s.addBatch("delete from request");
			s.addBatch("delete from assistant");
			s.addBatch("delete from course");
			s.addBatch("delete from instructor");


			int[] r = s.executeBatch();
			if (r[0]>0){
				result = true;
			}
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();


		}
		return result;
	}

	public void addCostToDB(int aid, int cid, int cost) {
		// TODO Auto-generated method stub
		Connection c;
		try
		{
			c =  connectToDatabase();
			String sql = "Insert into Cost (Assistant_ID,Course_ID,cost) values(?,?,?)";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, aid);
			ps.setInt(2, cid);
			ps.setInt(3, cost);
			int s = ps.executeUpdate();
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();


		}
	}

	public HashMap<Course, Integer> getCostsForAssistant(int id2) {
		// TODO Auto-generated method stub

		Connection c;
		HashMap<Course, Integer> hm = new HashMap<Course, Integer>();
		try{

			c = connectToDatabase();
			String sql = "Select Course_id,cost from Cost where assistant_id= ?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, id2);

			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				int cid = rs.getInt("Course_id");
				int cost = rs.getInt("cost");
				Course co = getCourseFromID(cid);
				
				hm.put(co, cost);
			}
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return hm;
	}

	private Course getCourseFromID(int cid) {

		Connection c;

		Course co = null;
		try{

			c = connectToDatabase();
			String sql = "Select * from course where id= ?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, cid);

			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				
				//Department d = getDepartmentInformation(rs.getString("department_code"));
				String code = rs.getString("department_code");
				int no = rs.getInt("number");
				int capacity = rs.getInt("capacity");
				int max = rs.getInt("maxassistantnumber");
				
				 co = new Course(code,no,capacity,max);
			
			}
			c.close();
		}catch (InstantiationException | IllegalAccessException | SQLException e){
			e.printStackTrace();
		}

		return co;
	}

}


