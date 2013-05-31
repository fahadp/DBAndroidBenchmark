package benchmarks;

public abstract class DBbenchmark {
	
	//Database Set Up Functions
	/**
	 * Create people and transaction tables
	 * 		people (int id PRIMARy KEY, String name, int age, String gender, String color)
	 * 		
	 */
	
	/**
	 * DELETE * FROM people
	 * DELETE * FROM transaction
	**/
	public abstract void reset();
	
	/**
	 * DROP TABLE people;
	 * DROP TABLE transaction;
	 */
	public abstract void delete();
	
	
	
	/**
	 * INSERT INTO people VALUES(?,?,?,?,?)
	**/
	public abstract void insertPeopleRecord(int id,String name,int age,String gender,String color);
	
	/**
	 * 
	 * INSERT INTO transaction VALUES(?,?,?,?,?,?)
	**/
	public abstract void insertTransactionRecord(int transactionID, float price,String item,int buyer,int seller,String date);
	
	
	/**
	DELETE * FROM people WHERE id=?
	**/
	public abstract void deletePeopleRecord(int id);
	
	/**
	DELETE * FROM transaction WHERE transactionid=?
	**/
	public abstract void deleteTransactionRecord(int id);
	
	
	/**
	SELECT * FROM people WHERE id=?
	**/
	public abstract void selectTest1(int id);
	
	/**
	SELECT * FROM people WHERE name=?
	**/
	public abstract void selectTest2(String name);
	
	/**
	SELECT * FROM people WHERE ?<age AND age>?
	**/
	public abstract void selectTest3(int startAge,int endAge);
	
	/**
	SELECT age,count(*) FROM people GROUP BY age
	**/
	public abstract void selectTest4();
	
	/**
	SELECT buyer,sum(price) FROM people WHERE datetime=? GROUP BY transactionid,buyer
	**/
	public abstract void selectTest5(String date);
	
	/**
	SELECT p.name,p.age,t.transactionid FROM people p,transaction t WHERE p.id=t.buyer AND p.age>?
	**/
	public abstract void selectTest6(int age);
}
