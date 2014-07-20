package driver.meter;

import java.io.IOException;

import org.openmuc.jdlms.client.IClientConnection;
import org.openmuc.jdlms.client.IClientConnectionFactory;
import org.openmuc.jdlms.client.ClientConnectionSettings.Authentication;
import org.openmuc.jdlms.client.ClientConnectionSettings.ConfirmedMode;
import org.openmuc.jdlms.client.ClientConnectionSettings.ReferencingMethod;
import org.openmuc.jdlms.client.hdlc.HdlcAddress;
import org.openmuc.jdlms.client.hdlc.HdlcClientConnectionSettings;

import android.util.Log;

public class driver_connectionManager {

	public IClientConnection buildHDLCConnection(HdlcAddress hdlcAddress, String btAddress, int clientProfile){
		
		IClientConnection connection = null;
		Log.i("CONNECTION", "Building HDLC");
		HdlcClientConnectionSettings hdlcConnSettings = new HdlcClientConnectionSettings(btAddress,
		        new HdlcAddress(clientProfile), hdlcAddress, ReferencingMethod.LN);
		Log.i("CONNECTION", "Building HDLC Settings");
		hdlcConnSettings.setAuthentication(Authentication.LOW);
		hdlcConnSettings.setUseHandshake(false);
		hdlcConnSettings.setConfirmedMode(ConfirmedMode.CONFIRMED);
		Log.i("CONNECTION", "Config HDLC");
		
		
		
		IClientConnectionFactory factory =  HdlcClientConnectionSettings.getFactory();
		
		
		
		Log.i("CONNECTION", "Building Factory");
		try {
			connection = factory.createClientConnection(hdlcConnSettings);
		} catch (IOException ioExp) {
			connection = null;
		}
		return connection;
	}
}
