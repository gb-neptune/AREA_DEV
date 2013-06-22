package jm.org.data.area;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class AreaPreferencesActivity extends PreferenceActivity {
	//Class instance variables used to track app and screen activity to send google analytics
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Preference startupPreference = findPreference(getString(R.string.pref_startupKey));
		PreferenceScreen preferenceScreen = getPreferenceScreen();
		preferenceScreen.removePreference(startupPreference);
		
		mGaInstance = GoogleAnalytics.getInstance(this);
	    mGaTracker = mGaInstance.getTracker(this.getString(R.string.google_tracking_id));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, AreaActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	//Adding google analytics tracking feature to this activity
	@Override
	public void onStart(){
		super.onStart();
		EasyTracker.getInstance().setContext(this);		
		mGaTracker.sendView(this.getString(R.string.analytics_screen_prefrence));
		GAServiceManager.getInstance().dispatch();
		Toast.makeText(this, "Dispatch Complete",5).show();
	}
	@Override
	public void onPause(){
		super.onPause();
		EasyTracker.getInstance().activityStop(this);
	}
	@Override
	public void onStop(){
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

}
