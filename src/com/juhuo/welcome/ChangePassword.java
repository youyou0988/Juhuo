package com.juhuo.welcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;

public class ChangePassword extends Activity {
	private final String TAG = "ChangePassword";
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle;
	private Resources mResources;
	private EditText password1,password2;
	private RelativeLayout actionTitleLay,actionTitleLay2;
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//»•µÙ±ÍÃ‚¿∏
		setContentView(R.layout.change_password);
		mResources = getResources();
		actionTitleImg = (ImageView)findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)findViewById(R.id.action_title_img2);
		actionTitle = (TextView)findViewById(R.id.action_title);
		actionTitleLay = (RelativeLayout)findViewById(R.id.action_title_lay);
		actionTitleLay2 = (RelativeLayout)findViewById(R.id.action_title_lay2);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_check_inactive));
		actionTitleImg2.setVisibility(View.VISIBLE);
		password1 = (EditText)findViewById(R.id.password1);
		password2 = (EditText)findViewById(R.id.password2);
		actionTitleLay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		actionTitleLay2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(password1.getText().toString().equals(password2.getText().toString())){
					HashMap<String,Object> mapPara = new HashMap<String,Object>();
					mapPara.put("token", JuhuoConfig.token);
					mapPara.put("passwd_new", password1.getText().toString());
					getNetData(mapPara);
				}else{
					Tool.myToast(ChangePassword.this, mResources.getString(R.string.password_not_match));
				}
				
			}
		});
		actionTitle.setText(mResources.getString(R.string.cha_pass));
		
	}
	public void getNetData(HashMap<String,Object> map){
		ChangePass changePassword = new ChangePass();
		mAsyncTask.add(changePassword);
		changePassword.execute(map);
	}
	private class ChangePass extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.CHANGE_PASSWORD);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if (getStop()) {
                return;
            }
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(ChangePassword.this);
			}else{
				Tool.myToast(ChangePassword.this,mResources.getString(R.string.cha_pass_success));
				finish();
			}
			
		}
	}

}
