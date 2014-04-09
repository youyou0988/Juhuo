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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;

public class LoginActivity extends Activity {
	private ImageView action_title_img;
	private EditText mUserName;
	private EditText mPassword;
	private Button loginBtn;
	private TextView registerFree;
	private Resources mResources;
	private ProgressDialog mPgDialog;
	private LinearLayout loginInput,titleBar;
	private String TAG = "LoginActivity";
	private String PRECELL = "+86";
	private int WIDTH,HEIGHT;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		getSize();
		initComponents();
        setListener();
	}
	private void getSize(){
		Display display = getWindowManager().getDefaultDisplay(); 
		Point size = new Point(); 
		display.getSize(size); 
		WIDTH =size.x; //px
		HEIGHT = size.y;//px
	}
	private void initComponents(){
		mResources = getResources();
		loginInput = (LinearLayout)findViewById(R.id.login_input);
		titleBar = (LinearLayout)findViewById(R.id.titlebar);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			     LinearLayout.LayoutParams.MATCH_PARENT, HEIGHT/7);
		layoutParams.setMargins(WIDTH/18, HEIGHT/30, WIDTH/18, HEIGHT/30);
		loginInput.setLayoutParams(layoutParams);
		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
			     LinearLayout.LayoutParams.MATCH_PARENT, HEIGHT/12);
		titleBar.setLayoutParams(layoutParams2);
		
		action_title_img = (ImageView)findViewById(R.id.action_title_img);
		mUserName = (EditText)findViewById(R.id.user_name);
		mPassword = (EditText)findViewById(R.id.user_password);
		loginBtn = (Button)findViewById(R.id.login_btn);
		registerFree = (TextView)findViewById(R.id.register_free);
		mPgDialog = new ProgressDialog(this);
        mPgDialog.setMessage(mResources.getString(R.string.loginning));
	}
	private void setListener(){
		mUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					mUserName.setText(PRECELL);
				}
				
			}
		});
		action_title_img.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LoginActivity.this.finish();
			}
		});
		loginBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Tool.networkAvaliable(LoginActivity.this)) {
					//登录用户名密码不为空验证
					if (!"".equals(mUserName.getText().toString().trim())
							&& !"".equals(mPassword.getText().toString()
									.trim())) {
						mPgDialog.show();
						new LoginTask().execute();
						
					} else {
						Tool.myToast(LoginActivity.this, mResources.getString(R.string.login_input_name_password));
					}
				} else {
					Tool.myToast(LoginActivity.this,mResources.getString(R.string.current_net_invalide));
				}
			}
		});
	}
	private class LoginTask extends AsyncTask<String, String, JSONObject> {
		@Override
        protected JSONObject doInBackground(String... strings)
        {
			Log.i(TAG,mUserName.getText().toString().trim()+mPassword.getText().toString());
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("cell", mUserName.getText().toString().trim());
			map.put("passwd", mPassword.getText().toString());
        	JSONObject tmpResult = new JuhuoInfo().authenticate(map);
        	return tmpResult;
        }

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		public LoginTask() {
			
			super();
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			if(result == null){
				Log.i(TAG,"i am not in");
				Tool.myToast(LoginActivity.this,mResources.getString(R.string.login_failed_username_password));
			}else{
				Tool.myToast(LoginActivity.this,mResources.getString(R.string.login_success));
				Intent intent2Home = new Intent(LoginActivity.this, HomeActivity.class);
				try {
					JuhuoConfig.token = result.getString("token");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				startActivity(intent2Home);
                LoginActivity.this.finish();
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
