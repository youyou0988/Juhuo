package com.juhuo.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.adapter.LeftMenuAdapter;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class LeftMenuFragment extends Fragment{
	private String TAG="LeftMenuFragment";
	private Resources mResources;
	private ListView naviList;
	private LeftMenuAdapter leftMenuAdapter;
	private ImageView userImage;
	private TextView userText,userName;
	private List<String> mData;
	private List<Integer> focus;
	private HashMap<String,Object> mapParams = new HashMap<String,Object>();
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
	.showImageOnLoading(R.drawable.bg_ju_icon)
	.showImageForEmptyUri(R.drawable.bg_ju_icon)
	.showImageOnFail(R.drawable.bg_ju_icon)
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.considerExifParams(true)
	.displayer(new SimpleBitmapDisplayer())
	.build();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		setData();
	};
	public void setData(){
		mData=new ArrayList<String>();
		focus = new ArrayList<Integer>();
		mData.add("热门活动");focus.add(1);
		mData.add("我的活动");focus.add(0);
		mData.add("帐号设置");focus.add(0);
		mData.add("系统设置");focus.add(0);
		mData.add("");focus.add(0);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout parent = (RelativeLayout) inflater.inflate(
				R.layout.left_menu, null);
		naviList = (ListView)parent.findViewById(R.id.navi_list);
		leftMenuAdapter = new LeftMenuAdapter(getActivity(),mData,focus);
		leftMenuAdapter.setInflater(
				(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
				getActivity());
		naviList.setAdapter(leftMenuAdapter);
		
		userName = (TextView)parent.findViewById(R.id.user_name);
		userText = (TextView)parent.findViewById(R.id.text);
		userImage = (ImageView)parent.findViewById(R.id.image);
		mapParams.put("token", JuhuoConfig.token);
		getNetData(mapParams);
		initListener();
		return parent;
	}
	public void getNetData(HashMap<String,Object> map){
		LoadUserInfo loadUserInfo = new LoadUserInfo();
		loadUserInfo.execute(map);
	}
	private class LoadUserInfo extends AsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.USER_INFO);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
			}else if(result.has("wrong_data")){
				//sth is wrong invalid token
				Tool.dialog(getActivity());
			}else{
				try {
					String name = result.getString("name");
					userName.setText(name);
					if(result.has("suc_photos")){
						userText.setText("");
						String url = result.getJSONArray("suc_photos").getJSONObject(0).getString("url");
						imageLoader.displayImage(url, userImage, options, animateFirstListener);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
	}
	public void initListener(){
		naviList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i("clickpos", String.valueOf(position));
				for(int i=0;i<focus.size();i++){
					focus.set(i, 0);
				}
				focus.set(position, 1);
				leftMenuAdapter.notifyDataSetChanged();
			};
		});
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

}
