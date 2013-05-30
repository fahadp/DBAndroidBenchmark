package benchmarks;

public interface DBbenchmark {
	
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
	public void reset();
	
	/**
	 * DROP TABLE people;
	 * DROP TABLE transaction;
	 */
	public void delete();
	
	
	
	/**
	 * INSERT INTO people VALUES(?,?,?,?,?)
	**/
	public void insertPeopleRecord(int key,String name,int age,String gender,String color);
	
	/**
	 * Executes the equivalent SQL
	 * 
	 * INSERT INTO transaction VALUES(?,?,?,?,?,?)
	**/
	public void insertTransactionRecord(int  transactionID, float price,String item,int buyer,int seller,long dateTime);
	
	
	
	/**
	DELETE * FROM people WHERE id=?
	**/
	public void deletePeopleRecord(int id);
	
	/**
	DELETE * FROM transaction WHERE transactionid=?
	**/
	public void deleteTransactionRecord(int id);
	
	
	/**
	SELECT * FROM people WHERE id=?
	**/
	public void selectTest1(int id);
	
	/**
	SELECT * FROM people WHERE name=?
	**/
	public void selectTest2(String name);
	
	/**
	SELECT * FROM people WHERE ?<age AND age>?
	**/
	public void selectTest3(int startAge,int endAge);
	
	/**
	SELECT age,count(*) FROM people GROUP BY age
	**/
	public void selectTest4();
	
	/**
	SELECT buyer,sum(price) FROM people WHERE datetime=? GROUP BY transactionid,buyer
	**/
	public void selectTest5(long datetime);
	
	/**
	SELECT p.name,p.age,t.transactionid FROM people p,transaction t WHERE p.id=t.buyer AND p.age>?
	**/
	public void selectTest6(int age);
}
