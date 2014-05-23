package com.juhuo.contact;

import android.os.*;
import android.util.*;

import java.util.*;

public class Data {
	public static final String TAG = Data.class.getSimpleName();
	
	public static List<Pair<String, List<Contact>>> getAllData() {
		List<Pair<String, List<Contact>>> res = new ArrayList<Pair<String, List<Contact>>>();
		
		for (int i = 0; i < 4; i++) {
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
		String[] titles = {"Renaissance", "Baroque", "Classical", "Romantic"};
		Contact[][] composerss = {
			{
				new Contact("Thomas Tallis", "1510-1585"),
				new Contact("Josquin Des Prez", "1440-1521"),
				new Contact("Pierre de La Rue", "1460-1518"),
			},
			{
				new Contact("Johann Sebastian Bach", "1685-1750"),
				new Contact("George Frideric Handel", "1685-1759"),
				new Contact("Antonio Vivaldi", "1678-1741"),
				new Contact("George Philipp Telemann", "1681-1767"),
			},
			{
				new Contact("Franz Joseph Haydn", "1732-1809"),
				new Contact("Wolfgang Amadeus Mozart", "1756-1791"),
				new Contact("Barbara of Portugal", "1711�758"),
				new Contact("Frederick the Great", "1712�786"),
				new Contact("John Stanley", "1712�786"),
				new Contact("Luise Adelgunda Gottsched", "1713�762"),
				new Contact("Johann Ludwig Krebs", "1713�780"),
				new Contact("Carl Philipp Emanuel Bach", "1714�788"),
				new Contact("Christoph Willibald Gluck", "1714�787"),
				new Contact("Gottfried August Homilius", "1714�785"),
			},
			{
				new Contact("Ludwig van Beethoven", "1770-1827"),
				new Contact("Fernando Sor", "1778-1839"),
				new Contact("Johann Strauss I", "1804-1849"),
			},
		};
		return new Pair<String, List<Contact>>(titles[index], Arrays.asList(composerss[index]));
	}
}

