package com.juhuo.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.juhuo.welcome.R;

public class EditEventImage extends Fragment{
	private Resources mResources;
	private String TAG = "EditEventImage";
	private RelativeLayout parent;
	private Button chooseImg;
	private Fragment mContent;
	private ImageAdapter imageAdapter;
	private GridView imagegrid;
	private ArrayList<String> arrPath = new ArrayList<String>();
    private ArrayList<Bitmap> thumbnails = new ArrayList<Bitmap>();
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
		imagegrid = (GridView) parent.findViewById(R.id.PhoneImageGrid);
		imageAdapter = new ImageAdapter(arrPath,thumbnails);
		imagegrid.setAdapter(imageAdapter);
		return parent;
		
	}
	public void setImageGrid(ArrayList<String> selectedarr,ArrayList<Bitmap> bitmap){
		Log.i(TAG, selectedarr.toString());
		this.arrPath = selectedarr;
		this.thumbnails = bitmap;
		imageAdapter.notifyDataSetChanged();
		imageAdapter.notifyDataSetInvalidated();
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
                        R.layout.galleryitem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
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
            Log.i(TAG, thumbnails.get(position).toString());
            holder.imageview.setImageBitmap(thumbnails.get(position));
            holder.id = position;
            return convertView;
        }
    }
	class ViewHolder {
        ImageView imageview;
        int id;
    }

}
