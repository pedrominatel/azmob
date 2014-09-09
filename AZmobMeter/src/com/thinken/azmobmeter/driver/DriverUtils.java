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
							groupInfo[1] = groupNode.getAttribute("Desc");

							objGroupNames.add(groupInfo);
						}
					}
				}
			}
		} catch (Exception e) {
			return null;
		}		
		
		return objGroupNames;
	}
	
	public List<String[]> driver_getObjectsByGroup(String meterType, String firmwareVersion, String groupName) {
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

							if(groupNode.getAttribute("Name").equals(groupName)) {
								
								NodeList objects = childList.item(i).getChildNodes();
								
								for (int k = 0; k < objects.getLength(); k++) {
									
									Node object = objects.item(k);
									
									if ("CosemObject".equals(object.getNodeName())) {

										if (object.getNodeType() == Node.ELEMENT_NODE) {

											Element objectNode = (Element) object;
											
											String[] objectInfo  = new String[4];
											
											objectInfo[0] = objectNode.getAttribute("Name");
											objectInfo[1] = objectNode.getAttribute("LogicalName");
											objectInfo[2] = objectNode.getAttribute("ClassId");
											objectInfo[3] = objectNode.getAttribute("Index");
											objGroupNames.add(objectInfo);
											break;
											
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			return null;
		}		
		
		return objGroupNames;
	}
	
	public List<String[]> driver_getCommonObjects(String meterType, String firmwareVersion) {

		List<String[]> commonObj = new ArrayList<String[]>();
		
		try {
			
			String supMetersExt = fsys.fsSys_getExtStorageDir(fsys.METER_OBJECTS_FOLDER + "/"+meterType+"_"+firmwareVersion+".xml").getAbsolutePath();
			
			File file = new File(supMetersExt);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// Parse the XML
			Document doc = dBuilder.parse(file);
			
			doc.getDocumentElement().normalize();
			
			NodeList flowList = doc.getElementsByTagName("Objects");
			
			for (int i = 0; i < flowList.getLength(); i++) {
				
				NodeList childList = flowList.item(i).getChildNodes();
				
				for (int j = 0; j < childList.getLength(); j++) {
					
					Node childNode = childList.item(j);
					
					if ("CosemObject".equals(childNode.getNodeName())) {

						if (childNode.getNodeType() == Node.ELEMENT_NODE) {

							Element groupNode = (Element) childNode;

							String[] objectInfo  = new String[4];
							
							objectInfo[0] = groupNode.getAttribute("Name");
							objectInfo[1] = groupNode.getAttribute("LogicalName");
							objectInfo[2] = groupNode.getAttribute("ClassId");
							objectInfo[3] = groupNode.getAttribute("Index");

							commonObj.add(objectInfo);
							
						}
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		
		return commonObj;
	}
	
	public List<String[]> driver_getCommonObjects(String meterType, String firmwareVersion, String objectName) {

		List<String[]> commonObj = new ArrayList<String[]>();
		
		try {
			
			String supMetersExt = fsys.fsSys_getExtStorageDir(fsys.METER_OBJECTS_FOLDER + "/"+meterType+"_"+firmwareVersion+".xml").getAbsolutePath();
			
			File file = new File(supMetersExt);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// Parse the XML
			Document doc = dBuilder.parse(file);
			
			doc.getDocumentElement().normalize();
			
			NodeList flowList = doc.getElementsByTagName("Objects");
			
			for (int i = 0; i < flowList.getLength(); i++) {
				
				NodeList childList = flowList.item(i).getChildNodes();
				
				for (int j = 0; j < childList.getLength(); j++) {
					
					Node childNode = childList.item(j);
					
					if ("CosemObject".equals(childNode.getNodeName())) {

						if (childNode.getNodeType() == Node.ELEMENT_NODE) {

							Element groupNode = (Element) childNode;

							String[] objectInfo  = new String[4];
							
							if(groupNode.getAttribute("Name").equals(objectName)) {
								objectInfo[0] = groupNode.getAttribute("Name");
								objectInfo[1] = groupNode.getAttribute("LogicalName");
								objectInfo[2] = groupNode.getAttribute("ClassId");
								objectInfo[3] = groupNode.getAttribute("Index");
								commonObj.add(objectInfo);
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		
		return commonObj;
	}
	
}
