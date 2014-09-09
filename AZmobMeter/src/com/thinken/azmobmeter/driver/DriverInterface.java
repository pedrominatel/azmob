package com.thinken.azmobmeter.driver;

import java.io.IOException;

import org.openmuc.jdlms.client.AccessResultCode;
import org.openmuc.jdlms.client.GetResult;
import org.openmuc.jdlms.client.IClientConnection;
import org.openmuc.jdlms.client.MethodResult;
import org.openmuc.jdlms.client.ObisCode;
import org.openmuc.jdlms.client.hdlc.HdlcAddress;

import com.thinken.azmobmeter.utils.Filesys;
import com.thinken.azmobmeter.utils.Logging;

import android.bluetooth.BluetoothSocket;

public class DriverInterface {

	private static final int dlms_upperAddress = 1;
	private static final int dlms_lowerAddress = 17;
	private static final int dlms_addressSize = 4;
	private static final int CONN_TIMEOUT = 5000;

	private DriverManagement connManager = new DriverManagement();
	private DriverDataExchange data = new DriverDataExchange();
	private DriverPduIO parser = new DriverPduIO();
	
	private Filesys fs = new Filesys();
	
	Logging log = new Logging();
	
	private String tag = "DriverInterface";

	/**
	 * 
	 */
	public IClientConnection mtr_connect(BluetoothSocket socket) throws InterruptedException {
		// TODO Auto-generated method stub

		HdlcAddress hdlcAddress = new HdlcAddress(dlms_upperAddress, dlms_lowerAddress, dlms_addressSize);
		IClientConnection connection = connManager.buildHDLCConnection(hdlcAddress, socket, 1);

		try {
			log.log(tag, log.INFO, "Connecting", true);

			if (!connection.isConnected()) {
				connection.connect(CONN_TIMEOUT, "ABCDEFGH".getBytes("US-ASCII"));
			}

			if (connection.isConnected()) {
				log.log(tag, log.INFO, "Connected", true);
			}

		} catch (IOException ex) {
			log.log(tag, log.ERROR, ex.toString(), true);
			return null;
		} catch (Exception ex) {
			log.log(tag, log.ERROR, ex.toString(), true);
			return null;
		}
		return connection;
	}

	/**
	 * 
	 */
	public void mtr_disconnect(IClientConnection conn) {

		if (conn.isConnected()) {
			conn.disconnect(false);	
		}

	}

	/**
	 * 
	 */
	public boolean mtr_get_Object(IClientConnection connection) {
		// TODO: Get clock example
		// ObisCode obis = new ObisCode(0,0,98,133,5,255);//this function create
		// the OBIS code structure using the six elements as integer
		// GetResult getResult = data.GetObject(connection, obis, 7, 0);

//		ObisCode obis = new ObisCode(0, 0, 130, 0, 2, 255);//index parameters
//		int classId = 1;
//		int attribute = 2;

		//ObisCode obis = new ObisCode(0,0,98,133,2,255);//all energies
		ObisCode obis = new ObisCode(0,0,99,98,0,255);//logbook
		
		int classId = 7;
		int attribute = 2;
		
		GetResult getResult = data.GetObject(connection, obis, classId,	attribute);

		if (getResult.isSuccess()) {
			log.log(tag, log.INFO, "Read Success!", true);
			parser.createXml("1234567890", fs.fsSys_timeStamp()+"_read.xml", getResult, obis, classId, attribute); //this function create the XML file using the
			return true;
		} else {
			log.log(tag, log.WARNING, "Read Error " + getResult.getResultCode(), true);
			return false;
		}
	}
	
	/**
	 * 
	 */
	public boolean mtr_get_Object(IClientConnection connection, String serialNumber, String object, ObisCode obis, int classId, int attribute) {

		GetResult getResult = data.GetObject(connection, obis, classId,	attribute);

		if (getResult.isSuccess()) {
			log.log(tag, log.INFO, "Read Success!", true);
			parser.createXml(serialNumber, object+".xml", getResult, obis, classId, attribute); //this function create the XML file using the
			return true;
		} else {
			log.log(tag, log.WARNING, "Read Error " + getResult.getResultCode(), true);
			return false;
		}
	}
	
