package com.thinken.azmobmeter.driver;

import java.io.IOException;

import org.openmuc.jdlms.client.AccessResultCode;
import org.openmuc.jdlms.client.GetResult;
import org.openmuc.jdlms.client.IClientConnection;
import org.openmuc.jdlms.client.MethodResult;
import org.openmuc.jdlms.client.ObisCode;
import org.openmuc.jdlms.client.hdlc.HdlcAddress;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

public class DriverInterface {

	private static final int dlms_upperAddress = 1;
	private static final int dlms_lowerAddress = 17;
	private static final int dlms_addressSize = 4;
	private static final int CONN_TIMEOUT = 5000;

	private DriverManagement connManager = new DriverManagement();
	private DriverDataExchange data = new DriverDataExchange();
	private DriverPduIO parser = new DriverPduIO();
	
	private String tag = "TEST";

	public IClientConnection connect(BluetoothSocket socket) throws InterruptedException {
		// TODO Auto-generated method stub

		HdlcAddress hdlcAddress = new HdlcAddress(dlms_upperAddress, dlms_lowerAddress, dlms_addressSize);
		IClientConnection connection = connManager.buildHDLCConnection(hdlcAddress, socket, 1);

		try {
			Log.i(tag, "Trying to connect...");
			// Create the connection and connect using HDLC
			Log.i(tag, "Connecting...");

			if (!connection.isConnected()) {
				connection.connect(CONN_TIMEOUT, "ABCDEFGH".getBytes("US-ASCII"));
			}

			if (connection.isConnected()) {
				Log.i(tag, "Connected!");

				// // getObjectWithSelectorEx(connection);
				//
				// //Thread.sleep(1000);
				// //if(setActionEx(connection))
				// connection.disconnect(false);
				//
				// // setObjectEx(connection);
			}

		} catch (IOException ex) {
			return null;
		} catch (Exception ex) {
			return null;
		}
		return connection;
	}

	public void disconnect(IClientConnection conn) {

		if (conn.isConnected()) {
			conn.disconnect(false);
		}

	}

	public boolean getObjectEx(IClientConnection connection) {
		// TODO: Get clock example
		// ObisCode obis = new ObisCode(0,0,98,133,5,255);//this function create
		// the OBIS code structure using the six elements as integer
		// GetResult getResult = data.GetObject(connection, obis, 7, 0);

		ObisCode obis = new ObisCode(0, 0, 130, 0, 2, 255);//index parameters
		int classId = 1;
		int attribute = 2;

		GetResult getResult = data.GetObject(connection, obis, classId,	attribute);

		if (getResult.isSuccess()) {
			Log.i(tag, "Success!");
			parser.createXml("1234567890", "read.xml", getResult, obis, classId, attribute); //this function create the XML file using the
			return true;
		} else {
			Log.i(tag, "Reading Error. ErrorCode: " + getResult.getResultCode());
			return false;
		}
	}
	
	public boolean getObjectEx(IClientConnection connection, String serialNumber, String object, ObisCode obis, int classId, int attribute) {

		GetResult getResult = data.GetObject(connection, obis, classId,	attribute);

		if (getResult.isSuccess()) {
			Log.i(tag, "Success!");
			parser.createXml(serialNumber, object+".xml", getResult, obis, classId, attribute); //this function create the XML file using the
			return true;
		} else {
			Log.i(tag, "Reading Error. ErrorCode: " + getResult.getResultCode());
			return false;
		}
	}
	
	public String getSerialNumber() {
		//XXX get serial number
		//<CosemObject Name="SerialNumber" LogicalName="0;0;96;1;0;255;" ClassId="1" Index="2">
		return "";
	}
	
	public String getFirmwareVersion() {
		//XXX get firmware version
		//<CosemObject Name="CompleteExtFirmwareIdParameters" LogicalName="0;0;142;1;3;255;" ClassId="1" Index="2">
		return "";
	}
	
	public String getDateTime() {
		//XXX get firmware version
		return "";
	}
	
