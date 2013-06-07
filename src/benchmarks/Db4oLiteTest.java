package benchmarks;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.foundation.Hashtable4;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import tools.PeopleRow;
import tools.TransactionRow;

class peopleTuple { 
    int id;
    String name; 
    int age; 
    String gender; 
    String color;
};

class transactionTuple { //id,seller,buyer,price,item,date
    int id;
    int seller; 
    int buyer; 
    float price; 
    String item;
    long date;
};

public class Db4oLiteTest extends DBTestInterface {

  private final static String DB_NAME = "db4o";
	private ObjectContainer oc;
	
	private final static String LTAG = "DB4O";
	
	public Db4oLiteTest() {
		//Create SQL statments
		Log.i(LTAG,"Creating SQL Statments.....");
	}
	
	@Override
	public void create() {
		this.create(Db4oLiteTest.DB_NAME);
	}

	@Override
	public void create(String file) {
		Log.i(LTAG,"Creating Db4o DB");
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
        config.common().objectClass(PeopleRow.class).objectField("id").indexed(true);
        config.common().objectClass(TransactionRow.class).objectField("id").indexed(true);
        config.file().lockDatabaseFile(false);
        //config.idSystem().useInMemorySystem();
        config.idSystem().usePointerBasedSystem();
        //config.idSystem().useSingleBTreeSystem();
        //config.common().bTreeNodeSize(6400);
        
        //Open database
        new File(Benchmark.APP_DIR+this.DB_NAME).delete();
        oc = Db4oEmbedded.openFile(config, Benchmark.APP_DIR+this.DB_NAME);
		
        PeopleRow.people = 0;
		TransactionRow.transactions = 0;
        
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertPeopleRecord(PeopleRow row) {
		//this.db.execSQL(this.statements.get(ST.INSERT_PEOPLE),row.bindArgs());
		oc.store(row);
		oc.commit();
		//Log.i("DB4O","Insert Person: "+row);
	}

	@Override
	public void insertTransactionRecord(TransactionRow row) {
		//this.db.rawQuery(this.statements.get(ST.INSERT_TRANSACTION),row.bindArgs());
		row.setBuyerObject(getPeopleRecordById(row.buyer));
		row.setSellerObject(getPeopleRecordById(row.seller));
		oc.store(row);
		oc.commit();
		//Log.i("DB4O","Insert Transaction: "+row);
	}

	@Override
	public boolean deletePeopleRecord(int id) {
		//this.db.rawQuery(this.statements.get(ST.DELETE_PEOPLE),new String[]{Integer.valueOf(id).toString()});
		oc.delete(this.getPeopleRecordById(id));
		oc.commit();
		return true;
	}
	
	@Override
	public boolean deleteTransactionRecord(int id) {
		//this.db.rawQuery(this.statements.get(ST.DELETE_TRANSACTION),new String[]{Integer.valueOf(id).toString()});
		oc.delete(this.getTransactionRecordById(id));
		oc.commit();
		return true;
	}

	@Override
	/** select * from people where id=? **/
	public int selectTest1(int id) {
		//Cursor c = this.db.rawQuery(this.statements.get(ST.TEST1),new String[]{Integer.valueOf(id).toString()});
		Query query = oc.query();
    	query.constrain(PeopleRow.class);
    	query.descend("id").constrain(id);
    	ObjectSet<PeopleRow> result = query.execute();
    	return result.size();
	}

	@Override
	/** select * from people where name='?' **/
	public int selectTest2(String name) {
		Query query = oc.query();
    	query.constrain(PeopleRow.class);
    	query.descend("name").constrain(name);
    	return query.execute().size();
	}

	@Override
	/** select * from people where ?<age and age<? **/
	public int selectTest3(int startAge,int endAge) {
		Query q=oc.query();
		q.constrain(PeopleRow.class);
		Query cq=q.descend("age");
		q.descend("age").constrain(startAge).greater().and(cq.constrain(endAge).smaller());
		return q.execute().size();
	}

	@Override
	/** select age, count(*) from people group by age **/
	public int selectTest4() {
		Hashtable hash=new Hashtable();
		
		ObjectSet<PeopleRow> result =oc.queryByExample(PeopleRow.class);
		PeopleRow row=null;
		while(result.hasNext()){
			row=result.next();
			if(hash.containsKey(Integer.valueOf(row.age))){
				hash.put(Integer.valueOf(row.age), Integer.valueOf(((Integer)hash.get(Integer.valueOf(row.age)))+1));
			}else{
				hash.put(Integer.valueOf(row.age), Integer.valueOf(1));
			}
		}
		//Cursor c = this.db.rawQuery(this.statements.get(ST.TEST4),null);
		return hash.size();
	}

	@Override
	/** select buyer, sum(price) from transactions group by buyer **/
	public int selectTest5() {
		Hashtable hash=new Hashtable();
		ObjectSet<TransactionRow> result =oc.queryByExample(TransactionRow.class);
		TransactionRow row=null;
		while(result.hasNext()){
			row=result.next();
			if(hash.containsKey(Integer.valueOf(row.buyer))){
				hash.put(Integer.valueOf(row.buyer), Float.valueOf(((Float)hash.get(Integer.valueOf(row.buyer)))+row.price));
			}else{
				hash.put(Integer.valueOf(row.buyer), Float.valueOf(row.price));
			}
		}
		//Cursor c = this.db.rawQuery(this.statements.get(ST.TEST4),null);
		return hash.size();
		//Cursor c = this.db.rawQuery(this.statements.get(ST.TEST5),null);
		//return c.getCount();
	}

	@Override
	/** select t.id,p.name,p.age grom transactions t, people p where p.id=t.buyer and ?<p.age **/
	public int selectTest6(int age) {
		final int arg_age=age;
		ObjectSet<TransactionRow> result =oc.query(new Predicate<TransactionRow>() {
		    public boolean match(TransactionRow row) {
		        return row.buyer_object.age>arg_age;
		    }
		});
		//Cursor c = this.db.rawQuery(this.statements.get(ST.TEST6),new String[] {Integer.valueOf(age).toString()});
		//return c.getCount();
		return result.size();
	}
	
	private PeopleRow getPeopleRecordById(int id) {
    	Query query = oc.query();
    	query.constrain(PeopleRow.class);
    	query.descend("id").constrain(id);
    	ObjectSet<PeopleRow> result = query.execute();
    	if(result.hasNext())
    		return result.next();
    	return null;
    }
	private TransactionRow getTransactionRecordById(int id) {
    	Query query = oc.query();
    	query.constrain(TransactionRow.class);
    	query.descend("id").constrain(id);
    	ObjectSet<TransactionRow> result = query.execute();
    	if(result.hasNext())
    		return result.next();
    	return null;
    }
	private ObjectSet<PeopleRow> getPeopleRecordByString(String[] fieldName,String[] fieldValue) {
    	Query query = oc.query();
    	query.constrain(PeopleRow.class);
    	for(int i=0;i<fieldName.length;i++)
    		query.descend(fieldName[i]).constrain(fieldValue[i]);
    	return query.execute();
    }
	private ObjectSet<PeopleRow> getPeopleRecordByInt(String[] fieldName,int[] fieldValue) {
    	Query query = oc.query();
    	query.constrain(PeopleRow.class);
    	for(int i=0;i<fieldName.length;i++)
    		query.descend(fieldName[i]).constrain(fieldValue[i]);
    	return query.execute();
    }
	private ObjectSet<TransactionRow> getTransactionRecordByString(String[] fieldName,String[] fieldValue) {
    	Query query = oc.query();
    	query.constrain(TransactionRow.class);
    	for(int i=0;i<fieldName.length;i++)
    		query.descend(fieldName[i]).constrain(fieldValue[i]);
    	return query.execute();
    }
	private ObjectSet<TransactionRow> getTransactionRecordByInt(String[] fieldName,int[] fieldValue) {
    	Query query = oc.query();
    	query.constrain(TransactionRow.class);
    	for(int i=0;i<fieldName.length;i++)
    		query.descend(fieldName[i]).constrain(fieldValue[i]);
    	return query.execute();
    }

	@Override
	public boolean open() {
		// TODO Auto-generated method stub
		return this.open(Db4oLiteTest.DB_NAME);
	}

	@Override
	public boolean open(String file) {
		// TODO Auto-generated method stub
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
        config.common().objectClass(PeopleRow.class).objectField("id").indexed(true);
        config.common().objectClass(TransactionRow.class).objectField("id").indexed(true);
        config.file().lockDatabaseFile(false);
        //config.idSystem().useInMemorySystem();
        config.idSystem().usePointerBasedSystem();
        //config.idSystem().useSingleBTreeSystem();
        //config.common().bTreeNodeSize(6400);
        
        //Open database
        oc = Db4oEmbedded.openFile(config, Benchmark.APP_DIR+this.DB_NAME);
		return true;
	}

	@Override
	public String getName() {		
		return Db4oLiteTest.LTAG;
	}

}
