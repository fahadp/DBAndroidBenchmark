package benchmarks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.PeopleRow;
import tools.TransactionRow;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ReplicationCommand;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
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
import com.couchbase.cblite.CBLViewReduceBlock;
import com.couchbase.cblite.ektorp.CBLiteHttpClient;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;


public class CouchLiteTest extends DBTestInterface {
	
	private final static String DB_NAME = "couch-test";
	private final static String DOC_NAME = "couch-local";
	private final static String DOC_ID = "_design/"+DOC_NAME;
	private final static String LTAG = "COUCH";
	
	private final static HashMap<String,String> viewNames = new HashMap<String,String>(10);
	
	
	
	//couch internals (these were static in GrocerySync)
	private CBLDatabase db;
	private CBLServer server;
	private HttpClient httpClient;

	//ektorp impl
	private CouchDbInstance dbInstance;
	private CouchDbConnector couchDbConnector;
	
	//java.net.MalformedURLException: Unknown protocol: cblite
	{
	    CBLURLStreamHandlerFactory.registerSelfIgnoreError();
	}
	
	public CouchLiteTest() {
		viewNames.put("byName","byName");
		viewNames.put("byAge","byAge");
		viewNames.put("groupByAge","groupByAge");
		viewNames.put("sumBuyer","sumBuyer");
		viewNames.put("nameByTransaction","nameByTransaction");
		
	}
	
	
	@Override
	public boolean open() {
		return this.open(CouchLiteTest.DB_NAME);
	}

	@Override
	public boolean open(String file) {
		Log.i(LTAG,"Opening Datababses");
		
		//create server directory
		try {
			this.server = new CBLServer(Benchmark.APP_DIR);
		} catch (IOException e) {
			Log.e(LTAG, "Error starting CBLServer", e);
		}
		
		//create db
		this.db = server.getDatabaseNamed(file);
		if(!db.exists()) {
			Log.i(LTAG,"Database does not exist");
			return false;
		}
		
		//Setup Views
		this.makeViews();
		
		//Start Ektorp
				this.httpClient = new CBLiteHttpClient(this.server);
				this.dbInstance = new StdCouchDbInstance(this.httpClient);
				Log.i(LTAG,"DBInstance: "+dbInstance);
				

				this.couchDbConnector = this.dbInstance.createConnector(file, true);
		
		PeopleRow.people = this.count();
		
		return true;
	}

	@Override
	public void create() {
		this.create(CouchLiteTest.DB_NAME);
	}

	@Override
	public void create(final String file) {
		Log.i(LTAG,"Creating Datababses");
		
		//create server directory
		try {
			this.server = new CBLServer(Benchmark.APP_DIR);
		} catch (IOException e) {
			Log.e(LTAG, "Error starting CBLServer", e);
		}
		PeopleRow.people = 0;
		//create db
		this.db = server.getDatabaseNamed(file);
		Log.i(LTAG,"Created DB: "+db);
		if(db.exists()) {
			Log.i(LTAG,"Database exists...deleting");
			db.deleteDatabase();
		}
		
		
		
		//Setup Views
		this.makeViews();
		
		//Start Ektorp
		this.httpClient = new CBLiteHttpClient(this.server);
		this.dbInstance = new StdCouchDbInstance(this.httpClient);
		Log.i(LTAG,"DBInstance: "+dbInstance);
		

		this.couchDbConnector = this.dbInstance.createConnector(file, true);
		
		
	}
	
