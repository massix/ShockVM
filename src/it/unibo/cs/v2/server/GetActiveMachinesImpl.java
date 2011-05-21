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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

import it.unibo.cs.v2.servlets.GetActiveMachines;
import it.unibo.cs.v2.shared.MachineProcessInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GetActiveMachinesImpl extends RemoteServiceServlet implements GetActiveMachines {

	@Override
	public LinkedList<MachineProcessInfo> getActiveMachines() throws Exception {
		String user = (String) getThreadLocalRequest().getSession().getAttribute("login");
		String home = (String) getThreadLocalRequest().getSession().getAttribute("home");
		
		if (user.equals("") || home.equals(""))
			throw new Exception("Session expired. Please login again.");
		
		File activeMachinesFile = new File(home + "/ACTIVEMACHINES.txt");
		
		if (!activeMachinesFile.exists())
			return null;
		
		BufferedReader reader = new BufferedReader(new FileReader(activeMachinesFile));
		String line;
		
		LinkedList<MachineProcessInfo> ret = new LinkedList<MachineProcessInfo>();
		
		synchronized(this) {
			while ((line = reader.readLine()) != null) {
				if (line.length() < 7)
					continue;
				
				ret.add(buildMPI(line.split("::")));
			}
		}

		reader.close();
		
		if (ret.size() < 1) {
			activeMachinesFile.delete();
			return null;
		}
		
		return ret;
	}
	
	private MachineProcessInfo buildMPI(String[] values) {
		MachineProcessInfo mp = new MachineProcessInfo();
		
		mp.setMachineName(values[0]);
		mp.setPid(new Integer(values[1]));
		mp.setVncServer(new Integer(values[2]));
		
		return mp;
	}

}
