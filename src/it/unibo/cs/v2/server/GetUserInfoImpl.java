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

import it.unibo.cs.v2.servlets.GetUserInfo;

import java.io.File;
import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GetUserInfoImpl extends RemoteServiceServlet implements
		GetUserInfo {

	@Override
	public HashMap<String, String> getUserInfo() throws Exception {
		File usersFile = new File(getServletContext().getRealPath("users.xml"));

		try {
			String username = (String) getThreadLocalRequest().getSession().getAttribute("login");
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document users = db.parse(usersFile);

			users.getDocumentElement().normalize();
			
			NodeList usersNodeLst = users.getElementsByTagName("user");

			for (int i = 0; i < usersNodeLst.getLength(); i++) {
				Node n = usersNodeLst.item(i);

				if (!(n.getNodeType() == Node.ELEMENT_NODE))
					continue;

				Element user = (Element) n;
				Element login = (Element) user.getElementsByTagName("login").item(0);
				Element password = (Element) user.getElementsByTagName("password").item(0);
				Element home = (Element) user.getElementsByTagName("home").item(0);


				
				if (login.getTextContent().equals(username)) {		
					HashMap<String, String> ret = new HashMap<String, String>();

					HttpSession session = getThreadLocalRequest().getSession();
					
					ret.put("displayname", user.getAttribute("displayname"));
					ret.put("role", login.getAttribute("role"));
					ret.put("login", login.getTextContent());
					ret.put("password", password.getTextContent());
					ret.put("encrypt", password.getAttribute("encrypt"));
					ret.put("home", home.getTextContent());
					
					session.setAttribute("password", password.getTextContent());
					session.setAttribute("home", home.getTextContent());

					return ret;
				}

			}

			return null;
		} catch (Exception e) {
			throw e;
		}
	}

}
