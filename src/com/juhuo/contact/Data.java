package com.juhuo.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.util.Pair;

public class Data {
	public static final String TAG = Data.class.getSimpleName();
	private static Context context;
	/**
	 * 汉字转换成拼音的类
	 */
	
	private static List<Contact> SourceDateList;
	private static CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private static PinyinComparator pinyinComparator;
	
	public static List<Pair<String, List<Contact>>> getAllData(Context con) {
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		context = con;
		List<Pair<String, List<Contact>>> res = new ArrayList<Pair<String, List<Contact>>>();
		
		for (int i = 0; i < 27; i++) {
			res.add(getOneSection(i));
		}
		
		return res;
	}
	
	public static List<Contact> getFlattenedData() {
		 List<Contact> res = new ArrayList<Contact>();
		 
		 for (int i = 0; i < 4; i++) {
			 res.addAll(getOneSection(i).second);
		 }
		 
		 return res;
	}
	
	public static Pair<Boolean, List<Contact>> getRows(int page) {
		List<Contact> flattenedData = getFlattenedData();
		if (page == 1) {
			return new Pair<Boolean, List<Contact>>(true, flattenedData.subList(0, 5));
		} else {
			SystemClock.sleep(2000); // simulate loading
			return new Pair<Boolean, List<Contact>>(page * 5 < flattenedData.size(), flattenedData.subList((page - 1) * 5, Math.min(page * 5, flattenedData.size())));
		}
	}
	
	public static Pair<String, List<Contact>> getOneSection(int index) {
		String[] titles = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
				"W", "X", "Y", "Z", "#" };
		HashMap<String,List<Contact>> map = new HashMap<String,List<Contact>>();
		for(int i=0;i<titles.length;i++){
			map.put(titles[i], new ArrayList<Contact>());
		}
		Cursor cursor = null;
	    try {
	    	String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
	        cursor = context.getContentResolver().query(Phone.CONTENT_URI, null, null, null, sortOrder);
	        int contactIdIdx = cursor.getColumnIndex(Phone._ID);
	        int nameIdx = cursor.getColumnIndex(Phone.DISPLAY_NAME);
	        int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);
	        int photoIdIdx = cursor.getColumnIndex(Phone.PHOTO_ID);
	        cursor.moveToFirst();
	        do {
	            String idContact = cursor.getString(contactIdIdx);
	            String name = cursor.getString(nameIdx);
	            String phoneNumber = cursor.getString(phoneNumberIdx);
	            Contact contact = new Contact(name,phoneNumber);
	            //汉字转换成拼音
				String pinyin = characterParser.getSelling(name);
				String sortString = pinyin.substring(0, 1).toUpperCase();
				
				// 正则表达式，判断首字母是否是英文字母
				if(sortString.matches("[A-Z]")){
					contact.setSortLetters(sortString.toUpperCase());
				}else{
					contact.setSortLetters("#");
				}
				map.get(contact.getSortLetters()).add(contact);
	        } while (cursor.moveToNext());  
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (cursor != null) {
	            cursor.close();
	        }
	    }
	    return new Pair<String, List<Contact>>(titles[index], map.get(titles[index]));
	}
}

