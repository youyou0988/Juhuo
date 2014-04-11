package com.juhuo.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.juhuo.adapter.LeftMenuAdapter;
import com.juhuo.welcome.R;

public class LeftMenuFragment extends Fragment{
	private String TAG="LeftMenuFragment";
	private Resources mResources;
	private ListView naviList;
	private LeftMenuAdapter leftMenuAdapter;
	private List<String> mData;
	private List<Integer> focus;
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
		initListener();
		return parent;
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

}
