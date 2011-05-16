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

import it.unibo.cs.v2.servlets.CreateMachine;
import it.unibo.cs.v2.shared.MachineInfo;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class CreateMachineImpl extends RemoteServiceServlet implements CreateMachine {

	@Override
	public boolean createMachine(MachineInfo machineInfo) throws IOException {
		String userHome = (String) getThreadLocalRequest().getSession().getAttribute("home");
		VirtuaLogger logger = new VirtuaLogger(userHome, this.getClass().toString());
		
		File machineFile = new File(userHome + "/" + machineInfo.getName().replace(' ', '_') + ".xml");
		
		if (machineFile.exists())
			throw new IOException("A machine with that name already exists.");
		
		if (!machineFile.createNewFile())
			throw new IOException("Couldn't create a new file with that name.");
		
		DocumentBuilder db;
		try {
			ProcessBuilder qemuImg;
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.newDocument();
			Element root = doc.createElement("VirtualMachine");
			
			Element newElem = doc.createElement("name");
			newElem.setTextContent(machineInfo.getName());
			root.appendChild(newElem);
			
			newElem = doc.createElement("description");
			newElem.setTextContent(machineInfo.getDescription());
			root.appendChild(newElem);
			
			newElem = doc.createElement("iso");
			newElem.setAttribute("path", machineInfo.getIso());
			root.appendChild(newElem);
			
			newElem = doc.createElement("hda");
			String hdaFileName = machineInfo.getHda().replace(' ', '_') + ".img";
			newElem.setAttribute("path", hdaFileName);
			newElem.setAttribute("size", machineInfo.getHdaSize());
			newElem.setAttribute("enabled", "true");
			root.appendChild(newElem);
			
			newElem = doc.createElement("hdb");
			String hdbFileName = machineInfo.isHdbEnabled()? machineInfo.getHdb().replace(' ', '_') + ".img" : "false";
			newElem.setAttribute("enabled", machineInfo.isHdbEnabled()? "true" : "false");
			newElem.setAttribute("path", hdbFileName);
			newElem.setAttribute("size", machineInfo.isHdbEnabled()? machineInfo.getHdbSize() : "");
			root.appendChild(newElem);
			
			newElem = doc.createElement("virtuacluster");
			newElem.setAttribute("enabled", machineInfo.isVirtuacluster()? "true" : "false");
			root.appendChild(newElem);
			
			newElem = doc.createElement("secondaryNetwork");
			newElem.setAttribute("enabled", machineInfo.isSecondNetwork()? "true" : "false");
			newElem.setAttribute("socket", machineInfo.getSocketPath());
			newElem.setAttribute("macaddress", machineInfo.getMacAddress());
			root.appendChild(newElem);
			
			doc.appendChild(root);
			
			doc.normalize();
			
			Transformer t = TransformerFactory.newInstance().newTransformer();
			
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			
			t.transform(new DOMSource(doc), new StreamResult(machineFile));
			
			try {
				qemuImg = new ProcessBuilder("qemu-img", "create", hdaFileName, machineInfo.getHdaSize());
				qemuImg.directory(new File(userHome));
				Process p = qemuImg.start();
				p.waitFor();
				
				if (machineInfo.isHdbEnabled()) {
					qemuImg = new ProcessBuilder("qemu-img", "create", hdbFileName, machineInfo.getHdbSize());
					qemuImg.directory(new File(userHome));
					Process p2 = qemuImg.start();
					p2.waitFor();
				}
			} catch (IOException e) {
				logger.log(e.getMessage());
			}
			
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		logger.log("Created a new machine (" + machineInfo.getName() + ")");
		return true;
	}

}
