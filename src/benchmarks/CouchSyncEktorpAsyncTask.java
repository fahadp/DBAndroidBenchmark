package benchmarks;

import org.ektorp.DbAccessException;
import org.ektorp.android.util.EktorpAsyncTask;

import android.util.Log;


public abstract class CouchSyncEktorpAsyncTask extends EktorpAsyncTask {

	@Override
	protected void onDbAccessException(DbAccessException dbAccessException) {
		Log.e("COUCH_ASYNC", "DbAccessException in background", dbAccessException);
	}

}

