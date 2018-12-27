package com.il360.shenghecar.common;

import android.os.Environment;

public class Variables {	

	public static Class<?> ACTIVITY_INTNET_CLASS;
	//摘要认证
	public static final String WEBSERVICE_USER = "carFinancecaomeiwangluo695478";
	public static final String WEBSERVICE_PWD = "caomeiwangluo897541";
	
	public static final String LOGSERVICE_USER = "appLog";
	public static final String LOGSERVICE_PWD = "99999zj360cbb";
	
	// QQ(分享)
	/**同时要修改mainfest.xml文件
	 * <activity  
    android:name="com.tencent.tauth.AuthActivity"**/
	public static final String QQ_APPID = "1106698792";
	public static final String QQ_APPSECRET = "LedbuzBrIjE8cgFR";
	
	// 微信(分享)
	public static final String WX_APPID = "wx4857f9bcc51f7828";
	public static final String WX_APPSECRET = "44be288768c4e0424a1827416f3c5fe0";

	//魔蝎配置
	//public static final String MX_APIKEY = "d5199603865544efbfe236f3926a54c1";//测试
	public static final String MX_APIKEY = "411142d098524df19c86f5ec1a2498f6";//正式
	public static final String MX_PLATFORM = "5";//app平台号

	//同盾配置
	public static final String MH_PARTNER_CODE = "cmsc_mohe";
	public static final String MH_PARTNER_KEY = "5303afa68a284da688e4c99b8eb93eaa";

	//cos保存本地绝对路径
	public static final String COS_TXYUSERPIC_URL = "/storage/sdcard0/DCIM/Camera/";
	
	public static final String PUBLIC_UTILITY_MAC_KEY  = "C1C8EA2D2044729396CC12AB33C9AFD3";
	
	/*********************************pic Base Url***************************************/
	
	public static final String APP_BASE_URL = URLFactory.DIANXIN_URL.replace("services/", "");
	
	public static final String APP_BASE_URL_NO =URLFactory.DIANXIN_URL.replace("/services/", "");
	// 用户头像
	public static final String USER_PICTURE_URL = APP_BASE_URL + "files/upload/";
	// 卡服务——银行卡.png
	public static final String CARG_BANK_PICTURE_URL = APP_BASE_URL + "files/bankpic/";
	// 卡服务——信用卡.jpg
	public static final String CARG_CREDIT_PICTURE_URL = APP_BASE_URL + "files/creditpic/";

	// T0验证信用卡图片
	public static final String TO_PICTURE_URL = APP_BASE_URL + "files/card/";


    public static final String SDPATH = Environment.getExternalStorageDirectory().toString();
    public static final String APP_SDPATH = SDPATH + "/InfoLife";
    
    // 缓存目录
    public static final String APP_CACHE_SDPATH = APP_SDPATH + "/cache";
    
    
	public static final int EDUCATION_CODE_SECCESS = 1001;// 学历
	public static final int MARITAL_STATUS_CODE_SECCESS = 1002;// 婚姻状况
	public static final int ADDRESS_STATUS_CODE_SECCESS = 1003;// 省市区

}
