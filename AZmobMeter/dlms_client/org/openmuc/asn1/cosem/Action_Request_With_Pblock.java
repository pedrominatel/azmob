/**
 * This class file was automatically generated by jASN1 (http://www.openmuc.org)
 */

package org.openmuc.asn1.cosem;

import java.io.IOException;
import java.io.InputStream;

import org.openmuc.jasn1.axdr.AxdrByteArrayOutputStream;
import org.openmuc.jasn1.axdr.AxdrType;

public class Action_Request_With_Pblock implements AxdrType {

	public byte[] code = null;
	public Invoke_Id_And_Priority invoke_id_and_priority = null;

	public DataBlock_SA pBlock = null;

	public Action_Request_With_Pblock() {
	}

	public Action_Request_With_Pblock(byte[] code) {
		this.code = code;
	}

	public Action_Request_With_Pblock(Invoke_Id_And_Priority invoke_id_and_priority, DataBlock_SA pBlock) {
		this.invoke_id_and_priority = invoke_id_and_priority;
		this.pBlock = pBlock;
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
			codeLength += pBlock.encode(axdrOStream);

			codeLength += invoke_id_and_priority.encode(axdrOStream);

		}

		return codeLength;

	}

	@Override
	public int decode(InputStream iStream) throws IOException {
		int codeLength = 0;

		invoke_id_and_priority = new Invoke_Id_And_Priority();
		codeLength += invoke_id_and_priority.decode(iStream);

		pBlock = new DataBlock_SA();
		codeLength += pBlock.decode(iStream);

		return codeLength;
	}

	public void encodeAndSave(int encodingSizeGuess) throws IOException {
		AxdrByteArrayOutputStream axdrOStream = new AxdrByteArrayOutputStream(encodingSizeGuess);
		encode(axdrOStream);
		code = axdrOStream.getArray();
	}
}