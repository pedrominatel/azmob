package com.thinken.azmobmeter.driver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thinken.azmobmeter.SplashScreen;
import com.thinken.azmobmeter.utils.Filesys;
import com.thinken.azmobmeter.utils.Logging;

public class DriverUtils {
	
	Filesys fsys = new Filesys();
	Logging log = new Logging();	
	
	String tag = "Driver";
	
	public List<String> driver_getSupportedMeters() {
		
		List<String> supportedMeters = new ArrayList<String>();
		
		return supportedMeters;
	}

	public List<String[]> driver_getObjectsGroupsNames(String meterType, String firmwareVersion) {
		//List
		List<String[]> objGroupNames = new ArrayList<String[]>();
		
		try {
			
			String supMetersExt = fsys.fsSys_getExtStorageDir(fsys.METER_OBJECTS_FOLDER + "/"+meterType+"_"+firmwareVersion+".xml").getAbsolutePath();
			
			File file = new File(supMetersExt);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// Parse the XML
			Document doc = dBuilder.parse(file);
			
			doc.getDocumentElement().normalize();
			
			NodeList flowList = doc.getElementsByTagName("Groups");
			
			for (int i = 0; i < flowList.getLength(); i++) {
				
				NodeList childList = flowList.item(i).getChildNodes();
				
				for (int j = 0; j < childList.getLength(); j++) {
					
					Node childNode = childList.item(j);
					
					if ("ReadingGroup".equals(childNode.getNodeName())) {

						if (childNode.getNodeType() == Node.ELEMENT_NODE) {

							Element groupNode = (Element) childNode;

							String[] groupInfo  = new String[2];
							
							groupInfo[0] = groupNode.getAttribute("Name");
							groupInfo[0] = groupNode.getAttribute("Desc");

							objGroupNames.add(groupInfo);
						}
					}
				}
			}
			
			
//			//Get Node <Meter>
//			NodeList nList = doc.getElementsByTagName("Groups");
//			
//			for (int i = 0; i < nList.getLength(); i++) {
//
//				Node nNode = nList.item(i);
//
//				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//
//					Element eElement = (Element) nNode;
//
//					String meterTypeCheck = eElement.getAttribute("Model");
//					String firmwareVerCheck = eElement.getAttribute("FirmwareVersion");
//
//					if (meterType.equals(meterTypeCheck)&&firmwareVersion.equals(firmwareVerCheck)) {
//
//						NodeList groups = eElement.getElementsByTagName("Groups");
//						
//						for (int c = 0; c < groups.getLength(); c++) {
//							
//							NodeList readingGroup = groups.item(i).getChildNodes();
//							
//					        for (int j = 0; j < readingGroup.getLength(); j++) {
//					            Node childNode = readingGroup.item(j);
//					            if ("ReadingGroup".equals(childNode.getNodeName())) {
//					            	
//					        		log.log(tag, log.INFO,readingGroup.item(j).getTextContent().trim(), false);
//					            	
//					            }
//					        }
//
//							Node group = groups.item(c);
//
//							if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//								
//								Element groupNode = (Element) group;
//
//								//String id = groupNode.getAttribute("Id");
//								String name = groupNode.getAttribute("Name");
//								//String desc = groupNode.getAttribute("Desc");
//								
//								objGroupNames.add(name);	
//							}
//						}
//					}
//				}
//			}
			
		} catch (Exception e) {
			return null;
		}		
		
		return objGroupNames;
	}
	
	public List<Object> driver_getCommonObjects(String objectName) {
		//List
		List<Object> commonObj = new ArrayList<Object>();
		
		return commonObj;
	}
	
}
