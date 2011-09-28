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

import it.unibo.cs.v2.servlets.RefuseShare;
import it.unibo.cs.v2.shared.NotificationType;
import it.unibo.cs.v2.shared.ShareMachineNotification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOError;
import java.util.Date;

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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class RefuseShareImpl extends RemoteServiceServlet implements RefuseShare {

	@Override
	public boolean refuseShare(ShareMachineNotification notification) throws Exception {
		String user = (String) getThreadLocalRequest().getSession().getAttribute("login");
		String home = (String) getThreadLocalRequest().getSession().getAttribute("home");

		VirtuaLogger logger = new VirtuaLogger(home, this.getClass().getName());
		
		if (user.equals("") || home.equals(""))
			throw new Exception("Session has expired.");
		
		// Remove the notification file
		File notificationFile = new File(home + "/" + notification.getFileName());
		if (!notificationFile.exists())
			throw new Exception("Notification not found on server. This is programmers' fault, not yours.");
		
		else
			notificationFile.delete();
		
		logger.log(user + " is refusing the share of " + notification.getMachineName() + " sent by " + notification.getFrom());
		
		// Remove the pending share from the remote machine's xml
		String machineName = notification.getMachineName().replace(' ', '_');
		File remoteMachineFile = new File(getServletContext().getRealPath("users/" + notification.getFrom() + "/" + machineName + ".xml"));
		logger.log("modifying file: " + remoteMachineFile.getAbsolutePath());
		if (remoteMachineFile.exists()) {
			logger.log("Removing " + user + " from the pending shares of machine " + notification.getMachineName());
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document xmlDoc = db.parse(remoteMachineFile);
			
			Element rootElement = xmlDoc.getDocumentElement();
			
			NodeList shares = rootElement.getElementsByTagName("share");
			for (int i = 0; i < shares.getLength(); i++) {
				Element e = (Element) shares.item(i);
				
				if (e.getAttribute("status").equals("pending") && e.getAttribute("user").equals(user)) 
					rootElement.removeChild(e);
			}
			
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(xmlDoc), new StreamResult(remoteMachineFile));
			logger.log ("Removed " + user + " from the pending shares..");
			
			// Drop a notification to the requesting user
			Date today = new Date();
			
			try {
				File newNotification = new File(getServletContext().getRealPath("users/" + notification.getFrom() + "/notification-" + today.getTime()));
				newNotification.createNewFile();
			
				BufferedWriter br = new BufferedWriter(new FileWriter(newNotification));
				
				// First line is the notification type
				br.write(NotificationType.REFUSEDSHARE.toString());
				br.newLine();
				
				// Second line is the sender
				br.write(user);
				br.newLine();
				
				// Third line is the machine name
				br.write(notification.getMachineName());
				br.newLine();
				
				// And then the message
				br.write("User " + user + " refused the share of the machine " + notification.getMachineName());
				br.newLine();
				
				br.flush();
				br.close();
			} catch (IOError e) {
				throw new Exception(e.getMessage());
			}
			
			
		}
		
		else
			throw new Exception("Remote machine's file not found. This is more likely to be a programmers' fault.");
		
		return true;
	}

}
