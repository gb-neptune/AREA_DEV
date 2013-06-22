package jm.org.data.area;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class ReportDetailViewActivity extends BaseActivity {
	public final String TAG = getClass().getSimpleName();
	//Class instance variables used to track app and screen activity to send google analytics
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_detail_view);
		Log.e(TAG, "Creating Reports View");
		
		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker(this.getString(R.string.google_tracking_id));
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// only for android newer than gingerbread
			 ActionBar actionBar = getActionBar();
			 actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	//Adding google analytics tracking feature to this activity
		@Override
		public void onStart(){
			super.onStart();
			EasyTracker.getInstance().setContext(this);		
			mGaTracker.sendView(this.getString(R.string.analytics_screen_report_detail_viewer));
		}
		
		@Override
		public void onStop(){
			super.onStop();
			EasyTracker.getInstance().activityStop(this);
		}
		

}
