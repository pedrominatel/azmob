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

	public final short DEBUG = 0;
	public final short ERROR = 1;
	public final short WARNING = 2;
	public final short INFO = 3;
	
	Filesys logFile = new Filesys();

	public void log(String module, short level, String strLog, boolean outputFile) {
		
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
			logFile.fsSys_createLog(strLog, level);
		}
		
	}
}
