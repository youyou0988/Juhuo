package com.juhuo.welcome;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ScrollView;
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
import com.juhuo.tool.Tool;

public class CreateEvent extends Activity implements LocationSource,AMapLocationListener{
	private TextView actionTitleText,actionTitleText2,eventBeginTime,eventEndTime,eventType
		,eventDetail;
	private EditText eventPlace;
	private Resources mResources;
	private MapView mapView;
	private AMap aMap;
	private UiSettings mUiSettings;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private AMapLocation currentLoc;
	private String dest;
	private static final int SelectLocation = 0;
	private static final int EditDetailEvent = 1;
	final String[] items = {"���Ѿۻ�", "���鿴��", "���ֵ�Ӱ","��������","����"};
	
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
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// ����Ҫд
		init();
		
	}
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
				Log.i("click", "txt");
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
				}else{
					eventEndTime.setText(Tool.getStringDate(date));
				}
			}
		});
		dialog.show();
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
					intent.putExtra("description", dest);
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
		        case 0:  
		            Bundle NoticeBuddle = data.getExtras();  
		            String NoticeMessage = NoticeBuddle.getString("description");  
		            LatLng NoticeLoc = (LatLng)NoticeBuddle.get("newLocation");
		            eventPlace.setText(NoticeMessage);
		            //�������ĵ�����ű���  
			        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(NoticeLoc,8,8,8)));  
			        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
		            break;  
		        case 1:
		        	Bundle buddle = data.getExtras();  
		            String message = buddle.getString("detail");
		            eventDetail.setText(message);
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
			Double geoLat = aLocation.getLatitude();
			Double geoLng = aLocation.getLongitude();
			String cityCode = "";
			String desc = "";
			Bundle locBundle = aLocation.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			eventPlace.setText(desc);
			LatLng marker1 = new LatLng(geoLat, geoLng);                
	        //�������ĵ�����ű���  
	        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(marker1,8,8,8)));  
	        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
	        currentLoc = aLocation;
	        dest = desc;
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
