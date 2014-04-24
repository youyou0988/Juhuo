package com.juhuo.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juhuo.welcome.R;

public class LeftMenuAdapter extends BaseAdapter{
	private String TAG="LeftMenuAdapter";
	private LayoutInflater mInflater;
	private ArrayList<HashMap<String,Object>> mData;
//	private List<Integer> focus;
	private Resources mResources;
	private Activity activity;
	
	public LeftMenuAdapter(Context context,ArrayList<HashMap<String,Object>> mdata){
		this.mInflater = LayoutInflater.from(context);
		this.mData = mdata;
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
		text.setText((String)mData.get(position).get("title"));
		if((Integer)mData.get(position).get("focus")==0){
			text.setTextColor(mResources.getColor(R.color.mgray));
			img.setBackgroundDrawable((Drawable)mData.get(position).get("icon"));
		}else{
			text.setTextColor(mResources.getColor(R.color.mgreen));
			img.setBackgroundDrawable(((Drawable)mData.get(position).get("icon-selected")));
		}
		return convertView;
	}

}
