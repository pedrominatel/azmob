package com.thinken.azmobmeter.driver;

import org.openmuc.jdlms.client.IClientConnection;
import org.openmuc.jdlms.client.IClientConnectionFactory;
import org.openmuc.jdlms.client.ClientConnectionSettings.Authentication;
import org.openmuc.jdlms.client.ClientConnectionSettings.ConfirmedMode;
import org.openmuc.jdlms.client.ClientConnectionSettings.ReferencingMethod;
import org.openmuc.jdlms.client.hdlc.HdlcAddress;
import org.openmuc.jdlms.client.hdlc.HdlcClientConnectionSettings;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class DriverManagement {
	
	public IClientConnection buildHDLCConnection(HdlcAddress hdlcAddress, BluetoothSocket btSocket, int clientProfile){
		
		IClientConnection connection = null;
		
		HdlcClientConnectionSettings hdlcConnSettings = new HdlcClientConnectionSettings(btSocket,
		        new HdlcAddress(clientProfile), hdlcAddress, ReferencingMethod.LN);
		
		//Log.i("CONNECTION", "btDevice = "+hdlcConnSettings.getBtAddress());
		
		hdlcConnSettings.setAuthentication(Authentication.LOW);
		hdlcConnSettings.setUseHandshake(false);
		hdlcConnSettings.setConfirmedMode(ConfirmedMode.CONFIRMED);
		
		IClientConnectionFactory factory =  HdlcClientConnectionSettings.getFactory();
		
		try {
			connection = factory.createClientConnection(hdlcConnSettings);
		} catch (Exception ioExp) {
			Log.i("CONNECTION", "Error: "+ioExp.toString());
			connection = null;
		}
		Log.i("CONNECTION", "Return Factory");
		return connection;
	}
	
}
