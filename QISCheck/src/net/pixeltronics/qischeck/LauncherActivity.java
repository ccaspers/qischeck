/**
 * 
 */
package net.pixeltronics.qischeck;

import net.pixeltronics.qischeck.ui.GradesView;
import net.pixeltronics.qischeck.ui.LoginActivity;
import net.pixeltronics.qischeck.ui.Settings;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

/**
 * @author Christian
 *
 */
public class LauncherActivity extends Activity {


	@Override
	protected void onResume() {
		Log.v("LAUNCHER", "onResume LAUNCHER");
		super.onResume();
		if(Settings.isLoggedIn(getApplicationContext())){
			showGradesActivity();
		}else{
			showLoginActivity();
		}
	}
	
	private void showGradesActivity() {
		Intent intent = new Intent(this, GradesView.class);
		startActivity(intent);
		finish();
	}
	
	private void showLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
}
