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

import it.unibo.cs.v2.servlets.GetMachines;
import it.unibo.cs.v2.shared.MachineInfo;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GetMachinesImpl extends RemoteServiceServlet implements
		GetMachines {

	@Override
	public LinkedList<MachineInfo> getMachines() {
		String username = (String) getThreadLocalRequest().getSession().getAttribute("login");
		File userDir = new File(getServletContext().getRealPath("/users/" + username));
		if (!userDir.exists() || !userDir.isDirectory())
			return null;
		else {
			File xmlFiles[] = userDir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File arg0) {
					if (arg0.getName().endsWith(".xml"))
						return true;
					else
						return false;
				}
			});
			
			if (xmlFiles.length < 1)
				return null;
			
			LinkedList<MachineInfo> machines = new LinkedList<MachineInfo>();
			
			for (File f : xmlFiles) {
				try {
					MachineInfo machineInfo = new MachineInfo();
					DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document machine = db.parse(f);
					Element root = (Element) machine.getElementsByTagName("VirtualMachine").item(0);
					
					machineInfo.setConfigurationFile(f.getAbsolutePath());
					
					Element got = (Element)	root.getElementsByTagName("name").item(0);
					machines.add(machineInfo);
					machineInfo.setName(got.getTextContent());
					
					got = (Element) root.getElementsByTagName("description").item(0);
					machineInfo.setDescription(got.getTextContent());
					
					got = (Element) root.getElementsByTagName("iso").item(0);
					machineInfo.setIso(got.getAttribute("path"));
					
					if (root.getElementsByTagName("realowner").getLength() > 0) {
						got = (Element) root.getElementsByTagName("realowner").item(0);
						machineInfo.setRealOwner(got.getTextContent());
					}
					
					else
						machineInfo.setRealOwner(username);
					
					got = (Element) root.getElementsByTagName("hda").item(0);
					machineInfo.setHda(got.getAttribute("path"));
					machineInfo.setHdaSize(got.getAttribute("size"));
					
					got = (Element) root.getElementsByTagName("hdb").item(0);
					machineInfo.setHdbEnabled(got.getAttribute("enabled").equals("true"));
					
					if (machineInfo.isHdbEnabled()) {
						machineInfo.setHdb(got.getAttribute("path"));
						machineInfo.setHdbSize(got.getAttribute("size"));
					}
					
					NodeList sharedWith = root.getElementsByTagName("share");
					if (sharedWith != null && sharedWith.getLength() > 0) {
						for (int i = 0; i < sharedWith.getLength(); i++) {
							got = (Element) sharedWith.item(i);
							if (got.getAttribute("status").equals("pending"))
								machineInfo.addPendingShare(got.getAttribute("user"));
							else
								machineInfo.addShare(got.getAttribute("user"));
						}
					}

					
					got = (Element) root.getElementsByTagName("virtuacluster").item(0);
					machineInfo.setVirtuacluster(got.getAttribute("enabled").equals("true"));
					
					got = (Element) root.getElementsByTagName("secondaryNetwork").item(0);
					machineInfo.setSecondNetwork(got.getAttribute("enabled").equals("true"));
					
					if (machineInfo.isSecondNetwork()) {
						machineInfo.setSocketPath(got.getAttribute("socket"));
						machineInfo.setMacAddress(got.getAttribute("macaddress"));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return machines;
		}
	}

}
