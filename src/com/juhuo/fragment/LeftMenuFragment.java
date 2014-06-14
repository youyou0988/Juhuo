package com.juhuo.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
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
import com.juhuo.welcome.HomeActivity;
import com.juhuo.welcome.LoginActivity;
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
	private RelativeLayout naviTitle;
	private LeftMenuAdapter leftMenuAdapter;
	public static ImageView userImage;
	private TextView userText,userName;
	private ArrayList<HashMap<String,Object>> mData;
	private HashMap<String,Object> tmpmap;
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
		if(JuhuoConfig.token==JuhuoConfig.PUBLIC_TOKEN){
			setGuestData();
		}else{
			setData();
		}
		
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout parent = (RelativeLayout) inflater.inflate(
				R.layout.left_menu, null);
		naviList = (ListView)parent.findViewById(R.id.navi_list);
		naviTitle = (RelativeLayout)parent.findViewById(R.id.navi_title);
		leftMenuAdapter = new LeftMenuAdapter(getActivity(),mData);
		leftMenuAdapter.setInflater(
				(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
				getActivity());
		naviList.setAdapter(leftMenuAdapter);
		
		userName = (TextView)parent.findViewById(R.id.user_name);
		userText = (TextView)parent.findViewById(R.id.text);
		userImage = (ImageView)parent.findViewById(R.id.image);
		if(!JuhuoConfig.token.equals(JuhuoConfig.PUBLIC_TOKEN)){
			JSONObject jsonCache = new JSONObject();
			jsonCache = Tool.loadJsonFromFile(JuhuoConfig.USERINFO+JuhuoConfig.userId,getActivity());
			if(jsonCache==null){
				mapParams.put("token", JuhuoConfig.token);
				LoadUserInfo loadUserInfo = new LoadUserInfo();
				loadUserInfo.execute(mapParams);
			}else{
				//initial components contents
				try {
					String name = jsonCache.getString("name");
					userName.setText(name);
					JuhuoConfig.userId = jsonCache.getInt("id");
					if(jsonCache.has("suc_photos")){
						userText.setText("");
						String url = jsonCache.getJSONArray("suc_photos").getJSONObject(0).getString("url");
						imageLoader.displayImage(url, userImage, options, animateFirstListener);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			naviTitle.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),LoginActivity.class);
					startActivity(intent);
					getActivity().finish();
				}
			});
		}
		
		initListener();
		return parent;
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
			}else if(result.has("no_data")){
				Tool.myToast(getActivity(),mResources.getString(R.string.current_net_invalide));
			}else{
				try {
					String name = result.getString("name");
					userName.setText(name);
					JuhuoConfig.userId = result.getInt("id");
					if(result.has("suc_photos")){
						userText.setText("");
						String url = result.getJSONArray("suc_photos").getJSONObject(0).getString("url");
						imageLoader.displayImage(url, userImage, options, animateFirstListener);
					}
					Tool.writeJsonToFile(result,getActivity(),JuhuoConfig.USERINFO+JuhuoConfig.userId);
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
				for(int i=0;i<mData.size();i++){
					mData.get(i).put("focus", 0);
				}
				mData.get(position).put("focus", 1);
				leftMenuAdapter.notifyDataSetChanged();
				HomeActivity activity = (HomeActivity)getActivity();
				if(mData.size()==2){ // guest user
					if(position==1){
						
					}else if(position==0){
						Fragment myEvent = new HotEventsFragment();
						activity.switchContent(myEvent);
					}
				}else{
					if(position==1){
						Fragment myEvent = new MyEventFragment();
						activity.switchContent(myEvent);
					}else if(position==0){
						Fragment hotEvent = new HotEventsFragment();
						activity.switchContent(hotEvent);
					}else if(position==2){
						Fragment followEvent = new FollowFragment();
						activity.switchContent(followEvent);
					}else if(position==3){
						Fragment userSetting = new UserSettingFragment();
						activity.switchContent(userSetting);
					}
				}
				
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
	public void setData(){
		mData=new ArrayList<HashMap<String,Object>>();
		tmpmap = new HashMap<String,Object>();
		tmpmap.put("title", "热门活动");
		tmpmap.put("icon", mResources.getDrawable(R.drawable.icon_all_events));
		tmpmap.put("icon-selected", mResources.getDrawable(R.drawable.icon_all_events_selected));
		tmpmap.put("focus", 1);
		mData.add(tmpmap);
		tmpmap = new HashMap<String,Object>();
		tmpmap.put("title", "我的活动");
		tmpmap.put("icon", mResources.getDrawable(R.drawable.icon_my_events));
		tmpmap.put("icon-selected", mResources.getDrawable(R.drawable.icon_my_events_selected));
		tmpmap.put("focus", 0);
		mData.add(tmpmap);
		tmpmap = new HashMap<String,Object>();
		tmpmap.put("title", "我的关注");
		tmpmap.put("icon", mResources.getDrawable(R.drawable.icon_my_events));
		tmpmap.put("icon-selected", mResources.getDrawable(R.drawable.icon_my_events_selected));
		tmpmap.put("focus", 0);
		mData.add(tmpmap);
		tmpmap = new HashMap<String,Object>();
		tmpmap.put("title", "帐号设置");
		tmpmap.put("icon", mResources.getDrawable(R.drawable.icon_user_settings));
		tmpmap.put("icon-selected", mResources.getDrawable(R.drawable.icon_user_settings_selected));
		tmpmap.put("focus", 0);
		mData.add(tmpmap);
		tmpmap = new HashMap<String,Object>();
		tmpmap.put("title", "系统设置");
		tmpmap.put("icon", mResources.getDrawable(R.drawable.icon_system_settings));
		tmpmap.put("icon-selected", mResources.getDrawable(R.drawable.icon_system_settings_selected));
		tmpmap.put("focus", 0);
		mData.add(tmpmap);
		
	}
	public void setGuestData(){
		mData=new ArrayList<HashMap<String,Object>>();
		tmpmap = new HashMap<String,Object>();
		tmpmap.put("title", "热门活动");
		tmpmap.put("icon", mResources.getDrawable(R.drawable.icon_all_events));
		tmpmap.put("icon-selected", mResources.getDrawable(R.drawable.icon_all_events_selected));
		tmpmap.put("focus", 1);
		mData.add(tmpmap);
		tmpmap = new HashMap<String,Object>();
		tmpmap.put("title", "系统设置");
		tmpmap.put("icon", mResources.getDrawable(R.drawable.icon_system_settings));
		tmpmap.put("icon-selected", mResources.getDrawable(R.drawable.icon_system_settings_selected));
		tmpmap.put("focus", 0);
		mData.add(tmpmap);
		
	}

}
