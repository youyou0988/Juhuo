package com.juhuo.welcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.adapter.HotEventsAdapter;
import com.juhuo.control.MyListView;
import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;

public class SearchEvent extends Activity {
	private final String TAG="SearchEvent";
	private ImageView actionTitleImg;
	private TextView actionTitle,cancel,noEventsText;
	private Resources mResources;
	private EditText keyword;
	private MyListView listview;
	private RelativeLayout titlebar;
	private ProgressDialog mPgDialog;
	private HotEventsAdapter hotEventsAdapter;
	private JSONArray mData;
	private String handle;
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//»•µÙ±ÍÃ‚¿∏
		setContentView(R.layout.search_event);
		mResources = getResources();
		titlebar = (RelativeLayout)findViewById(R.id.titlebar);
		actionTitleImg = (ImageView)findViewById(R.id.action_title_img);
		actionTitle = (TextView)findViewById(R.id.action_title);
		cancel = (TextView)findViewById(R.id.cancel);
		noEventsText = (TextView)findViewById(R.id.no_events_found);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		actionTitle.setText(mResources.getString(R.string.search_event));
		keyword = (EditText)findViewById(R.id.keyword);
		listview = (MyListView)findViewById(R.id.search_listview);
		keyword.setOnFocusChangeListener(new OnFocusChangeListener(){
	        public void onFocusChange(View v, boolean hasFocus) {
	        	if(hasFocus){
	        		titlebar.setVisibility(View.GONE);
	        		cancel.setVisibility(View.VISIBLE);
	        	}
	        }
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				titlebar.setVisibility(View.VISIBLE);
				cancel.setVisibility(View.GONE);
				keyword.clearFocus();
			}
		});
		keyword.setImeOptions(EditorInfo.IME_ACTION_DONE); 
		keyword.setOnKeyListener(new OnKeyListener() {  
			  
            @Override  
            public boolean onKey(View v, int keyCode, KeyEvent event) {
            	if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {  
                	mPgDialog = new ProgressDialog(SearchEvent.this);
                    mPgDialog.setMessage(mResources.getString(R.string.getting_data));
                    mPgDialog.show();
                    HashMap<String,Object> mapPara = new HashMap<String,Object>();
            		mapPara.put("token", JuhuoConfig.token);
            		mapPara.put("title", keyword.getText().toString());
                    getNetData(mapPara);
                    return true;  
                }  
                return false;  
  
            }  
        }); 
	}
	public void getNetData(HashMap<String,Object> map){
		noEventsText.setText("");
		listview.onRefreshing();
		LoadEventList loadEventList = new LoadEventList();
		mAsyncTask.add(loadEventList);
		loadEventList.execute(map);
	}
	private class LoadEventList extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_LIST);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return;
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				listview.onRefreshComplete();
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(SearchEvent.this);
			}else{
				try {
					if(result.has("handle")){
						handle = result.getString("handle");
					}
					JSONArray ja = result.getJSONArray("events");
					if(ja.length()!=0) {
						mData = ja;
						listview.setVisibility(View.VISIBLE);
						hotEventsAdapter = new HotEventsAdapter();
						hotEventsAdapter.setInflater(
								(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
								SearchEvent.this);
						hotEventsAdapter.setJSONData(mData,"HOT",mAsyncTask);
						hotEventsAdapter.notifyDataSetChanged();
						hotEventsAdapter.setListView(listview);
						listview.setAdapter(hotEventsAdapter);
					}else{
						//no events found
						listview.setVisibility(View.INVISIBLE);
						noEventsText.setText(mResources.getString(R.string.no_events_found));	
					}
					listview.onRefreshComplete();
					mPgDialog.dismiss();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
}
