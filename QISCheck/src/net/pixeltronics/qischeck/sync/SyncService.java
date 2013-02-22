package net.pixeltronics.qischeck.sync;

import java.io.IOException;
import java.util.List;

import net.pixeltronics.qischeck.db.GradesContract;
import net.pixeltronics.qischeck.qis.MIParser;
import net.pixeltronics.qischeck.qis.QIS;
import net.pixeltronics.qischeck.qis.WebParser;
import net.pixeltronics.qischeck.ui.Settings;

import org.apache.http.client.ClientProtocolException;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class SyncService extends IntentService {

	public static final String TAG = SyncService.class.getName();
	public static final String ACTION_LOGOUT = TAG+"LOGOUT";
	private NotificationManager mNotificationManager;
	
	public SyncService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int notifyID = 1;
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		showNotification(notifyID);
		downloadGrades();
		cancelNotification(notifyID);
	}

	private void cancelNotification(int notifyID) {
		mNotificationManager.cancel(notifyID);
	}

	private void showNotification(int notifyID) {
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
		    .setContentTitle("QIS Sync")
		    .setContentText("Deine Noten werden aktualisiert")
		    .setTicker("QIS Sync gestartet");
		mNotificationManager.notify( notifyID, mNotifyBuilder.build());
		
	}

	private void downloadGrades() {
	    String uname = Settings.getUserName(getApplicationContext());
	    String pword = Settings.getPassword(getApplicationContext());
	    
		WebParser parser = new MIParser();
		QIS qis = new QIS(uname, pword, parser);
		try{
	        qis.login();
	        List<ContentValues> categories = qis.getCategories();
	        List<ContentValues> grades = qis.getGrades();
			insertValues(GradesContract.Category.BASE_URI, categories);
	        insertValues(GradesContract.Grade.BASE_URI, grades);
	        qis.logout();
		}catch(ClientProtocolException e1){
			toast("ClientProtocolException");
			Log.e(TAG,"Error while syncing grades", e1);
		}catch(IOException e2){
			toast("IOException");
			Log.e(TAG,"Error while syncing grades", e2);
		}catch(ArrayIndexOutOfBoundsException e3){
			Log.v(TAG, "Login fehlgeschlagen");
			LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
			Intent logout = new Intent(ACTION_LOGOUT);
			localBroadcastManager.sendBroadcast(logout);
		}
	}

	private void toast(String msg) {
		Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 50);
		toast.show();
	}

	
// TODO: BATCH MODE
//	private void insertValues(Uri uri, List<ContentValues> values){
//		ContentResolver cr = getContentResolver();
//		ArrayList<ContentProviderOperation> ops =  new ArrayList<ContentProviderOperation>();
//		for(ContentValues row : values){
//			ops.add(ContentProviderOperation.newInsert(uri).withValues(row).build());
//		}
//		try {
//			cr.applyBatch(GradesContract.AUTH, ops);
//		} catch (RemoteException e) {
//			Log.e(TAG,"Error while inserting values", e);
//		} catch (OperationApplicationException e) {
//			Log.e(TAG,"Error while inserting values", e);
//		}
//	}
	
	private void insertValues(Uri uri, List<ContentValues> values){
		ContentResolver cr = getContentResolver();
		for(ContentValues row : values){
			try{
				Uri inserturi = cr.insert(uri, row);
				Log.v(TAG,"Inserting to " + inserturi.toString());
			}catch(Exception e){
				Log.e(TAG, "Knallt bei :" + row.toString(), e);
			}
		}
	}
}
