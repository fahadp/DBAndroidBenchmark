package tools;

public class PeopleRow {
	
	// People Row Creator
	
	//int id,String name,int age,String gender,String color
	public final int id;
	public final String name;
	public final int age;
	public final String gender;
	public final String color;
	
	public static int people = 0;
	
	public PeopleRow() {
		this(PeopleRow.people,FieldGenerator.randomName(), FieldGenerator.randomAge(),FieldGenerator.randomGender(),FieldGenerator.randomColor());
	}
	
	public PeopleRow(int id) {
		this(id,FieldGenerator.randomName(),FieldGenerator.randomAge(),FieldGenerator.randomGender(),FieldGenerator.randomColor());
	}
		
	public PeopleRow(String name,int age,String gender,String color) {
		this(PeopleRow.people,name,age,gender,color);
	}

	public PeopleRow(int id,String name,int age,String gender,String color) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.gender = gender;
		this.color = color;
		PeopleRow.people++;
	}
	
	/**
	 * Return an array of arguments as strings
	 * @return String[] {id,name,age,gender,color}
	 */
	public String[] bindArgs() {
		return new String[]  {Integer.valueOf(this.id).toString(),this.name,Integer.valueOf(this.age).toString(),this.gender,this.color};
	}
	
	/**
	 * insert into people (id,name,age,gender,color) values (?,?,?,?,?)
	 * @return
	 */
	public static int randomPerson() {
		return XSRandom.xsr.nextInt(PeopleRow.people);
	}
	
	public String toString() {
		String out = "("+id+") ";
		out += name +" "+age;
		return out;
	}
	
}
