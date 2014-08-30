package com.thinken.azmobmeter.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class DeviceInfo {

	public String deviceInfo_getIMEI(Context c) {
		//Returns the IMEI code
		TelephonyManager telephonyManager;
		telephonyManager = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();

	}
	
	public String deviceInfo_getSubId(Context c) {
		//Returns the Subscriber Id
		TelephonyManager telephonyManager;
		telephonyManager = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSubscriberId();

	}
	
}
