package com.juhuo.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.CreateEvent;
import com.juhuo.welcome.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class EditEventImage extends Fragment{
	private Resources mResources;
	private String TAG = "EditEventImage";
	private RelativeLayout parent;
	private Button chooseImg;
	private Fragment mContent;
	private ImageAdapter imageAdapter;
	private GridView imagegrid;
	private TextView saveText,cancelText;
	private ArrayList<String> arrPath = new ArrayList<String>();
    private int[] isuploaded;
    private String photoids="";
    private boolean allUploaded,activityFinish=false;
    private ProgressDialog mPgDialog;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		options = new DisplayImageOptions.Builder()
    	.showImageOnLoading(R.drawable.default_image)
    	.showImageForEmptyUri(R.drawable.default_image)
    	.showImageOnFail(R.drawable.default_image)
    	.cacheInMemory(true)
    	.cacheOnDisc(true)
    	.considerExifParams(true)
    	.bitmapConfig(Bitmap.Config.RGB_565)
    	.build();
		mPgDialog = new ProgressDialog(getActivity());
        mPgDialog.setMessage(mResources.getString(R.string.uploading_photo));
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		parent = (RelativeLayout) inflater.inflate(
				R.layout.edit_image, null);
		saveText = (TextView)parent.findViewById(R.id.action_title_text2);
		cancelText = (TextView)parent.findViewById(R.id.action_title_text);
		chooseImg = (Button)parent.findViewById(R.id.choose_img);
		chooseImg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Fragment content = new UploadEventImage();
				((UploadEventImage)content).setFragment(EditEventImage.this);
				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

				transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
					.replace(R.id.content_frame, content,"uploadfragment");
				transaction.addToBackStack(null);

				// Commit the transaction
				transaction.commit();
			}
		});
		
		OnClickListener txtClick = new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				  // TODO Auto-generated method stub
				 TextView v = (TextView)arg0;
				  boolean completed = true;
				  if(isuploaded!=null){
					  for(int i=0; i<isuploaded.length; i++) {
					    if(isuploaded[i] == 0) {
					       completed = false;
					       break;
					     }   
					  }
				  }
				  if(completed){
					    //array has no zero values
					    switch(v.getId()){
						case R.id.action_title_text:
							getFragmentManager().beginTransaction().remove(EditEventImage.this).commit();
							getActivity().getSupportFragmentManager().popBackStack();
							getActivity().finish();
							break;
						case R.id.action_title_text2:
							Intent intent = new Intent(getActivity(),CreateEvent.class);
							intent.putExtra("photo_ids",photoids);
							intent.putExtra("imageurl", "file://"+arrPath.get(0));
							intent.putExtra("photo_num", arrPath.size());
							getActivity().setResult(getActivity().RESULT_OK, intent);
							getActivity().finish();
							activityFinish = true;
						}
						
				  }else{
						switch(v.getId()){
						case R.id.action_title_text:
							getFragmentManager().beginTransaction().remove(EditEventImage.this).commit();
							getActivity().getSupportFragmentManager().popBackStack();
							getActivity().finish();
							activityFinish = true;
							break;
						case R.id.action_title_text2:
							Tool.myToast(getActivity(), mResources.getString(R.string.uploading_photo));
						}
				  }
				
			}
		};
		saveText.setOnClickListener(txtClick);
		cancelText.setOnClickListener(txtClick);
		imagegrid = (GridView) parent.findViewById(R.id.PhoneImageGrid);
		imageAdapter = new ImageAdapter(arrPath);
		imagegrid.setAdapter(imageAdapter);
		return parent;
		
	}
	public void setImageGrid(ArrayList<String> selectedarr){
		this.arrPath = selectedarr;
		this.isuploaded = new int[this.arrPath.size()];
		
		imageAdapter.notifyDataSetChanged();
		imageAdapter.notifyDataSetInvalidated();
		uploadimage(0);
	}
	public void uploadimage(int i){
		if(i<this.arrPath.size()){
			UploadPhoto photoup = new UploadPhoto(i);
			//upload photo consequence
			HashMap<String,Object> map = new HashMap<String,Object>();
	        map.put("token", JuhuoConfig.token);
	        map.put("photo", this.arrPath.get(i));
	        photoup.execute(map);
		}else{
			//�����ʱ��û��ȡ���ϴ���Ƭ�Ĳ���
			if(activityFinish==false){
				Tool.myToast(getActivity(), "ȫ��ͼƬ�ϴ��ɹ�");
				allUploaded = true;
			}
			
		}
	}
	private class UploadPhoto extends AsyncTask<HashMap<String,Object>,Integer,JSONObject>{
		int index;
		protected UploadPhoto(int i){
			this.index = i;
		}
		@Override
		protected void onPreExecute(){
//			mPgDialog.show(); 
		}
    	@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub
    		
			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().callPost(mapped,JuhuoConfig.COMMON_UPLOADPHOTO);
		}
    	@Override
    	protected void onProgressUpdate(Integer... values) {  
//    		mPgDialog.setMessage("��ǰ�ϴ����ȣ�" + values[0] + "%");  
        }
		@Override
		protected void onPostExecute(JSONObject result) {
//			mPgDialog.dismiss();
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				Tool.myToast(getActivity(), mResources.getString(R.string.error_network));
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				Log.i("result-"+index, result.toString());
//				Tool.myToast(getActivity(), mResources.getString(R.string.upload_photo_success));
				try {
					if(index==0){
						photoids+=String.valueOf(result.getInt("id"));
					}else{
						photoids+=","+String.valueOf(result.getInt("id"));
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isuploaded[index] = 1;
				imageAdapter.notifyDataSetChanged();
				uploadimage(index+1);
			}	
		}
    }
	private class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        
        public ImageAdapter(ArrayList<String> selectedarr) {
        	mInflater = (LayoutInflater) getActivity()
            		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
        }
 
        public int getCount() {
        	return arrPath.size();
        }
 
        public Object getItem(int position) {
            return position;
        }
 
        public long getItemId(int position) {
            return position;
        }
 
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.uploaditem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
//                holder.transview = (ImageView)convertView.findViewById(R.id.above);
                holder.che = (CheckBox)convertView.findViewById(R.id.che);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imageview.setId(position);
            holder.imageview.setOnClickListener(new OnClickListener() {
 
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int id = v.getId();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + arrPath.get(id)), "image/*");
                    startActivity(intent);
                }
            });
            holder.che.setChecked(isuploaded[position]==1?true:false);
            imageLoader.displayImage("file://"+ arrPath.get(position), holder.imageview, options);
//            holder.transview.setVisibility(isuploaded[position]==1?View.INVISIBLE:View.VISIBLE);
            holder.id = position;
            return convertView;
        }
    }
	class ViewHolder {
        ImageView imageview;
//        ImageView transview;
        CheckBox che;
        int id;
    }

}
