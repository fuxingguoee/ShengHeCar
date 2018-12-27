package com.il360.shenghecar.common;

import android.Manifest;

import java.util.ArrayList;

public class MyPermissions {
	
    /**
     * 必要全选,如果这几个权限没通过的话,就无法使用APP
     */
	public static final ArrayList<String> FORCE_REQUIRE_PERMISSIONS = new ArrayList<String>() {
		{
			add(Manifest.permission.CAMERA);
			add(Manifest.permission.READ_EXTERNAL_STORAGE);
			add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			add(Manifest.permission.CALL_PHONE);
			add(Manifest.permission.READ_CONTACTS);
			add(Manifest.permission.RECEIVE_SMS);
			add(Manifest.permission.READ_SMS);
			add(Manifest.permission.READ_PHONE_STATE);
//			add(Manifest.permission.ACCESS_COARSE_LOCATION);//通过WiFi或移动基站的方式获取用户错略的经纬度信息，定位精度大概误差在30~1500米
//			add(Manifest.permission.ACCESS_FINE_LOCATION);//通过GPS芯片接收卫星的定位信息，定位精度达10米以内
		}
	};
}