	private void makeViews() {
		Log.i(LTAG,"Creating Views");
		//Name View
		CBLView view = this.db.getViewNamed(String.format("%s/%s",DOC_NAME,CouchLiteTest.viewNames.get("byName")));
		view.setMapReduceBlocks(new CBLViewMapBlock() {

			@Override
			public void map(Map<String, Object> document, CBLViewMapEmitBlock emitter) {
				String type = (String) document.get("type");
				if(type.equals("person")){
					emitter.emit(document.get("name"),document);
				}
			}
			
		}, null, "1.0");
		
		//Age View
		view = this.db.getViewNamed(String.format("%s/%s",DOC_NAME,CouchLiteTest.viewNames.get("byAge")));
		view.setMapReduceBlocks(new CBLViewMapBlock() {

			@Override
			public void map(Map<String, Object> document, CBLViewMapEmitBlock emitter) {
				String type = (String) document.get("type");
				if(type.equals("person")){
					emitter.emit(document.get("age"),document);
				}
			}
			
		}, null, "1.0");
		
		//Group by Age View
		view = this.db.getViewNamed(String.format("%s/%s",DOC_NAME,CouchLiteTest.viewNames.get("groupByAge")));
		view.setMapReduceBlocks(new CBLViewMapBlock() {

			@Override
			public void map(Map<String, Object> document, CBLViewMapEmitBlock emitter) {
				String type = (String) document.get("type");
				if(type.equals("person")){
					emitter.emit(document.get("age"),1);
				}
			}
			
		}, new CBLViewReduceBlock() {

			@Override
			public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {
				Log.i(LTAG,"Reduce: "+keys.size());
				return values.size();
			}
			
		}, "1.0");
		
		//Sum price by buyer
			view = this.db.getViewNamed(String.format("%s/%s",DOC_NAME,CouchLiteTest.viewNames.get("sumBuyer")));
			view.setMapReduceBlocks(new CBLViewMapBlock() {

				@Override
				public void map(Map<String, Object> document, CBLViewMapEmitBlock emitter) {
					String type = (String) document.get("type");
					if(type.equals("transaction")){
						emitter.emit(document.get("buyer"),document.get("price"));
					}
				}
				
			}, new CBLViewReduceBlock() {

				@Override
				public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {
					Log.i(LTAG,"Reduce: "+keys.size()+" "+keys.get(0));
					float sum = 0;
					for(Object v: values){
						sum += Float.valueOf((String)v);
					}
					return sum;
				}
				
			}, "1.0");
			
			//Sum price by buyer
				view = this.db.getViewNamed(String.format("%s/%s",DOC_NAME,CouchLiteTest.viewNames.get("nameByTransaction")));
				view.setMapReduceBlocks(new CBLViewMapBlock() {
	
					@Override
					public void map(Map<String, Object> document, CBLViewMapEmitBlock emitter) {
						String type = (String) document.get("type");
						if(type.equals("person")) {
							emitter.emit(new String[] {(String) document.get("_id"),"0"}, new String[] {(String)document.get("name"),(String)document.get("age")});
						}
						else if(type.equals("transaction")){
							emitter.emit(new String[] {"p_"+document.get("buyer"),"1"}, null);
						}
					}
					
				}, new CBLViewReduceBlock() {
	
					@Override
					public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {
						Log.i(LTAG,"Reduce: "+keys.size()+" "+keys.get(0)+" "+values.get(0));
						ArrayList<String> p = (ArrayList<String>) values.get(0);
						ArrayList<ObjectNode> l = new ArrayList<ObjectNode>();
						for(int i=1; i<values.size(); i++){
							ObjectNode o = JsonNodeFactory.instance.objectNode();
							o.put("t_id",""+keys.get(i));
							o.put("name",p.get(0));
							o.put("age",p.get(1));
							l.add(o);
						}
						return l;
					}
					
				}, "1.0");
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
		ObjectNode item = JsonNodeFactory.instance.objectNode();
		
		item.put("_id","p_"+row.id);
		item.put("type","person");
		item.put("name",row.name);
		item.put("color",row.color);
		item.put("age",""+row.age);
		item.put("gender",row.gender);
		
		this.couchDbConnector.create(item);
		Log.i(LTAG,String.format("Insert Person %d",row.id));
	}

	@Override
	public void insertTransactionRecord(TransactionRow row) {
		ObjectNode item = JsonNodeFactory.instance.objectNode();
		
		item.put("_id","t_"+row.id);
		item.put("type","transaction");
		item.put("seller",""+row.seller);
		item.put("buyer",""+row.buyer);
		item.put("price",""+row.price);
		item.put("item",row.item);
		item.put("date",row.date);

		this.couchDbConnector.create(item);
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
		JsonNode n = this.couchDbConnector.find(JsonNode.class, "p_"+id);
		Log.i(LTAG,"Found: "+n.toString());
		return 1;
	}

	@Override
	public int selectTest2(String name) {
		ViewQuery vq = new ViewQuery().designDocId(DOC_ID).viewName(viewNames.get("byName")).key(name);
		ViewResult vr = this.couchDbConnector.queryView(vq);
		Log.i(LTAG,"Select 2: "+vr.getSize());
		return vr.getSize();
	}

	@Override
	public int selectTest3(int startAge, int endAge) {
		ViewQuery vq = new ViewQuery().designDocId(DOC_ID).viewName(viewNames.get("byAge")).startKey(""+startAge).endKey(""+endAge);
		ViewResult vr = this.couchDbConnector.queryView(vq);
		Log.i(LTAG,"Select 3: "+vr.getSize());
		return vr.getSize();
	}

	@Override
	public int selectTest4() {
		ViewQuery vq = new ViewQuery().designDocId(DOC_ID).viewName(viewNames.get("groupByAge")).group(true);
		ViewResult vr = this.couchDbConnector.queryView(vq);
		return vr.getSize();
	}

	@Override
	public int selectTest5() {
		ViewQuery vq = new ViewQuery().designDocId(DOC_ID).viewName(viewNames.get("sumBuyer")).group(true);
		ViewResult vr = this.couchDbConnector.queryView(vq);
		this.logResult(vr);
		return vr.getSize();
	}

	@Override
	public int selectTest6(int age) {
		ViewQuery vq = new ViewQuery().designDocId(DOC_ID).viewName(viewNames.get("nameByTransaction")).group(true).groupLevel(1);
		ViewResult vr = this.couchDbConnector.queryView(vq);
		
		//new node for output
		ArrayList<JsonNode> node = new ArrayList<JsonNode>();
		for(Row row: vr.getRows()) {
			if(row.getValueAsNode().size() == 0) {
				continue; // no join data
			}
			int tmp_age = row.getValueAsNode().get(0).get("age").asInt(-1);
			//Log.i(LTAG,"Age: "+tmp_age);
			if(tmp_age <= age){
				Log.i(LTAG,"Age Match: "+tmp_age);
				JsonNode js = row.getValueAsNode();
				node.add(js);
			}
		}
		return node.size();
	}

	@Override
	public String getName() {
		return CouchLiteTest.LTAG;
	}
	
	public int count() {
		return this.couchDbConnector.getAllDocIds().size();
	}
	
	private void logResult(ViewResult r){
		for(Row row: r.getRows()){
			Log.d(LTAG,String.format("ResultRow: %s -> %s",row.getKey(),row.getValue()));
		}
	}

}
