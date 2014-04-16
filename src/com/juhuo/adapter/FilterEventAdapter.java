package com.juhuo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.juhuo.welcome.R;

public class FilterEventAdapter extends BaseAdapter {
	
	private Activity activity;
	private String[] eventTypeData;
	private int[] focusData;
	private LayoutInflater mInflater;
	private String type;
	
	public FilterEventAdapter(String[] data,Activity activity,int[] focus){
		this.mInflater = LayoutInflater.from(activity);
		this.activity = activity;
		this.eventTypeData = data;
		this.focusData = focus;
		this.type = data.length==6?"one":"two";
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return eventTypeData.length;
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
		convertView = mInflater.inflate(R.layout.subfilter_item, null);
		TextView eventType = (TextView)convertView.findViewById(R.id.event_type);
		
		eventType.setText(eventTypeData[position]);
		if(focusData[position]==1){
			eventType.setTextColor(activity.getResources().getColor(R.color.mgreen));
		}else{
			eventType.setTextColor(activity.getResources().getColor(R.color.mgray));
		}
		return convertView;
	}

}
