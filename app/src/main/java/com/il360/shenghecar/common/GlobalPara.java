package com.il360.shenghecar.common;

import com.il360.shenghecar.model.MyInfo;
import com.il360.shenghecar.model.address.UserAddress;
import com.il360.shenghecar.model.coupon.UserCoupon;
import com.il360.shenghecar.model.home.MySwitch;
import com.il360.shenghecar.model.hua.CardConfig;
import com.il360.shenghecar.model.hua.OutUserBank;
import com.il360.shenghecar.model.user.OutUserRz;

import java.util.List;

public class GlobalPara {

	public static Boolean canResubmitPhone = false;
	public static Boolean canResubmitFund = false;

	public static Boolean getCanResubmitPhone() {
		return canResubmitPhone;
	}

	public static Boolean getCanResubmitFund() {
		return canResubmitFund;
	}
	
	public static OutUserRz outUserRz = null;
	public static MyInfo myInfo = null;
	public static OutUserBank outUserBank = null;

	public static List<String> appNameList = null;
	public static List<CardConfig> cardConfigList = null;
	public static List<UserAddress> userAddressList = null;
	public static List<MySwitch> mySwitchList = null;
	public static List<UserCoupon> userCouponList = null;
	public static Boolean canAutoVerified = false;
	
	public static int remainTimes = 0;
	public static int maxTimes = 0;
	
	public static double defaultRate = 0.0;
	
	public static String cosAppID = null;
	public static String cosName = null;
	public static String cosEndPoint = null;

	public static String discoverHomeUrl = null;
	public static String telephone = null;//公司电话
	public static String insuranceTele = null;//保险电话
	public static String telephoneBookURL = null;//公司联系方式
	public static String getTelephoneBookURL() {
		return telephoneBookURL;
	}

	public static void setTelephoneBookURL(String telephoneBookUrl) {
		GlobalPara.telephoneBookURL = telephoneBookUrl;
	}

	public static String getCosAppID(){
		return cosAppID;
	}
	
	public static String getCosName(){
		return cosName;
	}
	
	public static String getCosEndPoint(){
		return cosEndPoint;
	}
	
	public static String getDiscoverHomeUrl(){
		return discoverHomeUrl;
	}

	public static OutUserRz getOutUserRz() {
		return outUserRz;
	}

	public static OutUserBank getOutUserBank() {
		return outUserBank;
	}
	public static MyInfo getMyInfo() {
		return myInfo;
	}
	public static List<String> getAppNameList() {
		return appNameList;
	}
	public static List<CardConfig> getCardConfigList() {
		return cardConfigList;
	}
	
	public static List<UserAddress> getUserAddressList() {
		return userAddressList;
	}
	
	public static int getRemainTimes(){
		return remainTimes;
	}
	
	public static int getMaxTimes(){
		return maxTimes;
	}
	
	public static double getDefaultRate(){
		return defaultRate;
	}
	
	public static List<MySwitch> getMySwitchList() {
		return mySwitchList;
	}
	
	public static List<UserCoupon> getUserCouponList() {
		return userCouponList;
	}
	
	public static String getTelephone(){
		return telephone;
	}

	public static String getInsuranceTele(){
		return insuranceTele;
	}

	public static Boolean getCanAutoVerified() {
		if(GlobalPara.getMySwitchList() != null && GlobalPara.getMySwitchList().size() > 0){
			for (int i = 0; i < GlobalPara.getMySwitchList().size(); i++) {
				if(GlobalPara.getMySwitchList().get(i).getSwitchKey().equals("faceRecognition")
						&& GlobalPara.getMySwitchList().get(i).getSwitchValue().equals("ON")){
					return true;
				}
			}
		}
		return false;
	}

	public static void clean() {
		canResubmitPhone = false;
		canResubmitFund = false;
		outUserRz = null;
		appNameList = null;
		outUserBank = null;
		cardConfigList = null;
		userAddressList = null;
		mySwitchList = null;
		userCouponList = null;
		remainTimes = 0;
		maxTimes = 0;
	}
}
