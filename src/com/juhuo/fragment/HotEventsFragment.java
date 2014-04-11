package com.juhuo.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.adapter.HotEventsAdapter;
import com.juhuo.tool.MyListView;
import com.juhuo.tool.MyListView.OnRefreshListener;
import com.juhuo.welcome.R;

public class HotEventsFragment extends Fragment{
	private Resources mResources;
	private String TAG = "HotEventsFragment";
	private ImageView actionTitleImg;
	private ImageView actionTitleImg2;
	private TextView actionTitle;
	private RelativeLayout parent;
	private MyListView hotEventsList;
	private ListView subFilterView01,subFilterView02;
	private Button filterAllEvent,filterDefaultEvent;
	private View transView;
	private HotEventsAdapter hotEventsAdapter;
	Animation animationSlideDown;
	
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
				R.layout.hot_event, null);
		hotEventsList = (MyListView)parent.findViewById(R.id.hotevents_listview);
		hotEventsList.setonRefreshListener(new OnRefreshListener() {
			
			public void onRefresh() {
				// TODO Auto-generated method stub
				Log.i("pull", "refresh");
				doSth();
			}
		});
		
		
		hotEventsAdapter = new HotEventsAdapter();
		hotEventsAdapter.setInflater(
				(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
				getActivity());
		hotEventsList.setAdapter(hotEventsAdapter);
		
		subFilterView01 = (ListView)parent.findViewById(R.id.subfiltertitle);
		String[] data = {"google","amazon","facebook"};
		subFilterView01.setAdapter(new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_single_choice, data));
		
		transView = (View)parent.findViewById(R.id.transview);
		actionTitleImg = (ImageView)parent.findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)parent.findViewById(R.id.action_title_img2);
		actionTitle = (TextView)parent.findViewById(R.id.action_title);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_navi));
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_search));
		actionTitleImg2.setVisibility(View.VISIBLE);
		actionTitle.setText(mResources.getString(R.string.hot_event));
		
		filterAllEvent = (Button)parent.findViewById(R.id.filter_all_events);
		filterDefaultEvent = (Button)parent.findViewById(R.id.filter_default_event);
		filterAllEvent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				animationSlideDown = AnimationUtils.loadAnimation(getActivity(),R.anim.slidedown);
				animationSlideDown.setAnimationListener(animationSlideDownListener);
				subFilterView01.startAnimation(animationSlideDown);
				subFilterView01.setVisibility(View.VISIBLE);
			}
		});
		return parent;
	}
	public void doSth(){
		hotEventsList.onRefreshComplete();
	}
	public void setTrans(){
		Log.i("sliding menu", transView.getBackground().toString());
		transView.setVisibility(View.VISIBLE);
	}
	public void setTransBack(){
		Log.i("sliding menu", transView.getBackground().toString());
		transView.setVisibility(View.INVISIBLE);
	}
	AnimationListener animationSlideDownListener
	= new AnimationListener(){

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			subFilterView01.clearAnimation();
			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}};

}
