package com.juhuo.welcome;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoConfig.Status;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
final class ApplyHandler{
	ImageView img;
	ImageView dailImg;
	TextView name;
	TextView description;
	TextView status;
	TextView cell;
	RelativeLayout whole;
}
public class ApplyDetailOne extends Activity {
	private ListView applyList;
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle;
	private EditText messageTxt;
	private Resources mResources;
	private RelativeLayout approve;
	private ApplyDetailAdapter mAdapter;
	private ArrayList<HashMap<String,Object>> mData;
	private LayoutInflater mInflater;
	private ArrayList<String> urls;
	private ProgressDialog mPgDialog;
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
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private String TAG="ApplyDetailOne";
	private String type="HOT";
	private Status st;
	private String event_id;
	private int organizer_status=7;
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.apply_detail_one);
		mResources = getResources();
		mPgDialog = new ProgressDialog(this);
        mPgDialog.setMessage(mResources.getString(R.string.approving));
		actionTitleImg = (ImageView)findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)findViewById(R.id.action_title_img2);
		actionTitle = (TextView)findViewById(R.id.action_title);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitleImg2.setVisibility(View.INVISIBLE);
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		messageTxt = (EditText)findViewById(R.id.message);
		applyList = (ListView)findViewById(R.id.applylist);
		st = (Status)getIntent().getExtras().get("TYPE");
		event_id = getIntent().getExtras().getString("event_id");
		organizer_status = getIntent().getExtras().getInt("ORGANIZER_STATUS");
		String res="";
		switch (st){
		case PARTICIPANT:
			actionTitle.setText(mResources.getString(R.string.participant));
			res = getIntent().getExtras().getString("APPLY_DETAIL");
			urls = getIntent().getExtras().getStringArrayList("APPLY_URLS");
			break;
		case INVITED:
			actionTitle.setText(mResources.getString(R.string.invited));
			res = getIntent().getExtras().getString("INVITED_DETAIL");
			Log.i(TAG, res);
			urls = getIntent().getExtras().getStringArrayList("INVITED_URLS");
			break;
		case NO:
			actionTitle.setText(mResources.getString(R.string.absent));
			res = getIntent().getExtras().getString("ABSENT_DETAIL");
			urls = getIntent().getExtras().getStringArrayList("ABSENT_URLS");
			break;
		case APPLY:
			actionTitle.setText(mResources.getString(R.string.apply));
			res = getIntent().getExtras().getString("APPLY_DETAIL");
			urls = getIntent().getExtras().getStringArrayList("APPLY_URLS");
			break;
		}
		type = getIntent().getExtras().getString("PAGE");
		try {
			JSONArray ja = new JSONArray(res);
			mData = Tool.commonJ2L(ja);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAdapter = new ApplyDetailAdapter();
		applyList.setAdapter(mAdapter);
		applyList.setOnItemClickListener(mListListener);
	}
	private class EventApproveClass extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
    	public int pos;
		public EventApproveClass(int pos){
    		this.pos = pos;
    	}
		@Override
	    protected void onPreExecute() {
			mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().callPostPlain(mapped,JuhuoConfig.EVENT_APPROVE);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if (getStop()) {
                return;
            }
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(ApplyDetailOne.this);
			}else{
				Tool.myToast(ApplyDetailOne.this, mResources.getString(R.string.approve_success));
				mData.remove(pos);
				mAdapter.notifyDataSetChanged();
				approve.setVisibility(View.GONE);
				mPgDialog.dismiss();
			}
		}
	}
	OnItemClickListener mListListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, final int position,
				long arg3) {
			// TODO Auto-generated method stub
			if(st==JuhuoConfig.Status.APPLY){
				approve = (RelativeLayout)findViewById(R.id.approve);
				approve.setVisibility(View.VISIBLE);
				TextView approveTxt = (TextView)findViewById(R.id.approvetxt);
				TextView declinedTxt = (TextView)findViewById(R.id.declinetxt);
				TextView refereeTxt = (TextView)findViewById(R.id.referee);
				String res = mData.get(position).get("referee_name")==null?"":(String)mData.get(position).get("referee_name");
				refereeTxt.setText("推荐人："+res);
				OnClickListener txtClick = new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						TextView tv = (TextView)arg0;
						HashMap<String,Object> params = new HashMap<String,Object>();
						params.put("token", JuhuoConfig.token);
						params.put("id", event_id);
						params.put("message", messageTxt.getEditableText().toString());
						params.put("guest_id", (String)mData.get(position).get("id"));
						switch(tv.getId()){
						case R.id.approvetxt:
							params.put("decision", 0);
							break;
						case R.id.declinetxt:
							params.put("decision", 1);
							break;
						}
						EventApproveClass task = new EventApproveClass(position);
						mAsyncTask.add(task);
						task.execute(params);
					}
				};
				approveTxt.setOnClickListener(txtClick);
				declinedTxt.setOnClickListener(txtClick);
			}else{
				Intent intent = new Intent(ApplyDetailOne.this,ApplyDetailTwo.class);
				intent.putExtra("name", (String)mData.get(position).get("name"));
				intent.putExtra("age", (String)mData.get(position).get("birthday"));
				intent.putExtra("gender", (String)mData.get(position).get("gender"));
				intent.putExtra("cell", (String)mData.get(position).get("cell"));
				intent.putExtra("description", (String)mData.get(position).get("description"));
				intent.putExtra("url", urls.get(position));
				startActivity(intent);
			}	
		}
		
	};
	public class ApplyDetailAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
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
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ApplyHandler holder = null;
			if (convertView == null) {
				holder = new ApplyHandler();
				convertView = mInflater.inflate(R.layout.apply_detail_item, null);
				holder.whole = (RelativeLayout)convertView.findViewById(R.id.whole);
				holder.name=(TextView)convertView.findViewById(R.id.name);
				holder.status=(TextView)convertView.findViewById(R.id.status);
				holder.cell=(TextView)convertView.findViewById(R.id.cell);
				holder.img=(ImageView)convertView.findViewById(R.id.image);
				holder.description = (TextView)convertView.findViewById(R.id.description);
				holder.dailImg = (ImageView)convertView.findViewById(R.id.dail);
				convertView.setTag(holder);
				//set item height
//				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(JuhuoConfig.WIDTH*9/32,JuhuoConfig.WIDTH*9/32);
//				holder.whole.setLayoutParams(param);
				holder.img.getLayoutParams().height = JuhuoConfig.WIDTH*9/32;
				holder.img.getLayoutParams().width = JuhuoConfig.WIDTH*9/32;
				holder.img.setScaleType(ScaleType.FIT_XY);
				
			} else {
				holder = (ApplyHandler) convertView.getTag();
			}
			final int pos = position;
			if(organizer_status!=5){
				holder.dailImg.setVisibility(View.INVISIBLE);
				holder.cell.setVisibility(View.INVISIBLE);
			}else{
				holder.dailImg.setVisibility(View.VISIBLE);
				holder.cell.setVisibility(View.VISIBLE);
				holder.dailImg.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Dialog alertDialog = new AlertDialog.Builder(ApplyDetailOne.this). 
				                setMessage((String)mData.get(pos).get("cell")). 
				                setPositiveButton("确定", new DialogInterface.OnClickListener() { 
				                     
				                    @Override 
				                    public void onClick(DialogInterface dialog, int which) { 
				                        // TODO Auto-generated method stub  
				                    	Intent intent = new Intent();
				                        intent.setAction(Intent.ACTION_CALL);
				                        intent.setData(Uri.parse("tel:"+(String)mData.get(pos).get("cell")));
				                        startActivity(intent);
				                    } 
				                }). 
				                setNegativeButton("取消", new DialogInterface.OnClickListener() { 
				                     
				                    @Override 
				                    public void onClick(DialogInterface dialog, int which) { 
				                        // TODO Auto-generated method stub  
				                    } 
				                }).
				                create(); 
				        alertDialog.show(); 
					}
				});
			}
			holder.name.setText((String)mData.get(position).get("name"));
			holder.cell.setText((String)mData.get(position).get("cell"));
			holder.description.setText((String)mData.get(position).get("description"));
			holder.status.setText(JuhuoConfig.STATUS[Integer.parseInt((String)mData.get(position).get("status"))]);
			imageLoader.displayImage(urls.get(position), holder.img, options, animateFirstListener);
			return convertView;
		}
		
	}

	private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

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