	/**
	 * 
	 */
	public String mtr_get_SerialNumber(IClientConnection connection) {
		// get serial number
		//<CosemObject Name="SerialNumber" LogicalName="0;0;96;1;0;255;" ClassId="1" Index="2">
		
		ObisCode obis = new ObisCode(0,0,96,1,0,255);//index parameters
		int classId = 1;
		int attribute = 2;
		
		String serialNumber = "";

		GetResult getResult = data.GetObject(connection, obis, classId,	attribute);

		if (getResult.isSuccess()) {
			log.log(tag, log.INFO, "Read Success!", true);
			serialNumber = (String)parser.pdu_decodeSingleNode(getResult.getResultData());
		} else {
			log.log(tag, log.WARNING, "Read Error " + getResult.getResultCode(), true);
			return null;
		}
		
		return serialNumber;
	}
	
	/**
	 * 
	 */
	public String mtr_get_FirmwareVersion(IClientConnection connection) {
		// get firmware version
		//<CosemObject Name="CompleteExtFirmwareIdParameters" LogicalName="0;0;142;1;3;255;" ClassId="1" Index="2">
		ObisCode obis = new ObisCode(0,0,142,1,3,255);//index parameters
		int classId = 1;
		int attribute = 2;
		
		String firmwareVersion = "generic";

		GetResult getResult = data.GetObject(connection, obis, classId,	attribute);

		if (getResult.isSuccess()) {
			log.log(tag, log.INFO, "Read Success!", true);
			firmwareVersion = (String)parser.pdu_decodeSingleNode(getResult.getResultData());
			
			String newFw = firmwareVersion.replace(".", "");
			newFw = newFw.substring(0, 4);
			
			return newFw;
		} else {
			log.log(tag, log.WARNING, "Read Error " + getResult.getResultCode(), true);
		}
		
		return firmwareVersion;
	}
	
	/**
	 * 
	 */
	public String mtr_get_CurrentDateTime(IClientConnection connection) {
		// get CurrentDateAndTime
		ObisCode obis = new ObisCode(0,0,1,0,0,255);//index parameters
		int classId = 8;
		int attribute = 2;
		
		String currentDateAndTime = "";

		GetResult getResult = data.GetObject(connection, obis, classId,	attribute);

		if (getResult.isSuccess()) {
			log.log(tag, log.INFO, "Read Success!", true);
			currentDateAndTime = (String)parser.pdu_decodeSingleNode(getResult.getResultData());
		} else {
			log.log(tag, log.WARNING, "Read Error " + getResult.getResultCode(), true);
		}
		
		return currentDateAndTime;
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
	
	/**
	 * 
	 */
	public boolean mtr_action_ResetNonFatalAlarmScriptTable(IClientConnection connection) {
		// TODO: Action example
		// ResetNonFatalAlarmScriptTable
		ObisCode obisAction = new ObisCode(0, 0, 10, 1, 0, 255);
		MethodResult actionResult = data.Action(connection, obisAction, 9, 1, 1);

		if (actionResult.isSuccess()) {
			log.log(tag, log.INFO, "Action Success!", true);
			return true;
		} else {
			log.log(tag, log.WARNING, "Set Error "+actionResult.toString(), true);
			return false;
		}
	}
	
	/**
	 * 
	 */
	public boolean mtr_action_AsynchronousEOBScriptTable(IClientConnection connection) {
		// TODO Auto-generated method stub
		// TODO: Action example
		// ResetNonFatalAlarmScriptTable
		ObisCode obisAction = new ObisCode(0, 0, 10, 0, 1, 255);
		MethodResult actionResult = data.Action(connection, obisAction, 9, 1, 1);

		if (actionResult.isSuccess()) {
			log.log(tag, log.INFO, "Action Success!", true);
			return true;
		} else {
			log.log(tag, log.WARNING, "Set Error "+actionResult.toString(), true);
			return false;
		}
	}
	
	/**
	 * 
	 */
	public boolean mtr_set_CurrentDateAndTime(IClientConnection connection) {
		// TODO: Set clock example

		ObisCode obisSet = new ObisCode(0, 0, 1, 0, 0, 255);
		
		AccessResultCode setResult = data.SetDateTime(connection, obisSet, 8, 2); //Set clock using system time

		if (setResult == AccessResultCode.SUCCESS) {
			log.log(tag, log.INFO, "Set Success!", true);
			return true;
		} else {
			log.log(tag, log.WARNING, "Set Error "+setResult.toString(), true);
			return false;
		}
	}

}
