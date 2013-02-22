package net.pixeltronics.qischeck.ui;

import net.pixeltronics.qischeck.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class Settings extends SherlockPreferenceActivity {

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
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
