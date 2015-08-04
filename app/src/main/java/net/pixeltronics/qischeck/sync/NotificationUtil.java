package net.pixeltronics.qischeck.sync;

import net.pixeltronics.qischeck.LauncherActivity;
import net.pixeltronics.qischeck.R;
import net.pixeltronics.qischeck.ui.GradesView;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationUtil {
	public static final int ID_SYNC = 0;
	public static final int ID_NEW_GRADES = 1;
	
	public static void showSyncInProgressNotification(Context context) {
		Intent intent = new Intent(context, LauncherActivity.class);
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
		    .setContentTitle("QIS Sync")
		    .setSmallIcon(R.drawable.notification)
		    .setContentText("Deine Noten werden aktualisiert")
		    .setTicker("QIS Sync gestartet")
		    .setContentIntent(PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_ONE_SHOT));
		
		Notification notification = mNotifyBuilder.build();
		getManager(context).notify(ID_SYNC, notification);
	}
	
	public static void showNewGradesReceivedNotification(Context context) {
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
		    .setContentTitle("QIS Check")
		    .setSmallIcon(R.drawable.notification)
		    .setContentText("Neue Noten online")
		    .setLights(0xFFFF8800, 1000, 1000)
		    .setVibrate(new long[]{100,100,100})
		    .setTicker("QIS Sync gestartet");

		Intent actionIntent = new Intent(context, GradesView.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(GradesView.class);
		stackBuilder.addNextIntent(actionIntent);
		
		PendingIntent action = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mNotifyBuilder.setContentIntent(action);
		Notification notification = mNotifyBuilder.build();
		getManager(context).notify(ID_NEW_GRADES, notification);
	}
	
	
	public static void cancelNotification(Context context, int notifyID) {
		getManager(context).cancel(notifyID);
	}
	
	private static NotificationManager getManager(Context context){
		return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
		
}

