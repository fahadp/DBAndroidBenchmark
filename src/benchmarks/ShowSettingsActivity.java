package benchmarks;

import edu.cs.washington.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class ShowSettingsActivity extends PreferenceActivity  {

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	
		 super.onCreate(savedInstanceState);
		 addPreferencesFromResource(R.xml.preferences);     
	 }
	
}

