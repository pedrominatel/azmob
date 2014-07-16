package driver.meter;


public class driver_setup {

	private int dlms_upperAddress;
	private int dlms_lowerAddress;
	private int dlms_addressSize;
	private int default_baudrate;
	private int default_dlmsClientAddress;
	private String commPort;
	private String password;
	
	public enum BaudRate {
		B300,//baudrate 300 (for IEC handshake)
		B600,//not used for Itron meters
		B1200,//not used for Itron meters
		B2400,//not used for Itron meters
		B4800,//not used for Itron meters
		B9600,//default for SL7000
		B19200//default for SL7000 IEC7 over HDLC
	}
	
	public driver_setup(int dlms_upperAddress, int dlms_lowerAddress,
			int dlms_addressSize, int default_baudrate, int default_dlmsClientAddress,
			String commPort, 	String password ){
		
		this.dlms_upperAddress = dlms_upperAddress;
		this.dlms_lowerAddress = dlms_lowerAddress;
		this.dlms_addressSize = dlms_addressSize;
		this.default_baudrate = default_baudrate;
		this.default_dlmsClientAddress = default_dlmsClientAddress;
		this.commPort = commPort;
		this.password = password;
	}
	
	public driver_setup() {
		// TODO Auto-generated constructor stub
	}

	public int get_DlmsUpperAddress(){
		return dlms_upperAddress;
	}
	
	public int get_DlmsLowerAddress(){
		return dlms_lowerAddress;
	}
	
	public int get_DlmsAddressSize(){
		return dlms_addressSize;
	}
	
	public int get_BaudRate(){
		return default_baudrate;
	}
	
	public int get_DlmsClientAddress(){
		return default_dlmsClientAddress;
	}
	
	public String get_SerialPort(){
		return commPort;
	}
	
	public String get_Password(){
		return password;
	}
	public void set_DlmsUpperAddress(int dlms_upperAddress){
		this.dlms_upperAddress = dlms_upperAddress;
	}
	
	public void set_DlmsLowerAddress(int dlms_lowerAddress){
		this.dlms_lowerAddress = dlms_lowerAddress;
	}
	
	public void set_DlmsAddressSize(int dlms_addressSize){
		this.dlms_addressSize = dlms_addressSize;
	}
	
	public void set_BaudRate(int default_baudrate){
		this.default_baudrate = default_baudrate;
	}
	
	public void set_DlmsClientAddress(int default_dlmsClientAddress){
		this.default_dlmsClientAddress = default_dlmsClientAddress;
	}
	
	public void set_SerialPort (String commPort){
		this.commPort = commPort;
	}
	
	public void set_Password(String password){
		this.password = password;
	}
	
	public void loadDefaultHdlcValues(){
		this.dlms_upperAddress = 1;
		this.dlms_lowerAddress = 17;
		this.dlms_addressSize = 4;
		this.default_baudrate = 9600;
		this.default_dlmsClientAddress = 1;
		this.commPort = "COM15";
		this.password = "ABCEDFGH";
	}
	
}
