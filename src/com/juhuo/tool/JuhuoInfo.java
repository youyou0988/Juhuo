package com.juhuo.tool;

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
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class JuhuoInfo {
	private String TAG="JuhuoInfo";
	private String SUCCESS_STATUS = "200";
	
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
			res = jo.getJSONObject("data");
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
            	else url.append("&"+entry.getKey()+"="+URLEncoder.encode((String)entry.getValue(),"utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            count++;
        }
		return url.toString();
	}
	/**
     * 异步执行登陆认证
     * @param username
     * @param password
     * @return return data
     */
	public JSONObject authenticate(HashMap<String,Object> para){	
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpParams params = httpclient.getParams(); 
		HttpClientParams.setRedirecting(params, false);
		try {
			String url = JuhuoConfig.LOGIN+makeUrl(para);
			Log.i(TAG, url);
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = null;
			response = httpclient.execute(httpGet);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String entityStr = EntityUtils.toString(responseEntity);
				String resultCode = getCode(entityStr);
				if(resultCode.equals(SUCCESS_STATUS)){
					return getData(entityStr);
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

}
