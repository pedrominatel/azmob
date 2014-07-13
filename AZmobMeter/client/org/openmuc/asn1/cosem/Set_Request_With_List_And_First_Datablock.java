/**
 * This class file was automatically generated by jASN1 (http://www.openmuc.org)
 */

package org.openmuc.asn1.cosem;

import java.io.IOException;
import java.io.InputStream;

import org.openmuc.jasn1.axdr.AxdrByteArrayOutputStream;
import org.openmuc.jasn1.axdr.AxdrType;
import org.openmuc.jasn1.axdr.types.AxdrSequenceOf;

public class Set_Request_With_List_And_First_Datablock implements AxdrType {

	public static class SubSeqOf_attribute_descriptor_list extends
			AxdrSequenceOf<Cosem_Attribute_Descriptor_With_Selection> {

		@Override
		protected Cosem_Attribute_Descriptor_With_Selection createListElement() {
			return new Cosem_Attribute_Descriptor_With_Selection();
		}

		protected SubSeqOf_attribute_descriptor_list(int length) {
			super(length);
		}

		public SubSeqOf_attribute_descriptor_list() {
		} // Call empty base constructor

	}

	public byte[] code = null;
	public Invoke_Id_And_Priority invoke_id_and_priority = null;

	public SubSeqOf_attribute_descriptor_list attribute_descriptor_list = null;

	public DataBlock_SA datablock = null;

	public Set_Request_With_List_And_First_Datablock() {
	}

	public Set_Request_With_List_And_First_Datablock(byte[] code) {
		this.code = code;
	}

	public Set_Request_With_List_And_First_Datablock(Invoke_Id_And_Priority invoke_id_and_priority,
			SubSeqOf_attribute_descriptor_list attribute_descriptor_list, DataBlock_SA datablock) {
		this.invoke_id_and_priority = invoke_id_and_priority;
		this.attribute_descriptor_list = attribute_descriptor_list;
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

			codeLength += attribute_descriptor_list.encode(axdrOStream);

			codeLength += invoke_id_and_priority.encode(axdrOStream);

		}

		return codeLength;

	}

	@Override
	public int decode(InputStream iStream) throws IOException {
		int codeLength = 0;

		invoke_id_and_priority = new Invoke_Id_And_Priority();
		codeLength += invoke_id_and_priority.decode(iStream);

		attribute_descriptor_list = new SubSeqOf_attribute_descriptor_list();
		codeLength += attribute_descriptor_list.decode(iStream);

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
