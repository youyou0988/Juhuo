package com.juhuo.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.juhuo.welcome.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 生成滑动图片区域布局
 * @Description: 生成滑动图片区域布局

 * @File: SlideImageLayout.java

 * @Package com.juhuo.control.layout

 * @Author 摘抄自网上

 * @Date 2012-6-18 上午09:04:14

 * @Version V1.0
 */
public class SlideImageLayout {
	// 包含图片的ArrayList
	private ArrayList<ImageView> imageList = null;
	private Activity activity = null;
	// 圆点图片集合
	private ImageView[] imageViews = null; 
	private ImageView imageView = null;
	// 表示当前滑动图片的索引
	private int pageIndex = 0;
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
	
	public SlideImageLayout(Activity activity) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		imageList = new ArrayList<ImageView>();
	}
	
	/**
	 * 生成滑动图片区域布局
	 * @param index
	 * @return
	 */
	public View getSlideImageLayout(int index){
		// 包含TextView的LinearLayout
		LinearLayout imageLinerLayout = new LinearLayout(activity);
		LinearLayout.LayoutParams imageLinerLayoutParames = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT,
				1);
		
		ImageView iv = new ImageView(activity);
		iv.setBackgroundResource(index);
		iv.setOnClickListener(new ImageOnClickListener());
		imageLinerLayout.addView(iv,imageLinerLayoutParames);
		imageList.add(iv);
		
		return imageLinerLayout;
	}
	
	/**
	 * 生成滑动图片区域布局
	 * @param index
	 * @return
	 */
	public View getSlideImageLayout2(String url){
		// 包含TextView的LinearLayout
		LinearLayout imageLinerLayout = new LinearLayout(activity);
		LinearLayout.LayoutParams imageLinerLayoutParames = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, 
				LinearLayout.LayoutParams.FILL_PARENT);
		RelativeLayout imageRelative = new RelativeLayout(activity);
//		RelativeLayout.LayoutParams imageRelLayoutParames = new RelativeLayout.LayoutParams(
//				RelativeLayout.LayoutParams.WRAP_CONTENT, 
//				RelativeLayout.LayoutParams.WRAP_CONTENT);
		ImageView iv = new ImageView(activity);
		RelativeLayout.LayoutParams textviewparams=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    textviewparams.addRule(RelativeLayout.CENTER_IN_PARENT);
		imageRelative.addView(iv,textviewparams);
		imageLoader.displayImage(url,iv, options, animateFirstListener);
		imageLinerLayout.addView(imageRelative,imageLinerLayoutParames);
		imageList.add(iv);
		
		return imageLinerLayout;
	}
	
	/**
	 * 获取LinearLayout
	 * @param view
	 * @param width
	 * @param height
	 * @return
	 */
	public View getLinearLayout(View view,int width,int height){
		LinearLayout linerLayout = new LinearLayout(activity);
		LinearLayout.LayoutParams linerLayoutParames = new LinearLayout.LayoutParams(
				width, 
				height,
				1);
		// 这里最好也自定义设置，有兴趣的自己设置。
		linerLayout.setBackgroundResource(R.color.transparent);
		linerLayout.setPadding(10, 0, 10, 0);
		linerLayout.addView(view, linerLayoutParames);
		
		return linerLayout;
	}
	
	/**
	 * 设置圆点个数
	 * @param size
	 */
	public void setCircleImageLayout(int size){
		imageViews = new ImageView[size];
	}
	
	/**
	 * 生成圆点图片区域布局对象
	 * @param index
	 * @return
	 */
	public ImageView getCircleImageLayout(int index){
		imageView = new ImageView(activity);  
		imageView.setLayoutParams(new LayoutParams(10,10));
        imageView.setScaleType(ScaleType.FIT_XY);
        
        imageViews[index] = imageView;
         
        if (index == 0) {  
            //默认选中第一张图片
            imageViews[index].setBackgroundResource(R.drawable.dot_selected1);  
        } else {  
            imageViews[index].setBackgroundResource(R.drawable.dot_none1);  
        }  
         
        return imageViews[index];
	}
	
	/**
	 * 设置当前滑动图片的索引
	 * @param index
	 */
	public void setPageIndex(int index){
		pageIndex = index;
	}
	
	// 滑动页面点击事件监听器
    private class ImageOnClickListener implements OnClickListener{
    	@Override
    	public void onClick(View v) {
    		// TODO Auto-generated method stub
    		
    	}
    }
    
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

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
}

