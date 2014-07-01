package com.juhuo.welcome;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FeedBackActivity extends Activity {
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle;
	private EditText detail;
	private Resources mResources;
	private RelativeLayout actionTitleLay;
	private final String TAG = "FeedBackActivity";
	private ProgressDialog mPgDialog;
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//»•µÙ±ÍÃ‚¿∏
		setContentView(R.layout.feedback);
		mResources = getResources();
		mPgDialog = new ProgressDialog(this);
		actionTitleLay = (RelativeLayout)findViewById(R.id.action_title_lay);
		actionTitle = (TextView)findViewById(R.id.action_title_txt2);
		detail = (EditText)findViewById(R.id.detail);
		detail.setSelectAllOnFocus(true);
		actionTitleLay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		actionTitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HashMap<String,Object> params = new HashMap<String,Object>();
				params.put("token", JuhuoConfig.token);
				params.put("feedback", detail.getText().toString());
				FeedBackTask task = new FeedBackTask();
				mAsyncTask.add(task);
				task.execute(params);
			}
		});
	}
	
	private class FeedBackTask extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mPgDialog.setMessage(mResources.getString(R.string.pulling_eventlist));
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... map) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().callPostPlain(mapped,JuhuoConfig.USER_FEEDBACK);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if (getStop()) {
                return;
            }
			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(FeedBackActivity.this, mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(FeedBackActivity.this);
			}else{
				finish();
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

