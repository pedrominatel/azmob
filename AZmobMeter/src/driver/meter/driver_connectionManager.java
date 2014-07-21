package driver.meter;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.openmuc.jdlms.client.ClientConnectionSettings;
import org.openmuc.jdlms.client.IClientConnection;
import org.openmuc.jdlms.client.IClientConnectionFactory;
import org.openmuc.jdlms.client.ClientConnectionSettings.Authentication;
import org.openmuc.jdlms.client.ClientConnectionSettings.ConfirmedMode;
import org.openmuc.jdlms.client.ClientConnectionSettings.ReferencingMethod;
import org.openmuc.jdlms.client.hdlc.HdlcAddress;
import org.openmuc.jdlms.client.hdlc.HdlcClientConnectionSettings;
import org.openmuc.jdlms.client.impl.ClientConnectionFactory;
import org.openmuc.jdlms.client.ip.TcpClientConnectionSettings;

import android.util.Log;

public class driver_connectionManager {

	public IClientConnection buildHDLCConnection(HdlcAddress hdlcAddress, String btAddress, int clientProfile){
		
		IClientConnection connection = null;
		
		Log.i("CONNECTION", "Building HDLC");
		Log.i("CONNECTION", "HDLC: "+hdlcAddress.toString());
		Log.i("CONNECTION", "Bluetooth Address: "+btAddress);
		Log.i("CONNECTION", "Client Profile: "+clientProfile);
		
		HdlcClientConnectionSettings hdlcConnSettings = new HdlcClientConnectionSettings(btAddress,
		        new HdlcAddress(clientProfile), hdlcAddress, ReferencingMethod.LN);
		
		Log.i("CONNECTION", "Building HDLC Settings");
		
		hdlcConnSettings.setAuthentication(Authentication.LOW);
		hdlcConnSettings.setUseHandshake(false);
		hdlcConnSettings.setConfirmedMode(ConfirmedMode.CONFIRMED);
		
		Log.i("CONNECTION", "Config HDLC");
		
		if(hdlcConnSettings.isFullyParametrized()) {
			Log.i("CONNECTION", "IS Fully Parametrized");
		} else {
			Log.i("CONNECTION", "NOT Fully Parametrized");
		}
		
		IClientConnectionFactory factory =  HdlcClientConnectionSettings.getFactory();
		
		try {
			connection = factory.createClientConnection(hdlcConnSettings);
			Log.i("CONNECTION", "Building Factory");
			return connection;
		} catch (IOException ioExp) {
			Log.i("CONNECTION", "Error: "+ioExp.toString());
			return null;
		}
		
	}
}
