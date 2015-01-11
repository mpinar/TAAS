/*
 * Created by Deniz Oztreves & Mert Pinar
 * Copyright (c) 2014-2015 Deniz Oztreves & Mert Pinar. All rights reserved
 * 
 */
package Model;

import java.util.ArrayList;

import Helper.DatabaseHelper;


public class Department
{

public String name;
	public String code;
	public String faculty;
	
	public DatabaseHelper dbh;
	
	public Department(String n, String c, String f){
		
	name = n;
		code = c;
		faculty = f;
		dbh = new DatabaseHelper();
		
	}
	public Department(String c){
		code = c;
		dbh = new DatabaseHelper();
		setOtherInfo();
	}

	private void setOtherInfo(){
		ArrayList<String> s = dbh.getMoreDeptInformation(this.code);
			
		if(!s.isEmpty()){
			name = s.get(0);
			faculty = s.get(1);
		}
	}
	public String toString(){
		String s = "[" + code+" - "+name+"]";
		return s;
	}
	
	public ArrayList<Assistant> getAssistantsForThisDepartment(){
		return dbh.getAssistantsForDepartment(this);
	}
}

