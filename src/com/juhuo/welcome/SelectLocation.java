package com.juhuo.welcome;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.location.core.GeoPoint;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMap.OnMarkerDragListener;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;



public class SelectLocation extends Activity implements LocationSource,AMapLocationListener,
	OnMarkerClickListener,OnInfoWindowClickListener, OnMarkerDragListener, OnMapLoadedListener,
	OnClickListener, InfoWindowAdapter{
	private MapView mapView;
	private AMap aMap;
	private ImageView back,check,search;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private MarkerOptions markerOption;
	private Marker marker2;// ������Ч����marker����
	private RadioGroup radioOption;
	private LatLng currentLoc;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��������
		setContentView(R.layout.set_location);
		back = (ImageView)findViewById(R.id.back);
		check = (ImageView)findViewById(R.id.check);
		search = (ImageView)findViewById(R.id.search);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// ����Ҫд
		currentLoc = (LatLng)getIntent().getExtras().get("currentLoc");
		init();
	}
	/**
	 * ��ʼ��AMap����
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentLoc,8,8,8)));
	        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
			aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng location) {
					// TODO Auto-generated method stub
					//�������ĵ�����ű���  
			        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(location,8,8,8)));
			        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
			        
				}
			});
			aMap.setOnMarkerClickListener(this);// ���õ��marker�¼�������
			addMarkersToMap();
		}
	}
	
	/**
	 * �ڵ�ͼ�����marker
	 */
	private void addMarkersToMap() {
		
		aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(currentLoc).title("�ɶ���")
				.snippet("�ɶ���:30.679879, 104.064855").draggable(true));

		markerOption = new MarkerOptions();
		markerOption.position(currentLoc);
		markerOption.title("������").snippet("�����У�34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.arrow));
		marker2 = aMap.addMarker(markerOption);
		drawMarkers();// ���10������ϵͳĬ��icon��marker
	}

	/**
	 * ����ϵͳĬ�ϵ�1��marker����ͼƬ
	 */
	public void drawMarkers() {
		Marker marker = aMap.addMarker(new MarkerOptions()
				.position(currentLoc)
				.title("�ú�ѧϰ")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.draggable(true));
		marker.showInfoWindow();// ����Ĭ����ʾһ��info window
		Log.i("draw markers", "draw");
	}

	/**
	 * ��marker��ע������Ӧ�¼�
	 */
	@Override
	public boolean onMarkerClick(final Marker marker) {
		jumpPoint(marker);
//		if (marker.equals(marker2)) {
//			if (aMap != null) {
//				jumpPoint(marker);
//			}
//		}
		return false;
	}

	/**
	 * marker���ʱ����һ��
	 */
	public void jumpPoint(final Marker marker) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = aMap.getProjection();
		Point startPoint = proj.toScreenLocation(currentLoc);
		startPoint.offset(0, -100);
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 1500;

		final Interpolator interpolator = new BounceInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * currentLoc.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * currentLoc.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				aMap.invalidate();// ˢ�µ�ͼ
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});

	}
	

	/**
	 * �Զ���infowinfow����
	 */
	public void render(Marker marker, View view) {
		Log.i("render", "render");
		String title = marker.getTitle();
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
					titleText.length(), 0);
			
		} 
		String snippet = marker.getSnippet();
		if (snippet != null) {
			SpannableString snippetText = new SpannableString(snippet);
			snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
					snippetText.length(), 0);
		}
	}

	

	/**
	 * �����϶�markerʱ�¼��ص�
	 */
	@Override
	public void onMarkerDrag(Marker marker) {
		String curDes = marker.getTitle() + "�϶�ʱ��ǰλ��:(lat,lng)\n("
				+ marker.getPosition().latitude + ","
				+ marker.getPosition().longitude + ")";
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
			LatLng marker1 = new LatLng(geoLat, geoLng);                
	        //�������ĵ�����ű���  
	        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(marker1,8,8,8)));  
	        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
