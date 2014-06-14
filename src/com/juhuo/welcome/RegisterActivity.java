package com.juhuo.welcome;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;

public class RegisterActivity extends Activity {
	private ImageView action_title_img;
	private EditText mUserName;
	private EditText mPassword,mPassword2;
	private Button loginBtn;
	private TextView registerFree,actionTitle;
	private Resources mResources;
	private ProgressDialog mPgDialog;
	private RelativeLayout titleBar,loginInput,cellLay;
	private String TAG = "LoginActivity";
	private String PRECELL = "+86";
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.register);
		
		initComponents();
        setListener();
	}
	private void initComponents(){
		mResources = getResources();
		cellLay = (RelativeLayout)findViewById(R.id.cell_lay);
		loginInput = (RelativeLayout)findViewById(R.id.login_input);
		titleBar = (RelativeLayout)findViewById(R.id.titlebar);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, JuhuoConfig.HEIGHT/7);
		layoutParams.setMargins(0, JuhuoConfig.HEIGHT/32, 0, 0);
		layoutParams.addRule(RelativeLayout.BELOW,cellLay.getId());
		loginInput.setLayoutParams(layoutParams);
		
		action_title_img = (ImageView)findViewById(R.id.action_title_img);
		action_title_img.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		actionTitle = (TextView)findViewById(R.id.action_title);
		actionTitle.setText(mResources.getString(R.string.register));
		mUserName = (EditText)findViewById(R.id.user_name);
		mPassword = (EditText)findViewById(R.id.user_password);
		mPassword2 = (EditText)findViewById(R.id.user_password2);
		loginBtn = (Button)findViewById(R.id.login_btn);
		mPgDialog = new ProgressDialog(this);
        mPgDialog.setMessage(mResources.getString(R.string.registering));

	}
	private void setListener(){
		mUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					mUserName.setText(PRECELL);
					mUserName.setSelection(PRECELL.length());
				}
				
			}
		});
		action_title_img.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				RegisterActivity.this.finish();
			}
		});
		loginBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Tool.networkAvaliable(RegisterActivity.this)) {
					//登录用户名密码不为空验证
					if (!"".equals(mUserName.getText().toString().trim())
							&& !"".equals(mPassword.getText().toString()
									.trim())) {
						if(!mPassword.getText().toString()
									.trim().equals(mPassword2.getText().toString()
									.trim())){
							
							new RegisterTask().execute();
						}else{
							Tool.myToast(RegisterActivity.this, mResources.getString(R.string.password_incorrect));
						}
						
						
					} else {
						Tool.myToast(RegisterActivity.this, mResources.getString(R.string.login_input_name_password));
					}
				} else {
					Tool.myToast(RegisterActivity.this,mResources.getString(R.string.current_net_invalide));
				}
			}
		});
	}
	private class RegisterTask extends AsyncTask<String, String, JSONObject> {
		@Override
        protected JSONObject doInBackground(String... strings)
        {
			Log.i(TAG,mUserName.getText().toString().trim()+mPassword.getText().toString());
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("cell", mUserName.getText().toString().trim());
			map.put("passwd", mPassword.getText().toString());
        	JSONObject tmpResult = new JuhuoInfo().loadNetData(map, JuhuoConfig.REGISTER);
        	return tmpResult;
        }

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPgDialog.show();
		}

		
		@Override
		protected void onPostExecute(JSONObject result) {
			if(result == null){
				Log.i(TAG,"i am not in");
				Tool.myToast(RegisterActivity.this,mResources.getString(R.string.current_net_invalide));
			}else{
				Tool.myToast(RegisterActivity.this,mResources.getString(R.string.register_success));
				Intent intent2Home = new Intent(RegisterActivity.this, HomeActivity.class);
				try {
					JuhuoConfig.token = result.getString("token");
					JuhuoConfig.userId = result.getInt("id");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				startActivity(intent2Home);
				RegisterActivity.this.finish();
			}
			mPgDialog.dismiss();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}

}

