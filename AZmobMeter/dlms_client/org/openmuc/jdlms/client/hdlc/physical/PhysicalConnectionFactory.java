/*
 * Copyright 2012-13 Fraunhofer ISE
 *
 * This file is part of jDLMS.
 * For more information visit http://www.openmuc.org
 *
 * jDLMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * jDLMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jDLMS.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openmuc.jdlms.client.hdlc.physical;

//import gnu.io.CommPort;
//import gnu.io.CommPortIdentifier;
//import gnu.io.NoSuchPortException;
//import gnu.io.PortInUseException;
//import gnu.io.SerialPort;
//import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Factory class to create a PhysicalConnection object using RXTX serial communication library
 * 
 * @author Karsten Mueller-Bier
 * 
 */
public class PhysicalConnectionFactory {

	private String tag = "PhysicalConnectionFactory";
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothDevice btDevice;
	private BluetoothSocket btSocket;
	private ConnectThread connectThread;
	
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	protected static final int SUCCESS_CONNECT = 0;
	protected static final int MESSAGE_READ = 1;
	protected static final int MESSAGE_READ_OK = 2;
	
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.i(tag, "in handler");
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS_CONNECT:
				btSocket = (BluetoothSocket) msg.obj;
				//isClosed = false;
				//Start Thread for BluetoothSocket
				//connectedThread = new ConnectedThread((BluetoothSocket) msg.obj);
				//connectedThread.start(); // start the read thread
				Log.i(tag, "Bluetooth Layer Connected"); // TODO Use R.string.xxx
				try {
					//send(data.getBytes());
				} catch (Exception e) {
					Log.i("CONNECTION", "Physical Connection Error: "+e.toString());
				}
				break;
			}
		}
	};
	
	/**
	 * Tries to acquire the port named in the constructor
	 * 
	 * @throws NoSuchPortException
	 * @throws PortInUseException
	 * @throws IOException
	 * @throws UnsupportedCommOperationException
	 */
	//XXX Refactored by Pedro Minatel
	public IPhysicalConnection acquireBluetooth(String btAddr) throws IOException {
		
//XXX SerialPort socket;
//XXX Refactoring
//		if (!System.getProperty("os.name").startsWith("Windows")) {
//			// Dirty hack to identify if jDLMS is run on a UNIX machine. It's assumed that every
//			// non-Windows machine runs a UNIX OS, which is simply false but highly likely. Because of the small chance
//			// that the result is a false positive, this hack is suffice for the moment.
//			// Feel free to change this check with a more robust one
//			if (!portName.startsWith("/dev/")) {
//				portName = "/dev/" + portName;
//			}
//		}
		//XXX Refactoring
//		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
//		if (portIdentifier.isCurrentlyOwned()) {
//			throw new PortInUseException();
//		}
//		CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
//		if (commPort instanceof SerialPort == false) {
//			throw new IOException("The specified CommPort is no serial port");
//		}
//		socket = (SerialPort) commPort;
//		socket.setSerialPortParams(300, SerialPort.DATABITS_7, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);

		/*
		REFACTORING to Android Bluetooth
		*/
		
		//XXX Gets the ConnectThread
		//Gets the Bluetooth MAC address
		String btAddress = btAddr;
		Log.i(tag, "Physical Connection Start: "+btAddress);
		btDevice = btAdapter.getRemoteDevice(btAddress);
		//XXX Create Thread
		Log.i(tag, "Creating Bluetooth Connect Thread");
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
		
		//XXX Send the Bluetooth Socket to PhysicalConnection
		PhysicalConnection result;
		try {
			result = new PhysicalConnection(btSocket);
		} catch (TooManyListenersException e) {
			throw new IOException();
		}

		return result;
	}

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
					Log.i(tag, "Connect - Succeeded");
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
				mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
				notify();
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
