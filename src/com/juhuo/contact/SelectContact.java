package com.juhuo.contact;

import java.util.ArrayList;
import java.util.List;

import com.foound.widget.AmazingAdapter;
import com.foound.widget.AmazingListView;
import com.juhuo.tool.Tool;
import com.juhuo.welcome.R;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SelectContact extends Fragment{
	private Resources mResources;
	private String TAG = "EditEventImage";
	private RelativeLayout parent;
	private AmazingListView lsContact;
	private SectionContactAdapter adapter;
	private ImageView actionTitleImg,actionTitleImg2;
	private TextView actionTitle;
	private Button confirmInvi;
	private List<Pair<String, List<Contact>>> all;
	private static CharacterParser characterParser;
	private ArrayList<Boolean> mCheckedStates = null; 
	private ArrayList<Contact> contactList;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mResources = getResources();
		all = Data.getAllData(getActivity());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		parent = (RelativeLayout) inflater.inflate(
				R.layout.activity_section_demo, null);
		
		mCheckedStates = new ArrayList<Boolean>(); 
		contactList = new ArrayList<Contact>();
		characterParser = CharacterParser.getInstance();
		confirmInvi = (Button) parent.findViewById(R.id.confirm_btn);
		actionTitleImg = (ImageView)parent.findViewById(R.id.action_title_img);
		actionTitleImg2 = (ImageView)parent.findViewById(R.id.action_title_img2);
		actionTitle = (TextView)parent.findViewById(R.id.action_title);
		actionTitleImg.setBackgroundDrawable(mResources.getDrawable(R.drawable.icon_back));
		actionTitleImg2.setBackgroundDrawable(mResources.getDrawable(R.drawable.plus));
		actionTitleImg2.setVisibility(View.VISIBLE);
		actionTitleImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getFragmentManager().popBackStack();
			}
		});
		actionTitleImg2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog();
			}
		});
		actionTitle.setText(mResources.getString(R.string.invited));
		lsContact = (AmazingListView) parent.findViewById(R.id.lsComposer);
