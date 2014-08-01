package com.thinken.azmobmeter.driver;

import java.io.IOException;

import org.openmuc.jdlms.client.IClientConnection;
import org.openmuc.jdlms.client.hdlc.HdlcAddress;
import org.openmuc.jdlms.client.hdlc.physical.PhysicalConnection;

import android.util.Log;



public class DriverTest {
	
	private static final int dlms_upperAddress = 1;
	private static final int dlms_lowerAddress = 17;
	private static final int dlms_addressSize = 4;
	private static final int CONN_TIMEOUT = 5000;

	private DriverManagement connManager = new DriverManagement();
	//private itr_driver_setup setup = new itr_driver_setup();
	//private itr_driver_dataExchange data = new itr_driver_dataExchange();
	//private itr_driver_parse_pduXml parse = new itr_driver_parse_pduXml();

	public String connect(String device) throws InterruptedException {
		// TODO Auto-generated method stub

		HdlcAddress hdlcAddress = new HdlcAddress(dlms_upperAddress,
				dlms_lowerAddress, dlms_addressSize);
		IClientConnection connection = connManager.buildHDLCConnection(hdlcAddress, device, 1);

		try {
			Log.i("CONNECTION", "Trying to connect...");
			//PhysicalConnection phy = new PhysicalConnection(device);
			// Create the connection and connect using HDLC
			//Log.i("CONNECTION", "Connected");
			Log.i("CONNECTION", "Connecting...");
			connection.connect(CONN_TIMEOUT, "ABCDEFGH".getBytes("US-ASCII"));
			
			//getObjectEx(connection);
			//getObjectWithSelectorEx(connection);
			//setActionEx(connection);
			//setObjectEx(connection);

			connection.disconnect(false);
			
		//} catch (IOException ex) {
			//connection.disconnect(false);
			//return "IOException "+ex.toString();
		} catch (Exception ex) {
			connection.disconnect(false);
			return "Exception "+ex.toString();
		}
		return "Ok";
	}

//	public static void getObjectEx(IClientConnection connection) {
//		// TODO: Get clock example
//		// ObisCode obis = new ObisCode(0,0,98,133,5,255);//this function create
//		// the OBIS code structure using the six elements as integer
//		// GetResult getResult = data.GetObject(connection, obis, 7, 0);
//
//		 ObisCode obis = new ObisCode(0,0,130,0,2,255);
//		 int classId = 1;
//		 int attribute = 2;
//		 
//		 GetResult getResult = data.GetObject(connection, obis, classId, attribute);
//
//		if (getResult.isSuccess()) {
//			System.out.println("Success!");
//			parse.createXml("C:\\teste.xml", getResult, obis, classId, attribute); // this function create the XML file using the GetResult
//			// parse.printLog(getResult);
//		} else {
//			System.out.println("Reading Error. ErrorCode: "
//					+ getResult.getResultCode());
//		}
//	}
//
//	public void getObjectWithSelectorEx(IClientConnection connection) {
//
//		// TODO: Get object with selector (i.e LoadProfile)
//		// Preparation for selective access description
//		// Structure defining the range object
//		List<Data> outerData = new ArrayList<Data>(4);
//		List<Data> arrayNull = new ArrayList<Data>(1);
//
//		// First element of the selective access is the above definition
//		outerData.add(new Data());
//		outerData.add(new Data());
//		outerData.add(new Data());
//		outerData.add(new Data());
//
//		outerData.get(0).setNull();
//		// Start of interval and linked status, or NULL data if not significative
//		outerData.get(1).setOctetString(
//				new byte[] { (byte) 0x07, (byte) 0xDE, (byte) 0x02,
//						(byte) 0x0E, (byte) 0xFF, (byte) 0x00, (byte) 0x00,
//						(byte) 0x00, (byte) 0xFF, (byte) 0x80, (byte) 0x00,
//						(byte) 0xFF });
//		// End date and linked status, or NULL data if not significative
//		outerData.get(2).setOctetString(
//				new byte[] { (byte) 0x07, (byte) 0xDE, (byte) 0x02,
//						(byte) 0x0F, (byte) 0xFF, (byte) 0x00, (byte) 0x00,
//						(byte) 0x00, (byte) 0xFF, (byte) 0x80, (byte) 0x00,
//						(byte) 0xFF });
//		// Time 1 and linked status, or NULL data if not significative
//		// Time 2 and linked status, or NULL data if not significative
//		outerData.get(3).setArray(arrayNull);
//
//		// Finally defining the selective access description and adding it
//		// the get parameter used in first get
//		Data outerStruct = new Data();
//		outerStruct.setStructure(outerData);
//
//		ObisCode obis = new ObisCode(0, 0, 99, 1, 0, 255);
//		 int classId = 7;
//		 int attribute = 2;
//		 
//		GetResult getResult = data.GetObjectWithSelector(connection, obis, classId, attribute, outerStruct, 1);
//
//		if (getResult.isSuccess()) {
//			System.out.println("Success!");
//			parse.createXml("C:\\teste.xml", getResult, obis, classId, attribute); // this function create the XML file using the GetResult
//			// parse.printLog(getResult);
//		} else {
//			System.out.println("Reading Error. ErrorCode: "
//					+ getResult.getResultCode());
//		}
//	}
//
//	public void setActionEx(IClientConnection connection) {
//		// TODO: Action example
//		 ObisCode obisAction = new ObisCode(0,0,10,1,0,255);
//		 MethodResult actionResult = data.Action(connection, obisAction, 9, 1, 1);
//		
//		if (actionResult.isSuccess()) {
//			System.out.println("Action success");
//		} else {
//			System.out.println("Action error");
//		}
//	}
//	
//	public static void setObjectEx(IClientConnection connection) {
//		// TODO: Set clock example
//			
//		AccessResultCode setResult = data.SetObjectXML(connection,"C:\\read.xml"); //Set clock using octet string
//
//		if (setResult == AccessResultCode.SUCCESS) {
//			System.out.println("Set Success");
//		} else {
//			System.out.println("Set Error: " + setResult.toString());
//		}
//	}
//	
//	public void setClockObjectEx(IClientConnection connection) {
//		// TODO: Set clock example
//		
//		ObisCode obisSet = new ObisCode(0,0,1,0,0,255);
//		
//		byte[] dateOctet = {(byte)0x07, (byte)0xde, (byte)0x01,
//		(byte)0x01, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00,
//		(byte)0xff, (byte)0x80, (byte)0x00, (byte)0x00};//Octet string with date and time
//		
//		AccessResultCode setResult = data.SetDateTimeObject(connection, obisSet, 8, 2, dateOctet); //Set clock using octet string
//		//AccessResultCode setResult = data.SetDateTimeObject(connection, obis, 8, 2); //Set clock using system time
//
//		if (setResult == AccessResultCode.SUCCESS) {
//			System.out.println("Set Success");
//		} else {
//			System.out.println("Set Error: " + setResult.toString());
//		}
//	}

}
	
