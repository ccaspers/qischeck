package net.pixeltronics.qischeck.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.pixeltronics.qischeck.db.GradesContract;
import net.pixeltronics.qischeck.qis.MIParser;
import net.pixeltronics.qischeck.qis.QIS;
import net.pixeltronics.qischeck.qis.WebParser;
import net.pixeltronics.qischeck.ui.Settings;

import org.apache.http.client.ClientProtocolException;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class SyncService extends IntentService {

	public static final String TAG = SyncService.class.getName();
	public static final String ACTION_LOGOUT = TAG+"LOGOUT";
	private Handler mHandler;
	
	public SyncService() {
		super(TAG);
		 mHandler = new Handler();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		NotificationUtil.showSyncInProgressNotification(this);
		downloadGrades();
		NotificationUtil.cancelNotification(this,NotificationUtil.ID_SYNC);
	}

	private void downloadGrades() {
	    String uname = Settings.getUserName(getApplicationContext());
	    String pword = Settings.getPassword(getApplicationContext());
	    
		WebParser parser = new MIParser();
		QIS qis = new QIS(uname, pword, parser);
		try{
	        qis.login();
	        
	        List<ContentValues> categories = qis.getCategories();
	        notificationForNewRows(GradesContract.Category.BASE_URI,categories);
	        insertValues(GradesContract.Category.BASE_URI, categories);
	        
	        List<ContentValues> grades = qis.getGrades();
	        notificationForNewRows(GradesContract.Grade.BASE_URI, grades);
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

	private void notificationForNewRows(Uri baseUri, List<ContentValues> values) {
		ArrayList<Integer> webIds = new ArrayList<Integer>();
		ArrayList<Integer> dbIds = getDatabaseIDList(baseUri);
		
		for(ContentValues v : values){
			webIds.add(v.getAsInteger(BaseColumns._ID));
		}
		webIds.removeAll(dbIds);
		
		if(webIds.size() > 0){
			NotificationUtil.showNewGradesReceivedNotification(this);
		}
	}

	private ArrayList<Integer> getDatabaseIDList(Uri baseUri) {
		ContentResolver cr = getContentResolver();
		ArrayList<Integer> dbIds = new ArrayList<Integer>();
		Cursor query = cr.query(baseUri, new String[]{BaseColumns._ID}, null, null, null);
		if(query.moveToFirst()){
			do{
				dbIds.add(query.getInt(0));
			}while(query.moveToNext());
		}
		query.close();
		return dbIds;
	}

	private void toast(final String msg) {
		final Context ctx = getApplicationContext();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 50);
				toast.show();
			}
		});
	}

	
	private void insertValues(Uri uri, List<ContentValues> values){
		ContentResolver cr = getContentResolver();
		ArrayList<ContentProviderOperation> ops =  new ArrayList<ContentProviderOperation>();
		for(ContentValues row : values){
			ops.add(ContentProviderOperation.newInsert(uri).withValues(row).build());
		}
		try {
			cr.applyBatch(GradesContract.AUTH, ops);
		} catch (RemoteException e) {
			Log.e(TAG,"Error while inserting values", e);
			toast("Fehler beim Eintragen in Datenbank");
		} catch (OperationApplicationException e) {
			Log.e(TAG,"Error while inserting values", e);
			toast("Fehler beim Eintragen in Datenbank");
		}
	}
}