//		RelativeLayout.LayoutParams listviewparams = new RelativeLayout.LayoutParams(JuhuoConfig.WIDTH, JuhuoConfig.HEIGHT-180);
//		listviewparams.addRule(RelativeLayout.BELOW, titlelay.getId());
//		lsContact.setLayoutParams(listviewparams);
		lsContact.setPinnedHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.item_composer_header, lsContact, false));
		lsContact.setAdapter(adapter = new SectionContactAdapter());
		confirmInvi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Fragment confirm = new ConfirmInvitation();
				//set confirm contact list
				int res = 0;
				for (int i = 0; i < all.size(); i++) {
					res += all.get(i).second.size();
				}
				for (int i = 0; i < res; ++i) {
			    	if(mCheckedStates.get(i)==true){
			    		contactList.add(adapter.getItem(i));
			    	}
			  	}
			    ((ConfirmInvitation)confirm).setData(contactList);
				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

				transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
					.replace(R.id.content_frame, confirm,"ConfirmInvitation");
				transaction.addToBackStack(null);

				// Commit the transaction
				transaction.commit();
			}
		});
		return parent;
	}
	class SectionContactAdapter extends AmazingAdapter {
		@Override
		public int getCount() {
			int res = 0;
			for (int i = 0; i < all.size(); i++) {
				res += all.get(i).second.size();
			}
			// 初始化所有checked box选中状态为false 
		    for (int i = 0; i < res; ++i) {
		    	mCheckedStates.add(false); 
		  	}
			return res;
		}

		@Override
		public Contact getItem(int position) {
			int c = 0;
			for (int i = 0; i < all.size(); i++) {
				if (position >= c && position < c + all.get(i).second.size()) {
					return all.get(i).second.get(position - c);
				}
				c += all.get(i).second.size();
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		protected void onNextPageRequested(int page) {
		}

		@Override
		protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
			if (displaySectionHeader) {
				view.findViewById(R.id.header).setVisibility(View.VISIBLE);
				TextView lSectionTitle = (TextView) view.findViewById(R.id.header);
				lSectionTitle.setBackgroundColor(mResources.getColor(R.color.titlegray));
				lSectionTitle.setText(getSections()[getSectionForPosition(position)]);
			} else {
				view.findViewById(R.id.header).setVisibility(View.GONE);
			}
		}

		@Override
		public View getAmazingView(int position, View convertView, ViewGroup parent) {
			View res = convertView;
			if (res == null) res = getActivity().getLayoutInflater().inflate(R.layout.item_composer, null);
			
			TextView lName = (TextView) res.findViewById(R.id.lName);
			TextView lYear = (TextView) res.findViewById(R.id.lYear);
			CheckBox lis = (CheckBox)res.findViewById(R.id.isinvited);
			Contact composer = getItem(position);
			lName.setText(composer.name);
			lYear.setText(composer.cell);
			final int pos = position; 
			lis.setChecked(mCheckedStates.get(position)); 
			lis.setOnClickListener(new CheckBox.OnClickListener() { 
		      @Override 
		      public void onClick(View v) { 
		        CheckBox cb = (CheckBox) v; 
		        mCheckedStates.set(pos, cb.isChecked()); 
		      } 
		    });
			return res;
		}

		@Override
		public void configurePinnedHeader(View header, int position, int alpha) {
			TextView lSectionHeader = (TextView)header;
			lSectionHeader.setText(getSections()[getSectionForPosition(position)]);
			lSectionHeader.setBackgroundColor(mResources.getColor(R.color.titlegray));
			lSectionHeader.setTextColor(alpha << 24 | (0x000000));
		}

		@Override
		public int getPositionForSection(int section) {
			if (section < 0) section = 0;
			if (section >= all.size()) section = all.size() - 1;
			int c = 0;
			for (int i = 0; i < all.size(); i++) {
				if (section == i) { 
					return c;
				}
				c += all.get(i).second.size();
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			int c = 0;
			for (int i = 0; i < all.size(); i++) {
				if (position >= c && position < c + all.get(i).second.size()) {
					return i;
				}
				c += all.get(i).second.size();
			}
			return -1;
		}

		@Override
		public String[] getSections() {
			String[] res = new String[all.size()];
			for (int i = 0; i < all.size(); i++) {
				res[i] = all.get(i).first;
			}
			return res;
		}
		
	}
	protected void dialog() {
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Drawable d = Tool.getShape(mResources.getColor(R.color.dialoggray));
		dialog.getWindow().setBackgroundDrawable(d);
		dialog.setContentView(R.layout.create_contact_dialog);
		final EditText nameEdit = (EditText) dialog.findViewById(R.id.name);
		final EditText cellEdit = (EditText) dialog.findViewById(R.id.cell);
		Button yes = (Button) dialog.findViewById(R.id.yes);
		Button no = (Button) dialog.findViewById(R.id.no);
		yes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				//新建添加联系人
				String[] titles = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
						"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
						"W", "X", "Y", "Z", "#" };
				String name = nameEdit.getText().toString();
				String cell = cellEdit.getText().toString();
				Contact contact = new Contact(name,cell);
				//汉字转换成拼音
				String pinyin = characterParser.getSelling(name);
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if(sortString.matches("[A-Z]")){
					contact.setSortLetters(sortString.toUpperCase());
				}else{
					contact.setSortLetters("#");
				}
				all.get(findIndex(titles,sortString)).second.add(contact);
				adapter.notifyDataSetChanged();
				adapter.notifyDataSetInvalidated();
			}
		});
		no.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	public int findIndex(String[] titles,String A){
		for(int i=0;i<titles.length;i++){
			if(titles[i].equals(A)){
				return i;
			}
		}
		return -1;
	}

}
