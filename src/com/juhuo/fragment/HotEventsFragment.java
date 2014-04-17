package com.juhuo.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.juhuo.adapter.FilterEventAdapter;
import com.juhuo.adapter.HotEventsAdapter;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.MyListView;
import com.juhuo.tool.MyListView.OnRefreshListener;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.EventDetailActivity;
import com.juhuo.welcome.R;

public class HotEventsFragment extends Fragment{
	private Resources mResources;
	private String TAG = "HotEventsFragment";
	private ImageView actionTitleImg;
	private ImageView actionTitleImg2;
	private TextView actionTitle,noEventsText;
	private RelativeLayout parent,filterlistlayout;
	private MyListView hotEventsList;
	private ListView subFilterView01,subFilterView02;
	private Button filterAllEvent,filterDefaultEvent;
	private View transView,transView2;
	private HotEventsAdapter hotEventsAdapter;
	private FilterEventAdapter filterEventAdapter;
	Animation animationSlideDown;
	private List<AsyncTask<String,String,Object>> mAsyncTask = 
			new ArrayList<AsyncTask<String,String,Object>>();
	private JSONArray mData;
	private HashMap<String,Object> mapPara;
	private String[] eventType={"所有活动","交友聚会","读书看报","音乐电影","体育锻炼","其他"};
	private String[] eventType2={"默认排序","参加人数从多到少","活动距离从近到远","活动费用从多到少","活动费用从少到多"};
	private String[] eventPara = {"","participant_count","distance","cost","cost"};
	private int[] focus={1,0,0,0,0,0};
	private int[] focus2={1,0,0,0,0};
    
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		mapPara = new HashMap<String,Object>();
		mapPara.put("token", JuhuoConfig.token);
		mapPara.put("incremental", "true");
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
				getNetData(mapPara);
			}
		});
		
		
		filterlistlayout = (RelativeLayout)parent.findViewById(R.id.filterlistlayout);
		
		transView = (View)parent.findViewById(R.id.transview);
		transView2 = (View)parent.findViewById(R.id.transview2);
		
		actionTitleImg = (ImageView)parent.findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)parent.findViewById(R.id.action_title_img2);
		actionTitle = (TextView)parent.findViewById(R.id.action_title);
		noEventsText = (TextView)parent.findViewById(R.id.no_events_found);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_navi));
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_search));
		actionTitleImg2.setVisibility(View.VISIBLE);
		actionTitle.setText(mResources.getString(R.string.hot_event));
		
		filterAllEvent = (Button)parent.findViewById(R.id.filter_all_events);
		filterDefaultEvent = (Button)parent.findViewById(R.id.filter_default_event);
		filterAllEvent.setOnClickListener(onClickListener);
		filterDefaultEvent.setOnClickListener(onClickListener);
		
		subFilterView01 = (ListView)parent.findViewById(R.id.subfiltertitle);
		subFilterView01.setOnItemClickListener(listOnClickListener);
		//open the sliding menu
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((SlidingFragmentActivity)getActivity()).toggle();
			}
		});
		
		getNetData(mapPara);
		return parent;
	}
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Button mb = (Button)v;
			mb.setTextColor(mResources.getColor(R.color.mgreen));
			
			switch(v.getId()){
			case R.id.filter_all_events:
				filterDefaultEvent.setTextColor(mResources.getColor(R.color.mgray));
				filterEventAdapter = new FilterEventAdapter(eventType,getActivity(),focus);
				break;
			case R.id.filter_default_event:
				filterAllEvent.setTextColor(mResources.getColor(R.color.mgray));
				filterEventAdapter = new FilterEventAdapter(eventType2,getActivity(),focus2);
				break;
			}
			subFilterView01.setAdapter(filterEventAdapter);
			
			// TODO Auto-generated method stub
			animationSlideDown = AnimationUtils.loadAnimation(getActivity(),R.anim.slidedown);
			animationSlideDown.setAnimationListener(animationSlideDownListener);
			filterlistlayout.startAnimation(animationSlideDown);
			filterlistlayout.setVisibility(View.VISIBLE);
			filterlistlayout.setBackgroundColor(Color.WHITE);
			subFilterView01.setVisibility(View.VISIBLE);
			transView2.setVisibility(View.VISIBLE);
		}
	};
	OnItemClickListener listOnClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int position,
				long arg3) {
			//decide which view has been clicked
			if(parent.getChildCount()==focus.length){
				for(int i=0;i<focus.length;i++){
					focus[i] = 0;
				}focus[position]=1;
				filterAllEvent.setTextColor(mResources.getColor(R.color.mgray));
			}else{
				for(int i=0;i<focus2.length;i++){
					focus2[i] = 0;
				}focus2[position] = 1;
				filterDefaultEvent.setTextColor(mResources.getColor(R.color.mgray));
			}
			mapPara = new HashMap<String,Object>();
			mapPara.put("token", JuhuoConfig.token);
			mapPara.put("incremental", "true");
			int po=0;
			for(po=0;po<6;po++){
				if(focus[po]==1) break;
			}
			filterAllEvent.setText(eventType[po]);
			if(po!=0) mapPara.put("event_type", String.valueOf(po));
			int po2=0;
			for(po2=0;po2<5;po2++){
				if(focus2[po2]==1) break;
			}
			filterDefaultEvent.setText(eventType2[po2]);
			if(po2!=0) mapPara.put("sort_param", eventPara[po2]);
			if(po2==1){
				mapPara.put("sort_order", "DESC");
			}else if(po2==2){
//				mapPara.put("", value) need put lat and lgn
			}else if(po2==3){
				mapPara.put("sort_order", "DESC");
			}else if(po2==4){
				mapPara.put("sort_order", "ASC");
			}	
			filterEventAdapter.notifyDataSetChanged();
			
			getNetData(mapPara);
			filterlistlayout.setBackgroundResource(R.color.transparent);
			subFilterView01.setVisibility(View.GONE);
			transView2.setVisibility(View.INVISIBLE);
			// TODO Auto-generated method stub
			
		}
	};

	public void getNetData(HashMap<String,Object> map){
		noEventsText.setText("");
		hotEventsList.onRefreshing();
		LoadEventList loadEventList = new LoadEventList();
		loadEventList.execute(map);
	}
	private class LoadEventList extends AsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_LIST);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				hotEventsList.onRefreshComplete();
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				try {
					JSONArray ja = result.getJSONArray("events");
					if(ja.length()!=0) {
						hotEventsList.setVisibility(View.VISIBLE);
						mData = ja;
						hotEventsAdapter = new HotEventsAdapter();
						hotEventsAdapter.setInflater(
								(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
								getActivity());
						hotEventsAdapter.setData(mData);
						hotEventsAdapter.setListView(hotEventsList);
						hotEventsList.setAdapter(hotEventsAdapter);
					}else{
						//no events found
						hotEventsList.setVisibility(View.INVISIBLE);
						noEventsText.setText(mResources.getString(R.string.no_events_found));	
					}
					hotEventsList.onRefreshComplete();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
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
