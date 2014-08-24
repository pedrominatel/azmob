package com.thinken.azmobmeter.driver;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openmuc.jdlms.client.Data;
import org.openmuc.jdlms.client.GetResult;
import org.openmuc.jdlms.client.ObisCode;
import org.openmuc.jdlms.client.SetRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.qos.logback.core.Context;

import com.thinken.azmobmeter.utils.Filesys;

import android.app.Activity;
import android.content.ContextWrapper;
import android.util.Log;

public class DriverPduIO extends Activity {
	// XML tags definition

	private static String tag = "DriverPduIO";
	private static final String COSEM_XML = "CosemXML";
	private static final String COSEM_DATA = "CosemData";
	private static final String COSEM_OBJECT = "CosemObject";
	private static final String COSEM_CONTAINER = "CosemContainer";
	private static final String COSEM_ELEMENT = "CosemElement";
	private static final String COSEM_TYPE = "CosemType";

	public enum elementType {
		Structure, Array, Unsigned8, Unsigned16, Unsigned32, Integer8, Integer16, Integer32, BitString, Boolean, VisibleString, Enumerated, OctetString
	}

	public enum cosemType {
		ELEMENT, CONTAINER, DATA
	}

	public void pduToXmlDebug(GetResult logObject) {
		pduToXmlDebugDecode(logObject.getResultData(), 0);
	}

	private static void pduToXml(Data data, Document xml, Element element) {

		switch (data.getChoiceIndex()) {
		case ARRAY:
			Element cosemArray = xml.createElement(COSEM_CONTAINER);
			cosemArray.setAttribute(COSEM_TYPE, "ARRAY");
			element.appendChild(cosemArray);
			for (Data subData : data.getComplex()) {
				pduToXml(subData, xml, cosemArray);
			}
			break;
		case COMPACT_ARRAY:
			Element cosemCompactArray = xml.createElement(COSEM_CONTAINER);
			cosemCompactArray.setAttribute(COSEM_TYPE, "COMPACT_ARRAY");
			element.appendChild(cosemCompactArray);
			for (Data subData : data.getComplex()) {
				pduToXml(subData, xml, cosemCompactArray);
			}
			break;
		case STRUCTURE:
			Element cosemStructure = xml.createElement(COSEM_CONTAINER);
			cosemStructure.setAttribute(COSEM_TYPE, "STRUCTURE");
			element.appendChild(cosemStructure);
			for (Data subData : data.getComplex()) {
				pduToXml(subData, xml, cosemStructure);
			}
			break;
		case OCTET_STRING:
			Element cosemOctet = xml.createElement(COSEM_ELEMENT);
			cosemOctet.setAttribute(COSEM_TYPE, "OCTET_STRING");
			String hex = "";
			for (byte b : data.getByteArray()) {
				hex = hex + Integer.toString(b & 0xFF) + ";";
			}
			cosemOctet.setTextContent(hex);
			element.appendChild(cosemOctet);
			break;
		case BOOL:
			Element cosemBol = xml.createElement(COSEM_ELEMENT);
			cosemBol.setAttribute(COSEM_TYPE, "BOOL");
			cosemBol.setTextContent(data.getNumber().toString());
			element.appendChild(cosemBol);
			break;
		case ENUMERATE:
			Element cosemEnumarate = xml.createElement(COSEM_ELEMENT);
			cosemEnumarate.setAttribute(COSEM_TYPE, "ENUMERATE");
			cosemEnumarate.setTextContent(data.getNumber().toString());
			element.appendChild(cosemEnumarate);
			break;
		case LONG_UNSIGNED:
			Element cosemLongUnsigned = xml.createElement(COSEM_ELEMENT);
			cosemLongUnsigned.setAttribute(COSEM_TYPE, "LONG_UNSIGNED");
			cosemLongUnsigned.setTextContent(data.getNumber().toString());
			element.appendChild(cosemLongUnsigned);
			break;
		case UNSIGNED:
			Element cosemUnsigned = xml.createElement(COSEM_ELEMENT);
			cosemUnsigned.setAttribute(COSEM_TYPE, "UNSIGNED");
			cosemUnsigned.setTextContent(data.getNumber().toString());
			element.appendChild(cosemUnsigned);
			break;
		case INTEGER:
			Element cosemInteger = xml.createElement(COSEM_ELEMENT);
			cosemInteger.setAttribute(COSEM_TYPE, "INTEGER");
			cosemInteger.setTextContent(data.getNumber().toString());
			element.appendChild(cosemInteger);
			break;
		case LONG_INTEGER:
			Element cosemLong = xml.createElement(COSEM_ELEMENT);
			cosemLong.setAttribute(COSEM_TYPE, "LONG_INTEGER");
			cosemLong.setTextContent(data.getNumber().toString());
			element.appendChild(cosemLong);
			break;
		case DOUBLE_LONG_UNSIGNED:
			Element cosemDoubleLongUnsigned = xml.createElement(COSEM_ELEMENT);
			cosemDoubleLongUnsigned.setAttribute(COSEM_TYPE,
					"DOUBLE_LONG_UNSIGNED");
			cosemDoubleLongUnsigned.setTextContent(data.getNumber().toString());
			element.appendChild(cosemDoubleLongUnsigned);
			break;
		case VISIBLE_STRING:
			Element cosemVisibleString = xml.createElement(COSEM_ELEMENT);
			cosemVisibleString.setAttribute(COSEM_TYPE, "VISIBLE_STRING");
			String visible = "";
			for (byte b : data.getByteArray()) {
				visible = visible + (char) b;
			}
			cosemVisibleString.setTextContent(visible);
			element.appendChild(cosemVisibleString);
			break;
		case BIT_STRING:
			Element cosemBitString = xml.createElement(COSEM_ELEMENT);
			cosemBitString.setAttribute(COSEM_TYPE, "BIT_STRING");
			String bitString = "";
			for (byte b : data.getByteArray()) {
				bitString = bitString + Integer.toBinaryString(b);
			}
			cosemBitString.setTextContent(bitString);
			element.appendChild(cosemBitString);
			break;
		case NULL_DATA:
			Element cosemNullData = xml.createElement(COSEM_ELEMENT);
			cosemNullData.setAttribute(COSEM_TYPE, "NULL_DATA");
			cosemNullData.setTextContent("");
			element.appendChild(cosemNullData);
			break;
		default:
			Log.i(tag, "UNKNOWN TYPE: " + data.getChoiceIndex());
		}
	}

