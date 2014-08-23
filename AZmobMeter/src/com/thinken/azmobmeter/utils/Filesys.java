package com.thinken.azmobmeter.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

public class Filesys extends Activity {

	private String tag = "Filesys";
	
	public static final String READOUTS_FOLDER = "/AZmob/readouts";
	public static final String METER_OBJECTS_FOLDER = "/AZmob/meter";
	public static final String UPLOADED_FOLDER = "/AZmob/uploaded";
	public static final String LOG_FOLDER = "/AZmob/logs";	

	public File fsSys_getExtStorageDir() {
		return android.os.Environment.getExternalStorageDirectory();
	}

	public boolean fsSys_checkExtMedia() {

		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// Can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// Can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Can't read or write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		if (mExternalStorageAvailable && mExternalStorageWriteable) {
			Log.i(tag, "External Media Available for RW");
			return true;
		}
		Log.i(tag, "External Media NOT FULL Available");
		return false;
	}
	
	public boolean fsSys_checkExtMediaFreeSize() {
		
		File sdcard = fsSys_getExtStorageDir();
		
		long totalSpace = sdcard.getTotalSpace();
		long freeSpace = sdcard.getFreeSpace();
		long free = (freeSpace*100)/totalSpace;
		
		Log.i(tag, sdcard.getAbsolutePath()+": Total free space: "+free+"%");
		
		//compare the free space in %
		if (free<5) {
			//XXX warn free space at sdcard
			return true;
		}
		
		return false;
	}

	public void fsSys_createFs() throws IOException {
		
		// Creates FS
		Log.i(tag, "Creating File System");
		// Check if the directories exists otherwise create it

		File path = fsSys_getExtStorageDir();
		Log.i(tag, "External file system root: " + path);

		// create the directory to store the readouts folders and files
		if(!fsSys_createFolder(path, READOUTS_FOLDER))
			Log.i(tag, "Folder already exists!");
		// create the directory to store the XML files with OBIS codes
		if(!fsSys_createFolder(path, METER_OBJECTS_FOLDER))
			Log.i(tag, "Folder already exists!");
		// create the directory to store the uploaded files (backup folder)
		if(!fsSys_createFolder(path, UPLOADED_FOLDER))
			Log.i(tag, "Folder already exists!");
		//log files folder
		if(!fsSys_createFolder(path, LOG_FOLDER))
			Log.i(tag, "Folder already exists!");
		
		Log.i(tag, "File System Created");
		
	}

	public boolean fsSys_createFolder(File path, String folder) {
		
		File dir = new File(path.getAbsolutePath() + folder);
		//Check if the directory already exists
		if (!dir.isDirectory())
			return dir.mkdirs();
		
		return false;
	}

	public boolean fsSys_delete(File file) {
		
		if (file.delete()) {
			Log.i(tag, "File " + file.getName() + " deleted!");
			return true;
		}
		
		Log.i(tag, "File " + file.getName() + " doesn't exists or invalid!");
		return false;
	}

	public void fsSys_createLog() {
		
		
		
		
	}
	
	public String fsSys_timeStamp() {
		
		   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		   Date date = new Date(System.currentTimeMillis());
		   
		   return dateFormat.format(date);
	}
	
	public String fsSys_dateStamp() {
		
		   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		   Date date = new Date(System.currentTimeMillis());
		   
		   return dateFormat.format(date);
	}
	
}
