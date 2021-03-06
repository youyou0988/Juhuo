package com.juhuo.welcome;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.juhuo.control.DateTimePickerDialog;
import com.juhuo.control.DateTimePickerDialog.OnDateTimeSetListener;
import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class CreateEvent extends Activity implements LocationSource,AMapLocationListener{
	private final String TAG="CreateEvent";
	private TextView actionTitleText,actionTitleText2,eventBeginTime,eventEndTime,eventType
		,picNumber,actionTitle,eventDetailText;
	private ImageView image;
	private ProgressDialog mPgDialog;
	private EditText eventPlace,eventTitle,eventCost,eventLink;
	private Button createBtn;
	private Resources mResources;
	private MapView mapView;
	private AMap aMap;
	private UiSettings mUiSettings;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private AMapLocation currentLoc;
	private String photo_ids="",time_begin,time_end,event_type="0",description,addr,title,event_id;
	private double lat,lng;
	private int privacy=0,need_approve_apply=0,allow_apns=0,picNumberInt=0;
	private Switch privacyche,need_approve_che,allow_apns_che;
	private SimpleDateFormat df = new SimpleDateFormat(Tool.ISO8601DATEFORMAT, Locale.getDefault());
	private Calendar time_begin_cal,time_end_cal;
	private static final int SelectLocation = 0;
	private static final int EditDetailEvent = 1;
	private static final int EditImage = 2;
	final String[] items = {"所有活动","交友聚会", "读书看报", "音乐电影","体育锻炼","其他"};
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
	private ArrayList<String> arrPath = new ArrayList<String>();
	private ArrayList<Integer> arrPathid = new ArrayList<Integer>();
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.create_event);
		mResources = getResources();
		mPgDialog = new ProgressDialog(this);
		actionTitleText = (TextView)findViewById(R.id.action_title_text);
		actionTitleText2 = (TextView)findViewById(R.id.action_title_text2);
		actionTitle = (TextView)findViewById(R.id.action_title);
		eventBeginTime = (TextView)findViewById(R.id.event_begin_time);
		eventEndTime = (TextView)findViewById(R.id.event_end_time);
		eventPlace = (EditText)findViewById(R.id.event_place);
		eventType = (TextView)findViewById(R.id.event_type);
		eventDetailText = (TextView)findViewById(R.id.event_detail_txt);
		eventLink = (EditText)findViewById(R.id.event_link);
		picNumber = (TextView)findViewById(R.id.t2);
		eventTitle = (EditText)findViewById(R.id.event_title);
		eventCost = (EditText)findViewById(R.id.event_cost);
		image = (ImageView)findViewById(R.id.image);
		createBtn = (Button)findViewById(R.id.create_btn);
		privacyche = (Switch)findViewById(R.id.ispublicche);
		need_approve_che = (Switch)findViewById(R.id.isapproveche);
		allow_apns_che = (Switch)findViewById(R.id.newmessageche);
		actionTitleText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		eventBeginTime.setText(Tool.getCurrentDateStr());
		eventEndTime.setText(Tool.getNextDateStr());
		eventBeginTime.setOnClickListener(timeClick);
		eventEndTime.setOnClickListener(timeClick);
		eventType.setOnClickListener(typeClick);
		eventDetailText.setOnClickListener(typeClick);
		image.setOnClickListener(imageClick);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 必须要写
		init();
		if(getIntent().getExtras().getString("type").equals("update")){
			createBtn.setText(mResources.getString(R.string.update_event));
			actionTitle.setText(mResources.getString(R.string.update_event));
			actionTitleText2.setText("确定");
			JSONObject jo = new JSONObject();
			jo = Tool.loadJsonFromFile(JuhuoConfig.EVENTINFO+getIntent().getExtras().getString("event_id"), this);
			if(jo!=null){
				//get network data
				setViewsContent(jo);
			}
		}else{
			
			time_begin = df.format(new Date());
			time_begin_cal = Calendar.getInstance();
			Calendar calendar = Calendar.getInstance();
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			calendar.set(Calendar.DAY_OF_MONTH, day+1);
			time_end_cal = calendar;
			Date tasktime=calendar.getTime();
			time_end = df.format(tasktime.getTime());
			
		}
		
		createBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveEvent();
			}
		});
		actionTitleText2.setOnClickListener(typeClick);
	}
	private void saveEvent(){
		privacy = privacyche.isChecked()?0:1;
		need_approve_apply = need_approve_che.isChecked()?1:0;
		allow_apns = allow_apns_che.isChecked()?1:0;
		title = eventTitle.getText().toString();
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("token", JuhuoConfig.token);
		map.put("title", title);
		map.put("time_begin", time_begin);
		map.put("time_end", time_end);
		map.put("description", description);
		map.put("cost", (eventCost.getText().toString().equals("")
				||eventCost.getText().toString().equals("免费"))?"0":
				eventCost.getText().toString());
		map.put("url", eventLink.getText().toString());
		map.put("event_type", event_type);
		map.put("need_approve_apply", String.valueOf(need_approve_apply));
		if(!photo_ids.equals("")){
			map.put("photo_ids", photo_ids);
		}
		map.put("privacy", String.valueOf(privacy));
		map.put("allow_apns", String.valueOf(allow_apns));
		map.put("lat", String.valueOf(lat));
		map.put("lng", String.valueOf(lng));
		map.put("addr",eventPlace.getText().toString());
		CreateEventWord task = new CreateEventWord(getIntent().getExtras().getString("type"));
		Log.i(TAG, map.toString());
		mAsyncTask.add(task);
		task.execute(map);
	}
	private void setViewsContent(JSONObject result){
		try {
			if(result.has("suc_photos")){
				JSONArray ja = result.getJSONArray("suc_photos");
				picNumber.setText(ja.length()+"张");
	            imageLoader.displayImage( ja.getJSONObject(0).getString("url"), image,options);
	            for(int i=0;i<ja.length();i++){
	            	arrPath.add(ja.getJSONObject(i).getString("url"));
	            	arrPathid.add(ja.getJSONObject(i).getInt("id"));
	            }
			}
			event_id = result.getString("id");
			eventTitle.setText(result.getString("title"));
			time_begin = result.getString("time_begin");
			time_end = result.getString("time_end");
			time_begin_cal = Tool.getCalendarFromISO(time_begin);
			time_end_cal = Tool.getCalendarFromISO(time_end);
			if(!time_begin.equals("null")){
				String tb = time_begin.substring(0,19).replace('T', ' ');
				eventBeginTime.setText(tb);
			}
			if(!time_end.equals("null")){
				String te = time_end.substring(0,19).replace('T', ' ');
				eventEndTime.setText(te);
			}
			addr = result.getString("addr");
			eventPlace.setText(addr.equals("null")?"":result.getString("addr"));
			eventLink.setText(result.getString("url").equals("null")?"":result.getString("url"));
			lat = result.getDouble("lat");
			lng = result.getDouble("lng");
			LatLng marker1 = new LatLng(lat, lng);                
	        //设置中心点和缩放比例  
	        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(marker1,8,8,8)));  
	        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
			int et = Integer.parseInt(result.getString("event_type").equals("")?"1":
				result.getString("event_type"));
			Log.i(TAG+"eventtype", String.valueOf(et));
			eventType.setText(items[et]);
			privacyche.setChecked(result.getInt("privacy")==0?true:false);
			eventCost.setText(result.getInt("cost")!=0?String.valueOf(result.getInt("cost")):"免费");
			eventDetailText.setText(result.getString("description"));
			photo_ids = result.getString("photo_ids");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	OnClickListener imageClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(CreateEvent.this,EditImages.class);
			intent.putExtra("createOrUpdate", getIntent().getExtras().getString("type"));
			intent.putExtra("arrPathid", arrPathid);
			intent.putExtra("arrPath", arrPath);
			startActivityForResult(intent,EditImage);
		}
	};
	OnClickListener typeClick = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			TextView vt = (TextView)v;
			switch(vt.getId()){
			case R.id.event_type:
				showDialog2();
				break;
			case R.id.event_detail_txt:
				Log.i(TAG, "clicked");
				Intent intent = new Intent(CreateEvent.this,EditEventDetail.class);
				intent.putExtra("detail", vt.getText());
				startActivityForResult(intent,EditDetailEvent);
				break;
			case R.id.action_title_text2:
				saveEvent();
				break;
			}
			
		}
		
	};

	OnClickListener timeClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			TextView tv = (TextView)v;
			switch(tv.getId()){
				case R.id.event_begin_time:
					showDialog("begin",time_begin_cal);
					break;
				case R.id.event_end_time:
					showDialog("end",time_end_cal);
					break;
			}
		}
	};
	public void showDialog2(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setTitle("活动类型")
        .setNegativeButton(mResources.getString(R.string.cancel), (DialogInterface.OnClickListener)null)
        .setPositiveButton(mResources.getString(R.string.setting), (DialogInterface.OnClickListener)null)
		.setItems(items, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	            eventType.setText(items[item]);
	            event_type = String.valueOf(item);
	        }
	    });
		AlertDialog alert = builder.create(); // create one
		
		alert.show(); //display it
	}
	public void showDialog(final String type,Calendar cal)
	{
		Log.i(TAG, cal.toString());
		DateTimePickerDialog dialog  = new DateTimePickerDialog(this, cal.getTimeInMillis());
		dialog.setOnDateTimeSetListener(new OnDateTimeSetListener()
	      {
			public void OnDateTimeSet(AlertDialog dialog, long date)
			{
				if(type.equals("begin")){
					eventBeginTime.setText(Tool.getStringDate(date));
					time_begin = df.format(date);
				}else{
					eventEndTime.setText(Tool.getStringDate(date));
					time_end = df.format(date);
				}
			}
		});
		dialog.show();
	}
	private class CreateEventWord extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
		private String createOrUpdate;
		public CreateEventWord(String ty){
			createOrUpdate = ty;
		}
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if(createOrUpdate.equals("update")){
	        	mPgDialog.setMessage(mResources.getString(R.string.updating));
	        }else{
	        	mPgDialog.setMessage(mResources.getString(R.string.creating));
	        }
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... map) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = map[0];
			if(createOrUpdate.equals("update")){
				mapped.put("id", event_id);
				return new JuhuoInfo().callPostPlain(mapped,JuhuoConfig.EVENT_UPDATE);
			}else{
				return new JuhuoInfo().callPostPlain(mapped,JuhuoConfig.EVENT_CREATE);
			}
			
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return;
			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(CreateEvent.this, mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(CreateEvent.this);
			}else{
				if(createOrUpdate.equals("update")){
					Tool.myToast(CreateEvent.this, mResources.getString(R.string.update_event_success));
					Intent intent = new Intent(CreateEvent.this,EventDetailActivity.class);
					intent.putExtra("update_event", "success");
					intent.putExtra("addedPicNumber", picNumberInt);
					setResult(RESULT_OK, intent);
					
				}else{
					Tool.myToast(CreateEvent.this, mResources.getString(R.string.create_event_success));
					Intent intent = new Intent(CreateEvent.this,HomeActivity.class);
					intent.putExtra("create_event", "success");
					setResult(RESULT_OK, intent);
				}
				finish();
			}	
		}
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
			
			if(!getIntent().getExtras().getString("type").equals("update")){
				setUpMap();
			}
			
			aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng location) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(CreateEvent.this,SelectLocation.class);
					intent.putExtra("currentLoc", location);
					intent.putExtra("description", addr);
					startActivityForResult(intent,SelectLocation);
				}
			});
		}
	}
	@Override  
    public void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
		if(data!=null){
			switch (requestCode)  
	        {  
		        case SelectLocation:  
		            Bundle NoticeBuddle = data.getExtras();  
		            addr = NoticeBuddle.getString("description");  
		            LatLng NoticeLoc = (LatLng)NoticeBuddle.get("newLocation");
		            eventPlace.setText(addr);
		            lat = NoticeLoc.latitude;lng = NoticeLoc.longitude;
		            //设置中心点和缩放比例  
			        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(NoticeLoc,8,8,8)));  
			        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
		            break;  
		        case EditDetailEvent:
		        	Bundle buddle = data.getExtras();  
		            String message = buddle.getString("detail");
		            eventDetailText.setText(message);
		            description = message;
		            break;
		        case EditImage:
		        	Bundle photobuddle = data.getExtras();  
		            photo_ids = photobuddle.getString("photo_ids");
		            picNumber.setText(photobuddle.getInt("photo_num")+"张");
		            picNumberInt = photobuddle.getInt("add_photo_number");
		            imageLoader.displayImage(photobuddle.getString("imageurl"), image,options);
		        	break;
	        }  
		}	
    }
	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		
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
		deactivate();
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

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			lat = aLocation.getLatitude();
			lng = aLocation.getLongitude();
			String cityCode = "";
			Bundle locBundle = aLocation.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				addr = locBundle.getString("desc");
			}
			eventPlace.setText(addr);
			LatLng marker1 = new LatLng(lat, lng);                
	        //设置中心点和缩放比例  
	        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(marker1,8,8,8)));  
	        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
	        currentLoc = aLocation;
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.setGpsEnable(false);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
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

}
