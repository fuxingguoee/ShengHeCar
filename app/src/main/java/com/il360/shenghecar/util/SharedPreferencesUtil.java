package com.il360.shenghecar.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.il360.shenghecar.common.MyApplication;

public class SharedPreferencesUtil {
	public static void setOpenFirstTime(boolean status) {
		try {
			SharedPreferences sp = MyApplication.getContextObject().getSharedPreferences("SP", MyApplication.getContextObject().MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean("bananashop_isopenfirsttime", status);
			editor.commit();
		} catch (Exception e) {
			Log.e("SharedPreferencesUtil", "setOpenFirstTime", e);
		}
	}
	
	public static boolean getOpenFirstTime() {
		boolean isFirst = false ;
		try {
			SharedPreferences sp = MyApplication.getContextObject().getSharedPreferences("SP", MyApplication.getContextObject().MODE_PRIVATE);
			isFirst = sp.getBoolean("bananashop_isopenfirsttime", true);
		} catch (Exception e) {
			Log.e("SharedPreferencesUtil", "getOpenFirstTime", e);
		}
		return isFirst;
	}

	public static void setLastLoginName(String loginName) {
		try {
			SharedPreferences sp = MyApplication.getContextObject().getSharedPreferences("SP", MyApplication.getContextObject().MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("bananashop_lastloginname", loginName);
			editor.commit();
		}catch (Exception e) {
			Log.e("SharedPreferencesUtil", "setLastLoginName", e);
		}
	}

	public static String getLastLoginName() {
		String loginName = "";
		try {
			SharedPreferences sp = MyApplication.getContextObject().getSharedPreferences("SP", MyApplication.getContextObject().MODE_PRIVATE);
			loginName = sp.getString("bananashop_lastloginname", "");
		} catch (Exception e) {
			Log.e("SharedPreferencesUtil", "getLastLoginName", e);
		}
		return loginName;
	}


	public static void setUserIDNo(String userIDNo) {//身份证号查询还款信息
		try {
			SharedPreferences sp = MyApplication.getContextObject().getSharedPreferences("SP", MyApplication.getContextObject().MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("bananashop_useridno", userIDNo);
			editor.commit();
		}catch (Exception e) {
			Log.e("SharedPreferencesUtil", "setUserIDNo", e);
		}
	}

	public static String getUserIDNo() {
		String userIDNo = "";
		try {
			SharedPreferences sp = MyApplication.getContextObject().getSharedPreferences("SP", MyApplication.getContextObject().MODE_PRIVATE);
			userIDNo = sp.getString("bananashop_useridno", "");
		} catch (Exception e) {
			Log.e("SharedPreferencesUtil", "getUserIDNo", e);
		}
		return userIDNo;
	}

	public static void setUserName(String userName) {//
		try {
			SharedPreferences sp = MyApplication.getContextObject().getSharedPreferences("SP", MyApplication.getContextObject().MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("bananashop_username", userName);
			editor.commit();
		}catch (Exception e) {
			Log.e("SharedPreferencesUtil", "setUserName", e);
		}
	}

	public static String getUserName() {
		String userName = "";
		try {
			SharedPreferences sp = MyApplication.getContextObject().getSharedPreferences("SP", MyApplication.getContextObject().MODE_PRIVATE);
			userName = sp.getString("bananashop_username", "");
		} catch (Exception e) {
			Log.e("SharedPreferencesUtil", "getUserName", e);
		}
		return userName;
	}
}
