package com.juhuo.welcome;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.juhuo.fragment.HotEventsFragment;
import com.juhuo.fragment.LeftMenuFragment;
import com.juhuo.fragment.MyEventFragment;
import com.juhuo.fragment.UserSettingFragment;
import com.juhuo.tool.Tool;

public class HomeActivity extends SlidingFragmentActivity {
	private int WIDTH,HEIGHT;
	private Fragment mContent;
	private ImageView actionTitleImg;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_frame);
		getSize();
		initSlidingMenu(savedInstanceState);
		Tool.initImageLoader(this);

	}
	private void getSize(){
		Display display = getWindowManager().getDefaultDisplay(); 
		Point size = new Point(); 
		display.getSize(size); 
		WIDTH =size.x; //px
		HEIGHT = size.y;//px
	}
	
	private void initSlidingMenu(Bundle savedInstanceState) {
		// check if the content frame contains the menu frame
		// requestWindowFeature(Window.FEATURE_ACTION_BAR);
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		// ����������Fragment��ͼ����
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null) {
			mContent = new HotEventsFragment();
			
		}

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// set the Behind View Fragment
		LeftMenuFragment menuFragment = new LeftMenuFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, menuFragment).commit();

		// ���û����˵�������ֵ
		SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setShadowWidthRes(R.dimen.slidingmenu_shadow);
		sm.setFadeDegree(0.35f);
		sm.setOnOpenListener(new OnOpenListener() {
	        @Override
	        public void onOpen() {
	        	Log.i("sliding menu", "open");
	        	if(mContent instanceof HotEventsFragment){
	        		((HotEventsFragment) mContent).setTrans();
	        	}else if(mContent instanceof MyEventFragment){
	        		((MyEventFragment) mContent).setTrans();
	        	}else if(mContent instanceof UserSettingFragment){
	        		((UserSettingFragment) mContent).setTrans();
	        	}
	        	
	        }
	    });
		sm.setOnCloseListener(new OnCloseListener(){
			@Override
			public void onClose(){
				Log.i("sliding menu", "close");
				if(mContent instanceof HotEventsFragment){
	        		((HotEventsFragment) mContent).setTransBack();
	        	}else if(mContent instanceof MyEventFragment){
	        		((MyEventFragment) mContent).setTransBack();
	        	}else if(mContent instanceof UserSettingFragment){
	        		((UserSettingFragment) mContent).setTransBack();
	        	}
			}
		});
		
	}
	//�л���ͬ��fragment�е�����
	public void switchContent(final Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}
	
}
