package com.thinken.azmobmeter.driver;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.openmuc.jdlms.client.AccessResultCode;
import org.openmuc.jdlms.client.Data;
import org.openmuc.jdlms.client.GetRequest;
import org.openmuc.jdlms.client.GetResult;
import org.openmuc.jdlms.client.IClientConnection;
import org.openmuc.jdlms.client.MethodRequest;
import org.openmuc.jdlms.client.MethodResult;
import org.openmuc.jdlms.client.ObisCode;
import org.openmuc.jdlms.client.SelectiveAccessDescription;
import org.openmuc.jdlms.client.SetRequest;

import com.thinken.azmobmeter.driver.DriverPduIO;;

public class DriverDataExchange {

	private static final int HIGH_TIMEOUT = 60000;//60 seconds in miliseconds
	private static final int LOW_TIMEOUT =5000;//5 seconds in miliseconds
	private static final int DEFAULT_TIMEOUT = 3000;//3 seconds in miliseconds
	
	DriverPduIO parse = new DriverPduIO();
	
//GetObject
//	in Connection
//	in ObisCode
//	in ClassID
//	out GetResult
	public GetResult GetObject(IClientConnection connection, ObisCode obis, int classId, int attributeId){
		// Get parameter to read
		
		//itr_driver_parse_pduXml parse = new itr_driver_parse_pduXml();
		//parse.xmlHeader(obis, classId, attributeId);
		
		GetRequest getObject = new GetRequest(classId, obis, attributeId);
		GetResult result = null;
		// Read entire event log
		try {
			List<GetResult> getResults = connection.get(HIGH_TIMEOUT, true, getObject); //set the timeout with more than 15s for big data i.e Events
			result = getResults.get(0); 
		} catch (Exception e) {
			return null;
		}
		return result;
	}
	
//GetObjectWithSelector
//	in Connection
//	in ObisCode
//	in ClassID
//	out GetResult
	public GetResult GetObjectWithSelector(IClientConnection connection, ObisCode obis, int classId, int attributeId, SelectiveAccessDescription selector){
		// Get parameter to read
		GetRequest getObject = new GetRequest(classId, obis, attributeId);
		
		GetResult result = null;
		getObject.setAccessSelection(selector);

		// Read entire event log
		try {
			List<GetResult> getResults = connection.get(HIGH_TIMEOUT, true, getObject);//set the timeout with more than 15s for big data i.e Load Profile
			result = getResults.get(0); 
		} catch (Exception e) {
			return null;
		}
		return result;
	}
	
//GetObjectWithSelector
//	in Connection
//	in ObisCode
//	in ClassID
//	out GetResult
	public GetResult GetObjectWithSelector(IClientConnection connection, ObisCode obis, int classId, int attributeId, Data struct, int accessSelector){
		
		// Get parameter to read
		GetRequest getObject = new GetRequest(classId, obis, attributeId);
		getObject.setAccessSelection(new SelectiveAccessDescription(accessSelector, struct));

		GetResult result = null;
		// Read entire event log
		try {
			List<GetResult> getResults = connection.get(HIGH_TIMEOUT, true, getObject);//set the timeout with more than 15s for big data i.e Load Profile
			result = getResults.get(0); 
		} catch (Exception e) {
			return null;
		}
		return result;
	}

//SetObject
//	in Connection
//	in ObisCode
//	in ClassID
//	out AccessResultCode
public AccessResultCode SetObjectXML(IClientConnection connection, String xmlFilePath){
	
	AccessResultCode result = null;
	SetRequest setObject = parse.readXmlStructure(xmlFilePath);
	
	try {
		List<AccessResultCode> getResults = connection.set(DEFAULT_TIMEOUT, true, setObject); //set the timeout with more than 15s for big data i.e Events
		result = getResults.get(0); 
	} catch (Exception e) {
		return null;
	}
	return result;
}

//SetObject
//in Connection
//in ObisCode
//in ClassID
//out AccessResultCode
public AccessResultCode SetObject(IClientConnection connection, ObisCode obis, int classId, int attributeId, SetRequest setObject){

// Get parameter to read
setObject = new SetRequest(classId, obis, attributeId);
AccessResultCode result = null;

//setObject.data()...

try {
	List<AccessResultCode> getResults = connection.set(DEFAULT_TIMEOUT, true, setObject); //set the timeout with more than 15s for big data i.e Events
	result = getResults.get(0); 
} catch (Exception e) {
	return null;
}
return result;
}

//SetDateTimeObject
//	in Connection
//	in ObisCode
//	in ClassID
//	out AccessResultCode
public AccessResultCode SetDateTime(IClientConnection connection, ObisCode obis, int classId, int attributeId){
	
	// Get parameter to read
	SetRequest setObject = new SetRequest(classId, obis, attributeId);
	AccessResultCode result = null;

	Calendar.getInstance().getTime();
	Calendar calendar = new GregorianCalendar();
	
	setObject.data().setOctetString(parse.encodeClock(calendar));

	try {
		List<AccessResultCode> getResults = connection.set(DEFAULT_TIMEOUT, true, setObject); //set the timeout with more than 15s for big data i.e Events
		result = getResults.get(0);
	} catch (Exception e) {
		return null;
	}
	return result;
}

//SetDateTimeObject
//	in Connection
//	in ObisCode
//	in ClassID
//	out AccessResultCode
public AccessResultCode SetDateTime(IClientConnection connection, ObisCode obis, int classId, int attributeId, byte[] dateOctet){
	
	// Get parameter to read
	SetRequest setObject = new SetRequest(classId, obis, attributeId);
	AccessResultCode result = null;

	setObject.data().setOctetString(dateOctet);
	
	try {
		List<AccessResultCode> getResults = connection.set(DEFAULT_TIMEOUT, true, setObject); //set the timeout with more than 15s for big data i.e Events
		result = getResults.get(0); 
	} catch (Exception e) {
		return null;
	}
	return result;
}
	
//Action
//	in Connection
//	in ObisCode
//	in ClassID
//	out MethodResult
	public MethodResult Action(IClientConnection connection, ObisCode obis, int classId, int attributeId, int data){
		// Action
		MethodRequest action = new MethodRequest(classId, obis, attributeId);
		action.data().setUnsigned16(data);
		MethodResult result = null;
		// Read entire event log
		try {
			List<MethodResult> actionResults = connection.action(LOW_TIMEOUT, action);
			result = actionResults.get(0); 
		} catch (Exception e) {
			return null;
		}
		return result;
	}
	
}
