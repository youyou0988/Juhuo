package com.juhuo.contact;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.EventDetailActivity;
import com.juhuo.welcome.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class ConfirmInvitation  extends Fragment{
	private Resources mResources;
	private String TAG = "ConfirmInvitation";
	private RelativeLayout parent;
	private ProgressDialog mPgDialog;
//	private ImageView actionTitleImg;
	private TextView actionTitle,eventTitle,beginTime,endTime;
	private Button sendBtn;
	private ImageView image;
	private ListView confirmListView;
	private String event_id;
	private RelativeLayout actionTitleLay;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
	.showImageOnLoading(R.drawable.default_image)
	.showImageForEmptyUri(R.drawable.default_image)
	.showImageOnFail(R.drawable.default_image)
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.considerExifParams(true)
	.displayer(new SimpleBitmapDisplayer())
	.build();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private ArrayList<Contact> contactList;
	public void setData(ArrayList<Contact> li){
		this.contactList = li;
	}
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		mPgDialog = new ProgressDialog(getActivity());
		event_id = getActivity().getIntent().getExtras().getString("event_id");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		parent = (RelativeLayout) inflater.inflate(
				R.layout.confirm_invitation, null);
		actionTitleLay = (RelativeLayout)parent.findViewById(R.id.action_title_lay);
		actionTitle = (TextView)parent.findViewById(R.id.action_title);
		actionTitle.setText(mResources.getString(R.string.confirm_invitation));
		
		//open the sliding menu
		actionTitleLay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().popBackStack();
			}
		});
		eventTitle = (TextView)parent.findViewById(R.id.t2);
		beginTime = (TextView)parent.findViewById(R.id.t4);
		endTime = (TextView)parent.findViewById(R.id.t6);
		image = (ImageView)parent.findViewById(R.id.image);
		confirmListView = (ListView)parent.findViewById(R.id.confirmlist);
		confirmListView.setAdapter(new ConfirmAdapter());
		sendBtn = (Button) parent.findViewById(R.id.send_btn);
		sendBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				JSONObject json = new JSONObject();
				try {
					json.put("token", JuhuoConfig.token);
					json.put("id",event_id);
					JSONArray ja = new JSONArray();
					for(int i=0;i<contactList.size();i++){
						JSONObject tm = new JSONObject();
						tm.put("name", contactList.get(i).getName());
						tm.put("cell", contactList.get(i).getCell());
						ja.put(tm);
					}
					json.put("contacts", ja);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SendInvitation sendInvi = new SendInvitation();
				mAsyncTask.add(sendInvi);
				sendInvi.execute(json);
			}
		});
		JSONObject jo = new JSONObject();
		jo = Tool.loadJsonFromFile(JuhuoConfig.EVENTINFO+event_id, getActivity());
		if(jo==null){
			Log.i(TAG, "event is not exists!");
		}else{
			setViewsContent(jo);
		}
		return parent;
	}
	private class SendInvitation extends CheckStopAsyncTask<JSONObject,String,JSONObject>{
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mPgDialog.setMessage(mResources.getString(R.string.reminding));
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(JSONObject... map) {
			// TODO Auto-generated method stub
			JSONObject mapped = map[0];
			return new JuhuoInfo().callPostPlainNest(mapped,JuhuoConfig.EVENT_INVITE);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return;
			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(getActivity(), mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				Tool.myToast(getActivity(), mResources.getString(R.string.send_invitation_success));
			}	
		}
		
	}
	private void setViewsContent(JSONObject result){
		try {
			eventTitle.setText(result.getString("title"));
			String tb = result.getString("time_begin").substring(0,19).replace('T', ' ');
			String te = result.getString("time_end").substring(0,19).replace('T', ' ');
			beginTime.setText(tb);endTime.setText(te);
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
	public class ConfirmAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contactList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View res = convertView;
			if (res == null) res = getActivity().getLayoutInflater().inflate(R.layout.item_contact, null);
			
			TextView lName = (TextView) res.findViewById(R.id.lName);
			TextView lYear = (TextView) res.findViewById(R.id.lYear);
			lName.setText(contactList.get(pos).getName());
			lYear.setText(contactList.get(pos).getCell());
			return res;
		}
		
	}
	@Override
    public void onDestroyView()
    {
        for(int index = 0;index < mAsyncTask.size();index ++)
        {
            if(!(mAsyncTask.get(index).getStatus() == AsyncTask.Status.FINISHED) )
                mAsyncTask.get(index).setStop();
        }
        super.onDestroyView();
    }

}
