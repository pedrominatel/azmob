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

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * Factory class to create a PhysicalConnection object using RXTX serial communication library
 * 
 * @author Karsten Mueller-Bier
 * 
 */
public class PhysicalConnectionFactory {

	/**
	 * Tries to acquire the port named in the constructor
	 * 
	 * @throws NoSuchPortException
	 * @throws PortInUseException
	 * @throws IOException
	 * @throws UnsupportedCommOperationException
	 */
	public IPhysicalConnection acquireSerialPort(String portName) throws NoSuchPortException, PortInUseException,
			IOException, UnsupportedCommOperationException {
		SerialPort socket;

		if (!System.getProperty("os.name").startsWith("Windows")) {
			// Dirty hack to identify if jDLMS is run on a UNIX machine. It's assumed that every
			// non-Windows machine runs a UNIX OS, which is simply false but highly likely. Because of the small chance
			// that the result is a false positive, this hack is suffice for the moment.
			// Feel free to change this check with a more robust one
			if (!portName.startsWith("/dev/")) {
				portName = "/dev/" + portName;
			}
		}

		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			throw new PortInUseException();
		}

		CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

		if (commPort instanceof SerialPort == false) {
			throw new IOException("The specified CommPort is no serial port");
		}

		socket = (SerialPort) commPort;
		socket.setSerialPortParams(300, SerialPort.DATABITS_7, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);

		PhysicalConnection result;
		try {
			result = new PhysicalConnection(socket);
		} catch (TooManyListenersException e) {
			throw new PortInUseException();
		}

		return result;
	}
}
