package com.thinken.azmobmeter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

public class Filesys extends Activity {

	private String tag = "Filesys";

	public final String READOUTS_FOLDER = "/AZmob/readouts";
	public final String METER_OBJECTS_FOLDER = "/AZmob/meter";
	public final String UPLOADED_FOLDER = "/AZmob/uploaded";
	public final String LOG_FOLDER = "/AZmob/logs";

	public final short DEBUG = 0;
	public final short ERROR = 1;
	public final short WARNING = 2;
	public final short INFO = 3;

	public File fsSys_getExtStorageDir() {
		return android.os.Environment.getExternalStorageDirectory();
	}

	public File fsSys_getExtStorageDir(String folder) {

		File dir = new File(
				android.os.Environment.getExternalStorageDirectory() + folder);
		return dir;
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
		long free = (freeSpace * 100) / totalSpace;

		Log.i(tag, sdcard.getAbsolutePath() + ": Total free space: " + free
				+ "%");

		// compare the free space in %
		if (free < 5) {
			// XXX warn free space at sdcard
			return true;
		}

		return false;
	}

	public void fsSys_createFs() throws IOException {

		// Creates FS
		Log.i(tag, "Creating File System");
		// Check if the directories exists otherwise create it

		File path = fsSys_getExtStorageDir();

		// create the directory to store the readouts folders and files
		fsSys_createFolder(path, READOUTS_FOLDER);
		// create the directory to store the XML files with OBIS codes
		fsSys_createFolder(path, METER_OBJECTS_FOLDER);
		// create the directory to store the uploaded files (backup folder)
		fsSys_createFolder(path, UPLOADED_FOLDER);
		// log files folder
		fsSys_createFolder(path, LOG_FOLDER);

		Log.i(tag, "File System Created");

	}

	public String fsSys_createFolder(File path, String folder) {

		File dir = new File(path.getAbsolutePath() + folder);
		// Check if the directory already exists
		if (dir.isDirectory()) {
			Log.i(tag, "Folder already exists!");
		} else {
			dir.mkdirs();
		}

		return dir.getAbsolutePath();
	}

	public boolean fsSys_delete(File file) {

		if (file.delete()) {
			Log.i(tag, "File " + file.getName() + " deleted!");
			return true;
		}

		Log.i(tag, "File " + file.getName() + " doesn't exists or invalid!");
		return false;
	}

	public boolean fsSys_checkFile(String filePath) {
		
		File file = new File(filePath);
		
		if(!file.exists())
			return false;
		
		return true;
	}
	
	public void fsSys_copyFile(InputStream src, String dst) throws IOException {
	    
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = src.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    src.close();
	    out.close();
	}
	
	public void fsSys_copyFile(File src, File dst) throws IOException {
	    
		InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	public void fsSys_createLog(String log, short level) {

		String levelStr = "NONE";

		switch (level) {
		case DEBUG:
			levelStr = "DEBUG";
			break;
		case ERROR:
			levelStr = "ERROR";
			break;
		case WARNING:
			levelStr = "WARNING";
			break;
		case INFO:
			levelStr = "INFO";
			break;
		default:
			levelStr = "UNKNOWN";
			break;
		}

		File logFolder = fsSys_getExtStorageDir(LOG_FOLDER);

		File file = new File(logFolder, fsSys_dateStamp() + "_log.txt");

		if (!file.exists()) {
			try {
				FileOutputStream fileStream = new FileOutputStream(file);
				PrintWriter fileWrite = new PrintWriter(fileStream);
				fileWrite.println(fsSys_timeStamp() + ": " + levelStr
						+ " >> File Created");
				fileWrite.flush();
				fileWrite.close();
				fileStream.close();
				Log.i(tag, "Log File created!");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				Log.i(tag, "Error :" + e.toString());
			}
		}

		try {
			FileOutputStream fileStream = new FileOutputStream(file, true);
			PrintWriter fileWrite = new PrintWriter(fileStream);
			fileWrite.println(fsSys_timeStamp() + ": " + levelStr + " >> "
					+ log);
			fileWrite.flush();
			fileWrite.close();
			fileStream.close();
		} catch (FileNotFoundException e) {
			Log.i(tag, "File not found! " + e.toString());
		} catch (IOException e) {
			Log.i(tag, "Error :" + e.toString());
		}
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
