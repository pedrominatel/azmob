package com.thinken.azmobmeter;


import java.io.IOException;

import com.thinken.azmobmeter.utils.DeviceInfo;
import com.thinken.azmobmeter.utils.Filesys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

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
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash_screen);

        Filesys fsys = new Filesys();
        
        if (fsys.fsSys_checkExternalMedia()){
        	//TODO Warn window
        	//fsys.writeToSDFile();
        	//fsys.readRaw();
        }
        
        try {
			fsys.fsSys_createFs(SplashScreen.this.getApplicationContext());
			Log.i(tag, "FS created...");
		} catch (IOException e) {
			Log.i(tag, "FS Error" + e.toString());
			
		}
        
        DeviceInfo dInfo = new DeviceInfo();
        Log.i(tag, "IMEI " + dInfo.getIMEI(SplashScreen.this.getApplicationContext()));
        Log.i(tag, "Subscribed Id " + dInfo.getSubId(SplashScreen.this.getApplicationContext()));
        
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainMenu.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 
}
