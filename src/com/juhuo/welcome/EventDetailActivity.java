package com.juhuo.welcome;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.juhuo.control.SlideImageLayout;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;

public class EventDetailActivity extends Activity {
	// 滑动图片的集合，这里设置成了固定加载，当然也可动态加载。
	private int[] slideImages;
	// 滑动图片的集合
	private ArrayList<View> imagePageViews = null;
	private ViewGroup main = null;
	private ViewPager viewPager = null;
	// 当前ViewPager索引
	private int pageIndex = 0; 
	
	// 包含圆点图片的View
	private ViewGroup imageCircleView = null;
	private ImageView[] imageCircleViews = null; 
	
	// 布局设置类
	private SlideImageLayout slideLayout = null;
	private HashMap<String,Object> mapPara;
	
	private SlideImageAdapter adapter;
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle,eventTitle,eventBeginTime,eventEndTime,eventPlace
	 ,eventOrganizer,eventIspublic,eventCost,eventLink,eventType,eventTime,eventDetail;
	private Resources mResources;
	private MapView mapView;
	private AMap aMap;
	private String[] eventTypeStr={"所有活动","交友聚会","读书看报","音乐电影","体育锻炼","其他"};
	
	
	private final String TAG = "EventDetailActivity";
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		initViews(savedInstanceState);
		mapPara = new HashMap<String,Object>();
		mapPara.put("id", getIntent().getExtras().getString("eventId"));
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
					eventPlace.setText(result.getString("addr"));
					eventOrganizer.setText(result.getString("organizer_name"));
					int et = Integer.parseInt(result.getString("event_type"));
					eventType.setText(eventTypeStr[et]);
					eventIspublic.setText(result.getInt("privacy")==0?"公开":"不公开");
					eventCost.setText(result.getInt("cost")!=0?String.valueOf(result.getInt("cost")):"免费");
					eventLink.setText(result.getString("url").equals("null")?"":result.getString("url"));
					eventDetail.setText(result.getString("description"));
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
        public Object instantiateItem(View arg0, int arg1) {  
            // TODO Auto-generated method stub  
        	((ViewPager) arg0).addView(imagePageViews.get(arg1));
            
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
}
