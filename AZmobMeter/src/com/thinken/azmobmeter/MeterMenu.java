package com.thinken.azmobmeter;

import org.openmuc.jdlms.client.IClientConnection;

import com.thinken.azmobmeter.driver.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MeterMenu extends Activity {
	
	private String btAddress = "";
	DriverTest tst = new DriverTest();
	IClientConnection conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    btAddress = extras.getString("btAddress");
			Log.i("CONNECTION", "Starting Connection");
			
			try {
				conn = tst.connect(btAddress);
			} catch (Exception e) {
				// TODO: handle exception
			    Log.i("CONNECTION", "Error: "+e.toString());
				Toast.makeText(getApplicationContext(),"Error: "+e.toString(), 0).show();
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meter_read, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void clearAlarms(View view) {
		tst.setActionEx(conn);
		
		final Button btn = (Button)findViewById(R.id.bt_clearAlarms);
		btn.setEnabled(false);
		
	}
	
	public void disconnect(View view) {
		
		tst.disconnect(conn);
		
		Intent intent = new Intent(MeterMenu.this,MainMenu.class);
		startActivity(intent);
	}
	
	public void openReadouts(View view) {
		Intent intent = new Intent(MeterMenu.this,MeterReadout.class);
		startActivity(intent);
	}
	
	public void todo(View view) 
	{
	      Toast.makeText(getApplicationContext(),R.string.todo, 0).show();
	}
	
}
