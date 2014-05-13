package com.juhuo.welcome;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditEventDetail extends Activity {
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle;
	private EditText detail;
	private Resources mResources;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//»•µÙ±ÍÃ‚¿∏
		setContentView(R.layout.edit_event_detail);
		mResources = getResources();
		actionTitleImg = (ImageView)findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)findViewById(R.id.action_title_img2);
		actionTitle = (TextView)findViewById(R.id.action_title);
		detail = (EditText)findViewById(R.id.detail);
		detail.setText(getIntent().getExtras().getString("detail"));
		detail.setSelectAllOnFocus(true);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_check_inactive));
		actionTitleImg2.setVisibility(View.VISIBLE);
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		actionTitleImg2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(EditEventDetail.this,CreateEvent.class);
				intent.putExtra("detail", detail.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		actionTitle.setText(mResources.getString(R.string.detail));
	}

}
