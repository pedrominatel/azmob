package driver.meter;

import java.io.IOException;

import org.openmuc.jdlms.client.IClientConnection;
import org.openmuc.jdlms.client.IClientConnectionFactory;
import org.openmuc.jdlms.client.ClientConnectionSettings.Authentication;
import org.openmuc.jdlms.client.ClientConnectionSettings.ConfirmedMode;
import org.openmuc.jdlms.client.ClientConnectionSettings.ReferencingMethod;
import org.openmuc.jdlms.client.hdlc.HdlcAddress;
import org.openmuc.jdlms.client.hdlc.HdlcClientConnectionSettings;

public class itr_driver_connectionManager {

	public IClientConnection buildHDLCConnection(HdlcAddress hdlcAddress, String commPort, int baudRate, int clientProfile){
		
		IClientConnection connection = null;
		
		HdlcClientConnectionSettings hdlcConnSettings = new HdlcClientConnectionSettings(commPort,
		        new HdlcAddress(clientProfile), hdlcAddress, ReferencingMethod.LN);

		hdlcConnSettings.setAuthentication(Authentication.LOW);
		hdlcConnSettings.setBaudrate(baudRate);
		hdlcConnSettings.setUseHandshake(false);
		hdlcConnSettings.setConfirmedMode(ConfirmedMode.CONFIRMED);
		
		IClientConnectionFactory factory =  HdlcClientConnectionSettings.getFactory();
		
		try {
			connection = factory.createClientConnection(hdlcConnSettings);
		} catch (IOException ioExp) {
			connection = null;
		}
		return connection;
	}
	
	public void connect(){
		
		
	}
	
	
}
