/**
 * This class file was automatically generated by jASN1 (http://www.openmuc.org)
 */

package org.openmuc.asn1.cosem;

import java.io.IOException;
import java.io.InputStream;

import org.openmuc.jasn1.axdr.AxdrByteArrayOutputStream;
import org.openmuc.jasn1.axdr.AxdrType;
import org.openmuc.jasn1.axdr.types.AxdrBoolean;
import org.openmuc.jasn1.axdr.types.AxdrOctetString;

public class DataBlock_SA implements AxdrType {

	public byte[] code = null;
	public AxdrBoolean last_block = null;

	public Unsigned32 block_number = null;

	public AxdrOctetString raw_data = null;

	public DataBlock_SA() {
	}

	public DataBlock_SA(byte[] code) {
		this.code = code;
	}

	public DataBlock_SA(AxdrBoolean last_block, Unsigned32 block_number, AxdrOctetString raw_data) {
		this.last_block = last_block;
		this.block_number = block_number;
		this.raw_data = raw_data;
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
			codeLength += raw_data.encode(axdrOStream);

			codeLength += block_number.encode(axdrOStream);

			codeLength += last_block.encode(axdrOStream);

		}

		return codeLength;

	}

	@Override
	public int decode(InputStream iStream) throws IOException {
		int codeLength = 0;

		last_block = new AxdrBoolean();
		codeLength += last_block.decode(iStream);

		block_number = new Unsigned32();
		codeLength += block_number.decode(iStream);

		raw_data = new AxdrOctetString();
		codeLength += raw_data.decode(iStream);

		return codeLength;
	}

	public void encodeAndSave(int encodingSizeGuess) throws IOException {
		AxdrByteArrayOutputStream axdrOStream = new AxdrByteArrayOutputStream(encodingSizeGuess);
		encode(axdrOStream);
		code = axdrOStream.getArray();
	}
}
