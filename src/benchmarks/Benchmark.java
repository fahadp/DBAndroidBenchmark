package benchmarks;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.*;
import android.util.Log;
import android.view.*;
import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;

import tools.TaskMessage;

import edu.cs.washington.R;

public class Benchmark extends Activity {
    
    static final public int TEST_ITERATIONS = 10000;
    static final public int PROGRESS_NOTIFICATION = 1000; // in milliseconds
    
    
    static final private int M_COUCH_ID = Menu.FIRST;
    static final private int M_SQLITE_ID = Menu.FIRST + 1;
    static final private int M_DB4O_ID = Menu.FIRST + 2;
    static final private int M_DB_ALL = Menu.FIRST+3;
    static final private int M_SETTINGS = Menu.FIRST+4;
    
    //SD Card storage location
    private static final String  SDCARD = Environment.getExternalStorageDirectory().getPath();
	public static final String APP_DIR = SDCARD+"/Android/data/benchmakrs.cs/";
	public static String LOCAL_APP_DIR;
    
    private TextView tv;
    private SharedPreferences sharedPrefs;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        tv = new TextView(this);
        tv.setText(R.string.about);
        setContentView(tv);
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Benchmark.LOCAL_APP_DIR = getFilesDir().getAbsolutePath();
        
        
        //Make sure the path to APP_DIR exists
        Log.i("MAIN","Checking APP DIR: "+Benchmark.APP_DIR);
        File root = new File(Benchmark.APP_DIR);
        if (!root.exists()) 
        {
        	Log.i("MAIN","Creating Directories");
            root.mkdirs(); 
        }
    }
    
    /**
     * Called when your activity's options menu needs to be created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // We are going to create two menus. Note that we assign them
        // unique integer IDs, labels from our string resources, and
        // given them shortcuts.
        
        menu.add(0, M_COUCH_ID, 0, R.string.couchlite);
        menu.add(0, M_SQLITE_ID, 0, R.string.sqlite);
        menu.add(0, M_DB4O_ID, 0, R.string.db4o);
        menu.add(0, M_DB_ALL,0,R.string.dball);
        menu.add(0, M_SETTINGS,0,R.string.settings);
        
        return true;
    }
    
    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        DBTestInterface db;
        switch (item.getItemId()) {
        case M_DB_ALL:
        	Toast.makeText(getApplicationContext(),"all not implemented yet", Toast.LENGTH_SHORT).show();
            return true;
        case M_SQLITE_ID:
        	db = new SQLiteTest();
            break;
        case M_DB4O_ID:
        	db = new Db4oLiteTest();
        	break;
        case M_COUCH_ID:
        	db = new CouchLiteTest();
        	break;
        case M_SETTINGS:
        	startActivity(new Intent(this,ShowSettingsActivity.class));
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }


        ArrayBlockingQueue<TaskMessage> queue = new  ArrayBlockingQueue<TaskMessage>(25);
        this.tv.setText(String.format("Starting %s test....\n",db.getName()));
        
        TestHarness test = new TestHarness(db,queue);
        //set test preferences
        test.doCreate = this.sharedPrefs.getBoolean("create_tables", true);
        
        new Thread(test).start();
        new Thread(new DisplayMonitor(queue,this.tv)).start();
        
        return true;
   }

}
