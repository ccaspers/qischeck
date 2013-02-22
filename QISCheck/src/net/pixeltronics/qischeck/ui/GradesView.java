package net.pixeltronics.qischeck.ui;

import net.pixeltronics.qischeck.R;
import net.pixeltronics.qischeck.db.DBContract;
import net.pixeltronics.qischeck.db.GradesContract;
import net.pixeltronics.qischeck.sync.SyncService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SimpleCursorAdapter;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Android Activity zum Darstellen einer Notenliste
 * 
 * @author Christian Caspers
 *
 */
public class GradesView extends SherlockListActivity {
	
	private Cursor grades;
	
    public static final String TAG = "GradesView";

	private BroadcastReceiver receiver;

	private LocalBroadcastManager localBroadcastManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	registerBroadcastReceiver();
    	checkLogin();
    	updateView();
    	registerContentObserver();
    }

	private void registerBroadcastReceiver() {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				logout();
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(SyncService.ACTION_LOGOUT);
		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(receiver, filter);
	}

	private void checkLogin() {
		if(!Settings.isLoggedIn(this)){
			logout();
		}
	}

	private void registerContentObserver() {
		ContentObserver co = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange, Uri uri) {
				updateView();
			}
		};
		getContentResolver().registerContentObserver(
				GradesContract.Grade.BASE_URI, true, co);

	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.activity_grades_view, menu);
    	return true;
    }
	
    
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_settings:
			Intent intent = new Intent(this, Settings.class);
			startActivity(intent);
			break;
		case R.id.menu_refresh:
			grades = null;
			Intent i = new Intent(this, SyncService.class);
			startService(i);
			break;
		case R.id.menu_logout:
			logout();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


    private void updateView() {
    	grades = getContentResolver().query(GradesContract.Grade.BASE_URI, GradesContract.Grade.PROJECTION_FULL, null, null, null);
    	setupAdapter(grades);
	}

	private void setupAdapter(Cursor grades){
	    SimpleCursorAdapter gradesAdapter = new SimpleCursorAdapter(
	    		this,
	    		R.layout.grade_row,
	    		grades,
	    		new String[]{	DBContract.Table.Grade.ID,
	    						DBContract.Table.Grade.RESULT,
	    						DBContract.Table.Grade.SEMESTER,
	    						DBContract.Table.Grade.STATUS,
	    						DBContract.Table.Grade.TITLE
	    		},
	    		new int[] { R.id.grade_id,
	    					R.id.grade_result,
	    					R.id.grade_semester,
	    					R.id.grade_status,
	    					R.id.grade_title
				},
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
		);	
	    setListAdapter(gradesAdapter);
    }

	private void logout(){		
		setListAdapter(null);
		grades = null;		
		getListView().invalidate();
		
		Settings.clear(this);
		
		getContentResolver().delete(GradesContract.LOGOUT_URI, null, null);
		
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		localBroadcastManager.unregisterReceiver(receiver);
	}
	

}
