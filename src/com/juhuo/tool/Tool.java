package com.juhuo.tool;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.juhuo.welcome.MainActivity;
import com.juhuo.welcome.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class Tool {
	
	/**
     * 检测网络是否可用
     * */
    public static boolean networkAvaliable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (null == networkInfo || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }
    /**
     * toast方法
     *
     * @param toastContent
     */
    public static void myToast(Context context, String toastContent)
    {
        Toast toast = Toast.makeText(context,
                toastContent, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return;
    }
    
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, double pxValue) {  
        final double scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
  //设置弹出框的样式为圆角并透明的矩形
  	public static Drawable getShape(){
	    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] { Color.WHITE,
	    		Color.WHITE, Color.WHITE});
	    gradientDrawable.setGradientType(GradientDrawable.RECTANGLE);
	    gradientDrawable.setCornerRadii(getRandomFloatArray());
	    return gradientDrawable;
	}

	public static float [] getRandomFloatArray(){
	    float[] floats = new float[8];
	    for (int i =0; i < floats.length; i++){
	        floats[i] = 10;
	    }
	    return floats;
	}
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	public static void dialog(final Activity activity){
		//mind the order && donn't set background color
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Drawable d = Tool.getShape();
		dialog.getWindow().setBackgroundDrawable(d);
		dialog.setContentView(R.layout.alert_dialog);
		
        /*
         * 将对话框的大小按屏幕大小的百分比设置
         */
        WindowManager m = activity.getWindowManager();
        Display dd = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (dd.getHeight() * 0.25); // 高度设置为屏幕的0.6
        p.width = (int) (dd.getWidth() * 0.85); // 宽度设置为屏幕的0.65
        dialog.getWindow().setAttributes(p);
		
		
		Button yes = (Button) dialog.findViewById(R.id.alertmakesure);
		yes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				JuhuoConfig.token = JuhuoConfig.PUBLIC_TOKEN;
				dialog.dismiss();
				Intent intent = new Intent(activity,MainActivity.class);
				activity.startActivity(intent);
				activity.finish();
			}
		});
		dialog.show();
	}
    
    
}
