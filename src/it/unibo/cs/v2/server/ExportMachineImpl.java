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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import it.unibo.cs.v2.servlets.ExportMachine;
import it.unibo.cs.v2.shared.MachineInfo;
import it.unibo.cs.v2.shared.NotificationType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ExportMachineImpl extends RemoteServiceServlet implements ExportMachine {

	@Override
	public boolean exportMachine(final MachineInfo machine, final String description) throws Exception {
		final String user = (String) getThreadLocalRequest().getSession().getAttribute("login");
		final String home = (String) getThreadLocalRequest().getSession().getAttribute("home");
		
		if (user.equals("") || home.equals(""))
			throw new Exception("Session has expired.");
		
		
		// Source files
		final File machineXml = new File(machine.getConfigurationFile());
		final File hda = new File(home + "/" + machine.getHda());
		final File hdb = new File(home + "/" + machine.getHdb());
		
		File prebuiltDir = new File(getServletContext().getRealPath("prebuilt"));
		
		// Possible errors
		if (!machineXml.exists())
			throw new Exception("Configuration file for the machine not found.");
		
		if (!hda.exists())
			throw new Exception("Primary disk not found.");
		
		// Destinations
		String machineTxt = machine.getName().replace(' ', '_') + ".txt";
		
		final File machineXmlDestination = new File(prebuiltDir.getAbsolutePath() + "/" + user + "-" + machineXml.getName());
		final File descriptionDestination = new File(prebuiltDir.getAbsolutePath() + "/" + user + "-" + machineTxt);
		descriptionDestination.createNewFile();
		
		final File hdaDestination = new File(prebuiltDir.getAbsolutePath() + "/" + user + "-" + hda.getName());
		final File hdbDestination = new File(prebuiltDir.getAbsolutePath() + "/" + user + "-" + hdb.getName());
		
		// Possible errors
		if (machineXmlDestination.exists())
			throw new Exception("You've already exported that machine.");
		
		if (hdaDestination.exists())
			throw new Exception("A disk with that name already exists.");
		
		
		// Do all the work in another thread
		new Thread() {
			public void run() {
				try {
					// Copy the stuff
					Utils.customCopy(hda, hdaDestination, ((1024 * 1024) * 10));
					if (machine.isHdbEnabled())
						Utils.customCopy(hdb, hdbDestination, ((1024 * 1024) * 10));
					
					// Write the description to a file
					BufferedWriter bWriter = new BufferedWriter(new FileWriter(descriptionDestination));
					bWriter.write(description);
					bWriter.flush();
					bWriter.close();
					
					// Last, but not least, the Xml file.
					Utils.copy(machineXml, machineXmlDestination);
					
					// Drop a notification at the user's home.
					File notification = new File(home + "/notification-" + new Date().getTime());
					BufferedWriter notificationWriter = new BufferedWriter(new FileWriter(notification));
					
					// Notification type
					notificationWriter.write(NotificationType.EXPORTCOMPLETE.toString());
					notificationWriter.newLine();
					
					// Success
					notificationWriter.write("true");
					notificationWriter.newLine();
					
					// Machine's name
					notificationWriter.write(machine.getName());
					notificationWriter.newLine();
					
					// Ok, done.
					notificationWriter.flush();
					notificationWriter.close();
				} 
				
				catch (Exception e) {
					// Drop a notification at the user's home.
					File notification = new File(home + "/notification-" + new Date().getTime());
					try {
						VirtuaLogger logger = new VirtuaLogger(home, this.getClass().getName());
						BufferedWriter notificationWriter = new BufferedWriter(new FileWriter(notification));
						
						// Notification type
						notificationWriter.write(NotificationType.EXPORTCOMPLETE.toString());
						notificationWriter.newLine();
						
						// Success
						notificationWriter.write("false");
						notificationWriter.newLine();
						
						// Machine's name
						notificationWriter.write(machine.getName());
						notificationWriter.newLine();
						
						// Ok, done.
						notificationWriter.flush();
						notificationWriter.close();
						
						logger.log("Exception caught while exporting machine " + machine.getName() + ": " + e.getMessage());
					}
					catch (IOException inner) {
						inner.printStackTrace();
					}
				}
			};
		}.start();
		
		return true;
	}
}
