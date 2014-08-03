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

//import gnu.io.SerialPort;
//import gnu.io.SerialPortEvent;
//import gnu.io.SerialPortEventListener;
//import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import android.util.Log;

/**
 * Wrapper class around the actual SerialPort object, abstracting sending and
 * receiving of data.
 * 
 * @author Karsten Mueller-Bier
 */
public class PhysicalConnection implements IPhysicalConnection {

	// private static Logger logger =
	// LoggerFactory.getLogger(PhysicalConnection.class);
	// private final SerialPort port;
	
	//PM Bluetooth
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothDevice btDevice = null;
	private BluetoothSocket btSocket = null;
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	//PM Threads
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
	//PM Others
	private IPhysicalConnectionListener listener = null;
	private boolean isClosed;
	private String tag = "PhysicalConnection";

	public PhysicalConnection(String btAddr) throws TooManyListenersException {
		
		// XXX this.port = port;
		// XXX port.addEventListener(this);
		// XXX port.notifyOnDataAvailable(true);
		// XXX port.enableReceiveTimeout(35);
		
		connect(btAddr);
		isClosed = false;
	}

	public synchronized void connect(String btAddr) {
		
		//XXX Gets the ConnectThread
		//Gets the Bluetooth MAC address
		String btAddress = btAddr;
		Log.i(tag, "Physical Connection Start: "+btAddress);
		btDevice = btAdapter.getRemoteDevice(btAddress);
		//XXX Create Thread
		Log.i(tag, "Creating Bluetooth Connect Thread");
		
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

		Log.i(tag, "Creating Bluetooth Socket!");
		//Close any open connection before connect
		if (connectedThread != null) {
			Log.i(tag, "Socket Connection not NULL!");
			connectedThread.cancel();
			connectedThread = null;
		}
		
		//Creates the Socket Thread
		connectedThread = new ConnectedThread(btSocket);
		connectedThread.start(); // start the read thread
		
	}
	
	@Override
	public synchronized void send(byte[] data) throws IOException {
		// XXX LoggingHelper.logBytes(data, data.length, "Sending over " +
		// port.getName(), logger);
		// XXX port.getOutputStream().write(data);
		// XXX port.getOutputStream().flush();
		// XXX Log.i(tag, "Sending: "+data);
		
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            //if (mState != STATE_CONNECTED) return;
            r = connectedThread;
        }
        // Perform the write unsynchronized
        r.write(data);
	}

	@Override
	public synchronized void close() {
		if (isClosed == false) {
			// XXX port.removeEventListener();
			// XXX port.close();
			Log.i(tag, "Closing...");
			if (connectThread != null) {connectThread.cancel();	connectThread = null;}
			if (connectedThread != null) {connectedThread.cancel();	connectedThread = null;}
			isClosed = true;
			Log.i(tag, "Closed!");
		}

	}

	@Override
	public synchronized void setSerialParams(int baud, int databits, int stopbits, int parity)
	/* throws UnsupportedCommOperationException */{
		// XXX port.setSerialPortParams(baud, databits, stopbits, parity);
		// XXX port.enableReceiveTimeout(5);
	}

	@Override
	public synchronized void registerListener(IPhysicalConnectionListener listener)
			throws TooManyListenersException {
		if (this.listener != null) {
			throw new TooManyListenersException();
		}
		this.listener = listener;
	}

	@Override
	public synchronized void removeListener() {
		listener = null;
	}

	/**
	 * Callback method when data is received from the wrapped SerialPort object
	 */
	public synchronized void serialEvent(byte[] buffer) {
		int dataLen = buffer.length;
		Log.i(tag, "Receiving data over Bluetooth.....");
		listener.dataReceived(buffer, dataLen);
	}

	@Override
	public synchronized boolean isClosed() {
		return isClosed;
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

	// XXX Refactoring by Pedro Minatel
	// Thread to create Socket connection
	private class ConnectedThread extends Thread {
		
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private int curLength;

		public ConnectedThread(BluetoothSocket socket) {
			Log.i(tag, "Creating Socket Thread!");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {

				try {
					byte[] buffer = new byte[1024]; // buffer store for the stream
					int bytes; // bytes returned from read()
					// Keep listening to the InputStream until an exception occurs
					while (true) {
						// Read from the InputStream
						bytes = mmInStream.read(buffer, curLength, buffer.length
								- curLength);
						if (bytes > 0) {
							// still reading
							curLength += bytes;
						}

						// check if reading is done
						if (curLength > 0) {
							// reading finished
							listener.dataReceived(buffer, curLength);
							curLength = bytes = 0;
						}
					}
				} catch (Exception e) {
					Log.i(tag, "Receive Error: " + e.toString());
				}


			// /XXX Refactoring
			// while (true) {
			// try {
			// // Read from the InputStream
			// bytes = mmInStream.read(buffer);
			// // Send the obtained bytes to the UI activity
			// //XXX mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
			// buffer).sendToTarget();
			// serialEvent(buffer);
			// } catch (IOException e) {
			// break;
			// }
			// }
		}

		/* Call this from the main activity to send data to the remote device */
		public void write(byte[] bytes) {
			try {
				Log.i(tag, "Sending data over Bluetooth.....");
				mmOutStream.write(bytes);
			} catch (IOException e) {
			}
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

}
