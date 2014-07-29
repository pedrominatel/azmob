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

//import gnu.io.NoSuchPortException;
//import gnu.io.PortInUseException;
//import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.openmuc.jdlms.client.communication.ILowerLayer;
import org.openmuc.jdlms.client.communication.IUpperLayer;
import org.openmuc.jdlms.client.hdlc.HdlcAddress;
import org.openmuc.jdlms.client.hdlc.common.FrameInvalidException;
import org.openmuc.jdlms.client.hdlc.common.HdlcAddressPair;
import org.openmuc.jdlms.client.hdlc.common.HdlcHeaderParser;
import org.openmuc.jdlms.util.ByteBufferInputStream;
//import org.openmuc.jdlms.util.LoggingHelper;
import org.openmuc.jdlms.util.QueueHelper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import android.util.Log;

/**
 * This class represents a connection on the physical layer according to IEC 62056-21 in protocol mode E
 * 
 * @author Karsten Mueller-Bier
 */
public class LocalDataExchangeClient implements ILowerLayer<HdlcAddressPair>, IPhysicalConnectionListener {

	//private static Logger logger = LoggerFactory.getLogger(LocalDataExchangeClient.class);

	private static final byte[] REQUESTMESSAGE = new byte[] { (byte) 0x2F, (byte) 0x3F, (byte) 0x21, (byte) 0x0D,
			(byte) 0x0A };
	private static final byte[] ACKNOWLEDGE = new byte[] { (byte) 0x06, (byte) 0x32, (byte) 0x00, (byte) 0x32,
			(byte) 0x0D, (byte) 0x0A };

	private volatile boolean isConnected = false;
	private int connectedClients = 0;

	private IPhysicalConnection connection = null;
	private final HdlcHeaderParser parser = new HdlcHeaderParser();
	private final Map<Object, IUpperLayer> listeners;

	private final BlockingQueue<byte[]> receivingQueue = new ArrayBlockingQueue<byte[]>(3);

	private PhysicalConnectionFactory factory = null;
	//XXX Refactoring Pedro Minatel
	private final String btAddress;
	private final String tag = "LocalDataExchangeClient";

	//XXX	private final int maxBaudrate;
	private final boolean useHandshake;

	private final ByteBuffer receivingDataBuffer = ByteBuffer.allocate(2048);

	//XXX Refactoring
	public LocalDataExchangeClient(String btAddr, PhysicalConnectionFactory factory, boolean useHandshake) {
		this.btAddress = btAddr;
		this.factory = factory;
		listeners = new HashMap<Object, IUpperLayer>(8);
		//XXX maxBaudrate = baudrate;
		this.useHandshake = useHandshake;
	}

	@Override
	public void connect(long timeout) throws IOException {
		if (isConnected == false) {
			synchronized (this) {
				if (isConnected) {
					connectedClients++;
					return; // Some other thread already initiated the
					// connection
				}

				receivingQueue.clear();
				//XXX change to Bluetooth
				openPhysicalConnection();

				//XXX
				if (!useHandshake) {
					try {
						//XXX Not needed when using bluetooth
						//connection.setSerialParams(maxBaudrate, 8, 1, 0);
					} catch (/*UnsupportedCommOperationException*/Exception e) {
						//XXX not needed!
						//throw new IOException("Serial Port does not support " + maxBaudrate + "bd 8N1");
					}
				}
				else {
					connection.send(REQUESTMESSAGE);

					byte[] receivedData = null;
					int baudRateSetting;
					int baudRate;

					try {
						receivedData = QueueHelper.waitPoll(receivingQueue, timeout);
					} catch (InterruptedException e) {
						throw new IOException("Couldn't connect to server", e);
					}

					if (receivedData == null) {
						connection.close();
						throw new IOException("Server is not responding");
					}
					if (receivedData.length < 7) {
						connection.close();
						throw new IOException("Error on receiving server ident");
					}

					baudRateSetting = receivedData[4];
					baudRate = getBaudRate(baudRateSetting);

					if (receivedData[5] != 0x5C || receivedData[6] != 0x32 || baudRate == -1) {
						throw new IOException("Remote end point does not support HDLC");
					}

					receivedData = null;

					ACKNOWLEDGE[2] = (byte) baudRateSetting;
					connection.send(ACKNOWLEDGE);

					try {
						// Sleep for about 250 milliseconds to make sure, that the
						// acknowledge message has been completely transmitted prior
						// to changing the baud rate
						Thread.sleep(250);
						connection.setSerialParams(baudRate, 8, 1, 0);
					} catch (/*UnsupportedCommOperationException*/Exception e) {
						throw new IOException("Serial Port does not support " + baudRate + "bd 8N1");
					//} catch (InterruptedException e) {
						//LoggingHelper.logStackTrace(e, logger);
						//throw new IOException("Interrupted while establishing connection");
					}

					try {
						receivedData = QueueHelper.waitPoll(receivingQueue, timeout);
					} catch (InterruptedException e) {
						throw new IOException("Couldn't connect to meter", e);
					}
					receivedData = null;
				}

				isConnected = true;
			}
		}
		connectedClients++;
	}

