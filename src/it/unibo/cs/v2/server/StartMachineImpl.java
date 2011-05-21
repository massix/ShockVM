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

import it.unibo.cs.v2.servlets.StartMachine;
import it.unibo.cs.v2.shared.MachineInfo;
import it.unibo.cs.v2.shared.MachineProcessInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class StartMachineImpl extends RemoteServiceServlet implements StartMachine {

	@Override
	public MachineProcessInfo startViewMachine(MachineInfo machineInfo) throws Exception {
		MachineProcessInfo mp = new MachineProcessInfo();
		mp.setMachineName(machineInfo.getName());
		mp.setOwned(true);

		String home = (String) getThreadLocalRequest().getSession().getAttribute("home");
		String user = (String) getThreadLocalRequest().getSession().getAttribute("login");

		// TODO: check if the machine is owned by the user that invoked the applet
		String realOwner = machineInfo.getRealOwner();
		if (!realOwner.equals(user)) {
			File otherUserDir = new File(getServletContext().getRealPath("/users/" + realOwner));
			if (!otherUserDir.exists())
				throw new Exception("You don't own this machine, and appearently, the user that owns it doesn't exist.");
			
			File otherUserActiveFile = new File(getServletContext().getRealPath("users/" + realOwner + "/ACTIVEMACHINES.txt"));

			if (!otherUserActiveFile.exists())
				throw new Exception("You don't own this machine and " + realOwner + " hasn't started any machine yet.");
			
			else {
				BufferedReader br = new BufferedReader(new FileReader(otherUserActiveFile));
				String line;
				while ((line = br.readLine()) != null) {
					if (line.length() < 7)
						continue;
					
					String[] values = line.split("::");
					if (values == null)
						continue;
					
					if (values[0].equals(machineInfo.getName())) {
						mp.setPid(new Integer(values[1]));
						mp.setVncServer(new Integer(values[2]));
						
						br.close();
						
						mp.setOwned(false);
						
						return mp;
					}
				}
			}
			
			throw new Exception("You don't own this machine and " + realOwner + " hasn't started it yet.");

		}
		
		// The owner of the machine is the user that invoked the applet
		VirtuaLogger logger = new VirtuaLogger(home, this.getClass().toString());
		
		if (home.equals("") || user.equals(""))
			throw new Exception("Session expired.");

		File activeMachines = new File(home + "/ACTIVEMACHINES.txt");
		File freeServers = new File(getServletContext().getRealPath("/.freeServers"));
		File startup = new File(home + "/startup.sh");

		if (!freeServers.exists())
			throw new Exception("No free servers file found.");

		if (!activeMachines.exists()) 
			activeMachines.createNewFile();

		else {
			BufferedReader br = new BufferedReader(new FileReader(activeMachines));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() < 7)
					continue;

				String[] values = line.split("::");
				if (values == null)
					continue;

				if (values[0].equals(machineInfo.getName())) {
					mp.setPid(new Integer(values[1]));
					mp.setVncServer(new Integer(values[2]));

					br.close();

					return mp;
				}
			}
		}

		String freeVnc = "";

		// Guarantee a synchronized access to the resources
		synchronized (this) {
			final BufferedReader freeServersReader = new BufferedReader(new FileReader(freeServers));

			while (freeVnc.equals("")) {
				freeVnc = freeServersReader.readLine();
				if (freeVnc == null)
					throw new Exception("No free servers found. Wait your turn!");
			}

			final LinkedList<String> freeList = new LinkedList<String>();
			String read;
			while ((read = freeServersReader.readLine()) != null) {
				if (!read.equals(""))
					freeList.add(read);
			}

			freeServersReader.close();

			BufferedWriter freeServersWriter = new BufferedWriter(new FileWriter(freeServers));
			for (String server : freeList) {
				freeServersWriter.write(server);
				freeServersWriter.newLine();
			}

			freeServersWriter.close();
		};
		
		mp.setVncServer(new Integer(freeVnc));
		mp.setPid(0);
		
		String kvmCommand = "kvm -cdrom " + getServletContext().getRealPath(machineInfo.getIso());
	
		kvmCommand += " -hda " + machineInfo.getHda();
		
		if (machineInfo.isHdbEnabled())
			kvmCommand += " -hdb " + machineInfo.getHdb();
		
		if (machineInfo.isVirtuacluster()) {
			String mac = "";
			if ((mac = machineInfo.getMacAddress()) != null)
				kvmCommand += " -net nic,vlan=0,macaddr=" + mac;
			else
				kvmCommand += " -net nic,vlan=0,macaddr=52:54:00:AA:BB:" + freeVnc;
			kvmCommand += " -net vde,vlan=0,sock=/tmp/virtua_switch";
		}
		
		if (!machineInfo.isVirtuacluster() && !machineInfo.isSecondNetwork())
			kvmCommand += " -net none";
		
		kvmCommand += " -boot " + (machineInfo.isBootCdrom()? "d" : "c");
		
		kvmCommand += " -nographic -vnc :" + freeVnc;
		
		kvmCommand += " -m 1024 -enable-kvm -vga std";
		
		if (machineInfo.isTabletDevice())
			kvmCommand += " -usbdevice tablet";
		
		String pgrepCommand = "pgrep -nu tomcat6 -fx \"" + kvmCommand + "\" > .temp\n\n";
		
		FileWriter startupWriter = new FileWriter(startup);
		startupWriter.write("#!/bin/sh\n\n");
		startupWriter.write(kvmCommand + "&\n");
		startupWriter.write("sleep 3\n");
		startupWriter.write(pgrepCommand);
		startupWriter.flush();
		startupWriter.close();
		
		if (!startup.canExecute())
			startup.setExecutable(true, true);
		
		int pid = -1;
		try {
			ProcessBuilder execScript = new ProcessBuilder("/bin/sh", startup.getAbsolutePath());
			execScript.directory(new File(home));
			Process exec = execScript.start();
			
			exec.waitFor();
			
			File pidFile = new File(home + "/.temp");
			if (pidFile.exists()) {
				BufferedReader tempReader = new BufferedReader(new FileReader(pidFile));
				pid = new Integer(tempReader.readLine());
				tempReader.close();
				pidFile.delete();
			}
			
			BufferedWriter activeMachinesWriter = new BufferedWriter(new FileWriter(activeMachines, true));
			activeMachinesWriter.write(machineInfo.getName() + "::" + pid + "::" + freeVnc);
			activeMachinesWriter.newLine();
			activeMachinesWriter.close();
			mp.setPid(pid);

			
		}
		catch (IOException e) {
			logger.log(e.getMessage());
			throw e;
		}

		return mp;
	}

}
