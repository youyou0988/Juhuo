package com.juhuo.welcome;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends Activity {
	private ImageView action_title_img;
	private EditText userName;
	private EditText userPassword;
	private Button loginBtn;
	private TextView registerFree;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		action_title_img = (ImageView)findViewById(R.id.action_title_img);
		userName = (EditText)findViewById(R.id.user_name);
		userPassword = (EditText)findViewById(R.id.user_password);
		loginBtn = (Button)findViewById(R.id.login_btn);
		registerFree = (TextView)findViewById(R.id.register_free);
	}
	private void setListener(){
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
				
			}
		});
	}

}
