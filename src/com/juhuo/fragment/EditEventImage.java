package com.juhuo.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.CreateEvent;
import com.juhuo.welcome.R;

public class EditEventImage extends Fragment{
	private Resources mResources;
	private String TAG = "EditEventImage";
	private RelativeLayout parent;
	private Button chooseImg;
	private Fragment mContent;
	private ImageAdapter imageAdapter;
	private GridView imagegrid;
	private TextView saveText;
	private ArrayList<String> arrPath = new ArrayList<String>();
    private ArrayList<Bitmap> thumbnails = new ArrayList<Bitmap>();
    private int[] isuploaded;
    private String photoids="";
    private boolean allUploaded;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		parent = (RelativeLayout) inflater.inflate(
				R.layout.edit_image, null);
		mResources = getResources();
		saveText = (TextView)parent.findViewById(R.id.action_title_text2);
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
		saveText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),CreateEvent.class);
				intent.putExtra("photo_ids",photoids);
				intent.putExtra("bitmap", thumbnails.get(0));
				getActivity().setResult(getActivity().RESULT_OK, intent);
				getActivity().finish();
			}
		});
		imagegrid = (GridView) parent.findViewById(R.id.PhoneImageGrid);
		imageAdapter = new ImageAdapter(arrPath,thumbnails);
		imagegrid.setAdapter(imageAdapter);
		return parent;
		
	}
	public void setImageGrid(ArrayList<String> selectedarr,ArrayList<Bitmap> bitmap){
		Log.i(TAG, selectedarr.toString());
		this.arrPath = selectedarr;
		this.thumbnails = bitmap;
		this.isuploaded = new int[this.thumbnails.size()];
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
	        map.put("photo", Tool.Bitmap2File(this.thumbnails.get(i)));
	        photoup.execute(map);
		}else{
			Tool.myToast(getActivity(), "全部图片上传成功");
			allUploaded = true;
		}
	}
	private class UploadPhoto extends AsyncTask<HashMap<String,Object>,String,JSONObject>{
		int index;
		protected UploadPhoto(int i){
			this.index = i;
		}
    	@Override
		protected JSONObject doInBackground(HashMap<String,Object>... map) {
			// TODO Auto-generated method stub

			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().callPost(mapped,JuhuoConfig.COMMON_UPLOADPHOTO);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				Log.i("result-"+index, result.toString());
				Tool.myToast(getActivity(), mResources.getString(R.string.upload_photo_success));
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
				uploadimage(index+1);
			}	
		}
    }
	private class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        
        public ImageAdapter(ArrayList<String> selectedarr,ArrayList<Bitmap> map) {
        	Log.i(TAG, getActivity().toString());
            mInflater = (LayoutInflater) getActivity()
            		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
        }
 
        public int getCount() {
        	Log.i(TAG, String.valueOf(arrPath.size()));
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
                holder.transview = (View)convertView.findViewById(R.id.above);
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
            holder.transview.setVisibility(isuploaded[position]==1?View.INVISIBLE:View.GONE);
            holder.imageview.setImageBitmap(thumbnails.get(position));
            holder.id = position;
            return convertView;
        }
    }
	class ViewHolder {
        ImageView imageview;
        View transview;
        int id;
    }

}
