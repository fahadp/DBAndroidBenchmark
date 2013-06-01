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
	
	public static int randomPerson() {
		return XSRandom.xsr.nextInt(PeopleRow.people);
	}
	
}
