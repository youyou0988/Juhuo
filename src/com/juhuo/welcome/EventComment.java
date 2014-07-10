package com.juhuo.welcome;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.adapter.HotEventsAdapter.AnimateFirstDisplayListener;
import com.juhuo.control.MyListView;
import com.juhuo.control.MyListView.OnRefreshListener;
import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

final class CommentHandler{
	TextView name;
	TextView time;
	TextView content;
	ImageView image;
	RelativeLayout whole;
}
public class EventComment extends Activity{
	private final String TAG = "EventComment";
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle;
	private Resources mResources;
	private ProgressDialog mPgDialog;
	private MyListView commentList;
	private CommentListAdapter mAdapter;
	private ArrayList<HashMap<String,Object>> mData;
	private HashMap<String,Object> mapPara;
	private LayoutInflater mInflater;
	private String event_id;
	private EditText message;
	private ImageView send;
	private TextView noCommentText;
	private RelativeLayout actionTitleLay;
	private String content_url = "";
	private SimpleDateFormat df = new SimpleDateFormat(Tool.ISO8601DATEFORMAT, Locale.getDefault());
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
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
    // 创建一个以当前时间为名称的文件
    File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.comment);
		mResources = getResources();
		mPgDialog = new ProgressDialog(this);
		mData = new ArrayList<HashMap<String,Object>>();
		actionTitleImg = (ImageView)findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)findViewById(R.id.action_title_img2);
		actionTitleLay = (RelativeLayout)findViewById(R.id.action_title_lay);
		actionTitle = (TextView)findViewById(R.id.action_title);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitleImg2.setVisibility(View.INVISIBLE);
		actionTitleLay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		actionTitle.setText(mResources.getString(R.string.comment));
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		commentList = (MyListView)findViewById(R.id.commentlist);
		message = (EditText)findViewById(R.id.comment);
		send = (ImageView)findViewById(R.id.send_comment);
		noCommentText = (TextView)findViewById(R.id.no_comments_found);
		RelativeLayout comLay = (RelativeLayout)findViewById(R.id.commentlay);
