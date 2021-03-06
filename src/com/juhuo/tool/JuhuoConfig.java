package com.juhuo.tool;

public class JuhuoConfig {
	public static int HEIGHT;
	public static int WIDTH;
	public static String EVENTLISTFILE = "juhuo_event_list";// for public event list
	public static String EVENTINFO = "juhuo_event_info_";
	public static String EVENTCOMMENT = "juhuo_event_comment_";
	public static String EVENTLISTSPECIFIC = "juhuo_event_specific_list_";
	public static String USERINFO = "juhuo_user_info_";
	public static String APP_ID_WECHAT = "wxb4d0cc21ea52f7c4";
	public static String channelId = "C53aa42ae78f67";
	/** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
    public static final String APP_KEY      = "3834437266";

    /** 
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     */
    public static final String REDIRECT_URL = "http://www.sina.com";

    /**
     * WeiboSDKDemo 应用对应的权限，第三方开发者一般不需要这么多，可直接设置成空即可。
     * 详情请查看 Demo 中对应的注释。
     */
    public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
	
	public static int INVI_NULL=0;
	public static int INVI_YES=1;
	public static int INVI_NO=2;
	public static int INVI_MAYBE=3;
	public static int INVI_APPLY=4;
	public static int INVI_ORGANIZER=5;
	public static int INVI_REJECTOR=6;
	public static String[] STATUS = {"确定参加","确定参加","缺席人员","可能参加","点击审批","组织者","不参加","拒绝申请"};
	public static enum Status {  
		  PARTICIPANT, INVITED, NO,APPLY,ORGANIZER
	} 
	
	public static String PUBLIC_TOKEN="00000000000000000000";
	public static String token = "";
	public static Integer userId=0;
	public static String userName="";
	public static boolean notification = true; 
	
//	public static String PRODUCTIONPREFIX = "http://115.28.211.238/v0/";
	public static String PRODUCTIONPREFIX="http://115.29.141.31/v0/";
	public static String REGISTER = PRODUCTIONPREFIX+"user/create/";
	public static String LOGIN = PRODUCTIONPREFIX+"user/login/";
	public static String LOGOUT = PRODUCTIONPREFIX+"user/logout/";
	public static String USER_INFO = PRODUCTIONPREFIX+"user/info/";
	public static String USER_ICON = PRODUCTIONPREFIX+"user/icon/";
	public static String CHANGE_PASSWORD = PRODUCTIONPREFIX+"user/chgpass/";
	public static String USER_MODIFY = PRODUCTIONPREFIX+"user/modify/";
	public static String ACTIVE_ACCOUNT = PRODUCTIONPREFIX+"user/applyactivate/";
	public static String USER_FEEDBACK = PRODUCTIONPREFIX+"user/feedback/";
	
	public static String EVENT_CREATE = PRODUCTIONPREFIX+"event/create/";
	public static String EVENT_UPDATE = PRODUCTIONPREFIX+"event/modify/";
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
	public static String EVENT_COMMENT = PRODUCTIONPREFIX+"event/comment/";
	
	public static String CONTACT_CREATE = PRODUCTIONPREFIX+"contact/create/";
	public static String CONTACT_LIST =  PRODUCTIONPREFIX+"contact/list/";
	public static String CONTACT_JUDGE = PRODUCTIONPREFIX+"contact/judge/";
	public static String CONTACT_EVENT = PRODUCTIONPREFIX+"event/contactlist/";
	public static String CONTACT_DELETE = PRODUCTIONPREFIX+"contact/delete/";
	
	public static String COMMON_UPLOADPHOTO = PRODUCTIONPREFIX+"common/uploadphoto/";

}
