/**
 * This class file was automatically generated by jASN1 (http://www.openmuc.org)
 */

package org.openmuc.asn1.cosem;

import java.io.IOException;
import java.io.InputStream;

import org.openmuc.jasn1.axdr.AxdrByteArrayOutputStream;
import org.openmuc.jasn1.axdr.AxdrType;
import org.openmuc.jasn1.axdr.types.AxdrBoolean;
import org.openmuc.jasn1.axdr.types.AxdrDefault;
import org.openmuc.jasn1.axdr.types.AxdrOctetString;
import org.openmuc.jasn1.axdr.types.AxdrOptional;

public class InitiateRequest implements AxdrType {

	public byte[] code = null;
	public AxdrOptional<AxdrOctetString> dedicated_key = new AxdrOptional<AxdrOctetString>(new AxdrOctetString(), false);

	public AxdrDefault<AxdrBoolean> response_allowed = new AxdrDefault<AxdrBoolean>(new AxdrBoolean(),
			new AxdrBoolean());

	public AxdrOptional<Integer8> proposed_quality_of_service = new AxdrOptional<Integer8>(new Integer8(), false);

	public Unsigned8 proposed_dlms_version_number = null;

	public Conformance proposed_conformance = null;

	public Unsigned16 client_max_receive_pdu_size = null;

	public InitiateRequest() {
	}

	public InitiateRequest(byte[] code) {
		this.code = code;
	}

	public InitiateRequest(AxdrOctetString dedicated_key, AxdrBoolean response_allowed,
			Integer8 proposed_quality_of_service, Unsigned8 proposed_dlms_version_number,
			Conformance proposed_conformance, Unsigned16 client_max_receive_pdu_size) {
		this.dedicated_key.setValue(dedicated_key);
		this.response_allowed.setValue(response_allowed);
		this.proposed_quality_of_service.setValue(proposed_quality_of_service);
		this.proposed_dlms_version_number = proposed_dlms_version_number;
		this.proposed_conformance = proposed_conformance;
		this.client_max_receive_pdu_size = client_max_receive_pdu_size;
	}

	@Override
	public int encode(AxdrByteArrayOutputStream axdrOStream) throws IOException {

		int codeLength;

		if (code != null) {
			codeLength = code.length;
			for (int i = code.length - 1; i >= 0; i--) {
				axdrOStream.write(code[i]);
			}
		}
		else {
			codeLength = 0;
			codeLength += client_max_receive_pdu_size.encode(axdrOStream);

			codeLength += proposed_conformance.encode(axdrOStream);

			codeLength += proposed_dlms_version_number.encode(axdrOStream);

			codeLength += proposed_quality_of_service.encode(axdrOStream);

			codeLength += response_allowed.encode(axdrOStream);

			codeLength += dedicated_key.encode(axdrOStream);

		}

		return codeLength;

	}

	@Override
	public int decode(InputStream iStream) throws IOException {
		int codeLength = 0;

		dedicated_key = new AxdrOptional<AxdrOctetString>(new AxdrOctetString(), false);
		codeLength += dedicated_key.decode(iStream);

		response_allowed = new AxdrDefault<AxdrBoolean>(new AxdrBoolean(), new AxdrBoolean());
		codeLength += response_allowed.decode(iStream);

		proposed_quality_of_service = new AxdrOptional<Integer8>(new Integer8(), false);
		codeLength += proposed_quality_of_service.decode(iStream);

		proposed_dlms_version_number = new Unsigned8();
		codeLength += proposed_dlms_version_number.decode(iStream);

		proposed_conformance = new Conformance();
		codeLength += proposed_conformance.decode(iStream);

		client_max_receive_pdu_size = new Unsigned16();
		codeLength += client_max_receive_pdu_size.decode(iStream);

		return codeLength;
	}

	public void encodeAndSave(int encodingSizeGuess) throws IOException {
		AxdrByteArrayOutputStream axdrOStream = new AxdrByteArrayOutputStream(encodingSizeGuess);
		encode(axdrOStream);
		code = axdrOStream.getArray();
	}
}
