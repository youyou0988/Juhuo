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
import android.widget.RelativeLayout;
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
	private RelativeLayout titleBar,loginInput;
	private String TAG = "LoginActivity";
	private String PRECELL = "+86";
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		initComponents();
        setListener();
	}
	private void initComponents(){
		mResources = getResources();
		loginInput = (RelativeLayout)findViewById(R.id.login_input);
		titleBar = (RelativeLayout)findViewById(R.id.titlebar);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, JuhuoConfig.HEIGHT/7);
		loginInput.setLayoutParams(layoutParams);
		
		action_title_img = (ImageView)findViewById(R.id.action_title_img);
		mUserName = (EditText)findViewById(R.id.user_name);
		mPassword = (EditText)findViewById(R.id.user_password);
		loginBtn = (Button)findViewById(R.id.login_btn);
		registerFree = (TextView)findViewById(R.id.register_free);
		mPgDialog = new ProgressDialog(this);
        mPgDialog.setMessage(mResources.getString(R.string.loginning));
        //just for test
        mUserName.setText("+8615210588692");
        mPassword.setText("123456");
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
        	JSONObject tmpResult = new JuhuoInfo().loadNetData(map, JuhuoConfig.LOGIN);
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
			}else if(result.has("no_data")){
				Tool.myToast(LoginActivity.this,mResources.getString(R.string.current_net_invalide));
			}else{
//				Tool.myToast(LoginActivity.this,mResources.getString(R.string.login_success));
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
