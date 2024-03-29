package jm.org.data.area;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

/**
 *  DESC: Called when the Area application is first created. Activity downloads initial indicator names
 *  		country listings, and other initial data  from the World Bank API 
 *  
 **/
public class StartupActivity extends Activity {
	private static final String TAG = StartupActivity.class.getSimpleName();
	private boolean isRunning = false;

	protected boolean _active = true;
	protected int _splashTime = 1; // time to display the splash screen in ms

	private AreaApplication area;
	private ViewAnimator loadingAnimator;
	
	//Class instance variables used to track app and screen activity to send google analytics
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startupview);
		loadingAnimator = (ViewAnimator)findViewById(R.id.startupSwitcher);	//Loading Animator
		area = (AreaApplication) getApplication();
		
		//google analytics tracking 
		mGaInstance = GoogleAnalytics.getInstance(this);
	    mGaTracker = mGaInstance.getTracker(getResources().getString(R.string.google_tracking_id));
	    
	   

		if (!area.checkNetworkConnection()) {	//Check the Internet connection
			Log.e(TAG, "No Internet connectivity");
			Toast.makeText(
					StartupActivity.this,
					"There was an error connecting to the Internet. Please check your connection and start the application again",
					Toast.LENGTH_LONG).show();
		} else {
			if (!isRunning) 	//Check if initialization activity is already running
				new startupRequest().execute();
		}
	}

	private class startupRequest extends AsyncTask<Void, Void, Boolean> {
		private long startTime;
		private long elapsedTime;
		protected void onPreExecute() {
			//set the current time before executing start up services			
			startTime = System.currentTimeMillis();
			
			loadingAnimator.setDisplayedChild(0);
			area.initIsRunning = true;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// initial pull of country and indicator data
				area.areaData.updateAPIs();
				area.areaData.updatePeriod();

				// Error when debugging needs to be tested
				area.areaData.updateIndicators();
				area.areaData.updateCountries();

				// to test generic search
				//area.areaData.genericSearch(WORLD_SEARCH, "TX.VAL.AGRI.ZS.UN", new String[]{"Jamaica", "Kenya","Barbados", "World"});

				return true;
				
			} catch (Exception e) {
				Log.e(TAG, "Exception updating Area Data " + e.toString());
				loadingAnimator.setDisplayedChild(1);
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean initResult) {
			super.onPostExecute(initResult);
			
			if (initResult) {
				Log.e(TAG, "Correctly completed initialization");
				area.initIsRunning = false;
				setResult(RESULT_OK, new Intent());
				
				finish();
			} else {
				Log.e(TAG, "Failed initialization");
				/*Toast.makeText(
						StartupActivity.this,
						"An error was encountered while completing application initilization. " +
								"Please check your internet connection and start activity again.",
								Toast.LENGTH_LONG).show();
				*/
			}
			elapsedTime = System.currentTimeMillis()- startTime;
			mGaTracker.sendTiming("resources", elapsedTime, "setup_time", null);
			GAServiceManager.getInstance().dispatch();
		}
	}

	//Adding google analytics tracking feature to this activity
	@Override
	public void onStart(){
		super.onStart();
		EasyTracker.getInstance().setContext(this);		
		mGaTracker.sendView(this.getString(R.string.analytics_screen_startup));
		GAServiceManager.getInstance().dispatch();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}


	/*
	 * // TODO: default with 0 specify constants for apis
	 * 
	 * private void getCountryList() { int numOfCountries =
	 * mJsonParse.getWBTotal(mApiPull.HTTPRequest(0,
	 * "http://api.worldbank.org/country?per_page=1&format=json"));
	 * if(numOfCountries == 0 ){ // error in parsing JSON data Log.e(TAG,
	 * "Error In Parsing JSON data"); }else{
	 * mJsonParse.parseCountries(mApiPull.HTTPRequest(0,
	 * "http://api.worldbank.org/country?per_page="+ numOfCountries
	 * +"&format=json")); } }// end function
	 * 
	 * private void getIndicatorsList() {
	 * 
	 * int numOfIndicators = mJsonParse.getWBTotal(mApiPull.HTTPRequest(0,
	 * "http://api.worldbank.org/topic/1/Indicator?per_page=10&format=json"));
	 * if(numOfIndicators == 0 ){ // error in parsing JSON data Log.e(TAG,
	 * "Error In Parsing JSON data"); }else{
	 * mJsonParse.parseIndicators(mApiPull.HTTPRequest(0,
	 * "http://api.worldbank.org/topic/1/Indicator?per_page="+ numOfIndicators
	 * +"&format=json")); } }// end function
	 */
}