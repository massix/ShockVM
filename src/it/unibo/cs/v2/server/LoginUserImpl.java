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
 * along with Foobar. If not, see http://www.gnu.org/licenses/.
 */

package it.unibo.cs.v2.server;

import it.unibo.cs.v2.client.LoginUser;

import java.io.File;

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
					if (BCrypt.checkpw(password, passwordElem.getTextContent())) 
						return true;
					
					else
						return false;
				}

			}

			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

}
