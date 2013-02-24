package net.pixeltronics.qischeck.ui;

import net.pixeltronics.qischeck.R;
import net.pixeltronics.qischeck.sync.SyncService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class Settings extends SherlockPreferenceActivity {

	private static final String SYNC_TOGGLE = "syncToggle";
	private static final String SYNC_TIME = "synctime";
	
	OnSharedPreferenceChangeListener listener;
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        listener = createListener();
		getPrefs(this).registerOnSharedPreferenceChangeListener(listener);
    }
    
    private OnSharedPreferenceChangeListener createListener() {
    	return new OnSharedPreferenceChangeListener() {
			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences prefs,
					String key) {
				if(key.equals(SYNC_TOGGLE) || key.equals(SYNC_TIME)){
					boolean autosync = prefs.getBoolean(SYNC_TOGGLE, false);
					AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
					Intent sync = new Intent(getApplicationContext(), SyncService.class);
					PendingIntent operation = PendingIntent.getService(getApplicationContext(), 0, sync, PendingIntent.FLAG_UPDATE_CURRENT);
					if(autosync){
						int intervall = Integer.valueOf(prefs.getString(SYNC_TIME, "60"));
						long intervalMillis = intervall * 60 * 1000;
						long triggerAtMillis = SystemClock.elapsedRealtime() + intervalMillis;
						int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
						alarm.setRepeating(type, triggerAtMillis, intervalMillis, operation);
					}else{
						alarm.cancel(operation);
					}
				}
			}
		};
	}

	private static SharedPreferences getPrefs(Context context){
    	return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    public static String getUserName(Context context){
    	return getPrefs(context).getString("username",null);
    }
    
    public static String getPassword(Context context){
    	return getPrefs(context).getString("password",null);
    }

	public static void clear(Context context) {
		Editor edit = getPrefs(context).edit();
		edit.clear();
		edit.commit();
	}
	
	public static boolean isLoggedIn(Context context){
	    String uname = getUserName(context);
	    String pword = getPassword(context);
	    return uname != null && pword != null;
	}
}
