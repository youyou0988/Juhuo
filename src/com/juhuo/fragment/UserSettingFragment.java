package com.juhuo.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.ChangePassword;
import com.juhuo.welcome.MainActivity;
import com.juhuo.welcome.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class UserSettingFragment extends Fragment{
	private Resources mResources;
	private TextView name;
	private TextView age;
	private TextView gender;
	private TextView cell;
	private TextView description;
	private ImageView image;
	private String TAG = "UserSettingFragment";
	private ImageView actionTitleImg;
	private ImageView actionTitleImg2;
	private TextView actionTitle,noEventsText;
	private RelativeLayout parent,actionTitleLay;
	private View transView;
	private Button logout;
	private int menuType = 1;//0 for picture;1 for plus;2 for logout
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
	DisplayImageOptions options2 = new DisplayImageOptions.Builder()
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
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    // 创建一个以当前时间为名称的文件
    File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());
    private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		setHasOptionsMenu(true);
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parent = (RelativeLayout) inflater.inflate(
				R.layout.user_setting_layout, null);
		logout = (Button)parent.findViewById(R.id.logout);
		transView = (View)parent.findViewById(R.id.transview);
		actionTitleLay = (RelativeLayout)parent.findViewById(R.id.action_title_lay);
		actionTitleImg = (ImageView)parent.findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)parent.findViewById(R.id.action_title_img2);
		actionTitle = (TextView)parent.findViewById(R.id.action_title);
		noEventsText = (TextView)parent.findViewById(R.id.no_events_found);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_navi));
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.plus));
		actionTitleImg2.setVisibility(View.VISIBLE);
		actionTitle.setText(mResources.getString(R.string.user_setting));
		logout.setVisibility(View.VISIBLE);
		name = (TextView)parent.findViewById(R.id.t2);
		age = (TextView)parent.findViewById(R.id.t4);
		gender = (TextView)parent.findViewById(R.id.t6);
		cell = (TextView)parent.findViewById(R.id.t8);
		description = (TextView)parent.findViewById(R.id.t10);
		image = (ImageView)parent.findViewById(R.id.image);
		name.setOnClickListener(txtClick);
		age.setOnClickListener(txtClick);
		gender.setOnClickListener(txtClick);
		description.setOnClickListener(txtClick);
		actionTitleLay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((SlidingFragmentActivity)getActivity()).toggle();
			}
		});
		actionTitleImg2.setOnClickListener(clickLisnter);
		image.setOnClickListener(clickLisnter);
		logout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuType = 2;
				getActivity().openOptionsMenu();
				
			}
		});
		JSONObject jsonCache = new JSONObject();
		jsonCache = Tool.loadJsonFromFile(JuhuoConfig.USERINFO+JuhuoConfig.userId,getActivity());
		HashMap<String,Object> mapPara = new HashMap<String,Object>();
		mapPara.put("token", JuhuoConfig.token);
		LoadUserInfo loadUserInfo = new LoadUserInfo();
		mAsyncTask.add(loadUserInfo);
		loadUserInfo.execute(mapPara);
		
		return parent;
	}
	OnClickListener txtClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			TextView v = (TextView)arg0;
			Fragment content = new SetName();
			switch(v.getId()){
			case R.id.t2:
				((SetName)content).setContent(v.getText().toString(),"name");
				break;
			case R.id.t4:
				((SetName)content).setContent(v.getText().toString(),"age");
				break;
			case R.id.t6:
				((SetName)content).setContent(v.getText().toString(), "gender");
				break;
			case R.id.t10:
				((SetName)content).setContent(v.getText().toString(), "description");
				break;
			}
			FragmentTransaction transaction = getFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack
			transaction.replace(R.id.content_frame, content);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
	};
	//open the sliding menu
	OnClickListener clickLisnter = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ImageView vi = (ImageView)v;
			switch(vi.getId()){
			case R.id.action_title_img:
				((SlidingFragmentActivity)getActivity()).toggle();
				break;
			case R.id.action_title_img2:
				menuType = 1;
				getActivity().openOptionsMenu();
				break;
			case R.id.image:
				menuType = 0;
				getActivity().openOptionsMenu();
				break;
			}
			
		}
	};
	private class LogOut extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.LOGOUT);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return;
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				getActivity().finish();
			}
		}
	}
	
	private class ActiveAccount extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.ACTIVE_ACCOUNT);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return ;
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				Tool.myToast(getActivity(), mResources.getString(R.string.active_acc_success));
			}
			
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
				Tool.dialog(getActivity());
			}else{
				Tool.myToast(getActivity(), mResources.getString(R.string.upload_photo_success));
				try {
					HashMap<String,Object> map = new HashMap<String,Object>();
					map.put("token", JuhuoConfig.token);
					map.put("photo_ids", "["+String.valueOf(result.getInt("id"))+"]");
					ChangeUserIcon changeIcon = new ChangeUserIcon();
					mAsyncTask.add(changeIcon);
					changeIcon.execute(map);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mPgDialog.dismiss();
			}	
		}
    }
    private class ChangeUserIcon extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
    	@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.USER_ICON);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return ;
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				try {
					String url = result.getJSONArray("suc_photos").getJSONObject(0).getString("url");
					imageLoader.displayImage(url,image, options);
					HashMap<String,Object> mapPara = new HashMap<String,Object>();
					mapPara.put("token", JuhuoConfig.token);
					LoadUserInfo loadUserInfo = new LoadUserInfo();
					mAsyncTask.add(loadUserInfo);
					loadUserInfo.execute(mapPara);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}	
		}
    }
    private class LoadUserInfo extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.USER_INFO);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return;
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				try {
					String id = String.valueOf(result.getInt("id"));
					Tool.writeJsonToFile(result,getActivity(),JuhuoConfig.USERINFO+id);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setComponentsContent(result);
				
			}
			
		}
	}
	public void setComponentsContent(JSONObject result){
		try {
			name.setText(result.getString("name").equals("")?"匿名":result.getString("name"));
			String ag = result.getString("birthday").equals("null")?"未知":result.getString("birthday");
			if(ag.equals("未知")){
				age.setText(ag);
			}else{
				long a = Tool.getAgeFromBirthday(ag.substring(0,19).replace("T", " "));
				age.setText(String.valueOf(a));
			}
			gender.setText(result.getInt("gender")==1?"女":"男");
			cell.setText(result.getString("cell"));
			if(result.getString("description").equals("")){
				description.setText("暂无介绍");
			}else{
				description.setText(result.getString("description"));
			}
			image.getLayoutParams().height = JuhuoConfig.WIDTH*150/320;
			image.getLayoutParams().width = JuhuoConfig.WIDTH*150/320;
			String url="";
			if(result.has("suc_photos")){
				url = result.getJSONArray("suc_photos").getJSONObject(0).getString("url");
			}
			imageLoader.displayImage(url,image, options);
			//set left menu icon
			LeftMenuFragment.userName.setText(result.getString("name"));
			imageLoader.displayImage(url, LeftMenuFragment.userImage, options2);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//	    // TODO Add your menu entries here
//	    super.onCreateOptionsMenu(menu, inflater);
//	    getActivity().getMenuInflater().inflate(R.menu.setting, menu);
//	}
	@Override
	public void onPrepareOptionsMenu(Menu menu){
		MenuInflater inflater = getActivity().getMenuInflater();
		menu.clear();
		if(menuType==0){
			inflater.inflate(R.menu.pic, menu);
			
		}else if(menuType==1){//click title
			inflater.inflate(R.menu.setting, menu);
		}else{
			inflater.inflate(R.menu.logout, menu);
		}
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cha_pass:
			Intent intent = new Intent(getActivity(),ChangePassword.class);
			getActivity().startActivity(intent);
			break;
		case R.id.active_acc:
			ActiveAccount ac = new ActiveAccount();
			HashMap<String,Object> mapPara = new HashMap<String,Object>();
			mapPara.put("token", JuhuoConfig.token);
			mAsyncTask.add(ac);
			ac.execute(mapPara);
			break;
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
		case R.id.log_out:
			LogOut logOut = new LogOut();
			HashMap<String,Object> map= new HashMap<String,Object>();
			map.put("token", JuhuoConfig.token);
			mAsyncTask.add(logOut);
			logOut.execute(map);
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
            mPgDialog = new ProgressDialog(getActivity());
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
