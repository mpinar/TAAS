package Model;

import java.util.ArrayList;

import Helper.DatabaseHelper;


public class Department
{
	
	public int id;
	public String name;
	public String code;
	public String faculty;
	
	DatabaseHelper dbh;
	
	public Department(int i, String n, String c, String f){
		
		id = i;
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
			id = Integer.parseInt(s.get(0));
			name = s.get(1);
			faculty = s.get(2);
		}
	}
	public String toString(){
		String s = "[" + code+" - "+name+"]";
		return s;
	}
}

