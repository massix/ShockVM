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

import it.unibo.cs.v2.servlets.AcceptShare;
import it.unibo.cs.v2.shared.NotificationType;
import it.unibo.cs.v2.shared.ShareMachineNotification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class AcceptShareImpl extends RemoteServiceServlet implements AcceptShare {

	@Override
	public boolean acceptShare(ShareMachineNotification notification) throws Exception {
		String user = (String) getThreadLocalRequest().getSession().getAttribute("login");
		String home = (String) getThreadLocalRequest().getSession().getAttribute("home");
		
		if (user.equals("") || home.equals(""))
			throw new Exception("Session has expired.");
		
		File notificationFile = new File(home + "/" + notification.getFileName());
		
		if (!notificationFile.exists())
			throw new Exception("Notification not found on server. This is programmers' fault, not yours.");
		else
			notificationFile.delete();
		
		File remoteMachineFile = new File(getServletContext().getRealPath("users/" + notification.getFrom() + 
				"/" + notification.getMachineName().replace(' ', '_') + ".xml"));
		
		File localMachineFile = new File(home + "/shared-" + notification.getMachineName().replace(' ', '_') + "-" + notification.getFrom() + ".xml");
		
		if (!remoteMachineFile.exists())
			throw new Exception("Remote machine file not found. Likely, the user " + notification.getFrom() + " has deleted it.");
		
		if (localMachineFile.exists())
			throw new Exception("You already have a machine named that way. Delete it first.");
		
		Utils.copy(remoteMachineFile, localMachineFile);
		
		synchronized (this) {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document xmlDoc = db.parse(remoteMachineFile);
			
			NodeList shares = xmlDoc.getElementsByTagName("share");
			for (int i = 0; i < shares.getLength(); i++) {
				Element share = (Element) shares.item(i);
				if (share.getAttribute("user").equals(user)) {
					share.setAttribute("status", "accepted");
					break;
				}
			}
			
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(xmlDoc), new StreamResult(remoteMachineFile));
		}
		
		// Drop a notification to the other user
		File notify = new File(getServletContext().getRealPath("users/" + notification.getFrom() + "/notification-" + new Date().getTime() + ".txt"));
		notify.createNewFile();
		
		BufferedWriter br = new BufferedWriter(new FileWriter(notify));
		
		// First line is the notification type
		br.write(NotificationType.ACCEPTEDSHARE.toString());
		br.newLine();
		
		// Second line is the user sending the notification
		br.write(user);
		br.newLine();
		
		// Third line is the machine name
		br.write(notification.getMachineName());
		br.newLine();
		
		// And then the HTML message itself
		br.write("User " + user + " accepted the share of the machine " + notification.getMachineName());
		br.newLine();
		
		br.flush();
		br.close();
		return true;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		
		out.println("Get method not implemented here.");
		out.close();
		
//		super.doGet(req, resp);
	}

}
