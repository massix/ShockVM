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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.unibo.cs.v2.servlets.RemoveShare;
import it.unibo.cs.v2.shared.MachineInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class RemoveShareImpl extends RemoteServiceServlet implements RemoveShare {

	@Override
	public boolean removeShare(MachineInfo machineInfo, String dstUser) throws Exception {
		String user = (String) getThreadLocalRequest().getSession().getAttribute("login");
		String home = (String) getThreadLocalRequest().getSession().getAttribute("home");
		
		if (user.equals("") || home.equals(""))
			throw new Exception("Session has expired.");
		
		File machineFile = new File(machineInfo.getConfigurationFile());
		
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document xmlDoc = db.parse(machineFile);
		
		Element rootNode = xmlDoc.getDocumentElement();
		
		NodeList shares = rootNode.getElementsByTagName("share");
		for (int i = 0; i < shares.getLength(); i++) {
			Element e = (Element) shares.item(i);
			
			if (e.getAttribute("user").equals(dstUser)) {
				rootNode.removeChild(e);
				break;
			}
		}
		
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.transform(new DOMSource(xmlDoc), new StreamResult(machineFile));
		
		File remoteMachineFile = new File(getServletContext().getRealPath("users/" + dstUser + "/" + machineFile.getName()));
		if (remoteMachineFile.exists())
			remoteMachineFile.delete();
		
		/// TODO: drop a notification to the removed user
		
		return true;
	}

}
