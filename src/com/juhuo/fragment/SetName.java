package com.juhuo.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juhuo.control.DateTimePickerDialog;
import com.juhuo.control.DateTimePickerDialog.OnDateTimeSetListener;
import com.juhuo.tool.CheckStopAsyncTask;
import com.juhuo.tool.JuhuoConfig;
import com.juhuo.tool.JuhuoInfo;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.R;

public class SetName extends Fragment {
	private final String TAG="SetName";
	private EditText name,des;
	private TextView age;
	private String content;
	private String type,birthday;
	private Resources mResource;
	private ImageView titleimg;
	private RadioButton btn,btn2;
	private ProgressDialog mPgDialog;
	private RelativeLayout checkLay,titleLay;
	private List<CheckStopAsyncTask> mAsyncTask = new ArrayList<CheckStopAsyncTask>();
	private SimpleDateFormat df = new SimpleDateFormat(Tool.ISO8601DATEFORMAT, Locale.getDefault());
	public void setContent(String ct,String type){
		this.content = ct;
		this.type = type;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResource = getActivity().getResources();
		mPgDialog = new ProgressDialog(getActivity());
		
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
			birthday = df.format(new Date());
			parent = (RelativeLayout) inflater.inflate(
					R.layout.set_age, null);
			age = (TextView)parent.findViewById(R.id.age);
			if(content.equals("Î´Öª")){
				age.setText("1970-01-01");
			}else{
				age.setText(content);
			}
			age.setOnClickListener(txtClickListener);
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
		titleLay = (RelativeLayout)parent.findViewById(R.id.action_title_lay);
		checkLay = (RelativeLayout)parent.findViewById(R.id.checklay);
		titleLay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().beginTransaction().remove(SetName.this).commit();
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});
		checkLay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HashMap<String,Object> mapPara = new HashMap<String,Object>();
				mapPara.put("token", JuhuoConfig.token);
				if(type.equals("name")){
					mapPara.put("name", name.getText().toString());
				}else if(type.equals("age")){
					mapPara.put("birthday", birthday);
				}else if(type.equals("gender")){
					mapPara.put("gender", btn.isChecked()?"0":"1");
				}else{
					mapPara.put("description", des.getText().toString());
				}
				ChangeUserInfo changeUserInfo = new ChangeUserInfo();
				mAsyncTask.add(changeUserInfo);
				changeUserInfo.execute(mapPara);
				
			}
		});
		//not work
//		parent.setOnKeyListener( new View.OnKeyListener()
//		{
//		    @Override
//		    public boolean onKey( View v, int keyCode, KeyEvent event )
//		    {
//		        if( keyCode == KeyEvent.KEYCODE_BACK )
//		        {
//		        	getFragmentManager().beginTransaction().remove(SetName.this).commit();
//					getActivity().getSupportFragmentManager().popBackStack();
//		            return true;
//		        }
//		        return false;
//		    }
//		} );
		return parent;
	}
	OnClickListener txtClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			long date = 0;
			DateTimePickerDialog dialog  = new DateTimePickerDialog(getActivity(), date);
			dialog.setOnDateTimeSetListener(new OnDateTimeSetListener()
		      {
				public void OnDateTimeSet(AlertDialog dialog, long date)
				{
					age.setText(Tool.getStringDate(date));
					birthday = df.format(date);
				}
			});
			dialog.show();
		}
	};
	private class ChangeUserInfo extends CheckStopAsyncTask<HashMap<String,Object>,String,JSONObject>{
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mPgDialog.show();
	    }
		@Override
		protected JSONObject doInBackground(HashMap<String, Object>... map) {
			// TODO Auto-generated method stub
			HashMap<String,Object> mapped = map[0];
			return new JuhuoInfo().loadNetData(mapped,JuhuoConfig.USER_MODIFY);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			if(getStop()) return;
			mPgDialog.dismiss();
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
	@Override
    public void onDestroyView()
    {
        for(int index = 0;index < mAsyncTask.size();index ++)
        {
            if(!(mAsyncTask.get(index).getStatus() == AsyncTask.Status.FINISHED) )
                mAsyncTask.get(index).setStop();
        }
        super.onDestroyView();
    }
	

}
