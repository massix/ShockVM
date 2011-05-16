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

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.unibo.cs.v2.servlets.DeleteMachine;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DeleteMachineImpl extends RemoteServiceServlet implements DeleteMachine {

	@Override
	public boolean deleteMachine(String machineName) throws Exception {
		String home = (String) getThreadLocalRequest().getSession().getAttribute("home");
		String login = (String) getThreadLocalRequest().getSession().getAttribute("login");
		
		VirtuaLogger logger = new VirtuaLogger(home, this.getClass().toString());
		
		if (home.equals("") || login.equals(""))
			throw new Exception("Session expired, refresh page and login again.");
		
		if (machineName.equals(""))
			throw new Exception("Machine name is blank. Something went wrong server-side, refresh the page and login again.");
		
		File machineFile = new File(home + "/" + machineName.replace(' ', '_') + ".xml");
		
		if (!machineFile.exists())
			throw new Exception("Machine not found.");
		
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document machineDoc = db.parse(machineFile);
		
		Element hdElem = (Element) machineDoc.getElementsByTagName("hda").item(0);
		File hdFile = new File(home + "/" + hdElem.getAttribute("path"));
		if (hdFile.exists())
			hdFile.delete();
		
		hdElem = (Element) machineDoc.getElementsByTagName("hdb").item(0);
		hdFile = new File(home + "/" + hdElem.getAttribute("path"));
		if (hdFile.exists())
			hdFile.delete();
		
		if (machineFile.delete()) {
			logger.log("Deleted machine " + machineName);
			return true;
		}
		else {
			logger.log("Failed to delete machine " + machineName);
			return false;
		}
	}
}
