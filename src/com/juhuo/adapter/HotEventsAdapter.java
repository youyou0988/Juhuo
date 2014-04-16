package com.juhuo.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juhuo.welcome.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

final class HotEventsHandler{
	ImageView eventImg;
	TextView eventTitle;
	TextView eventLocation;
	TextView eventTime;
	TextView eventApply;
}

public class HotEventsAdapter extends BaseAdapter {
	private String TAG="HotEventsAdapter";
	private LayoutInflater mInflater;
	private Activity activity;
	private JSONArray jArr;
	private List<HashMap<String,String>> mData = new ArrayList<HashMap<String,String>>();
	private String[] URLS;
	private Calendar calendar= Calendar.getInstance();
	int month = calendar.get(Calendar.MONTH)+1;
	//设置当前的日期和时间
	private String currentTime = calendar.get(Calendar.YEAR)+"-"+month+"-"+calendar.get(Calendar.DAY_OF_MONTH)+"T"+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
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
    
	
//	private final ImageDownloader imageDownloader = new ImageDownloader();
	
	public void setData(JSONArray ja){
		this.jArr = ja;
		URLS = new String[ja.length()];
		for(int i=0;i<ja.length();i++){
			try {
				if(ja.getJSONObject(i).has("suc_photos")){
					URLS[i] = ja.getJSONObject(i).getJSONArray("suc_photos").
							getJSONObject(0).getString("url");
				}else{
					URLS[i] = "";
				}
				
				HashMap<String,String> tmp = new HashMap<String,String>();
				tmp.put("addr", ja.getJSONObject(i).getString("addr"));
				tmp.put("title", ja.getJSONObject(i).getString("title"));
				String timeEnd = ja.getJSONObject(i).getString("time_end");
				String timeBegin = ja.getJSONObject(i).getString("time_begin");
				String event_time = getCalendarByInintData(timeBegin.substring(0,19).replace('T', ' '),
						timeEnd.substring(0,19).replace('T', ' '));
				tmp.put("time", event_time);
				tmp.put("apply_number", ja.getJSONObject(i).getString("apply_number"));
				mData.add(tmp);
			} catch (JSONException e) { e.printStackTrace();}
		}
		
	}
    
	private String getCalendarByInintData(String beginDateTime,String endDatetime) {
		Calendar calendar = Calendar.getInstance();
		long current = calendar.getTimeInMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendarbegin = Calendar.getInstance();
		Calendar calendarend = Calendar.getInstance();
		try {
			calendarbegin.setTime(sdf.parse(beginDateTime));
			calendarend.setTime(sdf.parse(endDatetime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long time_begin = calendarbegin.getTimeInMillis();
		long time_end = calendarend.getTimeInMillis();
		if(current<time_begin) return "即将开始!";
		else if(current>=time_begin&&current<=time_end) return "正在进行!";
		else return "已经结束!";
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
//		return URLS[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
//		return URLS[position].hashCode();
		return 0;
	}
	
	public void setInflater(LayoutInflater inflater, Activity activity) {
		this.mInflater = inflater;
		this.activity = activity;
//		this.imageDownloader.setResources(activity.getResources());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HotEventsHandler holder = null;
		if (convertView == null) {
			holder = new HotEventsHandler();
			convertView = mInflater.inflate(R.layout.hot_events_item, null);
			holder.eventTitle=(TextView)convertView.findViewById(R.id.event_title);
			holder.eventLocation=(TextView)convertView.findViewById(R.id.event_location);
			holder.eventTime=(TextView)convertView.findViewById(R.id.event_time);
			holder.eventImg=(ImageView)convertView.findViewById(R.id.event_pic);
			holder.eventApply = (TextView)convertView.findViewById(R.id.event_apply);
			//set height to the imageView
			Display display = activity.getWindowManager().getDefaultDisplay(); 
			Point size = new Point(); 
			display.getSize(size); 
			holder.eventImg.getLayoutParams().height = size.y/3;
//			holder.eventImg.getLayoutParams().width = size.y/3;
			convertView.setTag(holder);
		} else {
			holder = (HotEventsHandler) convertView.getTag();
		}
//		imageDownloader.download(URLS[position], (ImageView) holder.eventImg);
		imageLoader.displayImage(URLS[position], holder.eventImg, options, animateFirstListener);
		holder.eventTitle.setText(mData.get(position).get("title"));
		holder.eventLocation.setText(mData.get(position).get("addr"));
        holder.eventTime.setText(mData.get(position).get("time"));
        if(mData.get(position).get("apply_number").equals("0")){
        	holder.eventApply.setVisibility(View.INVISIBLE);
        }else{
        	holder.eventApply.setVisibility(View.VISIBLE);
        	holder.eventApply.setText(mData.get(position).get("apply_number"));
        }
        
        if(holder.eventTime.getText().equals("正在进行!")){
        	holder.eventTime.setBackgroundColor(activity.getResources().getColor(R.color.lightgreen));
        }else if(holder.eventTime.getText().equals("即将开始!")){
        	holder.eventTime.setBackgroundColor(activity.getResources().getColor(R.color.darkred));
        }else{
        	holder.eventTime.setBackgroundColor(activity.getResources().getColor(R.color.graytrans));
        }
		return convertView;
	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

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
	
//	public ImageDownloader getImageDownloader() {
//        return imageDownloader;
//    }

}