	private static void pduToXmlDebugDecode(Data data, int indent) {

		for (int i = 0; i < indent; i++) {
			Log.i(tag, "   ");
		}

		switch (data.getChoiceIndex()) {
		case ARRAY:
			Log.i(tag, "Array");
			for (Data subData : data.getComplex()) {
				pduToXmlDebugDecode(subData, indent + 1);
			}
			break;
		case COMPACT_ARRAY:
			Log.i(tag, "Compact Array");
			break;
		case STRUCTURE:
			Log.i(tag, "Structure");
			for (Data subData : data.getComplex()) {
				pduToXmlDebugDecode(subData, indent + 1);
			}
			break;
		case OCTET_STRING:
			Log.i(tag, "Octet String: ");
			for (byte b : data.getByteArray()) {
				// String hex = Integer.toHexString(b & 0xFF);
				String hex = Integer.toString(b & 0xFF);
				// hex = hex.length() == 1 ? "0" + hex : hex;
				System.out.print(hex + ";");
			}
			
			break;
		case BOOL:
			Log.i(tag, "Boolean: " + data.getBoolean());
			break;
		case ENUMERATE:
			Log.i(tag, "Enumarate: " + data.getNumber());
			break;
		case LONG_UNSIGNED:
			Log.i(tag, "Long Unsigned: " + data.getNumber());
			break;
		case UNSIGNED:
			Log.i(tag, "Unsigned: " + data.getNumber());
			break;
		case INTEGER:
			Log.i(tag, "Integer: " + data.getNumber());
			break;
		case LONG_INTEGER:
			Log.i(tag, "Long Integer: " + data.getNumber());
			break;
		case DOUBLE_LONG_UNSIGNED:
			Log.i(tag, "Double: " + data.getNumber());
			break;
		case VISIBLE_STRING:
			Log.i(tag, "Visible String: ");
			String visible = "";
			for (byte b : data.getByteArray()) {
				visible = visible + (char) b;
			}
			Log.i(tag, visible);
			break;
		case BIT_STRING:
			Log.i(tag, "Bit String: ");
			for (byte b : data.getByteArray()) {
				String hex = Integer.toBinaryString(b);
				Log.i(tag, hex);
			}

			break;
		case NULL_DATA:
			Log.i(tag, "Null data ");
			break;
		default:
			Log.i(tag, "Unknown type: " + data.getChoiceIndex());
		}
	}

