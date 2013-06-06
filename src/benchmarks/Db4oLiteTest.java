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
        oc = Db4oEmbedded.openFile(config, Benchmark.APP_DIR+this.DB_NAME);
		

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
		Log.i("DB4O","Insert Person: "+row);
	}

	@Override
	public void insertTransactionRecord(TransactionRow row) {
		//this.db.rawQuery(this.statements.get(ST.INSERT_TRANSACTION),row.bindArgs());
		row.setBuyerObject(getPeopleRecordById(row.buyer));
		row.setSellerObject(getPeopleRecordById(row.seller));
		oc.store(row);
		oc.commit();
		Log.i("DB4O","Insert Transaction: "+row);
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
		ObjectSet<PeopleRow> result = getPeopleRecordByInt(new String[]{"id"},new int[]{id});
		return result.size();
	}

	@Override
	/** select * from people where name='?' **/
	public int selectTest2(String name) {
		//Cursor c = this.db.rawQuery(this.statements.get(ST.TEST2),new String[]{name});
		ObjectSet<PeopleRow> result = getPeopleRecordByString(new String[]{"name"},new String[]{name});
		return result.size();
	}

	@Override
	/** select * from people where ?<age and age<? **/
	public int selectTest3(int startAge,int endAge) {
		final int s=startAge;
		final int e=endAge;
		ObjectSet<PeopleRow> result =oc.query(new Predicate<PeopleRow>() {
		    public boolean match(PeopleRow row) {
		        return row.age>s && row.age<e;
		    }
		});
		//Cursor c = this.db.rawQuery(this.statements.get(ST.TEST3),new String[]{Integer.valueOf(startAge).toString(),Integer.valueOf(endAge).toString()});
		//ObjectSet<PeopleRow> result = getPeopleRecordByInt(new String[]{"id"},new int[]{id});
		return result.size();
	}

	@Override
	/** select age, count(*) from people group by age **/
	public int selectTest4() {
		Hashtable4 hash=new Hashtable4();
		ObjectSet<PeopleRow> result =oc.query(new Predicate<PeopleRow>() {
		    public boolean match(PeopleRow row) {
		        return true;
		    }
		});
		PeopleRow row=null;
		while(result.hasNext()){
			row=result.next();
			if(hash.containsKey(row.age)){
				hash.put(row.age, new Integer(((Integer)hash.get(row.age))+1));
			}else{
				hash.put(row.age, new Integer(1));
			}
		}
		//Cursor c = this.db.rawQuery(this.statements.get(ST.TEST4),null);
		return hash.size();
	}

	@Override
	/** select buyer, sum(price) from transactions group by buyer **/
	public int selectTest5() {
		Hashtable4 hash=new Hashtable4();
		ObjectSet<TransactionRow> result =oc.query(new Predicate<TransactionRow>() {
		    public boolean match(TransactionRow row) {
		        return true;
		    }
		});
		TransactionRow row=null;
		while(result.hasNext()){
			row=result.next();
			if(hash.containsKey(row.buyer)){
				hash.put(row.buyer, new Float(((Float)hash.get(row.buyer))+row.price));
			}else{
				hash.put(row.buyer, new Float(row.price));
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

}
