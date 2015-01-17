package com.thinken.azmobmeter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
	}
	
	//NCS-2015
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //Handle the back button
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	        //Ask the user if they want to quit
	        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(R.string.exit_question)
	        .setMessage(R.string.exit_warning)
	        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	MainMenu.this.finish();    
	            }
	        })
	        .setNegativeButton(R.string.no, null)
	        .show();

	        return true;
	    }
	    else {
	        return super.onKeyDown(keyCode, event);
	    }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
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
	
	public void connectBluetooth(View view) 
	{
	    Intent intent = new Intent(MainMenu.this, BluetoothManager.class);
	    startActivity(intent);
	}
	
	public void about(View view) 
	{
		//TODO: implement popup info
	}
	
	public void exit(View view) 
	{
		MainMenu.this.finish();
	}
	
	public void todo(View view) 
	{
	      Toast.makeText(getApplicationContext(),R.string.todo, 0).show();
	}
	
	public void openReadouts(View view) {
		Intent intents = new Intent(MainMenu.this,MeterReadout.class);
		startActivity(intents);
	}
	
}
