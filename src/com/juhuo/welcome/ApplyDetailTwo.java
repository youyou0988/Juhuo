package com.juhuo.welcome;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class ApplyDetailTwo extends Activity {
	private final String TAG="ApplyDetailTwo";
	private TextView name;
	private TextView age;
	private TextView gender;
	private TextView cell;
	private TextView description;
	private ImageView image;
	private ImageView actionTitleImg;
	private TextView actionTitle,actionTitleTxt2;
	private Resources mResources;
	private String ids;
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
		actionTitleTxt2 = (TextView)findViewById(R.id.action_title_txt2);
		actionTitle = (TextView)findViewById(R.id.action_title);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitle.setText(mResources.getString(R.string.description));
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		actionTitleTxt2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TextView tv = (TextView)arg0;
				if(tv.getText().equals("¹Ø×¢")){
					CreateFollowClass task = new CreateFollowClass();
					HashMap<String,Object> params = new HashMap<String,Object>();
					params.put("token", JuhuoConfig.token);
					ArrayList<HashMap<String,String>> paraslist = new ArrayList<HashMap<String,String>>();
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("name", getIntent().getExtras().getString("name"));
					map.put("cell", getIntent().getExtras().getString("cell"));
					paraslist.add(map);
					params.put("contacts", paraslist);
					task.execute(params);
				}else{
					
				}
				
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
	private class CreateFollowClass extends AsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... arg0) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = arg0[0];
			return new JuhuoInfo().callPostPlain(mapped,JuhuoConfig.CONTACT_CREATE);
		}
		@Override
		protected void onPostExecute(JSONObject result){
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(ApplyDetailTwo.this, mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(ApplyDetailTwo.this);
			}else{
				try {
					ids = result.getString("ids");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Tool.myToast(ApplyDetailTwo.this, mResources.getString(R.string.add_follow_success));
				actionTitleTxt2.setText(mResources.getString(R.string.cancel_follow));
			}
		}
	}

}
