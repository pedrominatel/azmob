package com.thinken.azmobmeter;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class BluetoothManager extends Activity implements OnItemClickListener {

	private ArrayAdapter<String> listAdapter;
	private ListView listView;
	private BluetoothAdapter btAdapter; // bt adapter contains the radio adapter
	BluetoothDevice btDevice; // bt device contains the selected bt

	Set<BluetoothDevice> devicesArray;
	private ArrayList<String> pairedDevices;
	private ArrayList<BluetoothDevice> devices;
	protected static final int SUCCESS_CONNECT = 0;
	protected static final int MESSAGE_READ = 1;
	protected static final int MESSAGE_READ_OK = 2;

	private ProgressBar spinner;

	IntentFilter filter;
	BroadcastReceiver receiver;
	String tag = "debugging";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_manager);
		init();
		
		if(btAdapter.isEnabled()) {
			getPairedDevices();
			startDiscovery();
		}
		
	}

	public class MyApplication extends Application {
	    BluetoothDevice device;

	    public synchronized void setBtConnection(BluetoothDevice btdevice) {
	    	this.device = btdevice;
	    }
	    
	    public synchronized BluetoothDevice getBtConnection() {
	        if (device == null) {
	            // construct a BluetoothDevice object and put it into variable device
	        }
	        return device;
	    }
	}

	private void startDiscovery() {
		// TODO Auto-generated method stub
		spinner = (ProgressBar) findViewById(R.id.progressBar);
		btAdapter.cancelDiscovery();
		btAdapter.startDiscovery();

	}
	
	public void btMan_checkBtEn() {
		if (btAdapter == null) {
			//XXX log
		} else {
			if (!btAdapter.isEnabled()) {
				turnOnBT();
			}
		}
	}

	private void turnOnBT() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(intent, 1);
	}

	private void turnOffBT() {
		// TODO Auto-generated method stub
		btAdapter.disable();
	}

	private void getPairedDevices() {
		// TODO Auto-generated method stub
		devicesArray = btAdapter.getBondedDevices();
		if (devicesArray.size() > 0) {
			for (BluetoothDevice device : devicesArray) {
				pairedDevices.add(device.getName());

			}
		}
	}

	private void init() {
		// TODO Auto-generated method stub
		listView = (ListView) findViewById(R.id.list_bluetooth);
		listView.setOnItemClickListener(this);
		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, 0);
		listView.setAdapter(listAdapter);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = new ArrayList<String>();
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		devices = new ArrayList<BluetoothDevice>();
		receiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();

				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					devices.add(device);
					String s = "";
					for (int a = 0; a < pairedDevices.size(); a++) {
						if (device.getName().equals(pairedDevices.get(a))) {
							// append
							s = "(Paired)";
							break;
						}
					}
					listAdapter.add(device.getName() + " " + s + " " + "\n"	+ device.getAddress());
				}

				else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
						.equals(action)) {
					findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {
					findViewById(R.id.progressBar).setVisibility(View.GONE);
				} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
					if (btAdapter.getState() == btAdapter.STATE_OFF) {
						turnOnBT();
					}
				}

			}
		};

		registerReceiver(receiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(receiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver, filter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bluetooth_manager, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
		case R.id.action_bt_off:
			turnOffBT();
			break;
		case R.id.action_bt_on:
			turnOnBT();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			Toast.makeText(getApplicationContext(),
					"Bluetooth must be enabled to continue", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
		if (listAdapter.getItem(arg2).contains("Paired")) {

			btDevice = devices.get(arg2);
			
			String btAddress = btDevice.getAddress();
			Log.i(tag, "in click listener");

			Intent i = new Intent(getApplicationContext(), MeterMenu.class);
			i.putExtra("btAddress",btAddress);
			startActivity(i);
			BluetoothManager.this.finish();

		} else {
			Toast.makeText(getApplicationContext(), "Device nao pareado!", 0)
					.show();
		}
	}

	public void update(View view) {
		listAdapter.clear();
		getPairedDevices();
		startDiscovery();
	}

}