	//
	// public void getObjectWithSelectorEx(IClientConnection connection) {
	//
	// // TODO: Get object with selector (i.e LoadProfile)
	// // Preparation for selective access description
	// // Structure defining the range object
	// List<Data> outerData = new ArrayList<Data>(4);
	// List<Data> arrayNull = new ArrayList<Data>(1);
	//
	// // First element of the selective access is the above definition
	// outerData.add(new Data());
	// outerData.add(new Data());
	// outerData.add(new Data());
	// outerData.add(new Data());
	//
	// outerData.get(0).setNull();
	// // Start of interval and linked status, or NULL data if not significative
	// outerData.get(1).setOctetString(
	// new byte[] { (byte) 0x07, (byte) 0xDE, (byte) 0x02,
	// (byte) 0x0E, (byte) 0xFF, (byte) 0x00, (byte) 0x00,
	// (byte) 0x00, (byte) 0xFF, (byte) 0x80, (byte) 0x00,
	// (byte) 0xFF });
	// // End date and linked status, or NULL data if not significative
	// outerData.get(2).setOctetString(
	// new byte[] { (byte) 0x07, (byte) 0xDE, (byte) 0x02,
	// (byte) 0x0F, (byte) 0xFF, (byte) 0x00, (byte) 0x00,
	// (byte) 0x00, (byte) 0xFF, (byte) 0x80, (byte) 0x00,
	// (byte) 0xFF });
	// // Time 1 and linked status, or NULL data if not significative
	// // Time 2 and linked status, or NULL data if not significative
	// outerData.get(3).setArray(arrayNull);
	//
	// // Finally defining the selective access description and adding it
	// // the get parameter used in first get
	// Data outerStruct = new Data();
	// outerStruct.setStructure(outerData);
	//
	// ObisCode obis = new ObisCode(0, 0, 99, 1, 0, 255);
	// int classId = 7;
	// int attribute = 2;
	//
	// GetResult getResult = data.GetObjectWithSelector(connection, obis,
	// classId, attribute, outerStruct, 1);
	//
	// if (getResult.isSuccess()) {
	// System.out.println("Success!");
	// parse.createXml("C:\\teste.xml", getResult, obis, classId, attribute); //
	// this function create the XML file using the GetResult
	// // parse.printLog(getResult);
	// } else {
	// System.out.println("Reading Error. ErrorCode: "
	// + getResult.getResultCode());
	// }
	// }
	//
	public boolean setActionEx(IClientConnection connection) {
		// TODO: Action example
		Log.i(tag, "Action...");
		// ResetNonFatalAlarmScriptTable
		ObisCode obisAction = new ObisCode(0, 0, 10, 1, 0, 255);
		MethodResult actionResult = data.Action(connection, obisAction, 9, 1, 1);

		if (actionResult.isSuccess()) {
			Log.i(tag, "Action success...");
			return true;
		} else {
			Log.i(tag, "Action error...");
			return false;
		}
	}

	//
	// public static void setObjectEx(IClientConnection connection) {
	// // TODO: Set clock example
	//
	// AccessResultCode setResult =
	// data.SetObjectXML(connection,"C:\\read.xml"); //Set clock using octet
	// string
	//
	// if (setResult == AccessResultCode.SUCCESS) {
	// System.out.println("Set Success");
	// } else {
	// System.out.println("Set Error: " + setResult.toString());
	// }
	// }
	//
	
	public void setClockObjectEx(IClientConnection connection) {
		// TODO: Set clock example

		ObisCode obisSet = new ObisCode(0, 0, 1, 0, 0, 255);

		byte[] dateOctet = { (byte) 0x07, (byte) 0xde, (byte) 0x01,
				(byte) 0x01, (byte) 0xFF, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0xff, (byte) 0x80, (byte) 0x00, (byte) 0x00 };
		
		AccessResultCode setResult = data.SetDateTimeObject(connection,
				obisSet, 8, 2, dateOctet); // Set clock using octet string
		// AccessResultCode setResult = data.SetDateTimeObject(connection, obis,
		// 8, 2); //Set clock using system time

		if (setResult == AccessResultCode.SUCCESS) {
			Log.i(tag, "Set Success");
		} else {
			Log.i(tag, "Set Error: " + setResult.toString());
		}
	}

}