//		String page = getIntent().getExtras().getString("PAGE");
//		comLay.setVisibility(page.equals("HOT")?View.INVISIBLE:View.VISIBLE);
		int organizer_status = getIntent().getExtras().getInt("organizer_status");
		if(organizer_status==0||organizer_status==1||organizer_status==3||organizer_status==5){
			comLay.setVisibility(View.VISIBLE);
		}else{
			comLay.setVisibility(View.INVISIBLE);
		}
		this.event_id = getIntent().getExtras().getString("id");
		//read from cache
		JSONObject jo = new JSONObject();
		jo = Tool.loadJsonFromFile(JuhuoConfig.EVENTCOMMENT+event_id, this);
		mapPara = new HashMap<String,Object>();
		mapPara.put("id", this.event_id);
		mapPara.put("token", JuhuoConfig.token);
		mapPara.put("incremental", String.valueOf(true));
		mAdapter = new CommentListAdapter();
		if(jo!=null){
			JSONArray ja;
			try {
				ja = jo.getJSONArray("comments");
				mData = Tool.commonJ2L(ja);
				if(mData.size()==0){
					noCommentText.setText(mResources.getString(R.string.no_comments_found));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			commentList.setAdapter(mAdapter);
		}
		//get network data
		LoadEventComment loadEventComment = new LoadEventComment();
		mAsyncTask.add(loadEventComment);
		loadEventComment.execute(mapPara);
		
		
		commentList.setonRefreshListener(new OnRefreshListener() {
			
			public void onRefresh() {
				// TODO Auto-generated method stub
				Log.i("pull", "refresh");
				LoadEventComment loadEventComment = new LoadEventComment();
				mAsyncTask.add(loadEventComment);
				loadEventComment.execute(mapPara);
			}
		});
		send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(message.getWindowToken(), 0);
				openOptionsMenu();

			}
		});
		message.setImeOptions(EditorInfo.IME_ACTION_DONE); 
		message.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int actionId, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if(!"".equals(message.getEditableText().toString().trim())){
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						 //do here your stuff f
						HashMap<String,Object> mapPara = new HashMap<String,Object>();
						mapPara.put("token", JuhuoConfig.token);
						mapPara.put("commenter_name", JuhuoConfig.userName);
						mapPara.put("id", event_id);
						
						mapPara.put("time", df.format(new Date()));
						mapPara.put("content", message.getEditableText().toString());
						mapPara.put("type", "");
							
						mData.add(0,mapPara);
						mAdapter.notifyDataSetChanged();
						noCommentText.setText("");
						SendCommentClass task = new SendCommentClass();
						mAsyncTask.add(task);
						task.execute(mapPara);
						return true;
				    }
				}else{
					Tool.myToast(EventComment.this, mResources.getString(R.string.please_enter_comment));
				}
				
				return false;
			} 
		});
		
	}
	public class CommentListAdapter extends BaseAdapter{
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			CommentHandler holder = null;
			
			if (convertView == null) {
				holder = new CommentHandler();
				convertView = mInflater.inflate(R.layout.comment_detail_tem,null);
				
				holder.whole = (RelativeLayout)convertView.findViewById(R.id.commentwhole);
				holder.name=(TextView)convertView.findViewById(R.id.commenter_name);
				holder.time=(TextView)convertView.findViewById(R.id.commenter_time);
				holder.content=(TextView)convertView.findViewById(R.id.commenter_content);
				holder.image = (ImageView)convertView.findViewById(R.id.comment_picture);
				
				convertView.setTag(holder);
			} else {
				holder = (CommentHandler) convertView.getTag();
				
			}
			holder.name.setText((String)mData.get(position).get("commenter_name"));
			if(((String)mData.get(position).get("time")).equals("null")){
				holder.time.setText("1970-01-01 08:00");
			}else{
				holder.time.setText(((String)mData.get(position).get("time")).substring(0,19).replace('T', ' '));
			}
			if(!mData.get(position).get("type").toString().equals("picture")){
//				Log.i(TAG, holder.content.toString());
				holder.content.setText((String)mData.get(position).get("content"));
				holder.content.setVisibility(View.VISIBLE);
				holder.image.setVisibility(View.GONE);
			}else{
				imageLoader.displayImage((String)mData.get(position).get("content"), holder.image, options, animateFirstListener);
				holder.image.setVisibility(View.VISIBLE);
				holder.content.setVisibility(View.GONE);
			}			
			return convertView;
		}
		
	}

	private class SendCommentClass extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mPgDialog.setMessage(mResources.getString(R.string.commenting));
	        mPgDialog.show();
	        message.setText("");
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... arg0) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = arg0[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_COMMENT);
		}
		@Override
		protected void onPostExecute(JSONObject result){
			if(getStop()) return;
			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(EventComment.this, mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(EventComment.this);
			}else{
				Tool.myToast(EventComment.this, mResources.getString(R.string.send_comment_success));
			}
		}
	}
	private class LoadEventComment extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        noCommentText.setText("");
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... map) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.EVENT_COMMENT_LIST);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return;
			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(EventComment.this);
			}else{
				Tool.writeJsonToFile(result, EventComment.this, JuhuoConfig.EVENTCOMMENT+event_id);
				try {
					JSONArray ja = result.getJSONArray("comments");
					mData = Tool.commonJ2L(ja);
					if(mData.size()==0){
						noCommentText.setText(mResources.getString(R.string.no_comments_found));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mAdapter = new CommentListAdapter();
				commentList.setAdapter(mAdapter);
			}
			commentList.onRefreshComplete();
		}
		
	}
	private class UploadPhoto extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
    	@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().callPost(mapped,JuhuoConfig.COMMON_UPLOADPHOTO);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return ;
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(EventComment.this);
			}else{
				Tool.myToast(EventComment.this, mResources.getString(R.string.upload_picture_success));
				try {
					HashMap<String,Object> mapPara = new HashMap<String,Object>();
					mapPara.put("token", JuhuoConfig.token);
					mapPara.put("commenter_name", JuhuoConfig.userName);
					mapPara.put("id", event_id);
					
					mapPara.put("time", df.format(new Date()));
					content_url = result.getString("url");
					if(!"".equals(content_url)){
						mapPara.put("type", "picture");
						mapPara.put("content", content_url);
					}
					mData.add(0,mapPara);
					mAdapter.notifyDataSetChanged();
					noCommentText.setText("");
					SendCommentClass task = new SendCommentClass();
					mAsyncTask.add(task);
					task.execute(mapPara);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mPgDialog.dismiss();
			}	
		}
    }
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		menu.clear();
		inflater.inflate(R.menu.pic, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.take_pic:
			// 调用系统的拍照功能
            Intent intentpic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 指定调用相机拍照后照片的储存路径
            intentpic.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));
            startActivityForResult(intentpic, PHOTO_REQUEST_TAKEPHOTO);
			break;
		case R.id.select_pic:
			//调用系统的相册
			Intent intentsel = new Intent(Intent.ACTION_PICK, null);
			intentsel.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            startActivityForResult(intentsel, PHOTO_REQUEST_GALLERY);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {
        case PHOTO_REQUEST_TAKEPHOTO:
            startPhotoZoom(Uri.fromFile(tempFile), 150);
            break;

        case PHOTO_REQUEST_GALLERY:
            if (data != null)
                startPhotoZoom(data.getData(), 150);
            break;

        case PHOTO_REQUEST_CUT:
            if (data != null) 
                setPicToView(data);
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
    //调用系统自带的图片剪裁
    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    //将进行剪裁后的图片上传到服务器
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            UploadPhoto upload = new UploadPhoto();
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("token", JuhuoConfig.token);
            map.put("photo", Tool.Bitmap2File(photo));
            mAsyncTask.add(upload);
            upload.execute(map);
            mPgDialog = new ProgressDialog(EventComment.this);
            mPgDialog.setMessage(mResources.getString(R.string.uploading_photo));
            mPgDialog.show();
            //上传用户的头像
            
        }
    }
	// 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
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
