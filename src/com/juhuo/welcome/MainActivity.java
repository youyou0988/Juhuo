package com.juhuo.welcome;

import com.juhuo.tool.Tool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	private LinearLayout login;
	private LinearLayout skipLogin;
	private int width,height;
	private String TAG="MainActivity";
	Animation animationSlideInTop,animationSlideInTop2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSize();
		initComponents();
		setListener();
	}
	private void getSize(){
		Display display = getWindowManager().getDefaultDisplay(); 
		Point size = new Point(); 
		display.getSize(size); 
		width =size.x; //px
		height = size.y;//px
	}
	
	private void initComponents(){
		Log.i(TAG, String.valueOf(Tool.px2dip(MainActivity.this, width)));
		Log.i(TAG, String.valueOf(Tool.px2dip(MainActivity.this, height)));
		login = (LinearLayout)findViewById(R.id.sl_login);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		layoutParams.setMargins(0, 0, 0, height/50);
		login.setLayoutParams(layoutParams);
		skipLogin = (LinearLayout)findViewById(R.id.sl_skip_login);
		skipLogin.setLayoutParams(layoutParams);
		animationSlideInTop = AnimationUtils.loadAnimation(this,R.anim.slidedown_in);
		animationSlideInTop.setDuration(1000);
		animationSlideInTop.setAnimationListener(animationSlideInTopListener);
		
		animationSlideInTop2 = AnimationUtils.loadAnimation(this,R.anim.slidedown_in);
		animationSlideInTop2.setDuration(1000);
//		animationSlideInTop2.setStartOffset(1000);
		animationSlideInTop2.setAnimationListener(animationSlideInTopListener2);
		
		skipLogin.startAnimation(animationSlideInTop);
		skipLogin.setVisibility(View.VISIBLE);
	}
	
	private void setListener(){
		login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mainToLogin = new Intent(MainActivity.this,LoginActivity.class);
				startActivity(mainToLogin);
			}
		});
		skipLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mainToLogin = new Intent(MainActivity.this,HomeActivity.class);
				startActivity(mainToLogin);
				MainActivity.this.finish();
			}
		});
	}
	
	AnimationListener animationSlideInTopListener
	= new AnimationListener(){

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			skipLogin.clearAnimation();
			login.startAnimation(animationSlideInTop2);
			login.setVisibility(View.VISIBLE);
//			skipLogin.clearAnimation();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}};
	
	AnimationListener animationSlideInTopListener2
	= new AnimationListener(){

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			login.clearAnimation();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}};
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		login.clearAnimation();
		skipLogin.clearAnimation();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