	@Override
	public void send(byte[] data) throws IOException {
		if (isConnected == false) {
			throw new IOException("Connection closed");
		}
		connection.send(data);
	}

	/**
	 * Additional note to {@code ITransportLayer#disconnect()}: Only disconnects the port if no other connection is
	 * using it
	 */
	@Override
	public void disconnect() throws IOException {
		connectedClients--;
		if (connectedClients <= 0) {
			isConnected = false;
			connection.removeListener();
			connection.close();
		}
	}

	@Override
	public void registerReceivingListener(HdlcAddressPair key, IUpperLayer listener) throws IllegalArgumentException {
		if (listeners.containsKey(key)) {
			throw new IllegalArgumentException("A connection with the addresses " + key.toString()
					+ " is already registered");
		}

		listeners.put(key, listener);
	}

	@Override
	public void removeReceivingListener(IUpperLayer listener) {
		while (listeners.values().remove(listener)) {
			; // Removes all references of listener
		}
	}

	@Override
	public void dataReceived(byte[] data, int length) {
		if (isConnected) {
			try {
				receivingDataBuffer.put(data, 0, length);

				ByteBuffer copy = ByteBuffer.wrap(receivingDataBuffer.array());
				copy.position(receivingDataBuffer.position());
				copy.flip();
				ByteBufferInputStream dataStream = new ByteBufferInputStream(copy);
				byte readByte = 0;
				while (dataStream.available() > 0 && readByte != 0x7E) {
					// Reads input stream until FLAG byte has been found
					readByte = (byte) dataStream.read();
				}
				while (dataStream.available() > 0) {

					do {
						copy.mark();
						readByte = (byte) dataStream.read();
					} while (readByte == 0x7E);
					copy.reset();

					byte[] frame = parser.readFrame(dataStream);
					HdlcAddressPair key = new HdlcAddressPair(parser.getLastValidDestination(),
							parser.getLastValidSource());

					if (dataStream.available() == 1) {
						dataStream.read(); // Read and discard final 0x7E flag
					}

					copy.compact();
					receivingDataBuffer.position(copy.position());
					copy.flip();

					if (HdlcAddress.isAllStation(parser.getLastValidSource())
							|| HdlcAddress.isNoStation(parser.getLastValidSource())) {
						// Source is not defined, discard
						//TODO logger.debug("Source is not defined. Frame discarded");
					}
					else if (listeners.containsKey(key)) {
						listeners.get(key).dataReceived(frame);
					}
				}

			} catch (IOException e) {
				//TODO LoggingHelper.logStackTrace(e, logger);
			} catch (FrameInvalidException e) {
				//TODO LoggingHelper.logStackTrace(e, logger);
				ByteBuffer copy = ByteBuffer.wrap(receivingDataBuffer.array());
				copy.position(receivingDataBuffer.position());
				copy.flip();
				copy.get(); // Read over first 0x7E Flag

				// Read until end of buffer or next 0x7E Flag
				while (copy.remaining() > 0 && copy.get() != 0x7E) {
				}
				copy.compact();
				receivingDataBuffer.position(copy.position());
			}
		}
		else {
			byte[] receivedData = new byte[length];
			System.arraycopy(data, 0, receivedData, 0, length);
			try {
				receivingQueue.put(receivedData);
			} catch (InterruptedException e) {
				//TODO LoggingHelper.logStackTrace(e, logger);
			}
		}
	}

	@Override
	public void discardMessage(byte[] data) {
		// No messages are buffered on this layer
	}

	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * Returns the baud rate chosen by the server for this communication
	 * 
	 * @param baudCharacter
	 *            Encoded baud rate (see 6.3.14 item 13c)
	 * @return The chosen baud rate or -1 on error
	 */
	private int getBaudRate(int baudCharacter) {
		int result = -1;
		switch (baudCharacter) {
		case 0x30:
			result = 300;
			break;
		case 0x31:
			result = 600;
			break;
		case 0x32:
			result = 1200;
			break;
		case 0x33:
			result = 2400;
			break;
		case 0x34:
			result = 4800;
			break;
		case 0x35:
			result = 9600;
			break;
		case 0x36:
			result = 19200;
			break;
		}
		return result;
	}

	private void openPhysicalConnection() throws IOException {
		if (connection == null || connection.isClosed()) {
			try {
				connection = factory.acquireBluetooth(btAddress);
			} catch (/*UnsupportedCommOperationException*/Exception e) {
				//TODO throw new IOException("Cannot intialize port", e);
			//} catch (NoSuchPortException e) {
				//TODO throw new IOException("No such port", e);
			//} catch (PortInUseException e) {
				//TODO throw new IOException("Por/t already in use", e);
				Log.i(tag, "Error on acquireBluetooth " + e.toString());
			}
		}

		try {
			connection.registerListener(this);
		} catch (TooManyListenersException e) {
			throw new IOException("Port already in use", e);
		}
	}
}
