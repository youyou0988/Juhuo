package com.juhuo.welcome;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.juhuo.adapter.HotEventsAdapter.AnimateFirstDisplayListener;
import com.juhuo.contact.SelectContactActivity;
import com.juhuo.control.PullDownElasticImp;
import com.juhuo.control.RefreshableView;
import com.juhuo.control.RefreshableView.RefreshListener;
import com.juhuo.control.SlideImageLayout;
import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoConfig.Status;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class EventDetailActivity extends Activity {
	//可下拉刷新的layout
	private RefreshableView mRefreshableView;
	// 滑动图片的集合，这里设置成了固定加载，当然也可动态加载。
	private int[] slideImages;
	private ProgressDialog mPgDialog;
	// 滑动图片的集合
	private ArrayList<View> imagePageViews = null;
	private ViewGroup main = null;
	private ViewPager viewPager = null;
	private Button applyEventBtn,status1,status2,status3,cancelBtn;
	private RelativeLayout statusbarLay,applyLay,shareLay,actionTitleLay,actionTitleLay2
			,eventLinkLay;
	// 当前ViewPager索引
	private int pageIndex = 0; 
	// event_id to work as cache index
	private String event_id;
	private int organizer_status=7;
	
	// 包含圆点图片的View
	private ViewGroup imageCircleView = null;
	private ArrayList<ImageView> imageCircleViews; 
	
	// 布局设置类
	private SlideImageLayout slideLayout = null;
	private HashMap<String,Object> mapPara;
	private HashMap<Integer,ArrayList<String>> map;
	private HashMap<Integer,JSONArray> applyList;
	
	private SlideImageAdapter adapter;
	private ImageView actionTitleImg,actionTitleImg2,wechat,weibo,timeline;
	private TextView actionTitle,eventTitle,eventBeginTime,eventEndTime,eventPlace
	 ,eventOrganizer,eventIspublic,eventCost,eventLink,eventType,eventTime,eventDetail;
	private TableRow partiRow;
	private Resources mResources;
	private MapView mapView;
	private AMap aMap;
	private UiSettings mUiSettings;
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
	private String description="";
	private String type,title,organizer,sharepicurl="",shareLink;
	private Calendar startcal,endcal;
	private Bitmap thumb;
	private IWXAPI wxApi;
	private IWeiboShareAPI  mWeiboShareAPI = null;
	private View transView;
	private SimpleDateFormat df = new SimpleDateFormat(Tool.ISO8601DATEFORMAT, Locale.getDefault());
	private final int UPDATE_EVENT = 0;
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		this.event_id = getIntent().getExtras().getString("eventId");
		initViews(savedInstanceState);
		Tool.initImageLoader(this);
		JSONObject jo = new JSONObject();
		jo = Tool.loadJsonFromFile(JuhuoConfig.EVENTINFO+event_id, this);
		mapPara = new HashMap<String,Object>();
		mapPara.put("id", this.event_id);
		mapPara.put("token", JuhuoConfig.token);
		if(jo!=null){
			//get network data
			setViewsContent(jo);
		}
		mRefreshableView.onRefreshing();
		LoadEventInfo loadEventInfo = new LoadEventInfo();
		mAsyncTask.add(loadEventInfo);
		loadEventInfo.execute(mapPara);
		mPgDialog = new ProgressDialog(this);
        
		//实例化
		wxApi = WXAPIFactory.createWXAPI(this, JuhuoConfig.APP_ID_WECHAT);
		wxApi.registerApp(JuhuoConfig.APP_ID_WECHAT);
		
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, JuhuoConfig.APP_KEY);
		mWeiboShareAPI.registerApp();
		if (!mWeiboShareAPI.isWeiboAppInstalled()) {
            mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
                @Override
                public void onCancel() {
                    Toast.makeText(EventDetailActivity.this, 
                            R.string.weibosdk_demo_cancel_download_weibo, 
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
		
	}
	
	private void initViews(Bundle savedInstanceState){
		mResources = getResources();
		int length = getIntent().getExtras().getInt("picNumber")==0?1:getIntent().getExtras().getInt("picNumber");
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
		imageCircleViews = new ArrayList<ImageView>();
		imageCircleView = (ViewGroup) main.findViewById(R.id.layout_circle_images);
		slideLayout = new SlideImageLayout(EventDetailActivity.this);
		slideLayout.setCircleImageLayout(length);
		View dummyView = new View(this);
		dummyView = slideLayout.getSlideImageLayout2("");
		for(int i = 0;i < length;i++){
			imagePageViews.add(dummyView);
			imageCircleViews.add(slideLayout.getCircleImageLayout(i));
			imageCircleView.addView(slideLayout.getLinearLayout(imageCircleViews.get(i), 10, 10));
		}
		
		setContentView(main);
		
		transView = (View)findViewById(R.id.transview3);
		actionTitleImg = (ImageView)findViewById(R.id.action_title_img);
		actionTitleLay = (RelativeLayout)findViewById(R.id.action_title_lay);
		actionTitleLay2 = (RelativeLayout)findViewById(R.id.action_title_lay2);
		actionTitleImg2 = (ImageView)findViewById(R.id.action_title_img2);
		actionTitle = (TextView)findViewById(R.id.action_title);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.plus));
		actionTitleImg2.setVisibility(View.VISIBLE);
		actionTitle.setText(mResources.getString(R.string.detail_event));
		
		actionTitleImg.setOnClickListener(clickListener);
		actionTitleImg2.setOnClickListener(clickListener);
		
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
		eventLinkLay = (RelativeLayout)findViewById(R.id.linklay);
		eventDetail = (TextView)findViewById(R.id.event_detail);
		applyEventBtn = (Button)findViewById(R.id.apply_event_btn);
		RelativeLayout eventComment = (RelativeLayout)findViewById(R.id.commentlay);
		eventComment.setOnClickListener(InviListener);
		actionTitleLay.setOnClickListener(InviListener);
		actionTitleLay2.setOnClickListener(InviListener);
		eventLinkLay.setOnClickListener(InviListener);
		eventOrganizer.setOnClickListener(txtListener);
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 必须要写
		init();
		
		applyLay = (RelativeLayout)findViewById(R.id.applylay);
		statusbarLay = (RelativeLayout)findViewById(R.id.statusbar);
		shareLay = (RelativeLayout)findViewById(R.id.share_layout);
		wechat = (ImageView)findViewById(R.id.wechat);
		timeline = (ImageView)findViewById(R.id.timeline);
		weibo = (ImageView)findViewById(R.id.weibo);
		timeline.setOnClickListener(clickListener);
		wechat.setOnClickListener(clickListener);
		weibo.setOnClickListener(clickListener);
		cancelBtn = (Button)findViewById(R.id.cancel_btn);
		type=getIntent().getExtras().getString("type");
		applyLay.setVisibility(View.GONE);
		applyEventBtn.setVisibility(View.GONE);
		statusbarLay.setVisibility(View.GONE);
		if(type.equals("MYorganizer")){//my events' detail
			applyLay.setVisibility(View.VISIBLE);
			applyEventBtn.setVisibility(View.GONE);
			statusbarLay.setVisibility(View.GONE);
		}else if(type.endsWith("MYrelated")){
			applyLay.setVisibility(View.VISIBLE);
			applyEventBtn.setVisibility(View.GONE);
			statusbarLay.setVisibility(View.VISIBLE);
		}
		applyEventBtn.setOnClickListener(btnClickListener);
		cancelBtn.setOnClickListener(btnClickListener);
		status1 = (Button)findViewById(R.id.status1);
		status2 = (Button)findViewById(R.id.status2);
		status3 = (Button)findViewById(R.id.status3);
		status2.setMinimumWidth(0);
		status2.setWidth(JuhuoConfig.WIDTH/3);
		status1.setOnClickListener(btnClickListener);
		status2.setOnClickListener(btnClickListener);
		status3.setOnClickListener(btnClickListener);
		Log.i(TAG, String.valueOf(status2.getWidth()));
		mRefreshableView = (RefreshableView) findViewById(R.id.refresh_root);    
		mRefreshableView.setRefreshListener(mRefreshListener);    
		mRefreshableView.setPullDownElastic(new PullDownElasticImp(this)); 
	}
	OnClickListener txtListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent invintent = new Intent(EventDetailActivity.this,ApplyDetailOne.class);
			invintent.putExtra("ORGANIZER_DETAIL", applyList.get(JuhuoConfig.INVI_ORGANIZER).toString());
			invintent.putExtra("ORGANIZER_URLS", map.get(JuhuoConfig.INVI_ORGANIZER));
			invintent.putExtra("TYPE", Status.ORGANIZER);
			invintent.putExtra("ORGANIZER_STATUS", organizer_status);
			startActivity(invintent);
		}
	};
	OnClickListener InviListener = new OnClickListener(){
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			RelativeLayout v = (RelativeLayout)view;
			switch (v.getId()){
			case R.id.participantwid:
				Intent parintent = new Intent(EventDetailActivity.this,ApplyDetailOne.class);
				parintent.putExtra("APPLY_DETAIL", applyList.get(JuhuoConfig.INVI_YES).toString());
				parintent.putExtra("APPLY_URLS", map.get(JuhuoConfig.INVI_YES));
				parintent.putExtra("TYPE", Status.PARTICIPANT);
				parintent.putExtra("ORGANIZER_STATUS", organizer_status);
				startActivity(parintent);
				break;
			case R.id.invitedwid:
				Intent invintent = new Intent(EventDetailActivity.this,ApplyDetailOne.class);
				invintent.putExtra("INVITED_DETAIL", applyList.get(JuhuoConfig.INVI_NULL).toString());
				invintent.putExtra("INVITED_URLS", map.get(JuhuoConfig.INVI_NULL));
				invintent.putExtra("TYPE", Status.INVITED);
				invintent.putExtra("ORGANIZER_STATUS", organizer_status);
				startActivity(invintent);
				break;
			case R.id.absentwid:
				Intent absenttent = new Intent(EventDetailActivity.this,ApplyDetailOne.class);
				absenttent.putExtra("ABSENT_DETAIL", applyList.get(JuhuoConfig.INVI_NO).toString());
				absenttent.putExtra("ABSENT_URLS", map.get(JuhuoConfig.INVI_NO));
				absenttent.putExtra("TYPE", Status.NO);
				absenttent.putExtra("ORGANIZER_STATUS", organizer_status);
				absenttent.putExtra("PAGE", type);
				startActivity(absenttent);
				break;
			case R.id.commentlay:
				Intent comintent = new Intent(EventDetailActivity.this,EventComment.class);
				comintent.putExtra("id", event_id);
				comintent.putExtra("organizer_status",organizer_status);
				startActivity(comintent);
				break;
			case R.id.applywid:
				Intent applytent = new Intent(EventDetailActivity.this,ApplyDetailOne.class);
				applytent.putExtra("APPLY_DETAIL", applyList.get(JuhuoConfig.INVI_APPLY).toString());
				applytent.putExtra("APPLY_URLS", map.get(JuhuoConfig.INVI_APPLY));
				applytent.putExtra("TYPE", Status.APPLY);
				applytent.putExtra("event_id", event_id);
				startActivity(applytent);
				break;
			case R.id.action_title_lay:
				finish();
				break;
			case R.id.action_title_lay2:
				openOptionsMenu();
				break;
			case R.id.linklay:
				if(!eventLink.getText().toString().equals("")){
					Uri uri = Uri.parse(eventLink.getText().toString());  
	                Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
	                intent.setClassName("com.android.browser",
							"com.android.browser.BrowserActivity");
	                startActivity(intent); 
				}
                break;
			}
		}
	};
	OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ImageView vi = (ImageView)v;
			switch(vi.getId()){
			case R.id.action_title_img:
				finish();
				break;
			case R.id.action_title_img2:
				openOptionsMenu();
				break;
			case R.id.wechat:
				Log.i(TAG, "wechat");
				shareLay.setVisibility(View.GONE);
				transView.setVisibility(View.GONE);
				wechatShare(0);
				break;
			case R.id.timeline:
				shareLay.setVisibility(View.GONE);
				transView.setVisibility(View.GONE);
				wechatShare(1);
				break;
			case R.id.weibo:
				shareLay.setVisibility(View.GONE);
				transView.setVisibility(View.GONE);
				weiBoShare();
				break;
			}
			
		}
	};
	OnClickListener btnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Button vb = (Button)arg0;
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("token", JuhuoConfig.token);
			params.put("id", event_id);
			switch(vb.getId()){
			case R.id.apply_event_btn:
				
				params.put("time", df.format(new Date()));
				ApplyEventClass task = new ApplyEventClass();
				mAsyncTask.add(task);
				task.execute(params);
				vb.setText(mResources.getString(R.string.apply_eventing));
				break;
			case R.id.status1:
				params.put("status", String.valueOf(1));
				ConfirmEventClass task2 = new ConfirmEventClass(1);
				mAsyncTask.add(task2);
				task2.execute(params);
				vb.setTextColor(mResources.getColor(R.color.mgreen));
				break;
			case R.id.status2:
				params.put("status", String.valueOf(2));
				ConfirmEventClass task3 = new ConfirmEventClass(2);
				mAsyncTask.add(task3);
				task3.execute(params);
				vb.setTextColor(mResources.getColor(R.color.mgreen));
				break;
			case R.id.status3:
				params.put("status", String.valueOf(3));
				ConfirmEventClass task4 = new ConfirmEventClass(3);
				mAsyncTask.add(task4);
				task4.execute(params);
				vb.setTextColor(mResources.getColor(R.color.mgreen));
				break;
			case R.id.cancel_btn:
				shareLay.setVisibility(View.GONE);
				transView.setVisibility(View.GONE);
				break;
			}
		}
	};
	
	RefreshListener mRefreshListener = new RefreshListener(){

		@Override
		public void onRefresh(RefreshableView view) {
			// TODO Auto-generated method stub
			Log.i(TAG, "on refresh");
			mRefreshableView.onRefreshing();
			LoadEventInfo loadEventInfo = new LoadEventInfo();
			mAsyncTask.add(loadEventInfo);
			loadEventInfo.execute(mapPara);
		}
		
	};
	
	private void setViewsContent(JSONObject result){
		try {
			if(result.has("suc_photos")){
				JSONArray ja = result.getJSONArray("suc_photos");
				Log.i(TAG+"imagePageViews", String.valueOf(imagePageViews.size()));
				int imagePageViewsSize = imagePageViews.size();
				for(int i=0;i<imagePageViewsSize;i++){
					String url = ja.getJSONObject(i).getString("url");
					imagePageViews.set(i, slideLayout.getSlideImageLayout2(url));
					if(i==0) sharepicurl = url;
				}
				if(imagePageViewsSize<ja.length()){
					for(int i=imagePageViewsSize;i<ja.length();i++){
						String url = ja.getJSONObject(i).getString("url");
						imagePageViews.add(slideLayout.getSlideImageLayout2(url));
						imageCircleViews.add(slideLayout.getCircleImageLayout(i-1));
					}
				}
				
			}
			adapter = new SlideImageAdapter();
			viewPager.setAdapter(adapter);  
		    viewPager.setOnPageChangeListener(new ImagePageChangeListener());
			title = result.getString("title");
			description = result.getString("description");
			shareLink = result.getString("share_link");
			eventTitle.setText(title);
			String tb="",te="";
			if(!result.getString("time_begin").equals("null")){
				tb = result.getString("time_begin").substring(0,19).replace('T', ' ');
				eventBeginTime.setText(tb);
			}
			if(!result.getString("time_end").equals("null")){
				te = result.getString("time_end").substring(0,19).replace('T', ' ');
				eventEndTime.setText(te);
			}
			
			startcal = Tool.getCalendarFromISO(result.getString("time_begin"));
			endcal = Tool.getCalendarFromISO(result.getString("time_end"));
			eventTime.setText(Tool.getCalendarByInintData(tb, te));
			if(eventTime.getText().equals("正在进行!")){
	        	eventTime.setBackgroundColor(mResources.getColor(R.color.lightgreen));
	        }else if(eventTime.getText().equals("即将开始!")){
	        	eventTime.setBackgroundColor(mResources.getColor(R.color.darkred));
	        }else{
	        	eventTime.setBackgroundColor(mResources.getColor(R.color.graytrans));
	        }	
			eventPlace.setText(result.getString("addr").equals("null")?"":result.getString("addr"));
			double lat = result.getDouble("lat");
			double lng = result.getDouble("lng");
			LatLng marker1 = new LatLng(lat, lng);                
	        //设置中心点和缩放比例  
	        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(marker1,8,8,8)));  
	        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
	        organizer = result.getString("organizer_name");
			eventOrganizer.setText(organizer);
			int et = Integer.parseInt(result.getString("event_type").equals("")?"1":
				result.getString("event_type"));
			eventType.setText(eventTypeStr[et]);
			eventIspublic.setText(result.getInt("privacy")==0?"公开":"不公开");
			eventCost.setText(result.getInt("cost")!=0?String.valueOf(result.getInt("cost")):"免费");
			eventLink.setText(result.getString("url").equals("null")?"":result.getString("url"));
			if(!result.getString("description").equals("")){
				eventDetail.setText(result.getString("description"));
				findViewById(R.id.detailgone).setOnClickListener(detailClickListener);
			}else{
				findViewById(R.id.detailgone).setVisibility(View.GONE);
			}
			
			JSONArray jaChoices = result.getJSONArray("choices");
			map = new HashMap<Integer,ArrayList<String>>();
			applyList = new HashMap<Integer,JSONArray>();
			for(int i=0;i<7;i++){
				map.put(i, new ArrayList<String>());
				applyList.put(i, new JSONArray());
			}
			Log.i(TAG+"juhuo id", String.valueOf(JuhuoConfig.userId));
			for(int i=0;i<jaChoices.length();i++){
				int status = jaChoices.getJSONObject(i).getInt("status");
				int id = jaChoices.getJSONObject(i).getInt("id");
				if(id==JuhuoConfig.userId){
					organizer_status = status;
				}
				String url;
				if(jaChoices.getJSONObject(i).has("suc_photos")){
					url = jaChoices.getJSONObject(i).
							getJSONArray("suc_photos").getJSONObject(0).getString("url");
				}else{
					url="";
				}
				if(status==JuhuoConfig.INVI_ORGANIZER){
					applyList.get(status).put(jaChoices.getJSONObject(i));//for next page
					map.get(status).add(url);
				}
				
				if(status==JuhuoConfig.INVI_YES||status==JuhuoConfig.INVI_MAYBE||status==JuhuoConfig.INVI_ORGANIZER){
					applyList.get(JuhuoConfig.INVI_YES).put(jaChoices.getJSONObject(i));//for next page
					map.get(JuhuoConfig.INVI_YES).add(url);
				}else{
					map.get(status).add(url);
					applyList.get(status).put(jaChoices.getJSONObject(i));
				}
			}
			setChoicesTable(JuhuoConfig.INVI_ORGANIZER,map);
			setChoicesTable(JuhuoConfig.INVI_NULL,map);
			setChoicesTable(JuhuoConfig.INVI_NO,map);
			setChoicesTable(JuhuoConfig.INVI_APPLY,map);
			Log.i(TAG+"map", String.valueOf(organizer_status));
			
			if(organizer_status==3||organizer_status==1||organizer_status==2||organizer_status==0){
				applyLay.setVisibility(View.GONE);
				applyEventBtn.setVisibility(View.GONE);
				statusbarLay.setVisibility(View.VISIBLE);
				status1.setTextColor(mResources.getColor(R.color.mgray));
				status2.setTextColor(mResources.getColor(R.color.mgray));
				status3.setTextColor(mResources.getColor(R.color.mgray));
				if(organizer_status==1) status1.setTextColor(mResources.getColor(R.color.mgreen));
				if(organizer_status==2) status2.setTextColor(mResources.getColor(R.color.mgreen));
				if(organizer_status==3) status3.setTextColor(mResources.getColor(R.color.mgreen));
			}else if(organizer_status==5){
				applyLay.setVisibility(View.VISIBLE);
				applyEventBtn.setVisibility(View.GONE);
				statusbarLay.setVisibility(View.GONE);
			}else if(organizer_status==7){
				if(!type.equals("MYorganizer")&&!type.equals("MYrelated")
						&&!JuhuoConfig.token.equals(JuhuoConfig.PUBLIC_TOKEN)){
					applyLay.setVisibility(View.GONE);
					applyEventBtn.setVisibility(View.VISIBLE);
					statusbarLay.setVisibility(View.INVISIBLE);
				}
			}else if(organizer_status==4){
				applyLay.setVisibility(View.GONE);
				applyEventBtn.setVisibility(View.VISIBLE);
				applyEventBtn.setText(mResources.getString(R.string.apply_eventing));
				applyEventBtn.setClickable(false);
				statusbarLay.setVisibility(View.INVISIBLE);
			}
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
				findViewById(R.id.absentwid).setOnClickListener(InviListener);
				tx.setText(mResources.getString(R.string.absent)+" ("+map.get(type).size()+")");
			}
			
		}else if(type==JuhuoConfig.INVI_APPLY){
			tr = (TableRow)findViewById(R.id.applyrows);
			tx = (TextView)findViewById(R.id.apply);
			if(map.get(type).size()==0){
				findViewById(R.id.applywid).setVisibility(View.GONE);
			}else{
				findViewById(R.id.applywid).setOnClickListener(InviListener);
				tx.setText(mResources.getString(R.string.apply)+" ("+map.get(type).size()+")");
			}
			
		}
		tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        tr.removeAllViews();
        
        
		int length = map.get(type).size()>4?4:map.get(type).size();
        for (int j = 0; j < length; j++) {
        	ImageView view = Tool.getNewImage(this);
            imageLoader.displayImage(map.get(type).get(j), view,options,animateFirstListener);
            tr.addView(view);
        }
	}
	public void addToCalender(){
		ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startcal.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endcal.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);

        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

        // default calendar
        values.put(CalendarContract.Events.CALENDAR_ID, 1);

        values.put(CalendarContract.Events.HAS_ALARM, 1);

        // insert event to calendar
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
	}
	private void weiBoShare(){
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

		weiboMessage.mediaObject = getWebpageObj();
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        
        mWeiboShareAPI.sendRequest(request);
    
	}
	private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = "组织者:"+organizer;
        mediaObject.description = "活动邀请:"+title;
        
        // 设置 Bitmap 类型的图片到视频对象里
        mediaObject.setThumbImage(thumb);
        mediaObject.actionUrl = shareLink;
        mediaObject.defaultText = "Juhuo WebPage";
        return mediaObject;
	}
	
	private void wechatShare(int flag){
		
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = shareLink;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "组织者:"+organizer;
		msg.description = "活动邀请:"+title;
		//这里替换一张自己工程里的图片资源
		if(!sharepicurl.equals("")&&thumb!=null&&thumb.getByteCount()<30000){
			Log.i(TAG+"wechatshare", "set image");
			msg.setThumbImage(thumb);
		}
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
		Log.i(TAG, "share");    
	}
	public Bitmap getBitmapFromURL(String src) {
	    try {
	        InputStream sourceStream;
//	        OutputStream outputStream;
	        File cachedImage = ImageLoader.getInstance().getDiscCache().get(src);
	        if (cachedImage.exists()) { // if image was cached by UIL
	            sourceStream = new FileInputStream(cachedImage);
//	            outputStream = new FileOutputStream(cachedImage);
	        } else { // otherwise - download image
	        	URL url = new URL(src);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoInput(true);
		        connection.connect();
		        sourceStream = connection.getInputStream();
//		        outputStream = connection.getOutputStream();
	        }
	        
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = false;
	        options.inSampleSize = 70;
	        Bitmap compressedImage = BitmapFactory.decodeStream((InputStream) new URL(src).getContent(),new Rect(),options);

//	        options.inSampleSize = calculateInSampleSize(options, 35, 35);
	        
	        // Decode bitmap with inSampleSize set
//	        options.inJustDecodeBounds = false;
//	        if(cachedImage.exists()){
//	        	Log.i(TAG, cachedImage.getAbsolutePath());
//	        	Bitmap compressedImage = BitmapFactory.decodeStream(new FileInputStream(ImageLoader.getInstance().getDiscCache().get(src)),null,options);
//		        Log.i(TAG,String.valueOf(compressedImage==null));
//		        return compressedImage;
//	        }else{
//	        	URL url = new URL(src);
//		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//		        connection.setDoInput(true);
//		        connection.connect();
//		        Bitmap compressedImage = BitmapFactory.decodeStream(sourceStream,null,options);
		       
		        if(compressedImage!=null){
		        	 Log.i(TAG+"compressimage",String.valueOf(compressedImage.getByteCount()));
		        }
		        sourceStream.close();
		        return compressedImage;
//	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	/**
	 * Calcuate how much to compress the image
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {

	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1; // default to not zoom image

	    if (height > reqHeight || width > reqWidth) {
	             final int heightRatio = Math.round((float) height/ (float) reqHeight);
	             final int widthRatio = Math.round((float) width / (float) reqWidth);
	             inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    Log.i(TAG+"samplesize=", String.valueOf(inSampleSize));
	    return inSampleSize;
	}
    
	
	
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			mUiSettings = aMap.getUiSettings();
			mUiSettings.setZoomControlsEnabled(false);  
			mUiSettings.setZoomGesturesEnabled(false); 
			mUiSettings.setScrollGesturesEnabled(false);  
			mUiSettings.setAllGesturesEnabled(false);
			mUiSettings.setScaleControlsEnabled(false);
			
			
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
        	
            for (int i = 0; i < imageCircleViews.size(); i++) {  
            	imageCircleViews.get(index).setBackgroundResource(R.drawable.dot_selected1);
                
                if (index != i) {  
                	imageCircleViews.get(i).setBackgroundResource(R.drawable.dot_none1);  
                }  
            }
        }  
    }
    OnClickListener detailClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(EventDetailActivity.this,EventDetailOne.class);
			intent.putExtra("detail",description);
			startActivity(intent);
		}
    };
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
		if(type.equals("MYorganizer")){
			menu.clear();
			getMenuInflater().inflate(R.menu.most, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		menu.clear();
		if(organizer_status==5){
			inflater.inflate(R.menu.most, menu);
		}else{
			inflater.inflate(R.menu.main, menu);
		}
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_to_calendar:
			try {
				addToCalender();
				Tool.myToast(this, mResources.getString(R.string.add_to_calendar_success));
	        } catch (ActivityNotFoundException e) {
	            // TODO: handle exception
	            Log.e("ActivityNotFoundException", e.toString());
	        }
			break;
		case R.id.invite_friends:
			Intent intent = new Intent(EventDetailActivity.this,SelectContactActivity.class);
			intent.putExtra("event_id", event_id);
			startActivity(intent);
			break;
		case R.id.update_event:
			Intent intentup = new Intent(EventDetailActivity.this,CreateEvent.class);
			intentup.putExtra("event_id", event_id);
			intentup.putExtra("type", "update");
			startActivityForResult(intentup,UPDATE_EVENT);
			break;
		case R.id.share_event:
			shareLay.setVisibility(View.VISIBLE);
			transView.setVisibility(View.VISIBLE);
			break;
		case R.id.send_remind:
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("token", JuhuoConfig.token);
			map.put("id", event_id);
			SendRemind task = new SendRemind();
			mAsyncTask.add(task);
			task.execute(map);
		}
		return super.onOptionsItemSelected(item);
	}
	@Override  
    public void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
		if(data!=null){
			switch (requestCode)  
	        {  
		        case UPDATE_EVENT:  
//		        	Log.i(TAG, "should update");
		        	mRefreshableView.onRefreshing();
		    		LoadEventInfo loadEventInfo = new LoadEventInfo();
		    		mAsyncTask.add(loadEventInfo);
		    		loadEventInfo.execute(mapPara);		    		
		    		Bundle buddle = data.getExtras(); 
		    		int a = buddle.getInt("addedPicNumber");
		    		for(int i=0;i<a;i++){
			    			View dummyView = new View(this);
				    		dummyView = slideLayout.getSlideImageLayout2("");
				    		imagePageViews.add(dummyView);
				    		imageCircleViews.add(slideLayout.getCircleImageLayout(i));
			    		}
		            break;
	        }  
		}	
    }
	@Override
    protected void onStop()
    {
        for(int index = 0;index < mAsyncTask.size();index ++)
        {
            if(!(mAsyncTask.get(index).getStatus() == AsyncTask.Status.FINISHED) )
                mAsyncTask.get(index).setStop();
        }
        super.onStop();
    }
	private class SendRemind extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mPgDialog.setMessage(mResources.getString(R.string.reminding));
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... arg0) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = arg0[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_REMIND);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if (getStop()) {
                return;
            }
			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(EventDetailActivity.this, mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(EventDetailActivity.this);
			}else{
				Tool.myToast(EventDetailActivity.this,mResources.getString(R.string.send_remind_success));
				finish();
			}
			
		}
	}
	private class ConfirmEventClass extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
		public int status;
		public ConfirmEventClass(int st){
			this.status = st;
		}
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mPgDialog.setMessage(mResources.getString(R.string.changing));
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... arg0) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = arg0[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_CONFIRM);
		}
		@Override
		protected void onPostExecute(JSONObject result){
			if (getStop()) {
                return;
            }
			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(EventDetailActivity.this, mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(EventDetailActivity.this);
			}else{
				status1.setTextColor(mResources.getColor(R.color.mgray));
				status2.setTextColor(mResources.getColor(R.color.mgray));
				status3.setTextColor(mResources.getColor(R.color.mgray));
				if(status==1) status1.setTextColor(mResources.getColor(R.color.mgreen));
				if(status==2) status2.setTextColor(mResources.getColor(R.color.mgreen));
				if(status==3) status3.setTextColor(mResources.getColor(R.color.mgreen));
				Tool.myToast(EventDetailActivity.this, mResources.getString(R.string.change_status_success));
			}
		}
	}
	private class ApplyEventClass extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mPgDialog.setMessage(mResources.getString(R.string.apply_eventing));
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... arg0) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = arg0[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_APPLY);
		}
		@Override
		protected void onPostExecute(JSONObject result){
			if (getStop()) {
                return;
            }
			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(EventDetailActivity.this, mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(EventDetailActivity.this);
			}else{
				Tool.myToast(EventDetailActivity.this, mResources.getString(R.string.apply_event_success));
			}
		}
	}
	private class GetBitMapClass extends CheckStopAsyncTask<String,String,Bitmap>{

		@Override
		protected Bitmap doInBackground(String... map) {
			// TODO Auto-generated method stub
			return getBitmapFromURL(map[0]);
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			if (getStop()) {
                return;
            }
			thumb = result;
		}
		
	}
	private class LoadEventInfo extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... map) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_INFO);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if (getStop()) {
                return;
            }
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(EventDetailActivity.this, mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(EventDetailActivity.this);
			}else{
				Tool.writeJsonToFile(result, EventDetailActivity.this, JuhuoConfig.EVENTINFO+event_id);
				setViewsContent(result);
			}
			mRefreshableView.finishRefresh("最近更新:" + new Date().toLocaleString()); 
			GetBitMapClass task = new GetBitMapClass();
			if(!sharepicurl.equals("")) {
				mAsyncTask.add(task);
				task.execute(sharepicurl);
			}
		}
		
	}
}
