package com.juhuo.fragment;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.juhuo.adapter.HotEventsAdapter;
import com.juhuo.control.MyListView;
import com.juhuo.control.MyListView.OnLoadListener;
import com.juhuo.control.MyListView.OnRefreshListener;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserSettingFragment extends Fragment{
	private Resources mResources;
	private TextView name;
	private TextView age;
	private TextView gender;
	private TextView cell;
	private TextView description;
	private ImageView image;
	private String TAG = "MyEventFragment";
	private ImageView actionTitleImg;
	private ImageView actionTitleImg2;
	private TextView actionTitle,noEventsText;
	private RelativeLayout parent;
	private View transView;
	private Button logout;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
	.showImageOnLoading(R.drawable.default_image)
	.showImageForEmptyUri(R.drawable.default_image)
	.showImageOnFail(R.drawable.default_image)
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.considerExifParams(true)
	.displayer(new SimpleBitmapDisplayer())
	.build();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parent = (RelativeLayout) inflater.inflate(
				R.layout.apply_detail_two, null);
		logout = (Button)parent.findViewById(R.id.logout);
		transView = (View)parent.findViewById(R.id.transview);
		
		actionTitleImg = (ImageView)parent.findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)parent.findViewById(R.id.action_title_img2);
		actionTitle = (TextView)parent.findViewById(R.id.action_title);
		noEventsText = (TextView)parent.findViewById(R.id.no_events_found);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_navi));
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.plus));
		actionTitle.setText(mResources.getString(R.string.user_setting));
		logout.setVisibility(View.VISIBLE);
		name = (TextView)parent.findViewById(R.id.t2);
		age = (TextView)parent.findViewById(R.id.t4);
		gender = (TextView)parent.findViewById(R.id.t6);
		cell = (TextView)parent.findViewById(R.id.t8);
		description = (TextView)parent.findViewById(R.id.t10);
		image = (ImageView)parent.findViewById(R.id.image);
		//open the sliding menu
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((SlidingFragmentActivity)getActivity()).toggle();
			}
		});
		logout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				JuhuoConfig.token = "";
				getActivity().finish();
			}
		});
		HashMap<String,Object> mapPara = new HashMap<String,Object>();
		mapPara.put("token", JuhuoConfig.token);
		getNetData(mapPara);
		return parent;
	}
	public void getNetData(HashMap<String,Object> map){
		LoadEventList loadEventList = new LoadEventList();
		loadEventList.execute(map);
	}
	private class LoadEventList extends AsyncTask<HashMap<String,Object>,String,JSONObject>{

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
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				try {
					name.setText(result.getString("name"));
					String ag = result.getString("birthday").equals("null")?"δ֪":result.getString("birthday");
					if(ag.equals("δ֪")){
						age.setText(ag);
					}else{
						long a = Tool.getAgeFromBirthday(ag.substring(0,19).replace("T", " "));
						age.setText(String.valueOf(a));
					}
					gender.setText(result.getInt("gender")==1?"Ů":"��");
					cell.setText(result.getString("cell"));
					description.setText(result.getString("description"));
					image.getLayoutParams().height = JuhuoConfig.WIDTH*150/320;
					image.getLayoutParams().width = JuhuoConfig.WIDTH*150/320;
					String url="";
					if(result.has("suc_photos")){
						url = result.getJSONArray("suc_photos").getJSONObject(0).getString("url");
					}
					imageLoader.displayImage(url,image, options);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	public void setTrans(){
		Log.i("sliding menu", transView.getBackground().toString());
		transView.setVisibility(View.VISIBLE);
	}
	public void setTransBack(){
		Log.i("sliding menu", transView.getBackground().toString());
		transView.setVisibility(View.INVISIBLE);
	}

}
