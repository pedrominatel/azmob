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

/**
 * Acquires and pools all serial interfaces that are used
 * 
 * @author Karsten Mueller-Bier
 */
public class LocalDataExchangeFactory {
	private final Map<String, LocalDataExchangeClient> localConnections = new HashMap<String, LocalDataExchangeClient>();

	private final PhysicalConnectionFactory physicalFactory = new PhysicalConnectionFactory();

	public LocalDataExchangeClient build(String portName, int baudrate, boolean useHandshake) throws IOException {
		LocalDataExchangeClient result = null;

		if (localConnections.containsKey(portName)) {
			result = localConnections.get(portName);
		}
		else {
			result = new LocalDataExchangeClient(portName, physicalFactory, baudrate, useHandshake);
			localConnections.put(portName, result);
		}
		return result;
	}
}
