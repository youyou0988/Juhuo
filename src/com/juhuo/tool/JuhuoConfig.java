package com.juhuo.tool;

public class JuhuoConfig {
	public static int HEIGHT;
	public static int WIDTH;
	public static String EVENTLISTFILE = "juhuo_event_list";// for public event list
	public static String EVENTINFO = "juhuo_event_info_";
	public static String EVENTCOMMENT = "juhuo_event_comment_";
	public static String EVENTLISTSPECIFIC = "juhuo_event_specific_list_";
	public static String USERINFO = "juhuo_user_info_";
	
	public static int INVI_NULL=0;
	public static int INVI_YES=1;
	public static int INVI_NO=2;
	public static int INVI_MAYBE=3;
	public static int INVI_APPLY=4;
	public static int INVI_ORGANIZER=5;
	public static int INVI_REJECTOR=6;
	public static String[] STATUS = {"确定参加","确定参加","缺席人员","可能参加","点击审批","组织者","不参加"};
	public static enum Status {  
		  PARTICIPANT, INVITED, NO,APPLY
	} 
	
	public static String PUBLIC_TOKEN="00000000000000000000";
	public static String token = "";
	public static Integer userId;
	
	public static String PRODUCTIONPREFIX = "http://115.28.211.238/v0/";
//	public static String PRODUCTIONPREFIX="http://115.29.141.31/v0/";
	public static String LOGIN = PRODUCTIONPREFIX+"user/login/";
	public static String LOGOUT = PRODUCTIONPREFIX+"user/logout/";
	public static String USER_INFO = PRODUCTIONPREFIX+"user/info/";
	public static String USER_ICON = PRODUCTIONPREFIX+"user/icon/";
	public static String CHANGE_PASSWORD = PRODUCTIONPREFIX+"user/chgpass/";
	public static String USER_MODIFY = PRODUCTIONPREFIX+"user/modify/";
	public static String ACTIVE_ACCOUNT = PRODUCTIONPREFIX+"user/applyactivate/";
	
	public static String EVENT_CREATE = PRODUCTIONPREFIX+"event/create/";
	public static String EVENT_LIST = PRODUCTIONPREFIX+"event/list/";
	public static String EVENT_LIST_INCREMENTAL = PRODUCTIONPREFIX+"event/listincremental/";
	public static String EVENT_DELETE = PRODUCTIONPREFIX+"event/delete/";
	public static String EVENT_INFO	= PRODUCTIONPREFIX + "event/info/";
	public static String EVENT_COMMENT_LIST = PRODUCTIONPREFIX+"event/listcomment/";
	public static String EVENT_INVITE = PRODUCTIONPREFIX+"event/invite/";
	public static String EVENT_REMIND = PRODUCTIONPREFIX+"event/remind/";
	public static String EVENT_APPLY = PRODUCTIONPREFIX+"event/apply/";
	public static String EVENT_CONFIRM = PRODUCTIONPREFIX+"event/confirm/";
	public static String EVENT_APPROVE = PRODUCTIONPREFIX+"event/approve/";
	
	public static String CONTACT_CREATE = PRODUCTIONPREFIX+"contact/create/";
	
	public static String COMMON_UPLOADPHOTO = PRODUCTIONPREFIX+"common/uploadphoto/";

}
