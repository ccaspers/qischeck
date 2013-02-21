package net.pixeltronics.qischeck.ui;

import net.pixeltronics.qischeck.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;

public class LoginActivity extends SherlockActivity {
	
	public static final String TAG = "LoginActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.login);
    }

    
    public void login(View view){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);    	
    	
    	EditText unameTxt	= (EditText) findViewById(R.id.username);
    	EditText pwdTxt		= (EditText) findViewById(R.id.password);
    	String username		= unameTxt.getText().toString();
    	String password		= pwdTxt.getText().toString();
    	
    	Editor editor = prefs.edit();
    	editor.putString("username", username);
    	editor.putString("password", password);
    	editor.commit();
    	    	
    	finish();

    }
}
