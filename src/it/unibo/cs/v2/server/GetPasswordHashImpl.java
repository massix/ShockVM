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

import it.unibo.cs.v2.client.GetPasswordHash;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GetPasswordHashImpl extends RemoteServiceServlet implements GetPasswordHash {

	public String getPasswordHash(String original) {
		if (original.equals("") || original == null)
			return null;

		return BCrypt.hashpw(original, BCrypt.gensalt());
	}

}
