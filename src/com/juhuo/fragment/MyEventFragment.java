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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.juhuo.adapter.HotEventsAdapter;
import com.juhuo.control.MyListView;
import com.juhuo.control.MyListView.OnLoadListener;
import com.juhuo.control.MyListView.OnRefreshListener;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.CreateEvent;
import com.juhuo.welcome.R;

public class MyEventFragment extends Fragment{
	private Resources mResources;
	private String TAG = "MyEventFragment";
	private ImageView actionTitleImg;
	private ImageView actionTitleImg2;
	private TextView actionTitle,noEventsText;
	private RelativeLayout parent,filterlistlayout;
	private MyListView hotEventsList;
	private Button filterAllEvent,filterDefaultEvent;
	private View transView,transView2;
	private HotEventsAdapter hotEventsAdapter;
	private List<AsyncTask<String,String,Object>> mAsyncTask = 
			new ArrayList<AsyncTask<String,String,Object>>();
	private JSONArray mData;
	private HashMap<String,Object> mapPara;
	private String handle;
	private int filter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		mapPara = new HashMap<String,Object>();
		mapPara.put("organizer", String.valueOf(JuhuoConfig.userId));
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
				//check refresh whom
				mapPara = new HashMap<String,Object>();
				mapPara.put("token", JuhuoConfig.token);
				mapPara.put("incremental", "true");
				if(filter==0){
					mapPara.put("organizer", String.valueOf(JuhuoConfig.userId));
					getNetData(mapPara);
				}else{
					mapPara.put("related", String.valueOf(JuhuoConfig.userId));
					getNetData(mapPara);
				}
				
			}
		});
		hotEventsList.setonLoadListener(new OnLoadListener() {  
            
            @Override  
            public void onLoad() {  
                //TODO 加载更多  
                Log.e(TAG, "onLoad");
                if(handle.equals("")){
                	hotEventsList.onLoadComplete(); //加载更多完成  
                }else{
                	HashMap<String,Object> mapMore = new HashMap<String,Object>();
                    mapMore.put("token", JuhuoConfig.token);
                    mapMore.put("handle", handle);
//                    loadMoreData(mapMore);
                }
                
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
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.plus));
		actionTitleImg2.setVisibility(View.VISIBLE);
		actionTitle.setText(mResources.getString(R.string.my_event));
		
		//open the sliding menu
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((SlidingFragmentActivity)getActivity()).toggle();
			}
		});
		actionTitleImg2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),CreateEvent.class);
				startActivity(intent);
			}
		});
		filterAllEvent = (Button)parent.findViewById(R.id.filter_all_events);
		filterDefaultEvent = (Button)parent.findViewById(R.id.filter_default_event);
		filterAllEvent.setText(mResources.getString(R.string.my_organize));
		filterDefaultEvent.setText(mResources.getString(R.string.my_participant));
		filterAllEvent.setTextColor(mResources.getColor(R.color.mgreen));
		filterAllEvent.setOnClickListener(onClickListener);
		filterDefaultEvent.setOnClickListener(onClickListener);

		hotEventsList.setVisibility(View.VISIBLE);
		hotEventsAdapter = new HotEventsAdapter();
		hotEventsAdapter.setInflater(
				(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
				getActivity());
		JSONObject jsonCache = new JSONObject();
		//get date from cache
		jsonCache = Tool.loadJsonFromFile(JuhuoConfig.EVENTLISTSPECIFIC+JuhuoConfig.userId,getActivity());
		if(jsonCache==null){
			noEventsText.setText(mResources.getString(R.string.no_events_found));
			getNetData(mapPara);
		}else{
			try {
				hotEventsAdapter.setJSONData(jsonCache.getJSONArray("events"),"MY");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hotEventsAdapter.notifyDataSetChanged();
			hotEventsAdapter.setListView(hotEventsList);
			hotEventsList.setAdapter(hotEventsAdapter);
		}
		
		return parent;
	}
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Button mb = (Button)v;
			mb.setTextColor(mResources.getColor(R.color.mgreen));
			HashMap<String,Object> mapPara = new HashMap<String,Object>();
			mapPara.put("token", JuhuoConfig.token);
			mapPara.put("incremental", "true");
			switch(v.getId()){
			case R.id.filter_all_events:
				filterDefaultEvent.setTextColor(mResources.getColor(R.color.mgray));
				filter = 0;
				mapPara.put("organizer", String.valueOf(JuhuoConfig.userId));
				break;
			case R.id.filter_default_event:
				filterAllEvent.setTextColor(mResources.getColor(R.color.mgray));
				filter = 1;
				mapPara.put("related", String.valueOf(JuhuoConfig.userId));
				break;
			}
			getNetData(mapPara);
		}
	};
	public void getNetData(HashMap<String,Object> map){
//		noEventsText.setText("");
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
				  
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				try {
					if(result.has("handle")){
						handle = result.getString("handle");
					}
					JSONArray ja = result.getJSONArray("events");
					if(ja.length()!=0) {
						noEventsText.setText("");
						//set cache
						Tool.writeJsonToFile(result,getActivity(),JuhuoConfig.EVENTLISTSPECIFIC+JuhuoConfig.userId);
						// display data from network
						mData = ja;
						hotEventsList.setVisibility(View.VISIBLE);
						hotEventsAdapter = new HotEventsAdapter();
						hotEventsAdapter.setInflater(
								(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
								getActivity());
						hotEventsAdapter.setJSONData(mData,"MY");
						hotEventsAdapter.notifyDataSetChanged();
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

}
