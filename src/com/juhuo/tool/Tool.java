package com.juhuo.tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.juhuo.refreshview.XListView;
import com.juhuo.welcome.EventDetailActivity;
import com.juhuo.welcome.MainActivity;
import com.juhuo.welcome.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class Tool {
	public final static String ISO8601DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss+00:00";
	
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
  	public static Drawable getShape(int color){
	    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] { color,
	    		color, color});
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
		Drawable d = Tool.getShape(Color.WHITE);
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
    
	public static void getScreenSize(Activity activity){
		Display display = activity.getWindowManager().getDefaultDisplay(); 
		Point size = new Point(); 
		display.getSize(size); 
		JuhuoConfig.WIDTH =size.x; //dp
		JuhuoConfig.HEIGHT = size.y; //px
	}
	/**
	* 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	*
	*/
	public static String getStringDate(Long date) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateString = formatter.format(date);
		
		return dateString;
	}
	
	public static String getCalendarByInintData(String beginDateTime,String endDatetime) {
		Calendar calendar = Calendar.getInstance();
		long current = calendar.getTimeInMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendarbegin = Calendar.getInstance();
		Calendar calendarend = Calendar.getInstance();
		try {
			calendarbegin.setTime(sdf.parse(beginDateTime));
			calendarend.setTime(sdf.parse(endDatetime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long time_begin = calendarbegin.getTimeInMillis();
		long time_end = calendarend.getTimeInMillis();
		if(current<time_begin) return "即将开始!";
		else if(current>=time_begin&&current<=time_end) return "正在进行!";
		else return "已经结束!";
	}
	public static Calendar getCalendarFromISO(String datestring) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()) ;
	    SimpleDateFormat dateformat = new SimpleDateFormat(ISO8601DATEFORMAT, Locale.getDefault());
	    try {
	      Date date = dateformat.parse(datestring);
	      date.setHours(date.getHours()-1); 
	      calendar.setTime(date);
	    } catch (ParseException e) {
	      e.printStackTrace();
	    }
	    return calendar;
	 }
	
	public static int getAgeFromBirthday(String time){
		Calendar calendar = Calendar.getInstance();
		int current = calendar.get(Calendar.YEAR);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendarbegin = Calendar.getInstance();
		try {
			calendarbegin.setTime(sdf.parse(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return current-calendarbegin.get(Calendar.YEAR);
	}
	
	public static String getCurrentDateStr(){
		Calendar calendar = Calendar.getInstance();
		Date tasktime=calendar.getTime();  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(tasktime);
	}
	
	public static String getNextDateStr(){
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, day+1);
		Date tasktime=calendar.getTime();  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(tasktime);
	}
	
	public static List<HashMap<String,Object>> Jsonarr2Hash(JSONArray ja){
		List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<ja.length();i++){
			HashMap<String,Object> tmp = new HashMap<String,Object>();
			try {
				if(ja.getJSONObject(i).has("suc_photos")){
					String url = ja.getJSONObject(i).getJSONArray("suc_photos").
							getJSONObject(0).getString("url");
					tmp.put("url", url);	
				}else{
					tmp.put("url", "");
				}
				tmp.put("addr", ja.getJSONObject(i).getString("addr"));
				tmp.put("title", ja.getJSONObject(i).getString("title"));
				String timeEnd = ja.getJSONObject(i).getString("time_end");
				String timeBegin = ja.getJSONObject(i).getString("time_begin");
				String event_time = getCalendarByInintData(timeBegin.substring(0,19).replace('T', ' '),
						timeEnd.substring(0,19).replace('T', ' '));
				tmp.put("time", event_time);
				tmp.put("apply_number", ja.getJSONObject(i).getString("apply_number"));
				tmp.put("eventId", String.valueOf(ja.getJSONObject(i).getInt("id")));
				list.add(tmp);
			} catch (JSONException e) { e.printStackTrace();}
		}
		return list;
	}
	
	public static ArrayList<HashMap<String,Object>> commonJ2L(JSONArray ja){
		ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		for (int i = 0; i < ja.length(); i++) {
		   HashMap<String, Object> pairs = new HashMap<String, Object>();
		   JSONObject j = ja.optJSONObject(i);
		   Iterator it = j.keys();
		   while (it.hasNext()) {
		      String n = (String)it.next();
		        try {
					pairs.put(n, j.getString(n));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   }
		   list.add(pairs);
		}
		return list;
	}
	
	public static void writeJsonToFile(JSONObject jo,Activity activity,String filename){
		String url = jo.toString();
	    try {
	        FileOutputStream fos = activity.openFileOutput(filename, Context.MODE_PRIVATE);
	        ObjectOutputStream oos = new ObjectOutputStream(fos);
	        oos.writeObject(url);
	        oos.close();
		 } catch (Exception e) {
		     e.printStackTrace();
		 }

	}
	public static JSONObject loadJsonFromFile(String serfilename,Activity activity) {
		String res="";
		JSONObject jo = new JSONObject();
		try {
		    FileInputStream fis = activity.openFileInput(serfilename);
		    ObjectInputStream ois = new ObjectInputStream(fis);
		    res = (String) ois.readObject();
		    jo = new JSONObject(res);
		 } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		 }
		return jo;
	}
	public static ImageView getNewImage(Context context){
		Activity acc = (Activity)context;
		int imageWidth;
		if(acc.getClass()==EventDetailActivity.class){
			imageWidth = JuhuoConfig.WIDTH*61/(21*16);
			Log.i("height", String.valueOf(imageWidth));
		}else{
			imageWidth = JuhuoConfig.WIDTH*7/30;
			Log.i("height2", String.valueOf(imageWidth));
		}
		ImageView view = new ImageView(context);
        view.setImageResource(R.drawable.default_image);
        TableRow.LayoutParams imPara = new TableRow.LayoutParams(imageWidth,imageWidth);
        imPara.setMargins(0, 0, imageWidth/8, 0);
        view.setLayoutParams(imPara);
        view.setScaleType(ScaleType.FIT_XY);
        return view;
	}
	//将bitmap转变为file
    public static String Bitmap2File(Bitmap bm){
    	FileOutputStream out;
		try {
			 //把需要上传的图片存在一个临时文件里面
			 out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/tmp.jpg");
			 bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			 return Environment.getExternalStorageDirectory()+"/tmp.jpg";
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (Exception e) {
    	    e.printStackTrace();
    	} 
    	return "";
    }

	public static JSONArray RemoveJSONArray(JSONArray jarray, int pos) {

		JSONArray Njarray = new JSONArray();
		try {
			for (int i = 0; i < jarray.length(); i++) {
				if (i != pos)
					Njarray.put(jarray.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Njarray;
	}
	public static void isShowFooter(XListView listView,int totalLength){
//		int visibleCount = listView.getLastVisiblePosition()-listView.getFirstVisiblePosition()+1;
//		
//		Log.i("lastposition", String.valueOf(listView.getLastVisiblePosition()));
//		Log.i("firstposition", String.valueOf(listView.getFirstVisiblePosition()));
//		Log.i("visibleones", String.valueOf(visibleCount));
		if(3>totalLength){
			listView.setPullLoadEnable(false);
		}else{
			listView.setPullLoadEnable(true);
		}
		return;
	}
}
