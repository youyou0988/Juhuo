package com.juhuo.welcome;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = "MyBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
    	if (intent.getAction().equals(MyService.ON_NTY)) {
            String str = intent.getStringExtra("data");
            //todo, handle the data received
            showNotification(context,str);
        } 
    }
    private void showNotification(Context context,String s) {
		NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		long when = System.currentTimeMillis();
		Intent notificationIntent = new Intent(context,MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		Notification notification = new Notification(R.drawable.app_icon, "活动通知", when);
		notification.defaults = Notification.DEFAULT_VIBRATE;
		notification.setLatestEventInfo(context, "活动通知", "聚伙:" + s,
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(0, notification);
	}
}
