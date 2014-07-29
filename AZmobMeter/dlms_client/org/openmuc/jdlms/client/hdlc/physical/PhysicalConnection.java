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
import java.util.TooManyListenersException;

import org.openmuc.jdlms.util.LoggingHelper;

import android.bluetooth.BluetoothSocket;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

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

	private final byte[] buffer = new byte[1024];

	public PhysicalConnection(BluetoothSocket btAddr) throws TooManyListenersException/*, UnsupportedCommOperationException*/ {
//		this.port = port;
//		port.addEventListener(this);
//		port.notifyOnDataAvailable(true);
//		port.enableReceiveTimeout(35);
		isClosed = false;
	}

	@Override
	public void send(byte[] data) throws IOException {
		//LoggingHelper.logBytes(data, data.length, "Sending over " + port.getName(), logger);
		//port.getOutputStream().write(data);
		//port.getOutputStream().flush();
	}

	@Override
	public void close() {
		if (isClosed == false) {
			//port.removeEventListener();
			//port.close();
			isClosed = true;
		}

	}

	@Override
	public void setSerialParams(int baud, int databits, int stopbits, int parity)
			/*throws UnsupportedCommOperationException*/ {
		//port.setSerialPortParams(baud, databits, stopbits, parity);
		//port.enableReceiveTimeout(5);
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
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		int data;

		try {
			int len = 0;
			while ((data = port.getInputStream().read()) > -1) {
				buffer[len++] = (byte) data;
			}
			//LoggingHelper.logBytes(buffer, len, "Received from " + port.getName(), logger);
			listener.dataReceived(buffer, len);
		} catch (IOException e) {
			//LoggingHelper.logStackTrace(e, logger);
		}
	}

	@Override
	public boolean isClosed() {
		return isClosed;
	}
}
