/**
 * This class file was automatically generated by jASN1 (http://www.openmuc.org)
 */

package org.openmuc.asn1.cosem;

import org.openmuc.jasn1.axdr.types.AxdrOctetString;

public class Cosem_Date_Time extends AxdrOctetString {

	public static final int length = 12;

	public Cosem_Date_Time() {
		super(length);
	}

	public Cosem_Date_Time(byte[] octetString) {
		super(length, octetString);
	}

	public Cosem_Date_Time(String octetString) {
		super(length, AxdrOctetString.getBytesFromString(octetString));
	}

}
