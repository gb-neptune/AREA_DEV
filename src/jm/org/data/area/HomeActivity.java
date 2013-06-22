package jm.org.data.area;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

/**HomeActivity
 *
 *DESC:		Main application activity. 
 */
public class HomeActivity extends BaseActivity {
	private static final String TAG = HomeActivity.class.getSimpleName();
	
	//Class instance variables used to track app and screen activity to send google analytics
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check for application initialization preference. If false, then runs startup activity
		if (!area.prefs.getBoolean("startupActivity", false)) {
			if (!area.initIsRunning) // Run startup activity
				startActivityForResult(new Intent(HomeActivity.this,
						StartupActivity.class), 0);
		}

		setContentView(R.layout.home_dashboard);
		// if (area.areaService != null) {
		// Log.e(TAG, "Running API call on service");
		// area.areaService.genericSearch(IDS_SEARCH, "TX.VAL.AGRI.ZS.UN", new
		// String[]{"Jamaica", "Kenya","Barbados"});
		// }
		
	    mGaInstance = GoogleAnalytics.getInstance(this);
	    mGaTracker = mGaInstance.getTracker(this.getString(R.string.google_tracking_id));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.home, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// only for android newer than gingerbread
			// TODO Implement a Search Dialog fall back for compatibility with
			// Android 2.3 and lower
			// Currently crashes on Gingerbread or lower
			
			// Get the SearchView and set the searchable configuration
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu
					.findItem(R.id.menu_search).getActionView();
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(true); // Do not iconify the
													// widget; expand it by
													// default
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_prefs:
			//Send Google Analytics ui event
			mGaTracker.sendEvent(this.getString(R.string.analytics_catergory_menu_action), 
								 this.getString(R.string.analytics_action_menu_option), 
								 this.getString(R.string.analytics_label_prefrence),
								 (long)01);
			startActivity(new Intent(HomeActivity.this,AreaPreferencesActivity.class));	
			break;
		case R.id.menu_startup:
			//Send Google Analytics ui event
			mGaTracker.sendEvent(this.getString(R.string.analytics_catergory_menu_action), 
								 this.getString(R.string.analytics_action_menu_option),
								 this.getString(R.string.analytics_label_startup),
								 (long)01);
			startActivity(new Intent(HomeActivity.this, StartupActivity.class));			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * DESC:	Function called on the completion (success||failure) of the Startup Activity
	 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_OK) {
			Editor editor = area.prefs.edit();
			editor.putBoolean(getString(R.string.pref_startupKey), true);
			editor.commit();

			IndicatorListFragment inFragment = (IndicatorListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.listFragment);
			inFragment.reload();
		}
		else {	//Startup Failed
			Toast.makeText(HomeActivity.this, "There was an error running the application initialization. Please try again.", Toast.LENGTH_SHORT).show();
		}
	}
	
	//Adding google analytics tracking feature to this activity
	@Override
	public void onStart(){
		super.onStart();
		EasyTracker.getInstance().setContext(this);		
		mGaTracker.sendView(this.getString(R.string.analytics_screen_home));
	}
	
	@Override
	public void onStop(){
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

}
