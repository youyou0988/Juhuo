package com.juhuo.tool;

public class JuhuoConfig {
	public static int HEIGHT;
	public static int WIDTH;
	public static String EVENTLISTFILE = "juhuo_event_list";
	public static String EVENTINFO = "juhuo_event_info_";
	
	public static int INVI_NULL=0;
	public static int INVI_YES=1;
	public static int INVI_NO=2;
	public static int INVI_MAYBE=3;
	public static int INVI_APPLY=4;
	public static int INVI_ORGANIZER=5;
	public static int INVI_REJECTOR=6;
	public static String[] STATUS = {"确定参加","确定参加","缺席人员","可能参加","点击审批","组织者","不参加"};
	public static enum Status {  
		  PARTICIPANT, INVITED, NO  
	} 
	
	public static String PUBLIC_TOKEN="00000000000000000000";
	public static String token = "";
	
	public static String TESTPREFIX = "http://115.28.211.238/v0/";
	public static String PRODUCTIONPREFIX="http://115.29.141.31/v0/";
	public static String LOGIN = PRODUCTIONPREFIX+"user/login/";
	public static String EVENT_LIST = PRODUCTIONPREFIX+"event/list/";
	public static String USER_INFO = PRODUCTIONPREFIX+"user/info/";
	public static String EVENT_INFO	= PRODUCTIONPREFIX + "event/info/";

}
