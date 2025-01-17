/**
 * This class file was automatically generated by jASN1 (http://www.openmuc.org)
 */

package org.openmuc.asn1.cosem;

import java.io.IOException;
import java.io.InputStream;

import org.openmuc.jasn1.axdr.AxdrByteArrayOutputStream;
import org.openmuc.jasn1.axdr.AxdrType;
import org.openmuc.jasn1.axdr.types.AxdrOptional;

public class Set_Request_With_First_Datablock implements AxdrType {

	public byte[] code = null;
	public Invoke_Id_And_Priority invoke_id_and_priority = null;

	public Cosem_Attribute_Descriptor cosem_attribute_descriptor = null;

	public AxdrOptional<Selective_Access_Descriptor> access_selection = new AxdrOptional<Selective_Access_Descriptor>(
			new Selective_Access_Descriptor(), false);

	public DataBlock_SA datablock = null;

	public Set_Request_With_First_Datablock() {
	}

	public Set_Request_With_First_Datablock(byte[] code) {
		this.code = code;
	}

	public Set_Request_With_First_Datablock(Invoke_Id_And_Priority invoke_id_and_priority,
			Cosem_Attribute_Descriptor cosem_attribute_descriptor, Selective_Access_Descriptor access_selection,
			DataBlock_SA datablock) {
		this.invoke_id_and_priority = invoke_id_and_priority;
		this.cosem_attribute_descriptor = cosem_attribute_descriptor;
		this.access_selection.setValue(access_selection);
		this.datablock = datablock;
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
			codeLength += datablock.encode(axdrOStream);

			codeLength += access_selection.encode(axdrOStream);

			codeLength += cosem_attribute_descriptor.encode(axdrOStream);

			codeLength += invoke_id_and_priority.encode(axdrOStream);

		}

		return codeLength;

	}

	@Override
	public int decode(InputStream iStream) throws IOException {
		int codeLength = 0;

		invoke_id_and_priority = new Invoke_Id_And_Priority();
		codeLength += invoke_id_and_priority.decode(iStream);

		cosem_attribute_descriptor = new Cosem_Attribute_Descriptor();
		codeLength += cosem_attribute_descriptor.decode(iStream);

		access_selection = new AxdrOptional<Selective_Access_Descriptor>(new Selective_Access_Descriptor(), false);
		codeLength += access_selection.decode(iStream);

		datablock = new DataBlock_SA();
		codeLength += datablock.decode(iStream);

		return codeLength;
	}

	public void encodeAndSave(int encodingSizeGuess) throws IOException {
		AxdrByteArrayOutputStream axdrOStream = new AxdrByteArrayOutputStream(encodingSizeGuess);
		encode(axdrOStream);
		code = axdrOStream.getArray();
	}
}
