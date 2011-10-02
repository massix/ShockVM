/* Copyright 2011 Massimo Gengarelli <gengarel@cs.unibo.it>
 * This file is part of Floz Configurator.
 * Floz Configurator is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 *
 * Floz Configurator is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License 
 * along with Floz Configurator. If not, see http://www.gnu.org/licenses/.
 */

package it.unibo.cs.v2.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.unibo.cs.v2.servlets.GetPrebuiltMachines;
import it.unibo.cs.v2.shared.MachineInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GetPrebuiltMachinesImpl extends RemoteServiceServlet implements GetPrebuiltMachines {
	@Override
	public HashMap<String,LinkedList<MachineInfo>> getPrebuiltMachines() throws Exception {
		File prebuiltDir = new File(getServletContext().getRealPath("prebuilt"));
		HashMap<String, LinkedList<MachineInfo>> ret = new HashMap<String, LinkedList<MachineInfo>>();
		
		if (!prebuiltDir.isDirectory())
			throw new Exception("Something went wrong on server side.");
		
		// Get all the XML files.
		File[] machines = prebuiltDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File arg0) {
				if (arg0.getName().endsWith(".xml"))
					return true;
				return false;
			}
		});
		
		// There are no machines.
		if (machines.length < 1)
			return null;
		
		// Insert all the users in the hashmap
		for (File f : machines) {
			String[] infos = f.getName().split("-");
			String user = infos[0];
			MachineInfo machineInfo = new MachineInfo();
			
			// Parse the XML file and build a MachineInfo object.
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document machine = db.parse(f);
			Element root = (Element) machine.getElementsByTagName("VirtualMachine").item(0);
			
			machineInfo.setConfigurationFile(f.getAbsolutePath());
			
			Element got = (Element)	root.getElementsByTagName("name").item(0);
			machineInfo.setName(got.getTextContent());
			
			got = (Element) root.getElementsByTagName("description").item(0);
			machineInfo.setDescription(got.getTextContent());
			
			got = (Element) root.getElementsByTagName("iso").item(0);
			machineInfo.setIso(got.getAttribute("path"));
			
			got = (Element) root.getElementsByTagName("hda").item(0);
			machineInfo.setHda(got.getAttribute("path"));
			machineInfo.setHdaSize(got.getAttribute("size"));
			
			got = (Element) root.getElementsByTagName("hdb").item(0);
			machineInfo.setHdbEnabled(got.getAttribute("enabled").equals("true"));
			
			if (machineInfo.isHdbEnabled()) {
				machineInfo.setHdb(got.getAttribute("path"));
				machineInfo.setHdbSize(got.getAttribute("size"));
			}
			
			got = (Element) root.getElementsByTagName("virtuacluster").item(0);
			machineInfo.setVirtuacluster(got.getAttribute("enabled").equals("true"));
			
			got = (Element) root.getElementsByTagName("secondaryNetwork").item(0);
			machineInfo.setSecondNetwork(got.getAttribute("enabled").equals("true"));
			
			if (machineInfo.isSecondNetwork()) {
				machineInfo.setSocketPath(got.getAttribute("socket"));
				machineInfo.setMacAddress(got.getAttribute("macaddress"));
			}
			
			// Get the long description from the txt file
			File descriptionFile = new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - 3) + "txt");
			
			BufferedReader descriptionReader = new BufferedReader(new FileReader(descriptionFile));
			String line = "";
			machineInfo.setLongDescription("");
			while ((line = descriptionReader.readLine()) != null)
				machineInfo.setLongDescription(machineInfo.getLongDescription() + line + "\n");
			descriptionReader.close();
			
			// The user is not registered in the hashmap, add him along with a new LinkedList.
			if (!ret.containsKey(user))
				ret.put(user, new LinkedList<MachineInfo>());
			
			ret.get(user).add(machineInfo);
		}
		
		return ret;
	}

}
