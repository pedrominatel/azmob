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

	public List<String> driver_getObjectsGroupsNames(String meterType, String firmwareVersion) {
		//List
		List<String> objGroupNames = new ArrayList<String>();
		
		try {
			
			String supMetersExt = fsys.fsSys_getExtStorageDir(fsys.METER_OBJECTS_FOLDER + "/supported_meters.xml").getAbsolutePath();
			
			File file = new File(supMetersExt);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// Parse the XML
			Document doc = dBuilder.parse(file);
			
			doc.getDocumentElement().normalize();
			
			//Get Node <Meter>
			NodeList nList = doc.getElementsByTagName("Meter");
			
			for (int i = 0; i < nList.getLength(); i++) {
				 
				Node nNode = nList.item(i);
		 
				//log.log(tag, log.INFO, "\nCurrent Element :" + nNode.getNodeName(), false);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					 
					Element eElement = (Element) nNode;
		 
					String meterTypeCheck = eElement.getAttribute("Model");
					String firmwareVerCheck = eElement.getAttribute("FirmwareVersion");
					
					if(meterType.equals(meterTypeCheck)&&firmwareVersion.equals(firmwareVerCheck)) {
						
						NodeList groups = eElement.getElementsByTagName("ReadingGroup");
						
							for (int c = 0; c < groups.getLength(); c++) {
								
								Node group = groups.item(c);
								
								if (nNode.getNodeType() == Node.ELEMENT_NODE) {
									Element groupNode = (Element) group;
									
									String id = groupNode.getAttribute("Id");
									String name = groupNode.getAttribute("Name");
									String desc = groupNode.getAttribute("Desc");
									
									System.out.println(id);
									System.out.println(name);
									System.out.println(desc);
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
	
	public List<Object> driver_getCommonObjects(String objectName) {
		//List
		List<Object> commonObj = new ArrayList<Object>();
		
		return commonObj;
	}
	
}
