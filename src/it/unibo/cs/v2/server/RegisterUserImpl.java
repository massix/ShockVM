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

import it.unibo.cs.v2.client.RegisterUser;

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
import org.w3c.dom.Node;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class RegisterUserImpl extends RemoteServiceServlet implements RegisterUser {

	@Override
	public boolean registerUser(String username, String password, String displayname) throws Exception {
		String encodedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		File usersFile = new File(getServletContext().getRealPath("users.xml"));
		
		if (!usersFile.canRead())
			throw new IOException("Can't read from users file");
		
		if (!usersFile.canWrite()) 
			throw new IOException("I can't write on destination file");
		
		if (!usersFile.exists())
			throw new IOException("Users file doesn't exist");

		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document users = db.parse(usersFile);

			users.getDocumentElement().normalize();
			
			Node rootElement = users.getFirstChild();

			Element newUser = users.createElement("user");
			newUser.setAttribute("displayname", displayname);
			
			Element newLogin = users.createElement("login");
			newLogin.setAttribute("role", "user");
			newLogin.setTextContent(username);
			
			Element newPassword = users.createElement("password");
			newPassword.setAttribute("encrypt", "BCrypt");
			newPassword.setTextContent(encodedPassword);
			
			rootElement.appendChild(newUser);
			newUser.appendChild(newLogin);
			newUser.appendChild(newPassword);
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(usersFile);
			DOMSource source = new DOMSource(users);
			transformer.transform(source, result);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
