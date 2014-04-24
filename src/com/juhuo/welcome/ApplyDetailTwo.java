package com.juhuo.welcome;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.Tool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class ApplyDetailTwo extends Activity {
	private TextView name;
	private TextView age;
	private TextView gender;
	private TextView cell;
	private TextView description;
	private ImageView image;
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle;
	private Resources mResources;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
	.showImageOnLoading(R.drawable.default_image)
	.showImageForEmptyUri(R.drawable.default_image)
	.showImageOnFail(R.drawable.default_image)
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.considerExifParams(true)
	.displayer(new SimpleBitmapDisplayer())
	.build();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apply_detail_two);
		mResources = getResources();
		actionTitleImg = (ImageView)findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)findViewById(R.id.action_title_img2);
		actionTitle = (TextView)findViewById(R.id.action_title);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitleImg2.setVisibility(View.INVISIBLE);
		actionTitle.setText(mResources.getString(R.string.detail));
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		name = (TextView)findViewById(R.id.t2);
		age = (TextView)findViewById(R.id.t4);
		gender = (TextView)findViewById(R.id.t6);
		cell = (TextView)findViewById(R.id.t8);
		description = (TextView)findViewById(R.id.t10);
		image = (ImageView)findViewById(R.id.image);
		name.setText(getIntent().getExtras().getString("name"));
		if(getIntent().getExtras().getString("age").equals("null")){
			age.setText("Î´Öª");
		}else{
			String a = getIntent().getExtras().getString("age").substring(0,19).replace('T', ' ');
			age.setText(String.valueOf(Tool.getAgeFromBirthday(a)));
		}
		String ge = getIntent().getExtras().getString("gender").equals("0")?"ÄÐ":"Å®";
		gender.setText(ge);
		cell.setText(getIntent().getExtras().getString("cell"));
		description.setText(getIntent().getExtras().getString("description"));
		image.getLayoutParams().height = JuhuoConfig.WIDTH*150/320;
		image.getLayoutParams().width = JuhuoConfig.WIDTH*150/320;
		imageLoader.displayImage(getIntent().getExtras().getString("url"),image, options);
	}

}
