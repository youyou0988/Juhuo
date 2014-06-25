package com.juhuo.welcome;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.amap.api.location.LocationManagerProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.juhuo.tool.Tool;



public class SelectLocation extends Activity implements 
	OnMarkerClickListener,OnInfoWindowClickListener, OnMarkerDragListener, OnMapLoadedListener,
	OnClickListener, InfoWindowAdapter,OnGeocodeSearchListener{
	private MapView mapView;
	private GeocodeSearch geocoderSearch;
	private AMap aMap;
	private ImageView back,check,search;
	private LocationManagerProxy mAMapLocationManager;
	private MarkerOptions markerOption;
	private Marker marker;// 有跳动效果的marker对象
	private LatLng currentLoc;
	private String description;
	private Resources mResources;
	private LatLonPoint latLonPoint;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.set_location);
		mResources = getResources();
		back = (ImageView)findViewById(R.id.back);
		check = (ImageView)findViewById(R.id.check);
		search = (ImageView)findViewById(R.id.search);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 必须要写
		currentLoc = (LatLng)getIntent().getExtras().get("currentLoc");
		description = getIntent().getExtras().getString("description");
		init();
		back.setOnClickListener(imgClick);
		search.setOnClickListener(imgClick);
		check.setOnClickListener(imgClick);
	}
	OnClickListener imgClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ImageView v = (ImageView)arg0;
			switch(v.getId()){
			case R.id.back:
				finish();
				break;
			case R.id.search:
				break;
			case R.id.check:
				Intent intent = new Intent(SelectLocation.this,CreateEvent.class);
				intent.putExtra("newLocation", currentLoc);  
				intent.putExtra("description", description);
	            setResult(RESULT_OK, intent);
				finish();
				break;
			}
		}
	};
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentLoc,8,8,8)));
	        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
	        geocoderSearch = new GeocodeSearch(this);
			geocoderSearch.setOnGeocodeSearchListener(this);
			aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng location) {
					// TODO Auto-generated method stub
					//设置中心点和缩放比例  
			        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(location,8,8,8)));
			        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
			        latLonPoint = new LatLonPoint(location.latitude, location.longitude);
			        getAddress(latLonPoint);
			        aMap.clear();
					
				}
			});
			aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
			aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
			addMarkersToMap(currentLoc,description);
		}
	}
	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}
	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				String addressName = result.getRegeocodeAddress().getFormatAddress()
						+ "附近";
				Log.i("search", addressName);
				description = addressName;
				currentLoc = new LatLng(latLonPoint.getLatitude(),latLonPoint.getLongitude());
				addMarkersToMap(currentLoc,addressName);
			} else {
				Tool.myToast(SelectLocation.this,mResources.getString(R.string.no_result));
			}
		} else if (rCode == 27) {
			Tool.myToast(SelectLocation.this,mResources.getString(R.string.error_network));
		} else if (rCode == 32) {
			Tool.myToast(SelectLocation.this,mResources.getString(R.string.error_key));
		} else {
			Tool.myToast(SelectLocation.this,mResources.getString(R.string.error_other));
		}
	}
	
	/**
	 * 在地图上添加marker
	 */
	private void addMarkersToMap(LatLng current,String description) {

		markerOption = new MarkerOptions();
		markerOption.position(current);
		markerOption.title(description);
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		marker = aMap.addMarker(markerOption);
		marker.showInfoWindow();// 设置默认显示一个info window
		
	}

	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(final Marker marker) {
		jumpPoint(marker);
		return false;
	}

	/**
	 * marker点击时跳动一下
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
//				aMap.invalidate();// 刷新地图
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});

	}
	

	/**
	 * 监听自定义infowindow窗口的infocontents事件回调
	 */
	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	/**
	 * 监听自定义infowindow窗口的infowindow事件回调
	 */
	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {
		String title = marker.getTitle();
//		TextView titleUi = ((TextView) view.findViewById(R.id.title));
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
					titleText.length(), 0);
//			titleUi.setTextSize(15);
//			titleUi.setText(titleText);

		} else {
//			titleUi.setText("");
		}
		String snippet = marker.getSnippet();
//		TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
		if (snippet != null) {
			SpannableString snippetText = new SpannableString(snippet);
			snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
					snippetText.length(), 0);
//			snippetUi.setTextSize(20);
//			snippetUi.setText(snippetText);
		} else {
//			snippetUi.setText("");
		}
	}

	

	/**
	 * 监听拖动marker时事件回调
	 */
	@Override
	public void onMarkerDrag(Marker marker) {
		String curDes = marker.getTitle() + "拖动时当前位置:(lat,lng)\n("
				+ marker.getPosition().latitude + ","
				+ marker.getPosition().longitude + ")";
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
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


}
