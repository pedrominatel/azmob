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
package org.openmuc.jdlms.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class LoggingHelper {

	private static Marker COMMUNICATION = MarkerFactory.getMarker("Communication");

	public static void logStackTrace(Throwable e, Logger logger) {
		if (logger.isDebugEnabled()) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.debug(COMMUNICATION, sw.toString());
		}
	}

	/**
	 * Logs sent and received bytes for debugging
	 * 
	 * @param data
	 *            Bytes sent/received
	 * @param length
	 *            Number of Bytes sent/received
	 * @param prefix
	 *            Logging prefix
	 */
	public static void logBytes(byte[] data, int length, String prefix, Logger logger) {
		if (logger.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < length; i++) {
				byte b = data[i];
				String byteString = Integer.toHexString(b & 0x000000FF);
				byteString = byteString.length() == 1 ? "0" + byteString : byteString;
				sb.append(" ").append(byteString);
			}

			logger.trace(COMMUNICATION, prefix + ": " + sb.toString());
		}
	}
}
