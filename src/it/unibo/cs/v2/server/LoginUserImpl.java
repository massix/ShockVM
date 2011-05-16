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

import it.unibo.cs.v2.servlets.LoginUser;

import java.io.File;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class LoginUserImpl extends RemoteServiceServlet implements LoginUser {

	public boolean loginUser(String userId, String password) {
		File usersFile = new File(getServletContext().getRealPath("users.xml"));
		VirtuaLogger logger = new VirtuaLogger(getServletContext().getRealPath("."), "login user");
		if (password == null || password.equals(""))
			return false;

		try {
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
				Element passwordElem = (Element) user.getElementsByTagName("password").item(0);

				if (login.getTextContent().equalsIgnoreCase(userId)) {
					if (BCrypt.checkpw(password, passwordElem.getTextContent())) {
						HttpSession session = getThreadLocalRequest().getSession();
						session.setAttribute("login", (String) login.getTextContent());
						logger.log(login.getTextContent() + " has logged in");
						return true;
					}
					
					else
						return false;
				}

			}

			logger.log(userId + " failed to login");
			return false;
		} catch (Exception e) {
			return false;
		}
	}

}
