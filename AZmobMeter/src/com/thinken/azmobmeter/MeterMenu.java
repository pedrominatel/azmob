package com.thinken.azmobmeter;

import java.io.IOException;
import java.util.UUID;

import org.openmuc.jdlms.client.IClientConnection;

import com.thinken.azmobmeter.driver.*;
import com.thinken.azmobmeter.utils.Filesys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MeterMenu extends Activity {

	private String btAddress = "";
	DriverInterface tst = new DriverInterface();
	IClientConnection conn;
	private String tag = "MeterMenu";
	// Threads
	private ConnectThread connectThread;

	// PM Bluetooth
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothDevice btDevice = null;
	private BluetoothSocket btSocket = null;
	public static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// Strings
	private String fwVersion = "generic";
	private String serialNumber = "";

	Filesys fsys = new Filesys();
	
	ProgressDialog progress;
	Handler updateBarHandler;

	private boolean btOpen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter);
		
		updateBarHandler = new Handler();

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			btAddress = extras.getString("btAddress");
			Log.i(tag, "Bluetooth MAC Address: " + btAddress);
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
	
    //To use the AsyncTask, it must be subclassed  
    private class LoadViewTask extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
            //Create a new progress dialog  
        	progress = new ProgressDialog(MeterMenu.this);  
            //Set the progress dialog to display a horizontal progress bar  
        	progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            //Set the dialog title to 'Loading...'  
        	progress.setTitle("Carregando...");  
            //Set the dialog message to 'Loading application View, please wait...'  
        	progress.setMessage("Aguarde um momento...");  
            //This dialog can't be canceled by pressing the back key  
        	progress.setCancelable(false);  
            //This dialog isn't indeterminate  
        	progress.setIndeterminate(false);  
            //Display the progress dialog  
        	progress.show();  
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {  
            /* 
             */  
            try  
            {  
                //Get the current thread's token  
                synchronized (this)  
                {  
                	connectToMeter();
                }  
            }  
            catch (Exception e)  
            {  
                e.printStackTrace();  
            }  
            return null;  
        }  
  
        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {  
            //close the progress dialog  
        	progress.dismiss();  
            //initialize the View
            setContentView(R.layout.activity_meter);
            
    		Intent intent = new Intent(MeterMenu.this, MeterReadout.class);
    		intent.putExtra("serialNumber", serialNumber);
    		intent.putExtra("fwVersion", fwVersion);
    		startActivity(intent);
    		
        }  
    }

	public void openMeterRead(View view) {

		new LoadViewTask().execute();
		
	}

	// Make it an thread
	public void connectToMeter() {

		if (!btOpen) {
			startBluetooth(btAddress);
		}

		try {
			conn = tst.mtr_connect(btSocket);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.i(tag, "Connecting Error: " + e.toString());
		}

		serialNumber = tst.mtr_get_SerialNumber(conn);

		if (serialNumber != null)
			Log.i(tag, "Meter Serial Number: " + serialNumber);

		fwVersion = tst.mtr_get_FirmwareVersion(conn);

		if (fwVersion != null)
			Log.i(tag, "Meter Firmware Version: " + fwVersion);

		disconnect();
		
	}

	public void clearAlarms(View view) {

		if (!btOpen) {
			startBluetooth(btAddress);
		}

		try {
			conn = tst.mtr_connect(btSocket);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.i(tag, "Connecting Error: " + e.toString());
		}

		if (tst.mtr_action_ResetNonFatalAlarmScriptTable(conn)) {
			Toast.makeText(getApplicationContext(), "Sucesso na operacao!", 0)
					.show();
		} else {
			Toast.makeText(getApplicationContext(), "Erro na operacao!", 0)
					.show();
		}

		final Button btn = (Button) findViewById(R.id.bt_clearAlarms);
		btn.setEnabled(false);
		
		disconnect();

	}

	public void disconnect(View view) {

		try {
			tst.mtr_disconnect(conn);
			closeBluetooth();
			btOpen = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(tag, "Disconnect Error: " + e.toString());
		}
		MeterMenu.this.finish();
	}
	
	public void disconnect() {

		try {
			tst.mtr_disconnect(conn);
			closeBluetooth();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(tag, "Disconnect Error: " + e.toString());
		}
	}

	public void openReadouts(View view) {

		Intent intent = new Intent(this, MeterReadout.class);
		intent.putExtra("serialNumber", serialNumber);
		intent.putExtra("firmwareVersion", fwVersion);
		startActivity(intent);
	}

	public void todo(View view) {
		Toast.makeText(getApplicationContext(), R.string.todo, 0).show();
	}

	private void startBluetooth(String btAddr) {

		Log.i(tag, "Creating Bluetooth Connect Thread");

		// Gets the Bluetooth MAC address
		String btAddress = btAddr;
		Log.i(tag, "Physical Connection Starting at MAC: " + btAddress);
		btDevice = btAdapter.getRemoteDevice(btAddress);

		// Close any open connection before connect
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}

		// Creates connect
		connectThread = new ConnectThread(btDevice);
		connectThread.start();

		// XXX Sync thread until Bluetooth connects!
		synchronized (connectThread) {
			try {
				Log.i(tag, "Waiting Bluetooth Connection...");
				connectThread.wait();
			} catch (InterruptedException e) {
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
				// XXX Check if it works here? notify();

				if (mmSocket.isConnected()) {
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
