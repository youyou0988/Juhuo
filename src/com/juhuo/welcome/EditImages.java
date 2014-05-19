package com.juhuo.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.juhuo.fragment.EditEventImage;
public class EditImages extends FragmentActivity{
	private Fragment mContent;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.home_frame);
		// 设置主界面Fragment视图内容
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null) {
			mContent = new EditEventImage();
			
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.content_frame, mContent,"editfragment");

		// Commit the transaction
		transaction.commit();
	}
	

}
