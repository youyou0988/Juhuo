package com.juhuo.fragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.juhuo.adapter.HotEventsAdapter;
import com.juhuo.adapter.HotEventsAdapter.AnimateFirstDisplayListener;
import com.juhuo.refreshview.XListView;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class FollowFragment extends Fragment{
	private Resources mResources;
	private String TAG = "MyEventFragment";
	private ImageView actionTitleImg;
	private TextView actionTitle,noEventsText;
	private RelativeLayout parent;
	private XListView followEventsList,followpersonList;
	private Button followMeBtn,myFollowBtn,followEventsBtn;
	private View transView,transView2;
	private FollowPersonAdapter followAdapter;
	private HotEventsAdapter hotEventsAdapter;
	private List<AsyncTask<String,String,Object>> mAsyncTask = 
			new ArrayList<AsyncTask<String,String,Object>>();
	private JSONArray mData;
	private HashMap<String,Object> mapPara;
	private int currentListType = 0;//0 关注我的人 1 我关注的人 2 我关注的活动列表
	private String handle;
	private final int COUNT=20;
    private int offset=20;
	private LayoutInflater mInflater;
	private ProgressDialog mPgDialog;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.imageScaleType(ImageScaleType.EXACTLY)
	.showImageOnLoading(R.drawable.default_image)
	.showImageForEmptyUri(R.drawable.default_image)
	.showImageOnFail(R.drawable.default_image)
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.considerExifParams(true)
	.displayer(new SimpleBitmapDisplayer())
	.build();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private List<HashMap<String,String>> followData = new ArrayList<HashMap<String,String>>(); 
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		mPgDialog = new ProgressDialog(getActivity());
		mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mapPara = new HashMap<String,Object>();
		mapPara.put("organizer", String.valueOf(JuhuoConfig.userId));
		mapPara.put("token", JuhuoConfig.token);
		mapPara.put("incremental", "true");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parent = (RelativeLayout) inflater.inflate(
				R.layout.follow_event, null);
		followEventsList = (XListView)parent.findViewById(R.id.followevents_listview);
		followpersonList = (XListView)parent.findViewById(R.id.followperson_listview);
		followpersonList.setXListViewListener(refresh);
		followEventsList.setXListViewListener(refresh);
		followpersonList.setOnItemClickListener(personItemListener);
		transView = (View)parent.findViewById(R.id.transview);
		transView2 = (View)parent.findViewById(R.id.transview2);
		
		actionTitleImg = (ImageView)parent.findViewById(R.id.action_title_img);
		actionTitle = (TextView)parent.findViewById(R.id.action_title);
		noEventsText = (TextView)parent.findViewById(R.id.no_events_found);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_navi));
		actionTitle.setText(mResources.getString(R.string.my_follow));
		
		//open the sliding menu
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((SlidingFragmentActivity)getActivity()).toggle();
			}
		});
		followMeBtn = (Button)parent.findViewById(R.id.follow_me_btn);
		myFollowBtn = (Button)parent.findViewById(R.id.my_follow_btn);
		followEventsBtn = (Button)parent.findViewById(R.id.follow_event_btn);
		followMeBtn.setOnClickListener(onClickListener);
		myFollowBtn.setOnClickListener(onClickListener);
		followEventsBtn.setOnClickListener(onClickListener);

		followMeBtn.setTextColor(mResources.getColor(R.color.mgreen));
		followAdapter = new FollowPersonAdapter();
		followpersonList.setAdapter(followAdapter);
		HashMap<String,Object> mapPara = new HashMap<String,Object>();
		mapPara.put("token", JuhuoConfig.token);
		LoadContactList task = new LoadContactList("person");
		task.execute(mapPara);		
		return parent;
	}
	OnItemClickListener personItemListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("token", JuhuoConfig.token);
			map.put("incremental", String.valueOf(true));
			map.put("contact_id", (String)followData.get(position-1).get("contact_id"));
			if(currentListType==0){
				map.put("followme", String.valueOf(true));
			}
			currentListType = 2;
			LoadContactList task = new LoadContactList("event");
			task.execute(map);
		}
		
	};
	XListView.IXListViewListener refresh = new XListView.IXListViewListener(){

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			Log.i("pull", "refresh");
			//check refresh whom
			if(currentListType==2){
				HashMap<String,Object> mapPara = new HashMap<String,Object>();
				mapPara.put("token", JuhuoConfig.token);
				mapPara.put("incremental",String.valueOf(true));
				LoadContactList task2 = new LoadContactList("event");
				task2.execute(mapPara);
			}else{
				followpersonList.stopRefresh();
				followpersonList.stopLoadMore();
				followpersonList.setRefreshTime("刚刚");
			}
			
			
		}

		@Override
		public void onLoadMore() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onLoad");
			if(currentListType==2){
				onLoaded();
			}else{
				followpersonList.stopRefresh();
				followpersonList.stopLoadMore();
				followpersonList.setRefreshTime("刚刚");
			}
			
		}
	};
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			followMeBtn.setTextColor(mResources.getColor(R.color.mgray));
			myFollowBtn.setTextColor(mResources.getColor(R.color.mgray));
			followEventsBtn.setTextColor(mResources.getColor(R.color.mgray));
			Button mb = (Button)v;
			mb.setTextColor(mResources.getColor(R.color.mgreen));
			HashMap<String,Object> mapPara = new HashMap<String,Object>();
			mapPara.put("token", JuhuoConfig.token);
			switch(v.getId()){
			case R.id.follow_me_btn:
				mapPara.put("followme", String.valueOf(true));
				LoadContactList task = new LoadContactList("person");
				task.execute(mapPara);
				currentListType = 0;
				followEventsList.setVisibility(View.GONE);
				followpersonList.setVisibility(View.VISIBLE);
				break;
			case R.id.my_follow_btn:
				LoadContactList task1 = new LoadContactList("person");
				task1.execute(mapPara);
				currentListType = 1;
				followEventsList.setVisibility(View.GONE);
				followpersonList.setVisibility(View.VISIBLE);
				break;
			case R.id.follow_event_btn:
				mapPara.put("incremental",String.valueOf(true));
				LoadContactList task2 = new LoadContactList("event");
				task2.execute(mapPara);
				currentListType = 2;
				followEventsList.setVisibility(View.VISIBLE);
				followpersonList.setVisibility(View.GONE);
				break;
			}
			
		}
	};
	
	private class LoadContactList extends AsyncTask<HashMap<String,Object>,String,JSONObject>{
		private String personOrEvent;
		public LoadContactList(String type){
			this.personOrEvent = type;
		}
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mPgDialog.show();
	        if(personOrEvent.equals("person")) followData.clear();
	    }
		
		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			if(personOrEvent.equals("person")){
				return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.CONTACT_LIST);
			}else{
				return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.CONTACT_EVENT);
			}
			
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(getActivity(), mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				try {
					if(personOrEvent.equals("person")){
						JSONArray ja = result.getJSONArray("contacts");
						for(int i=0;i<ja.length();i++){
							HashMap<String,String> map = new HashMap<String,String>();
							map.put("name", ja.getJSONObject(i).getString("name"));
							if(ja.getJSONObject(i).has("suc_photos")){
								map.put("url", ja.getJSONObject(i).getJSONArray("suc_photos").getJSONObject(0).getString("url"));
							}else{
								map.put("url", "");
							}
							map.put("contact_id", ja.getJSONObject(i).getString("contact_id"));
							followData.add(map);
						}
						followpersonList.setAdapter(followAdapter);
						followAdapter.setData(followData);
						followAdapter.notifyDataSetChanged();
					}else{
						followEventsList.setVisibility(View.VISIBLE);
						followpersonList.setVisibility(View.GONE);
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
							hotEventsAdapter = new HotEventsAdapter();
							hotEventsAdapter.setInflater(
									(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
									getActivity());
//							Log.i(TAG, mData.toString());
							hotEventsAdapter.setJSONData(mData,"MY"+"follow");
							hotEventsAdapter.notifyDataSetChanged();
							hotEventsAdapter.setListView(followEventsList);
							followEventsList.setAdapter(hotEventsAdapter);
							offset = ja.length();
						}else{
							//no events found
							followEventsList.setVisibility(View.INVISIBLE);
							noEventsText.setText(mResources.getString(R.string.no_events_found));	
						}
						onLoaded();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	private void onLoaded() {
		followEventsList.stopRefresh();
		followEventsList.stopLoadMore();
		followEventsList.setRefreshTime("刚刚");
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
	public class FollowPersonAdapter extends BaseAdapter{
		private List<HashMap<String,String>> mData = new ArrayList<HashMap<String,String>>(); 
		public void setData(List<HashMap<String,String>> data){
			this.mData = data;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			FollowHandler holder = null;
			if (convertView == null) {
				holder = new FollowHandler();
				convertView = mInflater.inflate(R.layout.follow_item, null);
				holder.icon=(ImageView)convertView.findViewById(R.id.icon);
				holder.name = (TextView)convertView.findViewById(R.id.follow_name);
				convertView.setTag(holder);
			} else {
				holder = (FollowHandler) convertView.getTag();
			}
			
			imageLoader.displayImage(mData.get(position).get("url"), holder.icon, options, animateFirstListener);
			holder.name.setText(mData.get(position).get("name"));
			//hide apply number for guest
	        
	        
	        return convertView;
		}
		
	}
	final class FollowHandler{
		ImageView icon;
		TextView name;
	}

}

