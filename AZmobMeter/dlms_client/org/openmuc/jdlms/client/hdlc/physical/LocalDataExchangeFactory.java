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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.bluetooth.BluetoothSocket;

/**
 * Acquires and pools all serial interfaces that are used
 * 
 * @author Karsten Mueller-Bier
 */
public class LocalDataExchangeFactory {
	private final Map<BluetoothSocket, LocalDataExchangeClient> localConnections = new HashMap<BluetoothSocket, LocalDataExchangeClient>();

	private final PhysicalConnectionFactory physicalFactory = new PhysicalConnectionFactory();

	public LocalDataExchangeClient build(BluetoothSocket btSock, boolean useHandshake) throws IOException {
		LocalDataExchangeClient result = null;

		if (localConnections.containsKey(btSock)) {
			result = localConnections.get(btSock);
		}
		else {
			result = new LocalDataExchangeClient(btSock, physicalFactory, useHandshake);
			localConnections.put(btSock, result);
		}
		return result;
	}
}
