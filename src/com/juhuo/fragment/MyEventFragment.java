package com.juhuo.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import com.juhuo.control.MyListView.OnLoadListener;
import com.juhuo.control.MyListView.OnRefreshListener;
import com.juhuo.refreshview.XListView;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.CreateEvent;
import com.juhuo.welcome.R;

public class MyEventFragment extends Fragment{
	private Resources mResources;
	private ProgressDialog mPgDialog;
	private String TAG = "MyEventFragment";
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle,noEventsText;
	private RelativeLayout parent,filterlistlayout,actionTitleLay,actionTitleLay2;
	private XListView hotEventsList;
	private Button filterAllEvent,filterDefaultEvent;
	private View transView,transView2;
	private HotEventsAdapter hotEventsAdapter;
	private List<AsyncTask<String,String,Object>> mAsyncTask = 
			new ArrayList<AsyncTask<String,String,Object>>();
	private JSONArray mData;
	private HashMap<String,Object> mapPara;
	private String handle;
	private int filter;
	private final int CREATE_EVENT = 0;
	private final int COUNT=20;
    private int offset=20;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		mPgDialog = new ProgressDialog(getActivity());
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
		hotEventsList = (XListView)parent.findViewById(R.id.hotevents_listview);
		hotEventsList.setXListViewListener(new XListView.IXListViewListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				Log.i("pull", "refresh");
				//check refresh whom
				mapPara = new HashMap<String,Object>();
				mapPara.put("token", JuhuoConfig.token);
				mapPara.put("incremental", "true");
				if(filter==0){
					mapPara.put("organizer", String.valueOf(JuhuoConfig.userId));
					getNetData(mapPara,"organizer");
				}else{
					mapPara.put("related", String.valueOf(JuhuoConfig.userId));
					getNetData(mapPara,"related");
				}
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				Log.e(TAG, "onLoad");
                if(handle.equals("")){
                	onLoaded();
                }else{
                	HashMap<String,Object> mapMore = new HashMap<String,Object>();
                    mapMore.put("token", JuhuoConfig.token);
                    mapMore.put("handle", handle);
                    mapMore.put("count", String.valueOf(COUNT));
                    mapMore.put("offset", String.valueOf(offset));
                    offset = offset+COUNT;
                    LoadMoreEvents task;
                    if(filter==0){
                    	task = new LoadMoreEvents("organizer");
    				}else{
    					task = new LoadMoreEvents("related");
    				}
                    task.execute(mapMore);
                }
			}
		});
		
		
		filterlistlayout = (RelativeLayout)parent.findViewById(R.id.filterlistlayout);
		
		transView = (View)parent.findViewById(R.id.transview);
		transView2 = (View)parent.findViewById(R.id.transview2);
		
		actionTitleImg = (ImageView)parent.findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)parent.findViewById(R.id.action_title_img2);
		actionTitleLay = (RelativeLayout)parent.findViewById(R.id.action_title_lay);
		actionTitleLay2 = (RelativeLayout)parent.findViewById(R.id.action_title_lay2);
		actionTitle = (TextView)parent.findViewById(R.id.action_title);
		noEventsText = (TextView)parent.findViewById(R.id.no_events_found);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_navi));
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.plus));
		actionTitleImg2.setVisibility(View.VISIBLE);
		actionTitle.setText(mResources.getString(R.string.my_event));
		
		//open the sliding menu
		actionTitleLay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((SlidingFragmentActivity)getActivity()).toggle();
			}
		});
		actionTitleLay2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),CreateEvent.class);
				intent.putExtra("type", "create");
				startActivityForResult(intent,CREATE_EVENT);
			}
		});
		filterAllEvent = (Button)parent.findViewById(R.id.filter_all_events);
		filterDefaultEvent = (Button)parent.findViewById(R.id.filter_default_event);
		filterAllEvent.setText(mResources.getString(R.string.my_participant));
		filterDefaultEvent.setText(mResources.getString(R.string.my_organize));
		filterDefaultEvent.setTextColor(mResources.getColor(R.color.mgreen));
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
		getNetData(mapPara,"organizer");
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
				filterAllEvent.setTextColor(mResources.getColor(R.color.mgray));
				filter = 1;
				mapPara.put("related", String.valueOf(JuhuoConfig.userId));
				getNetData(mapPara,"related");
				break;
			case R.id.filter_default_event:
				filterDefaultEvent.setTextColor(mResources.getColor(R.color.mgray));
				filter = 0;
				mapPara.put("organizer", String.valueOf(JuhuoConfig.userId));
				getNetData(mapPara,"organizer");
				break;
			}
		}
	};
	private class LoadMoreEvents extends AsyncTask<HashMap<String,Object>,String,JSONObject>{
		String type;
		public LoadMoreEvents(String type){
			this.type = type;
		}
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mPgDialog.setMessage(mResources.getString(R.string.pulling_eventlist));
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... arg0) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = arg0[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_LIST_INCREMENTAL);
		}
		@Override
		protected void onPostExecute(JSONObject result){
			mPgDialog.dismiss();
			if(getActivity()==null){
				return;
			}
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(getActivity(), mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				try {
					JSONArray ja = result.getJSONArray("events");
					Log.i(TAG, ja.toString());
					if(ja.length()!=0) {
						for(int i=0;i<ja.length();i++){
							mData.put(ja.getJSONObject(i));
						}
						hotEventsAdapter.setJSONData(mData,"MY"+type);
						hotEventsAdapter.notifyDataSetChanged();
						
					}else{
						Tool.myToast(getActivity(), mResources.getString(R.string.no_events_found));
					}
					onLoaded();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void getNetData(HashMap<String,Object> map,String type){
		LoadEventList loadEventList = new LoadEventList(type);
		loadEventList.execute(map);
	}
	private class LoadEventList extends AsyncTask<HashMap<String,Object>,String,JSONObject>{
		String type;
		public LoadEventList(String type){
			this.type = type;
		}
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mPgDialog.setMessage(mResources.getString(R.string.pulling_eventlist));
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_LIST);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			mPgDialog.dismiss();
			if(getActivity()==null){
				return;
			}
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(getActivity(), mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				noEventsText.setText("");
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
//						Log.i(TAG, mData.toString());
						hotEventsAdapter.setJSONData(mData,"MY"+type);
						hotEventsAdapter.notifyDataSetChanged();
						hotEventsAdapter.setListView(hotEventsList);
						hotEventsList.setAdapter(hotEventsAdapter);
						offset = ja.length();
					}else{
						//no events found
						hotEventsList.setVisibility(View.INVISIBLE);
						noEventsText.setText(mResources.getString(R.string.no_events_found));	
					}
					onLoaded();
					
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
	@Override  
    public void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
		if(data!=null){
			switch (requestCode)  
	        {  
		        case CREATE_EVENT:  
		        	getNetData(mapPara,"organizer");
		            break;  
		        
	        }  
		}	
    }
	private void onLoaded() {
		hotEventsList.stopRefresh();
		hotEventsList.stopLoadMore();
		hotEventsList.setRefreshTime("¸Õ¸Õ");
	}

}
