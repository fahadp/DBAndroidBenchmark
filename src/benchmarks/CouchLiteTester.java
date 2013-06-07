package benchmarks;

import tools.PeopleRow;
import tools.TransactionRow;
import edu.cs.washington.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class CouchLiteTester extends Activity {

	public static final String LTAG = "TESTER";
	
	private CouchLiteTest db;
	private TextView tv;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.db_tester);
	    
	    this.tv = (TextView) findViewById(R.id.db_tester_text);
	    
	    this.db = new CouchLiteTest();

	    // TODO Auto-generated method stub
	}
	
	public void create(View view) {
		this.log("Creating Couch-lite");
		this.db.create();
		
	}
	
	public void insert10(View view) {
		this.log("Insert 10");
		for(int i=0; i<10; i++) {
			this.db.insertPeopleRecord(new PeopleRow());
		}
		for(int i=0; i<40; i++){
			this.db.insertTransactionRecord(new TransactionRow());
		}
	}
	
	public  void getCount(View view){
		this.log("Count: "+this.db.count());
	}
	
	public void opendb(View view){
		this.log("Opening Couch");
		this.db.open();
	}
	
	public void select10(View view){
		this.log("Selecting");
		for(int i=0; i<10; i++){
			this.db.selectTest1(i);
		}
	}
	
	public void select2(View view) {
		this.log("Select 6: "+this.db.selectTest6(35));
		
	}
	
	private void log(String s) {
		Log.i(LTAG,s);
		this.tv.append(s+"\n");
	}
	
	

}
