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

import java.io.*;
import java.util.TooManyListenersException;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
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
	
	 //Constants Connection State 
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote device
	
	// PM Threads
	private ConnectedThread connectedThread;
	// PM Others
	private IPhysicalConnectionListener listener = null;
	private boolean isClosed;
	private String tag = "PhysicalConnection";

	public PhysicalConnection(BluetoothSocket btSock)
			throws TooManyListenersException {

		// XXX this.port = port;
		// XXX port.addEventListener(this);
		// XXX port.notifyOnDataAvailable(true);
		// XXX port.enableReceiveTimeout(35);

		connect(btSock);
		isClosed = false;
	}

	public synchronized void connect(BluetoothSocket btSocket) {

		// XXX Gets the ConnectedThread
		// XXX Create Thread
		Log.i(tag, "Creating Bluetooth Socket!");
		// Close any open connection before connect
		if (connectedThread != null) {
			Log.i(tag, "Socket Connection not NULL! Reusing Connection");
			connectedThread.cancel();
			connectedThread = null;
		}
			// Creates the Socket Thread
			connectedThread = new ConnectedThread(btSocket);
			connectedThread.start(); // start the read thread
	}

	@Override
	public synchronized void send(byte[] data) throws IOException {
		// XXX LoggingHelper.logBytes(data, data.length, "Sending over " +
		// XXX port.getName(), logger);
		// XXX port.getOutputStream().write(data);
		// XXX port.getOutputStream().flush();
		// XXX Log.i(tag, "Sending: "+data);

		// Synchronize
		synchronized (this) {
			connectedThread.write(data);
		}
	}

	@Override
	public synchronized void close() {
		if (isClosed == false) {
			// XXX port.removeEventListener();
			// XXX port.close();
			Log.i(tag, "Closing...");
			if (connectedThread != null) {connectedThread.cancel(); connectedThread = null;}
			isClosed = true;
			Log.i(tag, "Closed!");
		}

	}

	@Override
	public synchronized void setSerialParams(int baud, int databits,
			int stopbits, int parity)
	/* throws UnsupportedCommOperationException */{
		// XXX port.setSerialPortParams(baud, databits, stopbits, parity);
		// XXX port.enableReceiveTimeout(5);
	}

	@Override
	public synchronized void registerListener(
			IPhysicalConnectionListener listener)
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
	// Thread to create Socket connection
	private class ConnectedThread extends Thread {

		private BluetoothSocket mmSocket;
		private InputStream mmInStream;
		private OutputStream mmOutStream;
		private int curLength;

		public ConnectedThread(BluetoothSocket socket) {
			Log.i(tag, "Creating Socket Thread!");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = mmSocket.getInputStream();
				tmpOut = mmSocket.getOutputStream();
			} catch (IOException e) {
				Log.i(tag, "Creating Stream Error!" + e.toString());
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {

			try {
				byte[] buffer = new byte[1024]; // buffer store for the stream
				int bytes = -1; // bytes returned from read()
				// Keep listening to the InputStream until an exception occurs
				while (true) {
						
					// Read from the InputStream
					bytes = mmInStream.read(buffer, curLength, buffer.length - curLength);

					if (bytes != -1) {
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
			
			if(mmOutStream == null)
				throw new IllegalStateException("Wait connection to be opened");
				
			try {
				Log.i(tag, "Sending Stream");
				mmOutStream.write(bytes);
			} catch (IOException e) {
				Log.i(tag, "Sending Stream Error: " + e.toString());
			}
			
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
//			try {
//				mmInStream.close();
//				mmOutStream.close();
//				mmInStream = null;
//				mmOutStream = null;
//				
//			} catch (IOException e) {
//				Log.i(tag, "Closing Stream Error: " + e.toString());
//			}
		}
	}

}
