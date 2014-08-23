package com.thinken.azmobmeter;

import java.io.IOException;

import com.thinken.azmobmeter.utils.DeviceInfo;
import com.thinken.azmobmeter.utils.Filesys;
import com.thinken.azmobmeter.utils.Logging;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
	private static int SPLASH_TIME_OUT = 4000;
	private String tag = "SplashScreen";
	private static final int REQUEST_ENABLE_BT = 1;

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

		Filesys fsys = new Filesys();
		Logging log = new Logging();

		if (fsys.fsSys_checkExtMedia()) {
			// TODO Warn window
			if (fsys.fsSys_checkExtMediaFreeSize())
				alert("Alerta de Memoria", "Nivel de memoria baixa!");
		}

		//CheckRegistration();
		//CheckExternalMemory();
		//CheckFoldersCreation();
		CheckBlueToothState();

		try {
			fsys.fsSys_createFs();
			Log.i(tag, "FS created...");
		} catch (IOException e) {
			Log.i(tag, "FS Error" + e.toString());

		}

		Log.i(tag, fsys.fsSys_timeStamp());

		log.log(tag, log.INFO, "LOOOOOOOG!!", true);

		DeviceInfo dInfo = new DeviceInfo();
		Log.i(tag, "IMEI "	+ dInfo.getIMEI(SplashScreen.this.getApplicationContext()));
		Log.i(tag, "Subscribed Id "	+ dInfo.getSubId(SplashScreen.this.getApplicationContext()));

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

		BluetoothAdapter myBTadapter = BluetoothAdapter.getDefaultAdapter();
		if (myBTadapter == null) {
			Toast.makeText(getApplicationContext(),	"Device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
		}

		if (!myBTadapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
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
				Toast.makeText(getApplicationContext(),"BlueTooth is now Enabled", Toast.LENGTH_LONG).show();
				StartApplication();
			}
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(),"Error occured while enabling.Leaving the application..",Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	private void alert(String title, String msg) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
				.setNeutralButton("Ok", null).show();
	}

}
