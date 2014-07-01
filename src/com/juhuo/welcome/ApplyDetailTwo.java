package com.juhuo.welcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class ApplyDetailTwo extends Activity {
	private final String TAG="ApplyDetailTwo";
	private TextView name;
	private TextView age;
	private TextView gender;
	private TextView cell;
	private TextView description;
	private ImageView image;
	private ImageView actionTitleImg;
	private TextView actionTitle,actionTitleTxt2;
	private Resources mResources;
	private ProgressDialog mPgDialog;
	private RelativeLayout actionTitleLay;
	private int contact_id=0;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
	.showImageOnLoading(R.drawable.default_image)
	.showImageForEmptyUri(R.drawable.default_image)
	.showImageOnFail(R.drawable.default_image)
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.considerExifParams(true)
	.displayer(new SimpleBitmapDisplayer())
	.build();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.apply_detail_two);
		mResources = getResources();
		mPgDialog = new ProgressDialog(this);
		actionTitleTxt2 = (TextView)findViewById(R.id.action_title_txt2);
		actionTitle = (TextView)findViewById(R.id.action_title);
		actionTitle.setText(mResources.getString(R.string.description));
		actionTitleLay = (RelativeLayout)findViewById(R.id.action_title_lay);
		actionTitleLay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		if(JuhuoConfig.token.equals(JuhuoConfig.PUBLIC_TOKEN)){
			actionTitleTxt2.setVisibility(View.INVISIBLE);
		}else{
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("token", JuhuoConfig.token);
			map.put("id", getIntent().getExtras().getString("id"));
			ContactJudgeClass task = new ContactJudgeClass();
			mAsyncTask.add(task);
			task.execute(map);
			actionTitleTxt2.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					TextView tv = (TextView)arg0;
					if(tv.getText().equals("关注")){//加关注
						JSONObject json = new JSONObject();
						try {
							json.put("token", JuhuoConfig.token);
							JSONArray ja = new JSONArray();
							JSONObject tm = new JSONObject();
							tm.put("name", getIntent().getExtras().getString("name"));
							tm.put("cell", getIntent().getExtras().getString("cell"));
							ja.put(tm);
							json.put("contacts", ja);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						CreateFollowClass task = new CreateFollowClass();
						mAsyncTask.add(task);
						task.execute(json);
					}else{//取消关注
						JSONObject json = new JSONObject();
						try {
							json.put("token", JuhuoConfig.token);
							JSONArray ja = new JSONArray();
							ja.put(contact_id);
							json.put("ids", ja);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ContactDeleteClass task = new ContactDeleteClass();
						mAsyncTask.add(task);
						task.execute(json);
					}
					
				}
			});
		}
		
		name = (TextView)findViewById(R.id.t2);
		age = (TextView)findViewById(R.id.t4);
		gender = (TextView)findViewById(R.id.t6);
		cell = (TextView)findViewById(R.id.t8);
		description = (TextView)findViewById(R.id.t10);
		image = (ImageView)findViewById(R.id.image);
		name.setText(getIntent().getExtras().getString("name"));
		if(getIntent().getExtras().getString("age").equals("null")){
			age.setText("未知");
		}else{
			String a = getIntent().getExtras().getString("age").substring(0,19).replace('T', ' ');
			age.setText(String.valueOf(Tool.getAgeFromBirthday(a)));
		}
		String ge = getIntent().getExtras().getString("gender").equals("0")?"男":"女";
		gender.setText(ge);
		cell.setText(getIntent().getExtras().getString("cell"));
		description.setText(getIntent().getExtras().getString("description"));
		image.getLayoutParams().height = JuhuoConfig.WIDTH*150/320;
		image.getLayoutParams().width = JuhuoConfig.WIDTH*150/320;
		imageLoader.displayImage(getIntent().getExtras().getString("url"),image, options);
	}
	private class ContactJudgeClass extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.CONTACT_JUDGE);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return;
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(ApplyDetailTwo.this);
			}else if(result.has("not_found_wrong")){
				actionTitleTxt2.setText(mResources.getString(R.string.follow));
			}else{
				try {
					contact_id = result.getInt("contact_id");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				actionTitleTxt2.setText(mResources.getString(R.string.cancel_follow));
			}
			
		}
	}
	private class ContactDeleteClass extends CheckStopAsyncTask<JSONObject,String,JSONObject>{
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
//	        mPgDialog.setMessage(mResources.getString(R.string.changing));
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(JSONObject... map) {
			// TODO Auto-generated method stub

			JSONObject mapped = map[0];
			return new JuhuoInfo().callPostPlainNest(mapped,JuhuoConfig.CONTACT_DELETE);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return;
			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(ApplyDetailTwo.this);
			}else{
				Tool.myToast(ApplyDetailTwo.this, mResources.getString(R.string.cancel_follow_success));
				actionTitleTxt2.setText(mResources.getString(R.string.follow));
			}
			
		}
	}
	private class CreateFollowClass extends CheckStopAsyncTask<JSONObject,String,JSONObject>{
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
//	        mPgDialog.setMessage(mResources.getString(R.string.changing));
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(JSONObject... arg0) {
			// TODO Auto-generated method stub
			JSONObject mapped = arg0[0];
			return new JuhuoInfo().callPostPlainNest(mapped,JuhuoConfig.CONTACT_CREATE);
		}
		@Override
		protected void onPostExecute(JSONObject result){
			if (getStop()) {
                return;
            }
			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(ApplyDetailTwo.this, mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(ApplyDetailTwo.this);
			}else{
				Tool.myToast(ApplyDetailTwo.this, mResources.getString(R.string.add_follow_success));
				actionTitleTxt2.setText(mResources.getString(R.string.cancel_follow));
			}
		}
	}
	@Override
    protected void onStop()
    {
        for(int index = 0;index < mAsyncTask.size();index ++)
        {
            if(!(mAsyncTask.get(index).getStatus() == AsyncTask.Status.FINISHED) )
                mAsyncTask.get(index).setStop();
        }
        super.onStop();
    }

}
