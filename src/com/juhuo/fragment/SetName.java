package com.juhuo.fragment;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.R;

public class SetName extends Fragment {
	private final String TAG="SetName";
	private EditText name,des;
	private String content;
	private String type;
	private Resources mResource;
	private ImageView titleimg,titleimg2;
	private RadioButton btn,btn2;
	public void setContent(String ct,String type){
		this.content = ct;
		this.type = type;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResource = getActivity().getResources();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout parent;
		if(type.equals("name")){
			parent = (RelativeLayout) inflater.inflate(
					R.layout.set_name, null);
			name = (EditText)parent.findViewById(R.id.name);
			name.setText(content);
		}else if(type.equals("age")){
			parent = (RelativeLayout) inflater.inflate(
					R.layout.set_name, null);
			name = (EditText)parent.findViewById(R.id.name);
			TextView title = (TextView)parent.findViewById(R.id.action_title);
			title.setText(mResource.getString(R.string.age));
			if(content.equals("Î´Öª")){
				name.setText("1970-01-01");
			}else{
				name.setText(content);
			}
		}else if(type.equals("gender")){
			parent = (RelativeLayout) inflater.inflate(
					R.layout.set_gender, null);
			btn = (RadioButton)parent.findViewById(R.id.id1);
			btn2 = (RadioButton)parent.findViewById(R.id.id2);
			btn.setChecked(content.equals("ÄÐ")?true:false);
			btn2.setChecked(content.equals("ÄÐ")?false:true);
		}else{
			parent = (RelativeLayout) inflater.inflate(
					R.layout.set_des, null);
			des = (EditText)parent.findViewById(R.id.description);
			des.setText(content);
		}
		titleimg = (ImageView)parent.findViewById(R.id.action_title_img);
		titleimg2 = (ImageView)parent.findViewById(R.id.action_title_img2);
		titleimg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().beginTransaction().remove(SetName.this).commit();
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});
		titleimg2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HashMap<String,Object> mapPara = new HashMap<String,Object>();
				mapPara.put("token", JuhuoConfig.token);
				if(type.equals("name")){
					mapPara.put("name", name.getText().toString());
				}else if(type.equals("age")){
					mapPara.put("birthday", name.getText().toString());
				}else if(type.equals("gender")){
					mapPara.put("gender", btn.isChecked()?"0":"1");
				}else{
					mapPara.put("description", des.getText().toString());
				}
				ChangeUserInfo changeUserInfo = new ChangeUserInfo();
				changeUserInfo.execute(mapPara);
				
			}
		});
		
		return parent;
	}
	private class ChangeUserInfo extends AsyncTask<HashMap<String,Object>,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... map) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.USER_MODIFY);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(result == null){
				Log.i(TAG,"cannot get any");//we have reveived 500 error page
				
			}else if(result.has("wrong_data")){
				//sth is wrong
				Tool.dialog(getActivity());
			}else{
				Tool.writeJsonToFile(result,getActivity(),JuhuoConfig.USERINFO);
				Tool.myToast(getActivity(), mResource.getString(R.string.cha_info_success));
				getFragmentManager().beginTransaction().remove(SetName.this).commit();
				getActivity().getSupportFragmentManager().popBackStack();
			}
			
		}
		
	}

}
