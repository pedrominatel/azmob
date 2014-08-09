package com.thinken.azmobmeter;

import java.io.IOException;
import java.util.UUID;

import org.openmuc.jdlms.client.IClientConnection;

import com.thinken.azmobmeter.driver.*;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
	private String tag = "MeterMenu";
	//Threads
	private ConnectThread connectThread;
	
	//PM Bluetooth
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothDevice btDevice = null;
	private BluetoothSocket btSocket = null;
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private boolean btOpen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter);
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			
		    btAddress = extras.getString("btAddress");
			Log.i("CONNECTION", "Starting Connection");
			
			try {

				startBluetooth(btAddress); //get the Bluetooth Device from MAC Address
				btOpen = true;
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
	
	public void connectToMeter(View view) {
		
		if (!btOpen) {
			startBluetooth(btAddress);
		}
		
		try {
			conn = tst.connect(btSocket);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.i(tag, "Connecting Error: " + e.toString());
		}
		
	}
	
	public void clearAlarms(View view) {
		
		tst.setActionEx(conn);
		
		final Button btn = (Button)findViewById(R.id.bt_clearAlarms);
		btn.setEnabled(false);
		
	}

	public void disconnect(View view) {

		try {
			tst.disconnect(conn);
			closeBluetooth();
			btOpen = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(tag, "Disconnect Error: " + e.toString());
		}

//		Intent intent = new Intent(MeterMenu.this,MainMenu.class);
//		startActivity(intent);
	}
	
	public void openReadouts(View view) {
		Intent intent = new Intent(MeterMenu.this,MeterReadout.class);
		startActivity(intent);
	}
	
	public void todo(View view) 
	{
	      Toast.makeText(getApplicationContext(),R.string.todo, 0).show();
	}
	
	private void startBluetooth(String btAddr) {
		
		Log.i(tag, "Creating Bluetooth Connect Thread");
		
		//Gets the Bluetooth MAC address
		String btAddress = btAddr;
		Log.i(tag, "Physical Connection Starting at MAC: "+btAddress);
		btDevice = btAdapter.getRemoteDevice(btAddress);
		
		//Close any open connection before connect
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}
		
		//Creates connect
		connectThread = new ConnectThread(btDevice);
		connectThread.start();
		
		//XXX Sync thread until Bluetooth connects!
        synchronized(connectThread){
            try{
            	Log.i(tag, "Waiting Bluetooth Connection...");
                connectThread.wait();
            }catch(InterruptedException e){
            	Log.i(tag, "Thread Failed" + e.toString());
            }
        }
	}
	
	private void closeBluetooth() {
		
		connectThread.cancel();
		
	}
	
	// XXX Refactoring by Pedro Minatel
		// Thread to create Bluetooth connection
		private class ConnectThread extends Thread {

			private final BluetoothSocket mmSocket;

			public ConnectThread(BluetoothDevice device) {
				// Use a temporary object that is later assigned to mmSocket,
				// because mmSocket is final
				BluetoothSocket tmp = null;
				BluetoothDevice mmDevice = device;
				Log.i(tag, "Bluetooth Connect Constructor");
				// Get a BluetoothSocket to connect with the given BluetoothDevice
				try {
					// MY_UUID is the app's UUID string, also used by the server
					tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
				} catch (IOException e) {
					Log.i(tag, "Get Socket Failed");

				}
				mmSocket = tmp;
			}

			public void run() {
				synchronized (this) {
					// Cancel discovery because it will slow down the connection
					btAdapter.cancelDiscovery();
					Log.i(tag, "Connect - Run");
					try {
						// Connect the device through the socket. This will block
						// until it succeeds or throws an exception
						mmSocket.connect();
						Log.i(tag, "Socket Connect - Succeeded");
					} catch (IOException connectException) {
						Log.i(tag, "Connect Failed");
						// Unable to connect; close the socket and get out
						try {
							mmSocket.close();
						} catch (IOException closeException) {
						}
						return;
					}
					// Do work to manage the connection (in a separate thread)
					//XXX Check if it works here? notify();
					
					if(mmSocket.isConnected()){
						Log.i(tag, "Thread notify!");
						btSocket = mmSocket;
						notify();
					}
				}
			}

			/** Will cancel an in-progress connection, and close the socket */
			public void cancel() {
				try {
					mmSocket.close();
				} catch (IOException e) {
				}
			}
		}
	
}
