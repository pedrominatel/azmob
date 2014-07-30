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

import org.openmuc.jdlms.util.LoggingHelper;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import android.util.Log;

/**
 * Wrapper class around the actual SerialPort object, abstracting sending and receiving of data.
 * 
 * @author Karsten Mueller-Bier
 */
public class PhysicalConnection implements IPhysicalConnection/*, SerialPortEventListener*/ {

	//private static Logger logger = LoggerFactory.getLogger(PhysicalConnection.class);
	//private final SerialPort port;
	private IPhysicalConnectionListener listener = null;
	private boolean isClosed;
	protected static final int MESSAGE_READ = 0;
	private ConnectedThread connectedThread;
	private String tag = "PhysicalConnection";
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				serialEvent(readBuf);
				break;
			}
		}
	};

	public PhysicalConnection(BluetoothSocket btSocket) throws TooManyListenersException/*, UnsupportedCommOperationException*/ {
//XXX		this.port = port;
//XXX		port.addEventListener(this);
//XXX		port.notifyOnDataAvailable(true);
//XXX		port.enableReceiveTimeout(35);
		Log.i(tag, "Creating Bluetooth Socket!");
		connectedThread = new ConnectedThread(btSocket);
		connectedThread.start(); // start the read thread
		isClosed = false;
	}

	@Override
	public void send(byte[] data) throws IOException {
		//XXX LoggingHelper.logBytes(data, data.length, "Sending over " + port.getName(), logger);
		//XXX port.getOutputStream().write(data);
		//XXX port.getOutputStream().flush();
		Log.i(tag, "Sending: "+data.toString());
		connectedThread.write(data);
	}

	@Override
	public void close() {
		if (isClosed == false) {
			//XXX port.removeEventListener();
			//XXX port.close();
			connectedThread.cancel();
			isClosed = true;
		}

	}

	@Override
	public void setSerialParams(int baud, int databits, int stopbits, int parity)
			/*throws UnsupportedCommOperationException*/ {
		//XXX port.setSerialPortParams(baud, databits, stopbits, parity);
		//XXX port.enableReceiveTimeout(5);
	}

	@Override
	public void registerListener(IPhysicalConnectionListener listener) throws TooManyListenersException {
		if (this.listener != null) {
			throw new TooManyListenersException();
		}
		this.listener = listener;
	}

	@Override
	public void removeListener() {
		listener = null;
	}

	/**
	 * Callback method when data is received from the wrapped SerialPort object
	 */
	public void serialEvent(byte[] buffer) {
		int dataLen = buffer.length;
		listener.dataReceived(buffer, dataLen);
	}

	@Override
	public boolean isClosed() {
		return isClosed;
	}
	
	
	//XXX Refactoring by Pedro Minatel
	//Thread to create Socket connection
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

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
			byte[] buffer = new byte[1024]; // buffer store for the stream
			int bytes; // bytes returned from read()

			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					// Send the obtained bytes to the UI activity
					mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
				} catch (IOException e) {
					break;
				}
			}
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
