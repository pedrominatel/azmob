/**
 * Created by Pedro Minatel
 * pminatel@gmail.com
 */
package com.thinken.azmobmeter.driver;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * @author piiiters
 *
 */

public class DriverThread {

	private String tag = "DriverThread";
	// Threads
	private ConnectThread connectThread;

	// PM Bluetooth
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothDevice btDevice = null;
	private BluetoothSocket btSocket = null;
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
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
