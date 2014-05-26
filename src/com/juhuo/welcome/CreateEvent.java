package com.juhuo.welcome;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.juhuo.control.DateTimePickerDialog;
import com.juhuo.control.DateTimePickerDialog.OnDateTimeSetListener;
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
		,eventDetail;
	private ImageView image;
	private EditText eventPlace,eventTitle,eventCost;
	private Button createBtn;
	private Resources mResources;
	private MapView mapView;
	private AMap aMap;
	private UiSettings mUiSettings;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private AMapLocation currentLoc;
	private String photo_ids,time_begin,time_end,event_type="0",description,addr,title;
	private double lat,lng;
	private int privacy=0,need_approve_apply=0,allow_apns=0;
	private CheckBox privacyche,need_approve_che,allow_apns_che;
	private SimpleDateFormat df = new SimpleDateFormat(Tool.ISO8601DATEFORMAT, Locale.getDefault());
	private static final int SelectLocation = 0;
	private static final int EditDetailEvent = 1;
	private static final int EditImage = 2;
	final String[] items = {"���Ѿۻ�", "���鿴��", "���ֵ�Ӱ","��������","����"};
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
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��������
		setContentView(R.layout.create_event);
		mResources = getResources();
		actionTitleText = (TextView)findViewById(R.id.action_title_text);
		actionTitleText2 = (TextView)findViewById(R.id.action_title_text2);
		eventBeginTime = (TextView)findViewById(R.id.event_begin_time);
		eventEndTime = (TextView)findViewById(R.id.event_end_time);
		eventPlace = (EditText)findViewById(R.id.event_place);
		eventType = (TextView)findViewById(R.id.event_type);
		eventDetail = (TextView)findViewById(R.id.event_detail_txt);
		eventTitle = (EditText)findViewById(R.id.event_title);
		eventCost = (EditText)findViewById(R.id.event_cost);
		image = (ImageView)findViewById(R.id.image);
		createBtn = (Button)findViewById(R.id.create_btn);
		privacyche = (CheckBox)findViewById(R.id.ispublicche);
		need_approve_che = (CheckBox)findViewById(R.id.isapproveche);
		allow_apns_che = (CheckBox)findViewById(R.id.newmessageche);
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
		eventDetail.setOnClickListener(typeClick);
		image.setOnClickListener(imageClick);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// ����Ҫд
		init();
		time_begin = df.format(new Date());
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, day+1);
		Date tasktime=calendar.getTime();
		time_end = df.format(tasktime.getTime());
		createBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				privacy = privacyche.isChecked()?1:0;
				need_approve_apply = need_approve_che.isChecked()?1:0;
				allow_apns = allow_apns_che.isChecked()?1:0;
				title = eventTitle.getText().toString();
				HashMap<String,Object> map = new HashMap<String,Object>();
				map.put("token", JuhuoConfig.token);
				map.put("title", title);
				map.put("time_begin", time_begin);
				map.put("time_end", time_end);
				map.put("description", description);
				map.put("cost", eventCost.getText().toString().equals("")?"0":
						eventCost.getText().toString());
				map.put("event_type", event_type);
				map.put("need_approve_apply", String.valueOf(need_approve_apply));
				map.put("photo_ids", photo_ids);
				map.put("privacy", String.valueOf(privacy));
				map.put("allow_apns", String.valueOf(allow_apns));
				map.put("lat", String.valueOf(lat));
				map.put("lng", String.valueOf(lng));
				map.put("addr", String.valueOf(addr));
				CreateEventWord task = new CreateEventWord();
				Log.i(TAG, map.toString());
				task.execute(map);
			}
		});
	}
	OnClickListener imageClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(CreateEvent.this,EditImages.class);
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
				Intent intent = new Intent(CreateEvent.this,EditEventDetail.class);
				intent.putExtra("detail", vt.getText());
				startActivityForResult(intent,EditDetailEvent);
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
					showDialog("begin");
					break;
				case R.id.event_end_time:
					showDialog("end");
					break;
			}
		}
	};
	public void showDialog2(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setTitle("�����")
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
	public void showDialog(final String type)
	{
		DateTimePickerDialog dialog  = new DateTimePickerDialog(this, System.currentTimeMillis());
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
	private class CreateEventWord extends AsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... map) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().callPostPlain(mapped,JuhuoConfig.EVENT_CREATE);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(CreateEvent.this);
			}else{
				Tool.myToast(CreateEvent.this, mResources.getString(R.string.create_event_success));
				finish();
			}	
		}
	}
	
	/**
	 * ��ʼ��AMap����
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			mUiSettings = aMap.getUiSettings();
			mUiSettings.setZoomControlsEnabled(false);  
			mUiSettings.setZoomGesturesEnabled(false); 
			mUiSettings.setScrollGesturesEnabled(false);  
			setUpMap();
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
		            //�������ĵ�����ű���  
			        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(NoticeLoc,8,8,8)));  
			        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
		            break;  
		        case EditDetailEvent:
		        	Bundle buddle = data.getExtras();  
		            String message = buddle.getString("detail");
		            eventDetail.setText(message);
		            description = message;
		            break;
		        case EditImage:
		        	Bundle photobuddle = data.getExtras();  
		            photo_ids = "["+photobuddle.getString("photo_ids")+"]";
		            imageLoader.displayImage(photobuddle.getString("imageurl"), image,options);
		        	break;
	        }  
		}	
    }
	/**
	 * ����һЩamap������
	 */
	private void setUpMap() {
		// �Զ���ϵͳ��λС����
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// ����С�����ͼ��
		myLocationStyle.strokeColor(Color.BLACK);// ����Բ�εı߿���ɫ
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// ����Բ�ε������ɫ
		// myLocationStyle.anchor(int,int)//����С�����ê��
		myLocationStyle.strokeWidth(1.0f);// ����Բ�εı߿��ϸ
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// ���ö�λ����
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// ����Ĭ�϶�λ��ť�Ƿ���ʾ
		aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false
		
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * �˷����Ѿ�����
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
	 * ��λ�ɹ���ص�����
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// ��ʾϵͳС����
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
	        //�������ĵ�����ű���  
	        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(marker1,8,8,8)));  
	        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
	        currentLoc = aLocation;
		}
	}

	/**
	 * ���λ
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.setGpsEnable(false);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2�汾��������������true��ʾ��϶�λ�а���gps��λ��false��ʾ�����綨λ��Ĭ����true Location
			 * API��λ����GPS�������϶�λ��ʽ
			 * ����һ�������Ƕ�λprovider���ڶ�������ʱ�������2000���룬������������������λ���ף����ĸ������Ƕ�λ������
			 */
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	/**
	 * ֹͣ��λ
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

}
