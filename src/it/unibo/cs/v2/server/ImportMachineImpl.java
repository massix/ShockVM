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

import it.unibo.cs.v2.servlets.ImportMachine;
import it.unibo.cs.v2.shared.MachineInfo;
import it.unibo.cs.v2.shared.NotificationType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ImportMachineImpl extends RemoteServiceServlet implements ImportMachine {

	@Override
	public boolean importMachine(final MachineInfo machine) throws Exception {
		final String user = (String) getThreadLocalRequest().getSession().getAttribute("login");
		final String home = (String) getThreadLocalRequest().getSession().getAttribute("home");
		
		final VirtuaLogger logger = new VirtuaLogger(home, this.getClass().getSimpleName());
		
		if (user.equals("") || home.equals(""))
			throw new Exception("Session expired, please refresh the page.");
		
		System.out.println("User " + user + " requested to import machine " + machine.getName() + " (" + machine.getConfigurationFile() + ") in " + home);

		// Some objects we'll be using soon.
		final File machineXmlFile = new File(machine.getConfigurationFile());
		final String machineUser = machineXmlFile.getName().split("-")[0];
		final File hdaFile = new File(getServletContext().getRealPath("prebuilt") + "/" + machineUser + "-" + machine.getHda());
		final File machineXmlDestination = new File(home + "/" + machine.getName().replace(' ', '_') + ".xml");
		final File hdaFileDestination = new File(home + "/" + machine.getHda());
		
		if (!machineXmlFile.exists())
			throw new Exception("Configuration file not found. This is not your fault. Please contact the system administrators.");
		
		if (!hdaFile.exists())
			throw new Exception("HDA File not found. This is not your fault. Please contact the system administrators.");

		if (machineXmlDestination.exists() || hdaFileDestination.exists())
			throw new Exception("You already have that machine.");
		
		if (machine.isHdbEnabled()) 
			if (new File(home + "/" + machine.getHdb()).exists())
				throw new Exception("You already have that machine.");

		
		// Here we go, let's do the tedious work in background.
		new Thread() {
			
			@Override
			public void run() {
				logger.log("Starting to import machine " + machine.getName());
				boolean error = false;

				try {
					FileMonitor fm = new FileMonitor(hdaFile, hdaFileDestination, home, "Importing");
					
					fm.start();
					Utils.customCopy(hdaFile, hdaFileDestination, ((1024 * 1024) * 10));
					fm.cancel();
				
					if (machine.isHdbEnabled()) {
						File hdbFile = new File(getServletContext().getRealPath("prebuilt") + "/" + machineUser + "-" + machine.getHdb());
						File hdbFileDestination = new File(home + "/" + machine.getHdb());
						
						fm = new FileMonitor(hdbFile, hdbFileDestination, home, "Importing");
						fm.start();
						Utils.customCopy(hdbFile, hdbFileDestination, ((1024 * 1024) * 10));
						fm.cancel();
					}
							
					BufferedReader srcXml = new BufferedReader(new FileReader(machineXmlFile));
					BufferedWriter dstXml = new BufferedWriter(new FileWriter(machineXmlDestination));
						
					String line = "";
					while ((line = srcXml.readLine()) != null) {
						if (line.startsWith("<realowner>"))
							line = "<realowner>" + user + "</realowner>";
							
						if (line.startsWith("<share"))
							continue;
							
						dstXml.write(line);
						dstXml.newLine();
					}
						
					srcXml.close();
					dstXml.flush();
					dstXml.close();
				} catch (Exception e) {
					error = true;
				}
			
				logger.log("Complete");
				
				// Drop a notification
				try {
					BufferedWriter notificationWriter = new BufferedWriter(new FileWriter(new File(home + "/notification-" + new Date().getTime())));
					notificationWriter.write(NotificationType.IMPORTCOMPLETE.toString());
					notificationWriter.newLine();
					
					notificationWriter.write(error? "false" : "true");
					notificationWriter.newLine();
					
					notificationWriter.write(machine.getName());
					notificationWriter.newLine();
					
					notificationWriter.flush();
					notificationWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};

		}.start();
	
		return true;
	}

}
