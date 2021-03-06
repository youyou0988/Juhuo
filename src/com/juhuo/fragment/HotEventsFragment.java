package com.juhuo.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import com.juhuo.refreshview.XListView;
import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.R;
import com.juhuo.welcome.SearchEvent;

public class HotEventsFragment extends Fragment{
	private Resources mResources;
	private ProgressDialog mPgDialog;
	private ImageView actionTitleImg2,actionTitleImg;
	private TextView actionTitle,noEventsText;
	private RelativeLayout parent,filterlistlayout,actionTitleLay,actionTitleLay2;
	private XListView hotEventsList;
	private ListView subFilterView01,subFilterView02;
	private Button filterAllEvent,filterDefaultEvent;
	private View transView,transView2;
	private HotEventsAdapter hotEventsAdapter;
	private FilterEventAdapter filterEventAdapter;
	Animation animationSlideDown;
	private final String TAG = "HotEventsFragment";
	private final int EVENT_DETAIL=1;
//	private List<AsyncTask<String,String,Object>> mAsyncTask = 
//			new ArrayList<AsyncTask<String,String,Object>>();
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	
	private JSONArray mData;
	private HashMap<String,Object> mapPara;
	private String[] eventType={"所有活动类型","交友聚会","读书看报","音乐电影","体育锻炼","其他"};
	private String[] eventType2={"活跃活动","所有活动时间"};
//	private String[] eventType2={"默认排序","参加人数从多到少","活动距离从近到远","活动费用从多到少","活动费用从少到多"};
	private String[] eventPara = {"","participant_count","distance","cost","cost"};
	private int[] focus={1,0,0,0,0,0};
	private int[] focus2={1,0};
	private List<HashMap<String,String>> cacheList= new ArrayList<HashMap<String,String>>();
    private String handle;
    private final int COUNT=20;
    private int offset=0;
    private SimpleDateFormat df = new SimpleDateFormat(Tool.ISO8601DATEFORMAT, Locale.getDefault());
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		mPgDialog = new ProgressDialog(getActivity());
		mapPara = new HashMap<String,Object>();
		mapPara.put("token", JuhuoConfig.token);
		mapPara.put("incremental", "true");
		mapPara.put("time_end", df.format(new Date()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parent = (RelativeLayout) inflater.inflate(
				R.layout.hot_event, null);
		hotEventsList = (XListView)parent.findViewById(R.id.hotevents_listview);
		hotEventsList.setPullLoadEnable(true);
		hotEventsList.setXListViewListener(new XListView.IXListViewListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				Log.i("pull", "refresh");
				getNetData(mapPara);
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				Log.i("push", "load more");
				if(handle.equals("")){
					onLoaded();
                }else{
                	HashMap<String,Object> mapMore = new HashMap<String,Object>();
                    mapMore.put("token", JuhuoConfig.token);
                    mapMore.put("handle", handle);
                    mapMore.put("count", String.valueOf(COUNT));
                    mapMore.put("offset", String.valueOf(offset));
                    LoadMoreEvents task = new LoadMoreEvents();
                    mAsyncTask.add(task);
                    task.execute(mapMore);
                }
			}
			
		});
		
		
		filterlistlayout = (RelativeLayout)parent.findViewById(R.id.filterlistlayout);
		
		transView = (View)parent.findViewById(R.id.transview);
		transView2 = (View)parent.findViewById(R.id.transview2);
		
