package com.juhuo.tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;


public class JuhuoInfo {
	private String TAG="JuhuoInfo";
	private String SUCCESS_STATUS = "200";
	private String PASSWORD_WRONG = "401";
	private String INVALID_TOKEN = "402";
	private String USEREXIST = "403";
	private String NOT_FOUND_WRONG = "404";
	
	private String getCode(String tmp){
		String res="";
		try {
			JSONObject jo = new JSONObject(tmp);
			res = jo.getJSONObject("error").getString("code");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}
	private JSONObject getData(String tmp){
		JSONObject res = new JSONObject();
		try {
			
			JSONObject jo = new JSONObject(tmp);
			if(jo.has("data")){
				res = jo.getJSONObject("data");
			}else{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("good_data", "success");
				res = new JSONObject(map);
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	private String makeUrl(HashMap<String,Object> para){
		StringBuffer url = new StringBuffer();
		Iterator<Entry<String, Object>> it=para.entrySet().iterator();
		int count=0;
        while(it.hasNext()){
            Map.Entry<String, Object>  entry=(Entry<String, Object>) it.next();
            try {
            	if(count==0) url.append("?"+entry.getKey()+"="+URLEncoder.encode((String)entry.getValue(),"utf-8"));
            	else {url.append("&"+entry.getKey()+"="+URLEncoder.encode((String)entry.getValue(),"utf-8"));}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            count++;
        }
		return url.toString();
	}
	public JSONObject loadNetData(HashMap<String,Object> para,String preUrl){
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			String url = preUrl+makeUrl(para);
			Log.i(TAG, url);
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = null;
			response = httpclient.execute(httpGet);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String entityStr = EntityUtils.toString(responseEntity);
				String resultCode = getCode(entityStr);
				Log.i(TAG, resultCode);
				
				if(resultCode.equals(SUCCESS_STATUS)){
					return getData(entityStr);
				}else if(resultCode.equals(INVALID_TOKEN)){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("wrong_data", INVALID_TOKEN);
					JSONObject json = new JSONObject(map);
					return json;
				}else if(resultCode.equals(PASSWORD_WRONG)){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("password_wrong", PASSWORD_WRONG);
					JSONObject json = new JSONObject(map);
					return json;
				}else if(resultCode.equals(NOT_FOUND_WRONG)){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("not_found_wrong", NOT_FOUND_WRONG);
					JSONObject json = new JSONObject(map);
					return json;
				}else{
					return null;
				}
			}
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
		} catch(HttpHostConnectException e){
			Log.i(TAG, "connection timeout");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	public JSONObject callPost(HashMap<String,Object> para,String preUrl){
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			String url = preUrl;
			Log.i(TAG, url);
			HttpPost httppost = new HttpPost(url);
			FileBody bin = new FileBody(getimage((String)para.get("photo")));
            StringBody comment = new StringBody((String)para.get("token"), ContentType.TEXT_PLAIN);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("photo", bin) 
                    .addPart("token", comment)
                    .build();
            httppost.setEntity(reqEntity);
			HttpResponse response = null;
			response = httpclient.execute(httppost);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String entityStr = EntityUtils.toString(responseEntity);
				String resultCode = getCode(entityStr);
				Log.i(TAG, resultCode);
				
				if(resultCode.equals(SUCCESS_STATUS)){
					return getData(entityStr);
				}else if(resultCode.equals(INVALID_TOKEN)){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("wrong_data", INVALID_TOKEN);
					JSONObject json = new JSONObject(map);
					return json;
				}else{
					return null;
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	public JSONObject callPostPlain(HashMap<String,Object> para,String preUrl){
		DefaultHttpClient httpclient = new DefaultHttpClient();
		JSONObject message = new JSONObject();
		try {
			HttpPost httpPost = new HttpPost(preUrl);
			httpPost.setHeader("content-type","application/json");
			Iterator<Entry<String, Object>> it=para.entrySet().iterator();
			while(it.hasNext()){
	            Map.Entry<String, Object>  entry=(Entry<String, Object>) it.next();
	            message.put(entry.getKey(), entry.getValue());
	        }
			httpPost.setEntity(new StringEntity(message.toString(), "UTF8"));  
	        HttpResponse response = httpclient.execute(httpPost);  
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String entityStr = EntityUtils.toString(responseEntity);
				String resultCode = getCode(entityStr);
				if(resultCode.equals(SUCCESS_STATUS)){
					return getData(entityStr);
				}else if(resultCode.equals(INVALID_TOKEN)){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("wrong_data", INVALID_TOKEN);
					JSONObject json = new JSONObject(map);
					return json;
				}else{
					return null;
				}
			}
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
		} catch(HttpHostConnectException e){
			Log.i(TAG, "connection timeout");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
            e.printStackTrace();

        }
		return null;
	}
	public JSONObject callPostPlainNest(JSONObject para,String preUrl){
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httpPost = new HttpPost(preUrl);
			httpPost.setHeader("content-type","application/json");
			Log.i(TAG, preUrl+para.toString());
			httpPost.setEntity(new StringEntity(para.toString(), "UTF8"));  
	        HttpResponse response = httpclient.execute(httpPost);  
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String entityStr = EntityUtils.toString(responseEntity);
				String resultCode = getCode(entityStr);
				if(resultCode.equals(SUCCESS_STATUS)){
					return getData(entityStr);
				}else if(resultCode.equals(INVALID_TOKEN)){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("wrong_data", INVALID_TOKEN);
					JSONObject json = new JSONObject(map);
					return json;
				}else{
					return null;
				}
			}
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
		} catch(HttpHostConnectException e){
			Log.i(TAG, "connection timeout");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
            e.printStackTrace();

        }
		return null;
	}
	private File getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//��ʱ����bmΪ��
		
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
		float hh = 800f;//�������ø߶�Ϊ800f
		float ww = 480f;//�������ÿ��Ϊ480f
		//���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
		int be = 1;//be=1��ʾ������
		if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//�������ű���
		//���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 80;//����ϲ����80��ʼ,
		bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
		while (baos.toByteArray().length / 1024 > 30) { 
			Log.i(TAG, String.valueOf(baos.toByteArray().length/1024));
			baos.reset();
			options -= 10;
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
                "/commonphoto";
		File dir = new File(file_path);
		if(!dir.exists())
		    dir.mkdirs();
		File file = new File(file_path,"sketchpad.png");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
}
