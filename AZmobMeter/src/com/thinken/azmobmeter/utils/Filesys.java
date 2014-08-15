package com.thinken.azmobmeter.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class Filesys extends Activity{

	private String tag = "Filesys";
	
	public void fsSys_createFs(Context c) throws IOException {
		//Creates FS
		Log.i(tag, "Creating FS");
		//Check if the directories exists otherwise create it
		//create the directory to store the readouts folders and files
		fsSys_createFolder(c, "readouts");
		//create the directory to store the XML files with OBIS codes
		fsSys_createFolder(c, "meter");
		//create the directory to store the uploaded files (backup folder)
		fsSys_createFolder(c, "uploaded");
	}
	
	public void fsSys_createFile(Context c, String directory, String filename) throws IOException {
		File dir = c.getDir(directory, Context.MODE_PRIVATE); //Creating an internal dir;
		File file = new File(dir, filename);
		FileOutputStream outS = new FileOutputStream(file);
		outS.close();
	}
	
	public void fsSys_writeFile(Context c, String directory, String filename, String buffered) throws IOException {
		File dir = c.getDir(directory, Context.MODE_PRIVATE); //Creating an internal dir;
		File file = new File(dir, filename);
		
		FileOutputStream outS = new FileOutputStream(file);
		outS.write(buffered.getBytes());
		outS.close();
	}
	
	public boolean fsSys_deleteFile(Context c, String directory, String filename) {
		File dir = c.getDir(directory, Context.MODE_PRIVATE);
		File file = new File(dir, filename);
		return file.delete();
	}

	public boolean fsSys_createFolder(Context c, String folder) {
		File dir = c.getDir(folder, Context.MODE_PRIVATE);
		return dir.mkdirs();
	}
	
	public boolean fsSys_deleteFolder(Context c, String folder) {
		File dir = c.getDir(folder, Context.MODE_PRIVATE);
		return dir.delete();
	}
	
}
