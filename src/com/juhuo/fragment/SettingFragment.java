package com.juhuo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.welcome.FeedBackActivity;
import com.juhuo.welcome.MyService;
import com.juhuo.welcome.R;

public class SettingFragment extends Fragment{
	private String TAG = "SettingFragment";
	private ImageView actionTitleImg;
	private TextView actionTitle;
	private RelativeLayout parent,actionTitleLay,feedbackLay;
	private Resources mResources;
	private View transView;
	private Switch receiveNotification;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parent = (RelativeLayout) inflater.inflate(
				R.layout.setting, null);
		actionTitle = (TextView)parent.findViewById(R.id.action_title);
		actionTitle.setText(mResources.getString(R.string.setting));
		actionTitleImg = (ImageView)parent.findViewById(R.id.action_title_img);
		actionTitleLay = (RelativeLayout)parent.findViewById(R.id.action_title_lay);
		feedbackLay = (RelativeLayout)parent.findViewById(R.id.feedbackLay);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_navi));
		transView = (View)parent.findViewById(R.id.transview);
		receiveNotification = (Switch)parent.findViewById(R.id.receive_notice_swi);
		actionTitleLay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((SlidingFragmentActivity)getActivity()).toggle();
			}
		});
		feedbackLay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),FeedBackActivity.class);
				startActivity(intent);
			}
		});
		Log.i(TAG, String.valueOf(JuhuoConfig.notification));
		if(JuhuoConfig.notification){
			receiveNotification.setChecked(true);
		}else{
			receiveNotification.setChecked(false);
		}
		receiveNotification.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        // do something, the isChecked will be
		        // true if the switch is in the On position
		    	if(isChecked){
		    		// get device id
		    		String devId ="";
		            TelephonyManager tel = (TelephonyManager) 
		            getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		            if (tel != null) {
		            	devId = tel.getDeviceId();
		            }
		    		Intent intent = new Intent(MyService.START);
		    		intent.putExtra("ch", "C53aa42ae78f67");  
		    		intent.putExtra("devId", devId);    
		    		getActivity().startService(intent);
		    		JuhuoConfig.notification = true;
		    	}else{
		    		Intent intent = new Intent(MyService.STOP);
		    		getActivity().startService(intent);
		    		JuhuoConfig.notification = false;
		    	}
		    }
		});
		return parent;
	}
	//make background transparent
	public void setTrans(){
		Log.i("sliding menu", transView.getBackground().toString());
		transView.setVisibility(View.VISIBLE);
	}
	public void setTransBack(){
		Log.i("sliding menu", transView.getBackground().toString());
		transView.setVisibility(View.INVISIBLE);
	}

}
