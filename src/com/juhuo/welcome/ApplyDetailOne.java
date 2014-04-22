package com.juhuo.welcome;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoConfig.Status;
import com.juhuo.tool.Tool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
final class ApplyHandler{
	ImageView img;
	ImageView dailImg;
	TextView name;
	TextView description;
	TextView status;
	TextView cell;
	RelativeLayout whole;
}
public class ApplyDetailOne extends Activity {
	private ListView applyList;
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle;
	private Resources mResources;
	private ApplyDetailAdapter mAdapter;
	private ArrayList<HashMap<String,Object>> mData;
	private LayoutInflater mInflater;
	private ArrayList<String> urls;
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
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private String TAG="ApplyDetailOne";
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apply_detail_one);
		mResources = getResources();
		actionTitleImg = (ImageView)findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)findViewById(R.id.action_title_img2);
		actionTitle = (TextView)findViewById(R.id.action_title);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitleImg2.setVisibility(View.INVISIBLE);
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		applyList = (ListView)findViewById(R.id.applylist);
		Status st = (Status)getIntent().getExtras().get("TYPE");
		switch (st){
		case PARTICIPANT:
			
		}
		actionTitle.setText(mResources.getString(R.string.participant));
		setmData();
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAdapter = new ApplyDetailAdapter();
		applyList.setAdapter(mAdapter);
		applyList.setOnItemClickListener(mListListener);
	}
	private void setmData(String type){
		String res = getIntent().getExtras().getString("APPLY_DETAIL");
		urls = getIntent().getExtras().getStringArrayList("APPLY_URLS");
		try {
			JSONArray ja = new JSONArray(res);
			mData = Tool.commonJ2L(ja);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	OnItemClickListener mListListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(ApplyDetailOne.this,ApplyDetailTwo.class);
			intent.putExtra("name", (String)mData.get(position).get("name"));
			intent.putExtra("age", (String)mData.get(position).get("birthday"));
			intent.putExtra("gender", (String)mData.get(position).get("gender"));
			intent.putExtra("cell", (String)mData.get(position).get("cell"));
			intent.putExtra("description", (String)mData.get(position).get("description"));
			intent.putExtra("url", urls.get(position));
			startActivity(intent);
		}
		
	};
	public class ApplyDetailAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ApplyHandler holder = null;
			if (convertView == null) {
				holder = new ApplyHandler();
				convertView = mInflater.inflate(R.layout.apply_detail_item, null);
				holder.whole = (RelativeLayout)convertView.findViewById(R.id.whole);
				holder.name=(TextView)convertView.findViewById(R.id.name);
				holder.status=(TextView)convertView.findViewById(R.id.status);
				holder.cell=(TextView)convertView.findViewById(R.id.cell);
				holder.img=(ImageView)convertView.findViewById(R.id.image);
				holder.description = (TextView)convertView.findViewById(R.id.description);
				holder.dailImg = (ImageView)convertView.findViewById(R.id.dail);
				convertView.setTag(holder);
				//set item height
//				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(JuhuoConfig.WIDTH*9/32,JuhuoConfig.WIDTH*9/32);
//				holder.whole.setLayoutParams(param);
				holder.img.getLayoutParams().height = JuhuoConfig.WIDTH*9/32;
				holder.img.getLayoutParams().width = JuhuoConfig.WIDTH*9/32;
				holder.img.setScaleType(ScaleType.FIT_XY);
				holder.dailImg.setVisibility(View.INVISIBLE);
			} else {
				holder = (ApplyHandler) convertView.getTag();
			}
			holder.name.setText((String)mData.get(position).get("name"));
//			holder.cell.setText((String)mData.get(position).get("cell"));
			holder.description.setText((String)mData.get(position).get("description"));
			holder.status.setText(JuhuoConfig.STATUS[Integer.parseInt((String)mData.get(position).get("status"))]);
			imageLoader.displayImage(urls.get(position), holder.img, options, animateFirstListener);
			return convertView;
		}
		
	}
	private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				imageView.setBackgroundResource(android.R.color.transparent);//remove default image
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
