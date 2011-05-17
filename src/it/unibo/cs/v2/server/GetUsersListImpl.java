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
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.unibo.cs.v2.servlets.GetUsersList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GetUsersListImpl extends RemoteServiceServlet implements GetUsersList {
	private LinkedList<String> usersList = new LinkedList<String>();
	private Timer updateListTimer = new Timer();
	private TimerTask updateListTask = new TimerTask() {
		
		@Override
		public void run() {
			try {
				updateList();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private File usersXMLFile;
	
	public GetUsersListImpl() throws Exception {
		updateListTimer.schedule(updateListTask, 1500, 10000);
	}
	
	@Override
	public LinkedList<String> getUsersList() throws Exception {
		if (usersXMLFile == null)
			usersXMLFile = new File(getServletContext().getRealPath("users.xml"));
		
		if (usersList.size() < 1)
			updateList();
		
		return usersList;
	}
	
	private void updateList() throws Exception {
		usersList.clear();
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document usersXml = db.parse(usersXMLFile);
		
		NodeList users = usersXml.getElementsByTagName("login");
		
		for (int i = 0; i < users.getLength(); i++) 
			usersList.add(((Element) users.item(i)).getTextContent());
	}

}
