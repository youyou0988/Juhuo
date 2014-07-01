package com.juhuo.welcome;

import android.content.Intent;
import android.util.Log;

import com.apns.APNService;

public class MyService extends APNService {
	public static final String ON_NTY = "com.juhuo.welcome.MyService.ON_NTY";
	public static final String START = "com.juhuo.welcome.MyService.START";
	public static final String STOP = "com.juhuo.welcome.MyService.STOP";
	
	
	@Override
	public void onStart(Intent intent, int startId) {
		Intent i = null;
		if(intent!=null){
			if (intent.getAction().equals(MyService.START) == true) {
				Log.i("start", "service");
				i = new Intent(APNService.START);
				String dev = intent.getStringExtra("devId");
				String ch = intent.getStringExtra("ch");
				i.putExtra("ch", ch);  
				i.putExtra("devId", dev); 
			}else if(intent.getAction().equals(MyService.STOP) == true){
				i = new Intent(APNService.STOP);
			}else{
				i = intent;
			}
		}
		super.onStart(i, startId);
	}


	@Override
	protected void onRecvNty(String str) {
		Intent intent = new Intent(ON_NTY);
		intent.putExtra("data", str);
		sendBroadcast(intent);
	}
	
}

