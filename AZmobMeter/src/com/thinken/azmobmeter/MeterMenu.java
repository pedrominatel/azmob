package com.thinken.azmobmeter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MeterMenu extends Activity {
	
	BluetoothDevice btDevice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter);
		
		BluetoothManager app = (BluetoothManager) getApplicationContext();
		btDevice = app.getData();
		
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
	
	public void openReadouts(View view) {
		Intent intent = new Intent(MeterMenu.this,MeterReadout.class);
		startActivity(intent);
	}
	
	public void todo(View view) 
	{
	      Toast.makeText(getApplicationContext(),R.string.todo, 0).show();
	}
	
}