	public Calendar decodeClock(byte[] bytes) {
		// Decode clock OCTETSTRING
		int year = ((bytes[0] << 8) & 0xFF00) + (bytes[1] & 0xFF);
		int month = bytes[2];
		int dayOfMonth = bytes[3];
		int hour = bytes[5];
		int min = bytes[6];
		int sec = bytes[7];

		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month, dayOfMonth, hour, min, sec);

		return calendar;
	}

	public byte[] encodeClock(Calendar dateTime) {
		// encode clock OCTETSTRING
		// force to one byte each integer (32bits) to avoid overflow
		int yearHigh = 7 & (0xFF);// fixed to 7 high = 222
		int yearLow = dateTime.get(Calendar.YEAR) - 1792 & (0xFF);
		int month = dateTime.get(Calendar.MONTH) + 1 & (0xFF);// January = 0,
																// just sum 1 to
																// get the right
																// value
		int dayOfMonth = dateTime.get(Calendar.DAY_OF_MONTH) & (0xFF);
		int hourOfDay = dateTime.get(Calendar.HOUR_OF_DAY) & (0xFF); // 24 hour
																		// clock
		int minute = dateTime.get(Calendar.MINUTE) & (0xFF);
		int second = dateTime.get(Calendar.SECOND) & (0xFF);

		byte[] dateArray = { (byte) yearHigh, (byte) yearLow, (byte) month,
				(byte) dayOfMonth, (byte) 0xFF, (byte) hourOfDay,
				(byte) minute, (byte) second, (byte) 0xff, (byte) 0x80,
				(byte) 0x00, (byte) 0x00 };

		return dateArray;
	}

	public boolean createXml(String serialNumber, String fileName, GetResult pdu, ObisCode obis,
			int classId, int attribute) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// XML root elements
			Document xml = docBuilder.newDocument();

			// CosemXML tag
			Element cosemXML = xml.createElement(COSEM_XML);
			cosemXML.setAttribute("Version", "1.0.0");
			xml.appendChild(cosemXML);

			// CosemObject tag
			Element cosemObject = xml.createElement(COSEM_OBJECT);
			cosemObject.setAttribute("SerialNumber", serialNumber);
			cosemObject.setAttribute("LogicalName", obis.getHexCode());
			cosemObject.setAttribute("ClassId", Integer.toString(classId));
			cosemObject.setAttribute("Index", Integer.toString(attribute));
			cosemObject.setAttribute("ReadingDateTime", octetStringToString(encodeClock(Calendar.getInstance())));
			cosemObject.setAttribute("DataAccessResult", "success(0)");
			cosemXML.appendChild(cosemObject);

			// CosemData tag
			Element cosemData = xml.createElement(COSEM_DATA);
			cosemObject.appendChild(cosemData);

			// Parse the GetResult data
			pduToXml(pdu.getResultData(), xml, cosemData);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			// Set ident
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			Filesys fsys = new Filesys();
			
			fsys.fsSys_createFolder(fsys.fsSys_getExtStorageDir(fsys.READOUTS_FOLDER), serialNumber+"/"+fsys.fsSys_dateStamp());
			
			DOMSource source = new DOMSource(xml);
			StreamResult result = new StreamResult(new File(readoutFolder, fileName));

			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		return true;
	}

	public String octetStringToString(byte[] data) {
		String hexString = "";
		for (byte b : data) {
			hexString = hexString + (b & 0xFF) + ";";
		}
		return hexString;
	}

	private static byte[] convertToBytes(String[] strings) {
		byte[] data = new byte[strings.length];
		for (int i = 0; i < strings.length; i++) {
			String string = strings[i];
			data[i] = (byte) Integer.parseInt(string); // you can chose charset
		}
		return data;
	}

	public void xmlHeader(ObisCode obis, int classId, int attribute) {
		// TODO: Do nothing!
	}

	public SetRequest readXmlStructure(String filePathName) {

		List<Data> elementsList = new ArrayList<Data>();
		String[] obisArray = new String[6];
		int classId = 0, index = 0;

		SetRequest setRequest = null;

		try {

			File file = new File(filePathName);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			// Parse the XML
			Document doc = dBuilder.parse(file);

			// TODO: Check if this XML is valid by the root element. If !=
			// CosemXML throws Exception
			if (!doc.getDocumentElement().getNodeName().equals(COSEM_XML)) {
				// TODO: Create Exception here
				return null;
			}

			// TODO: Read CosemObject tag
			NodeList cosemObjectTag = doc.getElementsByTagName(COSEM_OBJECT);
			Node cosemObjectTagNode = cosemObjectTag.item(0);

			if (cosemObjectTagNode.hasAttributes()) {
				String objectNameAttr = cosemObjectTagNode.getAttributes()
						.getNamedItem("Name").getNodeValue();
				String logicalNameAttr = cosemObjectTagNode.getAttributes()
						.getNamedItem("LogicalName").getNodeValue();
				String classIdAttr = cosemObjectTagNode.getAttributes()
						.getNamedItem("ClassId").getNodeValue();
				String indexAttr = cosemObjectTagNode.getAttributes()
						.getNamedItem("Index").getNodeValue();
				if (objectNameAttr != null) {
					obisArray = logicalNameAttr.split(";");
					classId = Integer.parseInt(classIdAttr);
					index = Integer.parseInt(indexAttr);
				}
			}

			int[] obisIntArr = new int[6];

			for (int i = 0; i < obisArray.length; i++) {
				obisIntArr[i] = Integer.parseInt(obisArray[i]);
			}

			ObisCode obis = new ObisCode(obisIntArr[0], obisIntArr[1],
					obisIntArr[2], obisIntArr[3], obisIntArr[4], obisIntArr[5]);
			setRequest = new SetRequest(classId, obis, index);

			if (doc.hasChildNodes()) {
				// Find the tag CosemData and select the child nodes
				NodeList cosemDataList = doc.getElementsByTagName(COSEM_DATA);
				// Read the CosemData Node Structure
				readXml(cosemDataList, elementsList);
			} else {
				return null;
			}

			// Create the Data() structure/element
			switch (elementsList.get(0).getChoiceIndex()) {
			case STRUCTURE:
				setRequest.data()
						.setStructure(elementsList.get(0).getComplex());
				break;
			case ARRAY:
				setRequest.data().setArray(elementsList.get(0).getComplex());
				break;
			case OCTET_STRING:
				setRequest.data().setOctetString(
						elementsList.get(0).getByteArray());
				break;
			case BOOL:
				setRequest.data().setbool(elementsList.get(0).getBoolean());
				break;
			case ENUMERATE:
				setRequest.data().setEnumerate(
						elementsList.get(0).getNumber().longValue());
				break;
			case LONG_UNSIGNED:
				setRequest.data().setUnsigned16(
						elementsList.get(0).getNumber().longValue());
				break;
			case UNSIGNED:
				setRequest.data().setUnsigned8(
						elementsList.get(0).getNumber().longValue());
				break;
			case INTEGER:
				setRequest.data().setInteger8(
						elementsList.get(0).getNumber().longValue());
				break;
			case LONG_INTEGER:
				setRequest.data().setInteger16(
						elementsList.get(0).getNumber().longValue());
				break;
			case DOUBLE_LONG_UNSIGNED:
				setRequest.data().setUnsigned32(
						elementsList.get(0).getNumber().longValue());
				break;
			case VISIBLE_STRING:
				setRequest.data().setVisibleString(
						elementsList.get(0).getByteArray());
				break;
			case BIT_STRING:
				setRequest.data().setBitString(
						elementsList.get(0).getByteArray(),
						elementsList.get(0).getByteArray().length);
				break;
			default:
				break;
			}

			// TODO: Debug code #remove
			if (!setRequest.data().equals(null)) {
				pduToXmlDebugDecode(setRequest.data(), 0);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return setRequest;
	}

	public Data readXmlStructureData(String filePathName) {

		List<Data> elementsList = new ArrayList<Data>();
		Data dataStruct = new Data();

		try {

			File file = new File(filePathName);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			// Parse the XML
			Document doc = dBuilder.parse(file);

			// TODO: Check if this XML is valid by the root element. If !=
			// CosemXML throws Exception
			if (!doc.getDocumentElement().getNodeName().equals(COSEM_XML)) {
				// TODO: Create Exception here
				return null;
			}

			if (doc.hasChildNodes()) {
				// Find the tag CosemData and select the child nodes
				NodeList cosemDataList = doc.getElementsByTagName(COSEM_DATA);
				// Read the CosemData Node Structure
				readXml(cosemDataList, elementsList);
			} else {
				return null;
			}

			// Create the Data() structure/element
			switch (elementsList.get(0).getChoiceIndex()) {
			case STRUCTURE:
				dataStruct.setStructure(elementsList.get(0).getComplex());
				break;
			case ARRAY:
				dataStruct.setArray(elementsList.get(0).getComplex());
				break;
			case OCTET_STRING:
				dataStruct.setOctetString(elementsList.get(0).getByteArray());
				break;
			case BOOL:
				dataStruct.setbool(elementsList.get(0).getBoolean());
				break;
			case ENUMERATE:
				dataStruct.setEnumerate(elementsList.get(0).getNumber()
						.longValue());
				break;
			case LONG_UNSIGNED:
				dataStruct.setUnsigned16(elementsList.get(0).getNumber()
						.longValue());
				break;
			case UNSIGNED:
				dataStruct.setUnsigned8(elementsList.get(0).getNumber()
						.longValue());
				break;
			case INTEGER:
				dataStruct.setInteger8(elementsList.get(0).getNumber()
						.longValue());
				break;
			case LONG_INTEGER:
				dataStruct.setInteger16(elementsList.get(0).getNumber()
						.longValue());
				break;
			case DOUBLE_LONG_UNSIGNED:
				dataStruct.setUnsigned32(elementsList.get(0).getNumber()
						.longValue());
				break;
			case VISIBLE_STRING:
				dataStruct.setVisibleString(elementsList.get(0).getByteArray());
				break;
			case BIT_STRING:
				dataStruct.setBitString(elementsList.get(0).getByteArray(),
						elementsList.get(0).getByteArray().length);
				break;
			default:
				break;
			}

			// TODO: Debug code #remove
			if (!dataStruct.equals(null)) {
				pduToXmlDebugDecode(dataStruct, 0);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return dataStruct;
	}

	private void readXml(NodeList nodeList, List<Data> elementsList) {

		cosemType cosemTypeId = cosemType.ELEMENT;
		elementType elementTypeId = elementType.Unsigned8;

		for (int count = 0; count < nodeList.getLength(); count++) {

			Node tempNode = nodeList.item(count);

			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				String cosem = tempNode.getNodeName();

				cosemTypeId = getCosemType(cosem);

				switch (cosemTypeId) {
				case ELEMENT:

					String elementType = tempNode.getAttributes().item(0)
							.getTextContent();
					elementTypeId = getElementType(elementType);

					Data dataElement = new Data();
					String tagValueString = tempNode.getTextContent();
					int tagValueInt = 0;

					switch (elementTypeId) {
					case Unsigned8:
						tagValueInt = Integer.parseInt(tagValueString);
						dataElement.setUnsigned8(tagValueInt);
						break;
					case Unsigned16:
						tagValueInt = Integer.parseInt(tagValueString);
						dataElement.setUnsigned16(tagValueInt);
						break;
					case Unsigned32:
						tagValueInt = Integer.parseInt(tagValueString);
						dataElement.setUnsigned32(tagValueInt);
						break;
					case Integer8:
						tagValueInt = Integer.parseInt(tagValueString);
						dataElement.setInteger8(tagValueInt);
						break;
					case Integer16:
						tagValueInt = Integer.parseInt(tagValueString);
						dataElement.setInteger16(tagValueInt);
						break;
					case Integer32:
						tagValueInt = Integer.parseInt(tagValueString);
						dataElement.setInteger32(tagValueInt);
						break;
					case OctetString:
						String[] octetString = tagValueString.split(";");
						dataElement.setOctetString(convertToBytes(octetString));
						break;
					case BitString:
						String[] bitString = tagValueString.split(";");
						dataElement.setBitString(convertToBytes(bitString),
								bitString.length);
						break;
					case Boolean:
						if (tagValueString.equals("1")) {
							dataElement.setbool(true);
						} else {
							dataElement.setbool(false);
						}
						break;
					case Enumerated:
						tagValueInt = Integer.parseInt(tagValueString);
						dataElement.setEnumerate(tagValueInt);
						break;
					case VisibleString:
						dataElement.setVisibleString(tagValueString.getBytes());
						break;
					default:
						dataElement.setNull();
						break;
					}

					elementsList.add(dataElement);

					break;
				case CONTAINER:

					String containerType = tempNode.getAttributes().item(0)
							.getTextContent();

					if (containerType.equals("Array")) {
						// TODO: Cria o array e se caso tiver childNodes passa o
						// array

						if (tempNode.hasChildNodes()) {
							List<Data> structure = new ArrayList<Data>();
							readXml(tempNode.getChildNodes(), structure);
							Data dataStructure = new Data();
							dataStructure.setArray(structure);
							elementsList.add(dataStructure);
						}// if

					} else if ((containerType.equals("Structure"))) { // Structure
						// TODO: Cria a structure e se caso tiver childNodes
						// passa a structure

						if (tempNode.hasChildNodes()) {
							List<Data> structure = new ArrayList<Data>();
							readXml(tempNode.getChildNodes(), structure);
							Data dataStructure = new Data();
							dataStructure.setStructure(structure);
							elementsList.add(dataStructure);
						}// if

					} else {
						System.out.println("Missing CONTAINER TYPE: "
								+ containerType);
					}

					break;
				case DATA:
					if (tempNode.hasChildNodes()) {
						readXml(tempNode.getChildNodes(), elementsList);
					}// if
					break;
				default:
					break;
				}

			}// if
		}// for
	}

	private cosemType getCosemType(String cosem) {

		cosemType cosemTypeId = cosemType.ELEMENT;

		if (cosem == COSEM_ELEMENT)
			cosemTypeId = cosemType.ELEMENT;
		if (cosem == COSEM_CONTAINER)
			cosemTypeId = cosemType.CONTAINER;
		if (cosem == COSEM_DATA)
			cosemTypeId = cosemType.DATA;

		return cosemTypeId;
	}

	private elementType getElementType(String element) {

		elementType elementTypeId = elementType.Unsigned8;

		if (element == "Unsigned8")
			elementTypeId = elementType.Unsigned8;
		if (element == "Unsigned16")
			elementTypeId = elementType.Unsigned16;
		if (element == "Unsigned32")
			elementTypeId = elementType.Unsigned32;
		if (element == "Integer8")
			elementTypeId = elementType.Integer8;
		if (element == "Integer16")
			elementTypeId = elementType.Integer16;
		if (element == "Integer32")
			elementTypeId = elementType.Integer32;
		if (element == "OctetString")
			elementTypeId = elementType.OctetString;
		if (element == "BitString")
			elementTypeId = elementType.BitString;
		if (element == "Boolean")
			elementTypeId = elementType.Boolean;
		if (element == "Enumerated")
			elementTypeId = elementType.Enumerated;
		if (element == "VisibleString")
			elementTypeId = elementType.VisibleString;

		return elementTypeId;
	}

}
