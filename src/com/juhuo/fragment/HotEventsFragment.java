package com.juhuo.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juhuo.welcome.R;

public class HotEventsFragment extends Fragment{
	private Resources mResources;
	private String TAG = "HotEventsFragment";
	private ImageView actionTitleImg;
	private ImageView actionTitleImg2;
	private TextView actionTitle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout parent = (LinearLayout) inflater.inflate(
				R.layout.hot_event, null);
		actionTitleImg = (ImageView)parent.findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)parent.findViewById(R.id.action_title_img2);
		actionTitle = (TextView)parent.findViewById(R.id.action_title);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_navi));
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_search));
		actionTitleImg2.setVisibility(View.VISIBLE);
		actionTitle.setText(mResources.getString(R.string.hot_event));
		return parent;
	}

}
