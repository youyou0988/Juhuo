package com.juhuo.contact;

public class Contact {
	public static final String TAG = Contact.class.getSimpleName();
	
	public String name;
	public String cell;
	private String sortLetters;  //显示数据拼音的首字母
	
	public Contact(String name, String cell) {
		this.name = name;
		this.cell = cell;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
