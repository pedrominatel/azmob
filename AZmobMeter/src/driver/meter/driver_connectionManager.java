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
		
		Log.i("CONNECTION", "Building HDLC");
		Log.i("CONNECTION", "HDLC: "+hdlcAddress.toString());
		Log.i("CONNECTION", "Bluetooth Address: "+btAddress);
		Log.i("CONNECTION", "Client Profile: "+clientProfile);
		
		HdlcAddress serverAddress = new HdlcAddress(1, 17, 4);
		
		HdlcClientConnectionSettings settings = new HdlcClientConnectionSettings(btAddress,
		        new HdlcAddress(1), serverAddress, ReferencingMethod.LN);
		
		Log.i("CONNECTION", "Building HDLC Settings");
		
		settings.setAuthentication(Authentication.LOW);
		settings.setUseHandshake(false);
		settings.setConfirmedMode(ConfirmedMode.CONFIRMED);
		
		Log.i("CONNECTION", "Config HDLC");
		
		if(settings.isFullyParametrized()) {
			Log.i("CONNECTION", "IS Fully Parametrized");
		} else {
			Log.i("CONNECTION", "NOT Fully Parametrized");
		}
		
		try {
			IClientConnectionFactory factory =  HdlcClientConnectionSettings.getFactory();
			//IClientConnectionFactory factory =  new ClientConnectionFactory();
			IClientConnection connection = factory.createClientConnection(settings);
			Log.i("CONNECTION", "Building Factory");
			return connection;
		} catch (IOException ioExp) {
			return null;
		}
		
	}
}
