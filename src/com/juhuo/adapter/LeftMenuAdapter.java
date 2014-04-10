package com.juhuo.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juhuo.welcome.R;

public class LeftMenuAdapter extends BaseAdapter{
	private String TAG="LeftMenuAdapter";
	private LayoutInflater mInflater;
	private List<String> mData;
	private List<Integer> focus;
	private Resources mResources;
	private Activity activity;
	
	public LeftMenuAdapter(Context context,List<String> mdata,List<Integer> focus){
		this.mInflater = LayoutInflater.from(context);
		this.mData = mdata;
		this.focus = focus;
		this.mResources = context.getResources();
	}
	public void setInflater(LayoutInflater inflater, Activity activity) {
		this.mInflater = inflater;
		this.activity = activity;
	}

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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = mInflater.inflate(R.layout.left_navi_item, null);
		final ImageView img = (ImageView)convertView.findViewById(R.id.navi_img);
		final TextView text = (TextView)convertView.findViewById(R.id.navi_text);
		Log.d(TAG, mData.get(position));
		text.setText(mData.get(position));
		if(position==0){
			if(focus.get(position)==0) {
				text.setTextColor(mResources.getColor(R.color.mgray));
				img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_all_events));
			}
			else {
				text.setTextColor(mResources.getColor(R.color.mgreen));
				img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_all_events_selected));
			}
		}else if(position==1){
			if(focus.get(position)==0) {
				text.setTextColor(mResources.getColor(R.color.mgray));
				img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_my_events));
			}
			else {
				text.setTextColor(mResources.getColor(R.color.mgreen));
				img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_my_events_selected));
			}
		}else if(position==2){
			if(focus.get(position)==0) {
				text.setTextColor(mResources.getColor(R.color.mgray));
				img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_user_settings));
			}
			else {
				text.setTextColor(mResources.getColor(R.color.mgreen));
				img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_user_settings_selected));
			}
		}else if(position==3){
			if(focus.get(position)==0) {
				text.setTextColor(mResources.getColor(R.color.mgray));
				img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_system_settings));
			}
			else {
				text.setTextColor(mResources.getColor(R.color.mgreen));
				img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_system_settings_selected));
			}
		}else{
			img.setVisibility(View.INVISIBLE);
		}
//		convertView.setOnClickListener(new OnClickListener (){
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				text.setTextColor(mResources.getColor(R.color.mgreen));
//				if(position==0){
//					img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_all_events_selected));
//				}else if(position==1){
//					img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_my_events_selected));
//				}else if(position==2){
//					img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_user_settings_selected));
//				}else if(position==3){
//					img.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_system_settings_selected));
//				}else{
//					img.setVisibility(View.INVISIBLE);
//				}
//			}
//		});
		return convertView;
	}

}
