package Model;


public class Department
{
	
	public int id;
	public String name;
	public String code;
	public String faculty;
	
	public Department(int i, String n, String c, String f){
		super();
		id = i;
		name = n;
		code = c;
		faculty = f;
		
	}

	public String toString(){
		String s = "[" + code+" - "+name+"]";
		return s;
	}
}

