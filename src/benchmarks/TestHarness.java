package benchmarks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

import android.util.Log;

import tools.FieldGenerator;
import tools.TaskMessage;



public class TestHarness implements Runnable {

	public static final String DONE = "DONE";
	private DBTestInterface db;
	private ArrayBlockingQueue<TaskMessage> queue;
	private long start;
	private long delta;
	
	private final String LTAG = "HARNESS";
	private BufferedWriter logbw;	
	
	public static final int NUM_PEOPLE = 1000;
	public static final int NUM_TRANSACTIONS = NUM_PEOPLE*3;
	
	public TestHarness(DBTestInterface db, ArrayBlockingQueue<TaskMessage> queue ) {
		
		this.db = db;
		this.queue = queue;
		
		try {
			//TODO: get timestamp
			File logFile = new File(String.format("%s%s_%s.txt",Benchmark.APP_DIR,db.getName(),FieldGenerator.DATETIME_FORMAT.format(new Date())));
			if(!logFile.exists()) {
				logFile.createNewFile();
			}
			FileWriter fw = new FileWriter(logFile.getAbsoluteFile());
			logbw = new BufferedWriter(fw);
		} catch(IOException e) {}
		
	}
	
	private void startTimer() {
		this.start = System.currentTimeMillis();
	}
	
	private long endTimer() {
		this.delta = (System.currentTimeMillis() - start);
		return this.delta;
	}
	
	private void log(String task,String message) {
		TaskMessage t = new TaskMessage(task,this.delta,message);
		try {
			this.queue.put(t);
			this.logbw.write(t.toString()+"\n");
		} 
		catch(InterruptedException e) {} 
		catch (IOException e) {}
	}
	
	@Override
	public void run() {
		
		if(false) {// toggle create/insert 
			this.create();
			this.insert();
		}
		else
			this.db.open();
		
		this.select1();
		
		this.select2();
		
		this.select3();
		
		this.select4();
		
		this.select5();
		
		this.select6();
		
		//DONE
		this.delta = 0;
		this.log(TestHarness.DONE,"Finished");
		try {
			this.logbw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("TEST","DONE");
	}
	
	/**
	 * /Create the database
	 */
	private void create() {
		Log.i(LTAG,"Creating Databases....");
		this.startTimer();
		this.db.create();
		this.endTimer();
		this.log("Create","Created databases");
	}
	
	/**
	 * insert data
	 */
	private void insert() {
		Log.i(LTAG,"Inserting initial data....");
		this.startTimer();
		this.db.insertAll(TestHarness.NUM_PEOPLE,TestHarness.NUM_TRANSACTIONS);
		this.endTimer();
		this.log("Insert",String.format("Inserted %d People and %d Transactions", TestHarness.NUM_PEOPLE,TestHarness.NUM_TRANSACTIONS));
	}
	
	/**
 	 * Test 1
	 * SELECT * FROM people WHERE id=?
	 */
	private void select1() {
		Log.i(LTAG,"Select 1....");
		this.startTimer();
		for(int i=0; i<TestHarness.NUM_PEOPLE; i++) {
			this.db.selectTest1(i);
		}
		this.endTimer();
		this.log("Select Test 1",String.format("Selected %d records by primary key. Average %f",TestHarness.NUM_PEOPLE,(float)this.delta/TestHarness.NUM_PEOPLE));
		
	}
	
	
	/**
	 * Test 2
	 * SELECT * FROM people WHERE name=?
	 */
	private void select2() {
		Log.i(LTAG,"Select 2....");
		this.startTimer();
		for(int i=0; i<TestHarness.NUM_PEOPLE; i++) {
			int c = this.db.selectTest2(FieldGenerator.randomName());
			//Log.i(LTAG,String.format("Select 2: return %d rows",c));
		}
		this.endTimer();
		this.log("Select Test 2",String.format("Selected %d records by non-indexed field. Average %f",TestHarness.NUM_PEOPLE,(float)this.delta/TestHarness.NUM_PEOPLE));
	}
	
	/**
	 * Test 3
	 * SELECT * FROM people WHERE ?<age AND age>?
	 */
	private void select3() {
		Log.i(LTAG,"Select 3....");
		this.startTimer();
		for(int i=0; i<TestHarness.NUM_PEOPLE; i++) {
			int age = FieldGenerator.randomAge();
			int c = this.db.selectTest3(age,age+10);
			//Log.i(LTAG,String.format("Select 3: return %d rows",c));
		}
		this.endTimer();
		this.log("Select Test 3",String.format("Selected %d records by non-indexed field. Average %f",TestHarness.NUM_PEOPLE,(float)this.delta/TestHarness.NUM_PEOPLE));
	}
	
	/**
	 * Test 4
	 * SELECT age,count(*) FROM people GROUP BY age
	 */
	private void select4() {
		Log.i(LTAG,"Select 4....");
		this.startTimer();
		for(int i=0; i<TestHarness.NUM_PEOPLE; i++) {
			int c = this.db.selectTest4();
		}
		this.endTimer();
		this.log("Select Test 4",String.format("Selected %d records by non-indexed field. Average %f",TestHarness.NUM_PEOPLE,(float)this.delta/TestHarness.NUM_PEOPLE));
	}
	
	
	/**
	 * Test 5
	 *SELECT buyer,sum(price) FROM people GROUP BY transactionid,buyer
	 */
	private void select5() {
		Log.i(LTAG,"Select 5....");
		this.startTimer();
		for(int i=0; i<TestHarness.NUM_PEOPLE; i++) {
			int c = this.db.selectTest5();
		}
		this.endTimer();
		this.log("Select Test 5",String.format("Selected %d records by non-indexed field. Average %f",TestHarness.NUM_PEOPLE,(float)this.delta/TestHarness.NUM_PEOPLE));
	}
	
	/**
	 * Test 5
	 * SELECT p.name,p.age,t.transactionid FROM people p,transaction t WHERE p.id=t.buyer AND p.age>=?
	 */
	private void select6() {
		Log.i(LTAG,"Select 6....");
		this.startTimer();
		for(int i=0; i<TestHarness.NUM_PEOPLE; i++) {
			int c = this.db.selectTest6(FieldGenerator.randomAge());
		}
		this.endTimer();
		this.log("Select Test 6",String.format("Selected %d records by non-indexed field. Average %f",TestHarness.NUM_PEOPLE,(float)this.delta/TestHarness.NUM_PEOPLE));
	}

}
