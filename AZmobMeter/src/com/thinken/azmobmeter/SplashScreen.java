package com.thinken.azmobmeter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.xmlpull.v1.sax2.Driver;

import com.thinken.azmobmeter.driver.DriverUtils;
import com.thinken.azmobmeter.utils.DeviceInfo;
import com.thinken.azmobmeter.utils.Filesys;
import com.thinken.azmobmeter.utils.Logging;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashScreen extends Activity {
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 2000;
	private String tag = "SplashScreen";
	private static final int REQUEST_ENABLE_BT = 1;

	Filesys fsys = new Filesys();
	Logging log = new Logging();
	
	/** Called when the activity is first created. */
	BluetoothAdapter bluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash_screen);		
		
		// Check if the application is registred
		CheckRegistration();
		// Check free space at external memory
		CheckExternalMemory();
		// Check folders creation
		CheckFoldersCreation();
		// Check Bluetooth radio
		CheckBlueToothState();

	}

	/**
	 * 
	 */
	private void CheckRegistration() {
		// TODO Auto-generated method stub
		log.log(tag, log.INFO, fsys.fsSys_timeStamp(), true);
		DeviceInfo dInfo = new DeviceInfo();
		log.log(tag,
				log.INFO,
				"IMEI "
						+ dInfo.deviceInfo_getIMEI(SplashScreen.this
								.getApplicationContext()), true);
		log.log(tag,
				log.INFO,
				"Subscribed Id "
						+ dInfo.deviceInfo_getSubId(SplashScreen.this
								.getApplicationContext()), true);
	}

	/**
	 * 
	 */
	private void CheckFoldersCreation() {
		// TODO Auto-generated method stub
		try {
			fsys.fsSys_createFs();
			log.log(tag, log.INFO, "FS created", true);

			CheckMeterDataFiles();

		} catch (IOException e) {
			log.log(tag, log.ERROR, "FS Error" + e.toString() + e.toString(),
					true);
		}
	}

	/**
	 * 
	 */
	private void CheckMeterDataFiles() {
		// TODO Auto-generated method stub

		try {

			Field[] fields = R.raw.class.getFields();
			for (int count = 0; count < fields.length; count++) {

				int rid = fields[count].getInt(fields[count]);

				// Use that if you just need the file name
				String filename = fields[count].getName();

				// Use this to load the file
				try {
					Resources res = getResources();
					InputStream in = res.openRawResource(rid);

					String file = fsys.fsSys_getExtStorageDir(
							fsys.METER_OBJECTS_FOLDER +"/"+ filename + ".xml")
							.getAbsolutePath();

					if (!fsys.fsSys_checkFile(file)) {
						try {
							fsys.fsSys_copyFile(in, file);
						} catch (IOException e) {
							log.log(tag, log.ERROR, "Copy Error" + e.toString()
									+ e.toString(), true);
						}
					}
				} catch (Exception e) {
					// log error
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 
	 */
	private void CheckExternalMemory() {
		// TODO Auto-generated method stub
		log.log(tag, log.INFO, "Checking External Memory", true);
		if (fsys.fsSys_checkExtMedia()) {
			// TODO Warn window
			if (fsys.fsSys_checkExtMediaFreeSize()) {
				alert("Alerta de Memoria", "Nivel de memoria baixa!");
				log.log(tag, log.WARNING,
						"Alerta de Memoria - Nivel de memoria baixa!", true);
			}
		}
	}

	/**
	 * 
	 */
	private void StartApplication() {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(SplashScreen.this, MainMenu.class);
				startActivity(i);
				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

	private void CheckBlueToothState() {

		log.log(tag, log.INFO, "Checking Bluetooth state", true);

		BluetoothAdapter myBTadapter = BluetoothAdapter.getDefaultAdapter();
		if (myBTadapter == null) {
			Toast.makeText(getApplicationContext(),
					"Device doesn't support Bluetooth", Toast.LENGTH_LONG)
					.show();
			log.log(tag, log.INFO, "Device doesn't support Bluetooth", true);
		}

		if (!myBTadapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			StartApplication();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(getApplicationContext(),
						"BlueTooth is now Enabled", Toast.LENGTH_LONG).show();
				StartApplication();
			}
			if (resultCode == RESULT_CANCELED) {
				log.log(tag,
						log.ERROR,
						"Error occured while enabling.Leaving the application...",
						true);
				finish();
			}
		}
	}

	private void alert(String title, String msg) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
				.setNeutralButton("Ok", null).show();
	}

}