		actionTitleImg = (ImageView)parent.findViewById(R.id.action_title_img);
		actionTitleLay = (RelativeLayout)parent.findViewById(R.id.action_title_lay);
		actionTitleImg2 = (ImageView)parent.findViewById(R.id.action_title_img2);
		actionTitleLay = (RelativeLayout)parent.findViewById(R.id.action_title_lay);
		actionTitleLay2 = (RelativeLayout)parent.findViewById(R.id.action_title_lay2);
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
		actionTitleLay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((SlidingFragmentActivity)getActivity()).toggle();
			}
		});
		actionTitleLay2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),SearchEvent.class);
				getActivity().startActivity(intent);
			}
		});
		//get date from cache first
		hotEventsList.setVisibility(View.VISIBLE);
		hotEventsAdapter = new HotEventsAdapter();
		hotEventsAdapter.setInflater(
				(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
				getActivity());
		JSONObject jsonCache = new JSONObject();
		jsonCache = Tool.loadJsonFromFile(JuhuoConfig.EVENTLISTFILE,getActivity());
		
		if(jsonCache==null){
			noEventsText.setText(mResources.getString(R.string.no_events_found));	
		}else{
			int totalCount=0;
			try {
				hotEventsAdapter.setJSONData(jsonCache.getJSONArray("events"),"HOT",mAsyncTask);
				totalCount = jsonCache.getJSONArray("events").length();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hotEventsAdapter.notifyDataSetChanged();
			hotEventsAdapter.setListView(hotEventsList);
			hotEventsList.setAdapter(hotEventsAdapter);
			Tool.isShowFooter(hotEventsList,totalCount);
		}
		getNetData(mapPara);
		return parent;
	}
	
	private void onLoaded() {
		hotEventsList.stopRefresh();
		hotEventsList.stopLoadMore();
		hotEventsList.setRefreshTime("刚刚");
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
			mapPara.put("time_end", df.format(new Date()));
			int po=0;
			for(po=0;po<6;po++){
				if(focus[po]==1) break;
			}
			filterAllEvent.setText(eventType[po]);
			if(po!=0) mapPara.put("event_type", String.valueOf(po));
			int po2=0;
			for(po2=0;po2<2;po2++){
				if(focus2[po2]==1) break;
			}
			filterDefaultEvent.setText(eventType2[po2]);
//			if(po2!=0) mapPara.put("sort_param", eventPara[po2]);
//			if(po2==1){
//				mapPara.put("sort_order", "DESC");
//			}else if(po2==2){
//				mapPara.put("lat", "138.006");
//				mapPara.put("lng", "214.391");
//			}else if(po2==3){
//				mapPara.put("sort_order", "DESC");
//			}else if(po2==4){
//				mapPara.put("sort_order", "ASC");
//			}	
			if(po2==1){
				mapPara.remove("time_end");
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
		LoadEventList loadEventList = new LoadEventList();
		mAsyncTask.add(loadEventList);
		loadEventList.execute(map);
	}
	private class LoadMoreEvents extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
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
			if(getStop()) return;
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
					if(ja.length()!=0) {
						for(int i=0;i<ja.length();i++){
							mData.put(ja.getJSONObject(i));
						}
						hotEventsAdapter.setJSONData(mData,"HOT",mAsyncTask);
						hotEventsAdapter.notifyDataSetChanged();
						
					}else{
						Tool.myToast(getActivity(), mResources.getString(R.string.no_events_found));
					}
					
					offset +=ja.length();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			onLoaded();
		}
	}
	private class LoadEventList extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
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
			if(getStop()) return;
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
						Tool.writeJsonToFile(result,getActivity(),JuhuoConfig.EVENTLISTFILE);
						mData = ja;
						hotEventsList.setVisibility(View.VISIBLE);
						hotEventsAdapter = new HotEventsAdapter();
						hotEventsAdapter.setInflater(
								(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
								getActivity());
						hotEventsAdapter.setJSONData(mData,"HOT",mAsyncTask);
						hotEventsAdapter.notifyDataSetChanged();
						hotEventsAdapter.setListView(hotEventsList);
						hotEventsList.setAdapter(hotEventsAdapter);
						Tool.isShowFooter(hotEventsList,ja.length());
						offset += ja.length();
					}else{
						//no events found
						hotEventsList.setVisibility(View.INVISIBLE);
						noEventsText.setText(mResources.getString(R.string.no_events_found));	
					}
//					hotEventsList.onRefreshComplete();
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
    public void onDestroyView()
    {
        for(int index = 0;index < mAsyncTask.size();index ++)
        {
            if(!(mAsyncTask.get(index).getStatus() == AsyncTask.Status.FINISHED) )
                mAsyncTask.get(index).setStop();
        }
        super.onDestroyView();
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
			
		}
	};
	
//	@Override  
//    public void onActivityResult(int requestCode, int resultCode, Intent data)  
//    {  
//		Log.i(TAG, "delete_event"+requestCode);
//		if(data!=null){
//			switch (requestCode)  
//	        {  
//		        case EVENT_DETAIL:
//		        	Tool.RemoveJSONArray(mData, data.getExtras().getInt("pos"));
//		        	hotEventsAdapter.setJSONData(mData,"MY"+"organizer",mAsyncTask);
//		        	hotEventsAdapter.removeUrl(data.getExtras().getInt("pos"));
//					hotEventsAdapter.notifyDataSetChanged();
//	        }  
//		}	
//    }

}
