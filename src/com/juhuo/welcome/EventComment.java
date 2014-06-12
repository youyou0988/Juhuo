package com.juhuo.welcome;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.control.MyListView;
import com.juhuo.control.MyListView.OnRefreshListener;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;

final class CommentHandler{
	TextView name;
	TextView time;
	TextView content;
	RelativeLayout whole;
}
public class EventComment extends Activity{
	private final String TAG = "EventComment";
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle;
	private Resources mResources;
	private ProgressDialog mPgDialog;
	private MyListView commentList;
	private CommentListAdapter mAdapter;
	private ArrayList<HashMap<String,Object>> mData;
	private HashMap<String,Object> mapPara;
	private LayoutInflater mInflater;
	private String event_id;
	private EditText message;
	private ImageView send;
	private TextView noCommentText;
	private SimpleDateFormat df = new SimpleDateFormat(Tool.ISO8601DATEFORMAT, Locale.getDefault());
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment);
		mResources = getResources();
		mPgDialog = new ProgressDialog(this);
		mData = new ArrayList<HashMap<String,Object>>();
		actionTitleImg = (ImageView)findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)findViewById(R.id.action_title_img2);
		actionTitle = (TextView)findViewById(R.id.action_title);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitleImg2.setVisibility(View.INVISIBLE);
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		actionTitle.setText(mResources.getString(R.string.comment));
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		commentList = (MyListView)findViewById(R.id.commentlist);
		message = (EditText)findViewById(R.id.comment);
		send = (ImageView)findViewById(R.id.send_comment);
		noCommentText = (TextView)findViewById(R.id.no_comments_found);
		RelativeLayout comLay = (RelativeLayout)findViewById(R.id.commentlay);
//		String page = getIntent().getExtras().getString("PAGE");
//		comLay.setVisibility(page.equals("HOT")?View.INVISIBLE:View.VISIBLE);
		int organizer_status = getIntent().getExtras().getInt("organizer_status");
		if(organizer_status==0||organizer_status==1||organizer_status==3||organizer_status==5){
			comLay.setVisibility(View.VISIBLE);
		}else{
			comLay.setVisibility(View.INVISIBLE);
		}
		this.event_id = getIntent().getExtras().getString("id");
		//read from cache
		JSONObject jo = new JSONObject();
		jo = Tool.loadJsonFromFile(JuhuoConfig.EVENTCOMMENT+event_id, this);
		mapPara = new HashMap<String,Object>();
		mapPara.put("id", this.event_id);
		mapPara.put("token", JuhuoConfig.token);
		mapPara.put("incremental", String.valueOf(true));
		if(jo==null){
			//get network data
			LoadEventComment loadEventComment = new LoadEventComment();
			loadEventComment.execute(mapPara);
		}else{
			JSONArray ja;
			try {
				ja = jo.getJSONArray("comments");
				mData = Tool.commonJ2L(ja);
				if(mData.size()==0){
					noCommentText.setText(mResources.getString(R.string.no_comments_found));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mAdapter = new CommentListAdapter();
			commentList.setAdapter(mAdapter);
		}
		
		
		commentList.setonRefreshListener(new OnRefreshListener() {
			
			public void onRefresh() {
				// TODO Auto-generated method stub
				Log.i("pull", "refresh");
				LoadEventComment loadEventComment = new LoadEventComment();
				loadEventComment.execute(mapPara);
			}
		});
		send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HashMap<String,Object> mapPara = new HashMap<String,Object>();
				mapPara.put("token", JuhuoConfig.token);
				mapPara.put("id", event_id);
				mapPara.put("content", message.getEditableText().toString());
				mapPara.put("time", df.format(new Date()));
				SendCommentClass task = new SendCommentClass();
				task.execute(mapPara);
			}
		});
		
	}
	public class CommentListAdapter extends BaseAdapter{
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
			CommentHandler holder = null;
			
			if (convertView == null) {
				holder = new CommentHandler();
				convertView = mInflater.inflate(R.layout.comment_detail_tem,null);
				holder.whole = (RelativeLayout)convertView.findViewById(R.id.commentwhole);
				holder.name=(TextView)convertView.findViewById(R.id.commenter_name);
				holder.time=(TextView)convertView.findViewById(R.id.commenter_time);
				holder.content=(TextView)convertView.findViewById(R.id.commenter_content);
				convertView.setTag(holder);
			} else {
				holder = (CommentHandler) convertView.getTag();
			}
			holder.name.setText((String)mData.get(position).get("commenter_name"));
			if(((String)mData.get(position).get("time")).equals("null")){
				holder.time.setText("1970-01-01 08:00");
			}else{
				holder.time.setText(((String)mData.get(position).get("time")).substring(0,19).replace('T', ' '));
			}
			holder.content.setText((String)mData.get(position).get("content"));
			return convertView;
		}
		
	}

	private class SendCommentClass extends AsyncTask<HashMap<String,Object>,String,JSONObject>{
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mPgDialog.setMessage(mResources.getString(R.string.commenting));
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... arg0) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = arg0[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_COMMENT);
		}
		@Override
		protected void onPostExecute(JSONObject result){
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(EventComment.this, mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(EventComment.this);
			}else{
				Tool.myToast(EventComment.this, mResources.getString(R.string.send_comment_success));
			}
		}
	}
	private class LoadEventComment extends AsyncTask<HashMap<String,Object>,String,JSONObject>{
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        noCommentText.setText("");
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... map) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_COMMENT_LIST);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(EventComment.this);
			}else{
				Tool.writeJsonToFile(result, EventComment.this, JuhuoConfig.EVENTCOMMENT+event_id);
				try {
					JSONArray ja = result.getJSONArray("comments");
					mData = Tool.commonJ2L(ja);
					if(mData.size()==0){
						noCommentText.setText(mResources.getString(R.string.no_comments_found));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mAdapter = new CommentListAdapter();
				commentList.setAdapter(mAdapter);
			}
			commentList.onRefreshComplete();
		}
		
	}
	
	

}
