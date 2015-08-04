package net.pixeltronics.qischeck.ui;

import net.pixeltronics.qischeck.R;
import net.pixeltronics.qischeck.sync.SyncService;
import android.content.Intent;
import android.os.Bundle;
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
    	
    	EditText unameTxt	= (EditText) findViewById(R.id.username);
    	EditText pwdTxt		= (EditText) findViewById(R.id.password);
    	
    	String username		= unameTxt.getText().toString();
    	String password		= pwdTxt.getText().toString();

    	Settings.updateCredentials(this,username,password);
    	    	
    	//Sync on Login
		Intent sync = new Intent(this, SyncService.class);
		startService(sync);
		
		Intent intent = new Intent(this, GradesView.class);
		startActivity(intent);
    	finish();

    }
}
