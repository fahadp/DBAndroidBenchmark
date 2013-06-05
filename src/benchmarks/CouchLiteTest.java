package benchmarks;

import java.io.File;
import java.io.IOException;

import tools.PeopleRow;
import tools.TransactionRow;

import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ReplicationCommand;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult.Row;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;


import android.util.Log;
import android.widget.Toast;

import com.couchbase.cblite.CBLDatabase;
import com.couchbase.cblite.CBLServer;
import com.couchbase.cblite.CBLView;
import com.couchbase.cblite.CBLViewMapBlock;
import com.couchbase.cblite.CBLViewMapEmitBlock;
import com.couchbase.cblite.ektorp.CBLiteHttpClient;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;


public class CouchLiteTest extends DBTestInterface {
	
	private final static String DB_NAME = "couch.db";
	private final static String LTAG = "COUCH";
	
	private CBLServer server;

	@Override
	public boolean open() {
		return this.open(CouchLiteTest.DB_NAME);
	}

	@Override
	public boolean open(String file) {
		Log.w(LTAG,"Open Not Implemented");
		return false;
	}

	@Override
	public void create() {
		this.create(CouchLiteTest.DB_NAME);
	}

	@Override
	public void create(String file) {
		Log.i(LTAG,"Creating Datababses");
		
		//create server directory
		try {
			this.server = new CBLServer(Benchmark.APP_DIR);
		} catch (IOException e) {
			Log.e(LTAG, "Error starting CBLServer", e);
		}
		
		//create db
		CBLDatabase db = server.getDatabaseNamed(file);
		
		Log.i(LTAG,"Couch DB: "+db);
		
		//TODO: add views in here
		
		//Start Ektorp

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
		// TODO Auto-generated method stub

	}

	@Override
	public void insertTransactionRecord(TransactionRow row) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deletePeopleRecord(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteTransactionRecord(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int selectTest1(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int selectTest2(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int selectTest3(int startAge, int endAge) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int selectTest4() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int selectTest5() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int selectTest6(int age) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
