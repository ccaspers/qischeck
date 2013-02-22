package net.pixeltronics.qischeck.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.pixeltronics.qischeck.R;
import net.pixeltronics.qischeck.qis.MIParser;
import net.pixeltronics.qischeck.qis.QIS;
import net.pixeltronics.qischeck.qis.WebParser;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.widget.SimpleAdapter;

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
	
	/**
	 * AsyncTask um Noten unabhängig vom UI-Thread abzurufen
	 * 
	 * @author Christian Caspers
	 *
	 */
	private class AsyncGrades extends AsyncTask<String, Integer, List<Map<String,String>>> {
 	
		private boolean errorOccured = false;
		private boolean shouldRefresh = false;
    	@Override
		protected void onPreExecute(){
			super.onPreExecute();
				showProgress();
		}

		@Override
		protected List<Map<String,String>> doInBackground(String... params) {
			if(grades == null){
				grades = new ArrayList<Map<String,String>>();
		        try{
			        qis.login();
			        grades = qis.getGrades();
			        qis.logout();
					if(getListAdapter() != null){
						shouldRefresh = true;
					}
		        }catch(Exception e){
		        	Log.e(TAG, "fehler mit asi", e);
		        	errorOccured = true;
		        }
			}
	        return grades;
		}
    	
		@Override		
		protected void onPostExecute(List<Map<String,String>> result){
			super.onPostExecute(result);
			setupAdapter(result,shouldRefresh);
			closeProgress();
			if(errorOccured){
				showError();
			}
		}
    	
    }
	private QIS qis;
	private ProgressDialog progress;
	private List<Map<String,String>> grades;
	
    public static final String TAG = "GradesView";

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
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
			updateView();
			break;
		case R.id.menu_logout:
			logout();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    
    @Override
    public void onConfigurationChanged(Configuration newConfig){
    	super.onConfigurationChanged(newConfig);
    	updateView();
    }

    @Override
	protected void onResume(){
		super.onResume();
		if(grades == null){
	        updateView();
		}
	}

	private void updateView() {
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);        
	    String uname = prefs.getString("username",null);
	    String pword = prefs.getString("password",null);
	    WebParser parser = new MIParser();
		if(uname != null && pword != null){
	    	qis = new QIS(uname,pword,parser);
	    	new AsyncGrades().execute("");
	    }else{
	    	Intent login = new Intent(this, LoginActivity.class);
	    	startActivity(login);
	    }
	}
 
    private void setupAdapter(List<Map<String,String>> grades,boolean shouldRefresh){
    	if(getListView().getAdapter() == null || shouldRefresh){
		    SimpleAdapter gradesAdapter = new SimpleAdapter(
		    		this,
		    		grades,
		    		R.layout.grade_row,
		    		new String[] { 	"id",
		    						"note",
		    						"result",
		    						"semester",
		    						"status",
		    						"title"
		    		},
		    		new int[] { R.id.grade_id,
		    					R.id.grade_note,
		    					R.id.grade_result,
		    					R.id.grade_semester,
		    					R.id.grade_status,
		    					R.id.grade_title
					}
			);	
		    setListAdapter(gradesAdapter);
    	}
	}

	private void logout(){		
		setListAdapter(null);
		grades = null;		
		getListView().invalidate();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
		
		updateView();
		
	}
    
    private void showProgress(){
		if(grades == null)
			progress = ProgressDialog.show(this, "", "Warte kurz, deine Noten werden geladen...", true);
	}

	private void closeProgress(){
		if(progress != null && progress.isShowing())
			progress.dismiss();
	}

	private void showError(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Beim Download der Daten ist ein Fehler aufgetreten.")
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                // nothing here
		           }
		       });
		builder.create().show();
	}	

}
