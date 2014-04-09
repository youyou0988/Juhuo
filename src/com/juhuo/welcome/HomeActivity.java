package com.juhuo.welcome;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.juhuo.fragment.HotEventsFragment;
import com.juhuo.fragment.LeftMenuFragment;
import com.juhuo.tool.Tool;

public class HomeActivity extends SlidingFragmentActivity {
	private int WIDTH,HEIGHT;
	private Fragment mContent;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_frame);
		getSize();
		initSlidingMenu(savedInstanceState);
//		initComponents();
//        setListener();
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
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//			// show home as up so we can toggle
//			getActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		// 设置主界面Fragment视图内容
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

		// 设置滑动菜单的属性值
		SlidingMenu sm = getSlidingMenu();
		Log.i("dimension", String.valueOf(Tool.px2dip(HomeActivity.this, WIDTH*0.74)));
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setFadeDegree(0.35f);
	}
}
