package com.juhuo.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.ApplyDetailOne;
import com.juhuo.welcome.EventDetailActivity;
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
	private ListView listView;
	private JSONArray jArr;
	private List<HashMap<String,String>> mData = new ArrayList<HashMap<String,String>>();
	private String[] URLS;
	private int picNumber[];//for detail event page 
	private Calendar calendar= Calendar.getInstance();
	int month = calendar.get(Calendar.MONTH)+1;
	private final int EVENT_DETAIL=1;
	//设置当前的日期和时间
	private String currentTime = calendar.get(Calendar.YEAR)+"-"+month+"-"+calendar.get(Calendar.DAY_OF_MONTH)+"T"+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.imageScaleType(ImageScaleType.EXACTLY)
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
	private String type;
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	public void removeUrl(int pos){
		for(int i=pos;i<URLS.length-1;i++){
			URLS[pos] = URLS[pos+1];
		}
	}
	
//	private final ImageDownloader imageDownloader = new ImageDownloader();
	//for network New Data use
	public void setJSONData(JSONArray ja,String type,List<CheckStopAsyncTask> tasklist){
		this.mAsyncTask = tasklist;
		this.type = type;
		mData.clear();
		this.jArr = ja;
		URLS = new String[ja.length()];
		picNumber = new int[ja.length()];
		for(int i=0;i<ja.length();i++){
			try {
				if(ja.getJSONObject(i).has("suc_photos")&&ja.getJSONObject(i).getJSONArray("suc_photos").length()!=0){
					URLS[i] = ja.getJSONObject(i).getJSONArray("suc_photos").
							getJSONObject(0).getString("url");
					this.picNumber[i] = ja.getJSONObject(i).getJSONArray("suc_photos").length();
				}else{
					URLS[i] = "";
					this.picNumber[i] = 0;
				}
				
				HashMap<String,String> tmp = new HashMap<String,String>();
				tmp.put("addr", ja.getJSONObject(i).getString("addr"));
				tmp.put("title", ja.getJSONObject(i).getString("title"));
				String timeEnd = ja.getJSONObject(i).getString("time_end");
				String timeBegin = ja.getJSONObject(i).getString("time_begin");
				if(!timeBegin.equals("null")&&!timeEnd.equals("null")){
					String event_time = Tool.getCalendarByInintData(timeBegin.substring(0,19).replace('T', ' '),
							timeEnd.substring(0,19).replace('T', ' '));
					tmp.put("time", event_time);
				}else{
					tmp.put("time", "已经结束");
				}
				tmp.put("apply_number", ja.getJSONObject(i).getString("apply_number"));
				tmp.put("eventId", String.valueOf(ja.getJSONObject(i).getInt("id")));
				mData.add(tmp);
			} catch (JSONException e) { e.printStackTrace();}
		}
		
	}
	//for cache use
//	public void setMData(List<HashMap<String,String>> md){
//		this.mData.clear();
//		this.mData = md;
//		URLS = new String[mData.size()];
//		picNumber = new int[mData.size()];
//		for(int i=0;i<mData.size();i++){
//			URLS[i] = mData.get(i).get("url");
//		}	
//	}
	
	public void setListView(ListView lv){
		this.listView = lv;
		this.listView.setOnItemClickListener(evlistOnClickListener);
		if(type.equals("MYorganizer")){
			this.listView.setOnItemLongClickListener(evlistLoneClickListener);
		}else{
			this.listView.setOnItemLongClickListener(null);
		}
		
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
			holder.eventTitle=(TextView)convertView.findViewById(R.id.item_title);
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
		imageLoader.displayImage(URLS[position], holder.eventImg, options, animateFirstListener);
		holder.eventTitle.setText(mData.get(position).get("title"));
		holder.eventLocation.setText(mData.get(position).get("addr"));
        holder.eventTime.setText(mData.get(position).get("time"));
        //hide apply number for guest
        if(type.equals("MYorganizer")){
        	holder.eventApply.setVisibility(View.VISIBLE);
        	if(mData.get(position).get("apply_number").equals("0")){
            	holder.eventApply.setVisibility(View.INVISIBLE);
            }else{
            	holder.eventApply.setVisibility(View.VISIBLE);
            	holder.eventApply.setText(mData.get(position).get("apply_number"));
            }
        	
        }else{
        	holder.eventApply.setVisibility(View.INVISIBLE);
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
	
	public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

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
	
	OnItemClickListener evlistOnClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(activity,EventDetailActivity.class);
			intent.putExtra("picNumber",picNumber[position-1]);
			intent.putExtra("eventId", mData.get(position-1).get("eventId"));
			intent.putExtra("type", type);
			intent.putExtra("pos", position-1);
			activity.startActivityForResult(intent,EVENT_DETAIL);
		}
		
	};
	OnItemLongClickListener evlistLoneClickListener = new OnItemLongClickListener() {

        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                final int pos, long id) {
            // TODO Auto-generated method stub

//            Log.v("long clicked",type);
            Dialog alertDialog = new AlertDialog.Builder(activity)
					.setTitle("确定删除活动:").
					setPositiveButton("确定", new DialogInterface.OnClickListener() { 
		                 
		                @Override 
		                public void onClick(DialogInterface dialog, int which) { 
		                    // TODO Auto-generated method stub  
		                	dialog.dismiss();
		    				HashMap<String,Object> map = new HashMap<String,Object>();
		    	            map.put("token", JuhuoConfig.token);
		    	            map.put("id", mData.get(pos-1).get("eventId"));
		    	            DeleteEvent deleteEvent = new DeleteEvent(pos-1);
		    	            mAsyncTask.add(deleteEvent);
		    	            deleteEvent.execute(map);
		    	            int position = pos-1;
		    	            for(int i=position;i<URLS.length-1;i++){
		    					URLS[i] = URLS[i+1];
		    				}
		    				mData.remove(position);
		    				Log.i("datalist", String.valueOf(mData.size()));
		    				notifyDataSetChanged();
		                } 
		            }). 
		            setNegativeButton("取消", new DialogInterface.OnClickListener() { 
		                 
		                @Override 
		                public void onClick(DialogInterface dialog, int which) { 
		                    // TODO Auto-generated method stub  
		                	dialog.dismiss();
		                } 
		            }).
		            create(); 
		    alertDialog.show();
            return true;
        }
    };
    private class DeleteEvent extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
    	private int pos;
    	protected DeleteEvent(int pos){
    		this.pos = pos;
    	}
		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_DELETE);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return;
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(activity, "连接服务器失败,请检查您的网络连接");
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(activity);
			}else{
				Tool.myToast(activity, "删除活动成功");
			}
		}
	}
    

}
