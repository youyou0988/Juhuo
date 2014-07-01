package com.juhuo.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UploadEventImage extends Fragment{
	private Resources mResources;
	private String TAG = "UploadEventImage";
	private RelativeLayout parent,actionTitleLay;
	private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private ArrayList<Integer> rowindex;
    private String[] arrPath;
    private ImageAdapter imageAdapter;
    private TextView cancel,makesure,pager;
    private TableRow tr;
    private Fragment prevone;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    // 创建一个以当前时间为名称的文件
    File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor imagecursor = getActivity().managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy+ " DESC");
        options = new DisplayImageOptions.Builder()
    	.showImageOnLoading(R.drawable.default_image)
    	.showImageForEmptyUri(R.drawable.default_image)
    	.showImageOnFail(R.drawable.default_image)
    	.cacheInMemory(true)
    	.cacheOnDisc(true)
    	.considerExifParams(true)
    	.bitmapConfig(Bitmap.Config.RGB_565)
    	.build();
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.arrPath = new String[this.count];
        this.thumbnailsselection = new boolean[this.count];
        this.rowindex = new ArrayList<Integer>();
        double a= System.currentTimeMillis();
        for (int i = 0; i < this.count; i++) {
            imagecursor.moveToPosition(i);
            int id = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i]= imagecursor.getString(dataColumnIndex);
        }
       
	}
	public void setFragment(Fragment fr){
		this.prevone = (Fragment)fr;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parent = (RelativeLayout) inflater.inflate(
				R.layout.image_list, null);
		pager = (TextView)parent.findViewById(R.id.pager);
		cancel = (TextView)parent.findViewById(R.id.cancel);
		makesure = (TextView)parent.findViewById(R.id.makesure);
		actionTitleLay = (RelativeLayout)parent.findViewById(R.id.action_title_lay);
		actionTitleLay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getFragmentManager().beginTransaction().remove(UploadEventImage.this).commit();
				getActivity().getSupportFragmentManager().popBackStack();
				getActivity().finish();
			}
		});
		GridView imagegrid = (GridView) parent.findViewById(R.id.PhoneImageGrid);
		tr = (TableRow)parent.findViewById(R.id.rows);
		tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);
        cancel.setOnClickListener(txtClick);
        makesure.setOnClickListener(txtClick);
        pager.setText("0/"+count);
        return parent;
		
	}
	OnClickListener txtClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			TextView vt = (TextView)v;
			switch(vt.getId()){
			case R.id.cancel:
				getFragmentManager().beginTransaction().remove(UploadEventImage.this).commit();
				getActivity().getSupportFragmentManager().popBackStack();
				getActivity().finish();
				break;
			case R.id.makesure:
				final int len = thumbnailsselection.length;
                int cnt = 0;
                ArrayList<String> selectedarr = new ArrayList<String>();
                ArrayList<Bitmap> selectedmap = new ArrayList<Bitmap>();
                for (int i =0; i<len; i++)
                {
                    if (thumbnailsselection[i]){
                        cnt++;
                        selectedarr.add(arrPath[i]);
                    }
                }
//                Tool.myToast(getActivity(), "You've selected Total " + cnt + " image(s).");
                //put image to editimage fragment to upload
                ((EditEventImage)prevone).setImageGrid(selectedarr);
                getFragmentManager().executePendingTransactions();
                getActivity().getSupportFragmentManager().popBackStack();
				break;
			}
		}
	};
	
	private class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
 
        public ImageAdapter() {
            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        public int getCount() {
            return count+1;
        }
 
        public Object getItem(int position) {
            return null;
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
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
 
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            holder.checkbox.setOnClickListener(new OnClickListener() {
 
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    Integer idd = Integer.valueOf(id);
                    if (thumbnailsselection[id]){
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                        tr.removeViews(rowindex.indexOf(idd), 1);
                        rowindex.remove(idd);
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                        ImageView view = Tool.getNewImage(getActivity());
                        imageLoader.displayImage("file://"+ arrPath[id],view, options);
                        rowindex.add(idd);
                        tr.addView(view);
                    }
                    pager.setText(rowindex.size()+"/"+count);
                }
            });
//            holder.imageview.setOnClickListener(new OnClickListener() {
// 
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    int id = v.getId();
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
//                    startActivity(intent);
//                }
//            });
//            holder.imageview.setImageBitmap(thumbnails[position]);
            Log.i(TAG, arrPath.toString());
            if(position<count){
            	imageLoader.displayImage("file://"+ arrPath[position], holder.imageview, options);
            	holder.checkbox.setVisibility(View.VISIBLE);
            	holder.checkbox.setChecked(thumbnailsselection[position]);
            	holder.imageview.setOnClickListener(null);
            }else{
            	Log.i(TAG, "fsojfso");
            	imageLoader.displayImage("drawable://" + R.drawable.camera, holder.imageview, options);
            	holder.checkbox.setVisibility(View.INVISIBLE);
            	holder.imageview.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Log.i(TAG, "start to take a picture");
						// 调用系统的拍照功能
			            Intent intentpic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			            // 指定调用相机拍照后照片的储存路径
			            intentpic.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));
			            startActivityForResult(intentpic, PHOTO_REQUEST_TAKEPHOTO);
					}
				});
            }
            
            holder.id = position;
            return convertView;
        }
    }
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {
        case PHOTO_REQUEST_TAKEPHOTO:
        	//tempFile is the picture
        	Log.i(TAG, tempFile.getAbsolutePath());
        	if(tempFile.exists()){
        		ArrayList<String> selectedarr = new ArrayList<String>();
            	selectedarr.add(tempFile.getAbsolutePath());
            	((EditEventImage)prevone).setImageGrid(selectedarr);
                getFragmentManager().executePendingTransactions();
                getActivity().getSupportFragmentManager().popBackStack();
        	}
        	
            break;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
    //使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + "upload.jpg";
    }
    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }

}
