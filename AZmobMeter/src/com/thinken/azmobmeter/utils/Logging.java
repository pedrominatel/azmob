/**
 * Created by Pedro Minatel
 * pminatel@gmail.com
 */
package com.thinken.azmobmeter.utils;

import android.util.Log;
import com.thinken.azmobmeter.utils.Filesys;

/**
 * @author pminatel
 *
 */
public class Logging {

	public static final int DEBUG = 0;
	public static final int ERROR = 1;
	public static final int WARNING = 2;
	public static final int INFO = 3;
	
	Filesys logFile = new Filesys();

	public void log(String module, int level, String strLog, boolean outputFile) {
		
		switch (level) {
		case DEBUG:
			Log.d(module, strLog);
			break;
		case ERROR:
			Log.e(module, strLog);
			break;
		case WARNING:
			Log.w(module, strLog);
			break;
		case INFO:
			Log.i(module, strLog);
			break;
		default:
			break;
		}
		
		if(outputFile){
			
		}
		
	}
}
