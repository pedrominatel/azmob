package com.thinken.azmobmeter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openmuc.jdlms.client.IClientConnection;
import org.openmuc.jdlms.client.ObisCode;

import com.thinken.azmobmeter.driver.DriverInterface;
import com.thinken.azmobmeter.driver.DriverUtils;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MeterReadout extends Activity {

	DriverUtils driver_utils = new DriverUtils();
	//Strings
	private String meterType = "sl7000";
	private String serialNumber = "";
	private String fwVersion = "generic";
	private String tag = "MeterReadout";
	private String btAddress;
	
	// PM Bluetooth
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothDevice btDevice = null;
	private BluetoothSocket btSocket = null;
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private ConnectThread connectThread;
	
	DriverInterface tst = new DriverInterface();
	IClientConnection conn;
	
	private boolean btOpen = false;
	
	ProgressDialog progress;
	Handler updateBarHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_read);
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			serialNumber = extras.getString("serialNumber");
			fwVersion = extras.getString("fwVersion");
			btAddress = extras.getString("btAddress");
		}
		
		
		TextView serial = (TextView)findViewById(R.id.txt_serialNumber);
		TextView fw = (TextView)findViewById(R.id.txt_firmwareVersion);
		
		serial.setText("Número de Série: "+serialNumber);
		fw.setText("Versão do Firmware: "+fwVersion);
		
		// Setup the list view
		final ListView newsEntryListView = (ListView) findViewById(R.id.list);
		final MeterObjectsEntryAdapter newsEntryAdapter = new MeterObjectsEntryAdapter(this,
				R.layout.meter_entry_list_item);
		newsEntryListView.setAdapter(newsEntryAdapter);
		// Populate the list, through the adapter
		for (final MeterObjectsEntry entry : getMeterObjectsEntries()) {
			newsEntryAdapter.add(entry);
		}
		
		
		newsEntryListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String selectedObject = newsEntryAdapter.getItem(position).getObjectName();

				//new LoadViewTask().execute();
	
				List<String[]> objects = new ArrayList<String[]>();
				
				objects = driver_utils.driver_getObjectsByGroup(meterType, fwVersion, selectedObject);
				
				for (int i = 0; i < objects.size(); i++) {
					String[] object = new String[4];
					object = objects.get(i);
					
					Log.i(tag, object[0]);
					Log.i(tag, object[1]);
					Log.i(tag, object[2]);
					Log.i(tag, object[3]);

				}
				
			}
		});
		

	}

	private List<MeterObjectsEntry> getMeterObjectsEntries() {
		// Let's setup some test data.
		// Normally this would come from some asynchronous fetch into a data
		// source
		// such as a sqlite database, or an HTTP request
		final List<MeterObjectsEntry> entries = new ArrayList<MeterObjectsEntry>();
		
		List<String[]> objects = new ArrayList<String[]>();
		
		try {
			objects = driver_utils.driver_getObjectsGroupsNames(meterType, fwVersion);
		} catch (Exception e) {
			// TODO: handle exception
			objects = driver_utils.driver_getObjectsGroupsNames("sl7000", "generic");
		}
		
		for (int i = 0; i < objects.size(); i++) {
			String[] object = new String[2];
			object = objects.get(i);
			entries.add(new MeterObjectsEntry(object[0], object[1] , i % 2 == 0 ? R.drawable.ic_read: R.drawable.ic_read));
		}
		
		return entries;
	}

	public void todo(View view) {
		Toast.makeText(getApplicationContext(), R.string.todo, 0).show();
	}
	
	
	
    //To use the AsyncTask, it must be subclassed  
    private class LoadViewTask extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
            //Create a new progress dialog  
        	progress = new ProgressDialog(MeterReadout.this);  
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
            
    		Intent intent = new Intent(MeterReadout.this, MeterReadout.class);
    		intent.putExtra("serialNumber", serialNumber);
    		intent.putExtra("fwVersion", fwVersion);
    		startActivity(intent);
    		
        }  
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

		ObisCode obis = new ObisCode(1,1,2,8,0,255);//index parameters
		boolean res = tst.mtr_get_Object(conn, serialNumber, "ExportActiveAggregate", obis, 3, 2);
		
		if(res){
			Toast.makeText(getApplicationContext(), "Sucesso!", 0).show();
		}else{
			Toast.makeText(getApplicationContext(), "Erro!", 0).show();
		}
		

		disconnect();
		
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
	
	public void disconnect() {

		try {
			tst.mtr_disconnect(conn);
			closeBluetooth();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(tag, "Disconnect Error: " + e.toString());
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
						progress.dismiss(); 
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
