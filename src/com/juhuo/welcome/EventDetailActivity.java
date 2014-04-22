package com.juhuo.welcome;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.juhuo.adapter.HotEventsAdapter.AnimateFirstDisplayListener;
import com.juhuo.control.SlideImageLayout;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoConfig.Status;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class EventDetailActivity extends Activity {
	// 滑动图片的集合，这里设置成了固定加载，当然也可动态加载。
	private int[] slideImages;
	// 滑动图片的集合
	private ArrayList<View> imagePageViews = null;
	private ViewGroup main = null;
	private ViewPager viewPager = null;
	// 当前ViewPager索引
	private int pageIndex = 0; 
	// event_id to work as cache index
	private String event_id;
	
	// 包含圆点图片的View
	private ViewGroup imageCircleView = null;
	private ImageView[] imageCircleViews = null; 
	
	// 布局设置类
	private SlideImageLayout slideLayout = null;
	private HashMap<String,Object> mapPara;
	private HashMap<Integer,ArrayList<String>> map;
	private HashMap<Integer,JSONArray> applyList;
	
	private SlideImageAdapter adapter;
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle,eventTitle,eventBeginTime,eventEndTime,eventPlace
	 ,eventOrganizer,eventIspublic,eventCost,eventLink,eventType,eventTime,eventDetail;
	private TableRow partiRow;
	private Resources mResources;
	private MapView mapView;
	private AMap aMap;
	private String[] eventTypeStr={"所有活动","交友聚会","读书看报","音乐电影","体育锻炼","其他"};
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
	
	private final String TAG = "EventDetailActivity";
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		initViews(savedInstanceState);
		this.event_id = getIntent().getExtras().getString("eventId");
		JSONObject jo = new JSONObject();
		jo = Tool.loadJsonFromFile(JuhuoConfig.EVENTINFO+event_id, this);
		setViewsContent(jo);
		//get network data
		mapPara = new HashMap<String,Object>();
		mapPara.put("id", this.event_id);
		mapPara.put("token", JuhuoConfig.token);
		getNetData(mapPara);
	}
	public void getNetData(HashMap<String,Object> map){
		LoadEventInfo loadEventInfo = new LoadEventInfo();
		loadEventInfo.execute(map);
	}
	private class LoadEventInfo extends AsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... map) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_INFO);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(EventDetailActivity.this);
			}else{
				Tool.writeJsonToFile(result, EventDetailActivity.this, JuhuoConfig.EVENTINFO+event_id);
				setViewsContent(result);
			}
		}
		
	}
	private void initViews(Bundle savedInstanceState){
		mResources = getResources();
		int length = getIntent().getExtras().getInt("picNumber");
		slideImages = new int[length];
		for(int i=0;i<length;i++){
			slideImages[i] = R.drawable.default_image;
		}
		// 滑动图片区域
		imagePageViews = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();  
		main = (ViewGroup)inflater.inflate(R.layout.detail_event, null);
		viewPager = (ViewPager) main.findViewById(R.id.image_slide_page);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
                JuhuoConfig.HEIGHT*11/20);
		viewPager.setLayoutParams(layoutParams);
		
		// 圆点图片区域
		imageCircleViews = new ImageView[length];
		imageCircleView = (ViewGroup) main.findViewById(R.id.layout_circle_images);
		slideLayout = new SlideImageLayout(EventDetailActivity.this);
		slideLayout.setCircleImageLayout(length);
		View dummyView = new View(this);
		for(int i = 0;i < length;i++){
			imagePageViews.add(dummyView);
			imageCircleViews[i] = slideLayout.getCircleImageLayout(i);
			imageCircleView.addView(slideLayout.getLinearLayout(imageCircleViews[i], 10, 10));
		}
		
		setContentView(main);
		
		actionTitleImg = (ImageView)findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)findViewById(R.id.action_title_img2);
		actionTitle = (TextView)findViewById(R.id.action_title);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.plus));
		actionTitleImg2.setVisibility(View.VISIBLE);
		actionTitle.setText(mResources.getString(R.string.detail_event));
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		actionTitleImg2.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 openOptionsMenu();
			}
		});
		
		eventTitle = (TextView)findViewById(R.id.event_title);
		eventTime = (TextView)findViewById(R.id.event_time);
		eventBeginTime = (TextView)findViewById(R.id.event_begin_time);
		eventEndTime = (TextView)findViewById(R.id.event_end_time);
		eventPlace = (TextView)findViewById(R.id.event_place);
		eventOrganizer = (TextView)findViewById(R.id.event_organizer);
		eventType = (TextView)findViewById(R.id.event_type);
		eventIspublic = (TextView)findViewById(R.id.event_ispublic);
		eventCost = (TextView)findViewById(R.id.event_cost);
		eventLink = (TextView)findViewById(R.id.event_link);
		eventDetail = (TextView)findViewById(R.id.event_detail);
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 必须要写
		init();
		
	}
	
	private void setViewsContent(JSONObject result){
		try {
			if(result.has("suc_photos")){
				JSONArray ja = result.getJSONArray("suc_photos");
				for(int i=0;i<ja.length();i++){
					String url = ja.getJSONObject(i).getString("url");
					imagePageViews.set(i, slideLayout.getSlideImageLayout2(url));
				}
				adapter = new SlideImageAdapter();
				viewPager.setAdapter(adapter);  
			    viewPager.setOnPageChangeListener(new ImagePageChangeListener());
			}
			eventTitle.setText(result.getString("title"));
			String tb = result.getString("time_begin").substring(0,19).replace('T', ' ');
			String te = result.getString("time_end").substring(0,19).replace('T', ' ');
			eventBeginTime.setText(tb);
			eventEndTime.setText(te);
			eventTime.setText(Tool.getCalendarByInintData(tb, te));
			if(eventTime.getText().equals("正在进行!")){
	        	eventTime.setBackgroundColor(mResources.getColor(R.color.lightgreen));
	        }else if(eventTime.getText().equals("即将开始!")){
	        	eventTime.setBackgroundColor(mResources.getColor(R.color.darkred));
	        }else{
	        	eventTime.setBackgroundColor(mResources.getColor(R.color.graytrans));
	        }	
			eventPlace.setText(result.getString("addr"));
			eventOrganizer.setText(result.getString("organizer_name"));
			int et = Integer.parseInt(result.getString("event_type"));
			eventType.setText(eventTypeStr[et]);
			eventIspublic.setText(result.getInt("privacy")==0?"公开":"不公开");
			eventCost.setText(result.getInt("cost")!=0?String.valueOf(result.getInt("cost")):"免费");
			eventLink.setText(result.getString("url").equals("null")?"":result.getString("url"));
			if(!result.getString("description").equals("")){
				RelativeLayout.LayoutParams layoutDetailParams = new RelativeLayout.LayoutParams(
						JuhuoConfig.WIDTH*5/8,
		                JuhuoConfig.HEIGHT*6/54);
				layoutDetailParams.setMargins(Tool.dip2px(this, 7), 0, Tool.dip2px(this, 50), 0);
				eventDetail.setText(result.getString("description"));
				eventDetail.setLayoutParams(layoutDetailParams);
			}else{
				findViewById(R.id.detailgone).setVisibility(View.GONE);
			}
			
			JSONArray jaChoices = result.getJSONArray("choices");
			map = new HashMap<Integer,ArrayList<String>>();
			applyList = new HashMap<Integer,JSONArray>();
			for(int i=0;i<6;i++){
				map.put(i, new ArrayList<String>());
				applyList.put(i, new JSONArray());
			}
			for(int i=0;i<jaChoices.length();i++){
				int status = jaChoices.getJSONObject(i).getInt("status");
				String url;
				if(jaChoices.getJSONObject(i).has("suc_photos")){
					url = jaChoices.getJSONObject(i).
							getJSONArray("suc_photos").getJSONObject(0).getString("url");
				}else{
					url="";
				}
				
				if(status==JuhuoConfig.INVI_YES||status==JuhuoConfig.INVI_MAYBE||status==JuhuoConfig.INVI_ORGANIZER){
					applyList.get(JuhuoConfig.INVI_ORGANIZER).put(jaChoices.getJSONObject(i));//for next page
					map.get(JuhuoConfig.INVI_ORGANIZER).add(url);
				}else{
					map.get(status).add(url);
				}
			}
//			map.get(JuhuoConfig.INVI_ORGANIZER).addAll(map.get(JuhuoConfig.INVI_YES));
//			map.get(JuhuoConfig.INVI_ORGANIZER).addAll(map.get(JuhuoConfig.INVI_MAYBE));
			setChoicesTable(JuhuoConfig.INVI_ORGANIZER,map);
			setChoicesTable(JuhuoConfig.INVI_NULL,map);
			setChoicesTable(JuhuoConfig.INVI_NO,map);
			setChoicesTable(JuhuoConfig.INVI_APPLY,map);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setChoicesTable(int type,HashMap<Integer,ArrayList<String>> map){
		TableRow tr = new TableRow(this);
		TextView tx = new TextView(this);
//		map.get(type).removeAll(Collections.singleton(""));
		if(type==JuhuoConfig.INVI_ORGANIZER){
			//participant layout
			tr = (TableRow)findViewById(R.id.participantrows);
			tx = (TextView)findViewById(R.id.participant);
			if(map.get(type).size()==0){
				findViewById(R.id.participantwid).setVisibility(View.GONE);
			}else{
				findViewById(R.id.participantwid).setOnClickListener(InviListener);
				tx.setText(mResources.getString(R.string.participant)+" ("+map.get(type).size()+")");
			}
		}else if(type==JuhuoConfig.INVI_NULL){
			tr = (TableRow)findViewById(R.id.invitedrows);
			tx = (TextView)findViewById(R.id.invited);
			if(map.get(type).size()==0){
				findViewById(R.id.invitedwid).setVisibility(View.GONE);
			}else{
				findViewById(R.id.invitedwid).setOnClickListener(InviListener);
				tx.setText(mResources.getString(R.string.invited)+" ("+map.get(type).size()+")");
			}
		}else if(type==JuhuoConfig.INVI_NO){
			tr = (TableRow)findViewById(R.id.absentrows);
			tx = (TextView)findViewById(R.id.absent);
			if(map.get(type).size()==0){
				findViewById(R.id.absentwid).setVisibility(View.GONE);
			}else{
				tx.setText(mResources.getString(R.string.absent)+" ("+map.get(type).size()+")");
			}
			
		}else if(type==JuhuoConfig.INVI_APPLY){
			tr = (TableRow)findViewById(R.id.applyrows);
			tx = (TextView)findViewById(R.id.apply);
			if(map.get(type).size()==0){
				findViewById(R.id.applywid).setVisibility(View.GONE);
			}else{
				tx.setText(mResources.getString(R.string.apply)+" ("+map.get(type).size()+")");
			}
			
		}
		tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        tr.removeAllViews();
        
        
		int length = map.get(type).size()>4?4:map.get(type).size();
        for (int j = 0; j < length; j++) {
        	ImageView view = getNewImage();
            imageLoader.displayImage(map.get(type).get(j), view,options,animateFirstListener);
            tr.addView(view);
        }
	}
	OnClickListener InviListener = new OnClickListener(){
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			RelativeLayout v = (RelativeLayout)view;
			switch (v.getId()){
			case R.id.participantwid:
				Intent parintent = new Intent(EventDetailActivity.this,ApplyDetailOne.class);
				parintent.putExtra("APPLY_DETAIL", applyList.get(JuhuoConfig.INVI_ORGANIZER).toString());
				parintent.putExtra("APPLY_URLS", map.get(JuhuoConfig.INVI_ORGANIZER));
				parintent.putExtra("TYPE", Status.PARTICIPANT);
				startActivity(parintent);
				break;
			case R.id.invitedwid:
				Intent invintent = new Intent(EventDetailActivity.this,ApplyDetailOne.class);
				invintent.putExtra("APPLY_DETAIL", applyList.get(JuhuoConfig.INVI_NULL).toString());
				invintent.putExtra("APPLY_URLS", map.get(JuhuoConfig.INVI_NULL));
				invintent.putExtra("TYPE", Status.INVITED);
				startActivity(invintent);
				break;
			}
		}
	};
	public ImageView getNewImage(){
		int imageWidth = (Tool.dip2px(this, JuhuoConfig.WIDTH)-120)/4;
//		Log.i(TAG, String.valueOf(JuhuoConfig.WIDTH));
//		Log.i(TAG, String.valueOf(imageWidth));
		ImageView view = new ImageView(this);
        view.setImageResource(R.drawable.default_image);
        TableRow.LayoutParams imPara = new TableRow.LayoutParams(imageWidth/2,imageWidth/2);
        imPara.setMargins(0, 0, imageWidth/20, 0);
        view.setLayoutParams(imPara);
        view.setScaleType(ScaleType.FIT_XY);
        return view;
	}
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
	}
	// 滑动图片数据适配器
    private class SlideImageAdapter extends PagerAdapter {  
        @Override  
        public int getCount() { 
            return imagePageViews.size();  
        }  
  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
  
        @Override  
        public int getItemPosition(Object object) {  
            // TODO Auto-generated method stub  
            return super.getItemPosition(object);  
        }  
  
        @Override  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).removeView(imagePageViews.get(arg1));  
        }  
  
        @Override  
        public Object instantiateItem(View parent, int arg1) {  
            // TODO Auto-generated method stub  
        	((ViewPager) parent).addView(imagePageViews.get(arg1));
            return imagePageViews.get(arg1);  
        }
        
  
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
  
        @Override  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void finishUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
    }
	
 // 滑动页面更改事件监听器
    private class ImagePageChangeListener implements OnPageChangeListener {
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void onPageSelected(int index) {  
        	pageIndex = index;
        	slideLayout.setPageIndex(index);
        	
            for (int i = 0; i < imageCircleViews.length; i++) {  
            	imageCircleViews[index].setBackgroundResource(R.drawable.dot_selected1);
                
                if (index != i) {  
                	imageCircleViews[i].setBackgroundResource(R.drawable.dot_none1);  
                }  
            }
        }  
    }
    /**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
