package benchmarks;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import android.util.Log;

import tools.TaskMessage;

public class CouchLiteTester implements Runnable {
	
	private DBTestInterface db;
	private ArrayBlockingQueue<TaskMessage> queue;
	
	private final String LTAG = "COUCHTEST";
	
	public CouchLiteTester(DBTestInterface db, ArrayBlockingQueue<TaskMessage> queue ) {
		
		this.db = db;
		this.queue = queue;
		
	}
	
	
	@Override
	public void run() {
		
		Log.i(LTAG,"Creating Databases....");
		this.db.create();
		
	}
	
	private void log(String task,String message) {
		TaskMessage t = new TaskMessage(task,0,message);
		try {
			this.queue.put(t);
		} 
		catch(InterruptedException e) {} 
	}

}
