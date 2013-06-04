package benchmarks;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.*;
import android.util.Log;
import android.view.*;
import java.io.*;

import edu.cs.washington.R;

public class Benchmark extends Activity {
    
    static final public int TEST_ITERATIONS = 10000;
    static final public int PROGRESS_NOTIFICATION = 1000; // in milliseconds
    static final private int PERST_ID = Menu.FIRST;
    static final private int SQLITE_ID = Menu.FIRST + 1;
    static final private int DB4O_ID = Menu.FIRST + 2;
    
    //SD Card storage location
    private static final String  SDCARD = Environment.getExternalStorageDirectory().getPath();
	public static final String APP_DIR = SDCARD+"/Android/data/benchmakrs.cs/";
    
    TextView tv;
    
    /**
     * Called when your activity's options menu needs to be created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // We are going to create two menus. Note that we assign them
        // unique integer IDs, labels from our string resources, and
        // given them shortcuts.
        menu.add(0, PERST_ID, 0, R.string.perst);
        menu.add(0, SQLITE_ID, 0, R.string.sqlite);
        menu.add(0, DB4O_ID, 0, R.string.db4o);
        
        //Make sure the path to APP_DIR exists
        Log.i("MAIN","Checking APP DIR: "+Benchmark.APP_DIR);
        File root = new File(Benchmark.APP_DIR);
        if (!root.exists()) 
        {
        	Log.i("MAIN","Creating Directories");
            if(!root.mkdirs()) return false; // fail if directory not created
        }

        return true;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        tv = new TextView(this);
        tv.setText(R.string.about);
        setContentView(tv);
    }
    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        /*
        String databasePath = Benchmark.APP_DIR;
        try { 
            // It is necessary to create file using Activity.openFileOutput method otherwise
            // java.io.RandomAccessFile will not able to open it (even in write mode). It seems to be a bug
            // which I expect to be fixed in next release of Android, but right now this two lines fix
            // this problem.
            this.openFileOutput(databasePath, 0).close();
            databasePath = getFileStreamPath(databasePath).getAbsolutePath();
            //new File(databasePath).delete();
        } catch (IOException x) {} */
        
        DBTestInterface test = new SQLiteTest();
        test.create();
        Log.i("MAIN","Done!");
        test.insert();
        Log.i("MAIN","Insert Done!");
        /*
        PrintStream ps = new PrintStream(out);
        Test test;
        switch (item.getItemId()) {
        case PERST_ID:
            test = new PerstTest(Benchmark.APP_DIR, ps);
            break;
        case SQLITE_ID:
            test = new SqlLiteTest(Benchmark.APP_DIR, ps);
            break;
        case DB4O_ID:
            test = new Db4oTest(Benchmark.APP_DIR, ps);
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        new Thread(test).start();
        new Thread(new ProgressMonitor(test, tv, out)).start();
        */ 
        return true;
   }

}