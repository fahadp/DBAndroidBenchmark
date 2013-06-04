package benchmarks;

import java.io.File;
import java.util.HashMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import tools.PeopleRow;
import tools.TransactionRow;

public class SQLiteTest extends DBTestInterface {

	private final static String DB_NAME = "sqlite.db";
	SQLiteDatabase db;
	private final static String LTAG = "SQLITE";
	
	private HashMap<ST,String> statements;
	
	public enum ST {INSERT_PEOPLE,INSERT_TRANSACTION,DELETE_PEOPLE,DELETE_TRANSACTION,TEST1,TEST2,TEST3,TEST4,TEST5,TEST6,RESET,DELETE}
	
	public SQLiteTest() {
		//Create SQL statments
		Log.i(LTAG,"Creating SQL Statments.....");
		this.statements = new HashMap<ST,String>();
		this.statements.put(ST.INSERT_PEOPLE,"insert into people (id,name,age,gender,color) values (?,?,?,?,?)");
		this.statements.put(ST.INSERT_TRANSACTION, "insert into transactions (id,seller,buyer,price,item,date) values (?,?,?,?,?,?)");
		this.statements.put(ST.DELETE_PEOPLE,"delete from people where id=?");
		this.statements.put(ST.DELETE_TRANSACTION,"delete from transactions where id=?");
		this.statements.put(ST.TEST1,"select * from people where id=?");
		this.statements.put(ST.TEST2,"select * from people where name=?");
		this.statements.put(ST.TEST3,"select * from people where ?<age and age<?");
		this.statements.put(ST.TEST4,"select age, count(*) from people group by age");
		this.statements.put(ST.TEST5,"select buyer, sum(price) from transactions group by buyer");
		this.statements.put(ST.TEST6,"select t.id,p.name,p.age from transactions t, people p where p.id=t.buyer and ?<=p.age");
	}
	
	@Override
	public boolean open() {
		return this.open(SQLiteTest.DB_NAME);
	}

	@Override
	public boolean open(String file) {
		File dbfile = new File(Benchmark.APP_DIR+file);
		if(!dbfile.exists()) {
			return false; // database does not exist
		}
		this.db = SQLiteDatabase.openDatabase(dbfile.getAbsolutePath(),null,SQLiteDatabase.OPEN_READWRITE);
		Log.i(LTAG,String.format("Open Database: %s",dbfile.getAbsolutePath()));
		return true;
	}
	
	@Override
	public void create() {
		this.create(SQLiteTest.DB_NAME);
	}

	@Override
	public void create(String file) {
		Log.i(LTAG,"Creating SQLite DB");
		File dbfile = new File(Benchmark.APP_DIR+file);
		if(dbfile.exists()) {
			Log.i(LTAG,"File Exists Deleting: "+dbfile.delete());
		}
		
		this.db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
		
		//Create tables: People and Transaction Tabels
		Log.i(LTAG,"Creating Tables...");
		this.db.execSQL("create table people (id integer primary key, name text, age integer,  gender text, color text)");
		this.db.execSQL("create table transactions (id integer primary key, seller integer, buyer integer, price real, item text,  date text)");
		
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete() {
		
		
	}

	@Override
	public void insertPeopleRecord(PeopleRow row) {
		this.db.execSQL(this.statements.get(ST.INSERT_PEOPLE),row.bindArgs());
		//Log.i("SQLITE","Insert Person: "+row);
	}

	@Override
	public void insertTransactionRecord(TransactionRow row) {
		this.db.execSQL(this.statements.get(ST.INSERT_TRANSACTION),row.bindArgs());
	}

	@Override
	public boolean deletePeopleRecord(int id) {
		this.db.rawQuery(this.statements.get(ST.DELETE_PEOPLE),new String[]{Integer.valueOf(id).toString()});
		return true;
	}
	
	@Override
	public boolean deleteTransactionRecord(int id) {
		this.db.rawQuery(this.statements.get(ST.DELETE_TRANSACTION),new String[]{Integer.valueOf(id).toString()});
		return true;
	}

	@Override
	public int selectTest1(int id) {
		Cursor c = this.db.rawQuery(this.statements.get(ST.TEST1),new String[]{Integer.valueOf(id).toString()});
		int i;
		try {
			i = c.getCount();
		}
		finally {
			c.close();
		}
		return i;
		
	}

	@Override
	public int selectTest2(String name) {
		Cursor c = this.db.rawQuery(this.statements.get(ST.TEST2),new String[]{name});
		int i;
		try {
			i = c.getCount();
		}
		finally {
			c.close();
		}
		return i;
	}

	@Override
	public int selectTest3(int startAge, int endAge) {
		Cursor c = this.db.rawQuery(this.statements.get(ST.TEST3),new String[]{Integer.valueOf(startAge).toString(),Integer.valueOf(endAge).toString()});
		int i;
		try {
			i = c.getCount();
		}
		finally {
			c.close();
		}
		return i;
	}

	@Override
	public int selectTest4() {
		Cursor c = this.db.rawQuery(this.statements.get(ST.TEST4),null);
		int i;
		try {
			i = c.getCount();
		}
		finally {
			c.close();
		}
		return i;
	}

	@Override
	public int selectTest5() {
		Cursor c = this.db.rawQuery(this.statements.get(ST.TEST5),null);
		int i;
		try {
			i = c.getCount();
		}
		finally {
			c.close();
		}
		return i;
	}

	@Override
	public int selectTest6(int age) {
		Cursor c = this.db.rawQuery(this.statements.get(ST.TEST6),new String[] {Integer.valueOf(age).toString()});
		int i;
		try {
			i = c.getCount();
		}
		finally {
			c.close();
		}
		return i;
	}

	@Override
	public String getName() {
		return SQLiteTest.LTAG;
	}

}
