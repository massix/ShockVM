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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import it.unibo.cs.v2.servlets.ShutdownMachine;
import it.unibo.cs.v2.shared.MachineProcessInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ShutdownMachineImpl extends RemoteServiceServlet implements
		ShutdownMachine {

	private VirtuaLogger logger;
	
	@Override
	public void shutdownMachine(MachineProcessInfo machineProcess) throws Exception {
		String user = (String) getThreadLocalRequest().getSession().getAttribute("login");
		String home = (String) getThreadLocalRequest().getSession().getAttribute("home");
		
		logger = new VirtuaLogger(getServletContext().getContextPath(), this.getClass().getSimpleName());
		
		if (machineProcess == null)
			throw new Exception("Error while passing process informations. Contact the system administrator.");
		
		if (user.equals("") || home.equals(""))
			throw new Exception("Session expired, please refresh the page.");
		
		if (machineProcess.getPid() <= 0)
			throw new Exception("Wrong PID, contact the system administrator.");
		
		
		File activeMachines = new File(home + "/ACTIVEMACHINES.txt");
		
		if (!activeMachines.exists())
			throw new Exception("Appearently, you don't have running machines. Do you? Please contact the system administrator.");
		
		LinkedList<String[]> machines = new LinkedList<String[]>();
		BufferedReader activeMachinesReader = new BufferedReader(new FileReader(activeMachines));
		String line;
		
		while ((line = activeMachinesReader.readLine()) != null) {
			// Avoid empty lines
			if (line.length() > 7)
				machines.add(line.split("::"));
		}
		
		boolean found = false;
		for (String[] values : machines) {
			if (values[0].equals(machineProcess.getMachineName())) {
				found = true;
				break;
			}
		}
		
		activeMachinesReader.close();
		
		if (!found)
			throw new Exception("Machine not found in your active machines' pool. Please contact the system administrator.");
		
		logger.log("Shutting down machine " + machineProcess.getMachineName() + ", pid: " + machineProcess.getPid());

		ProcessBuilder killer = new ProcessBuilder("kill", "-15", String.valueOf(machineProcess.getPid()));
		Process exec;
		int exitValue = 0;
		
		synchronized (this) {
			try {
				exec = killer.start();
				exec.waitFor();
				exitValue = exec.exitValue();
			}
			catch (IOException e) {
				logger.log(e.getMessage());
			}
		};

		if (exitValue > 0)
			throw new Exception("Failed killing the machine.");
		
		BufferedWriter activeMachinesWriter = new BufferedWriter(new FileWriter(activeMachines));
		for (String[] values: machines) {
			if (values[0].equals(machineProcess.getMachineName()))
				continue;
			
			activeMachinesWriter.write(values[0] + "::" + values[1] + "::" + values[2]);
			activeMachinesWriter.newLine();
		}
		
		activeMachinesWriter.flush();
		activeMachinesWriter.close();
		
		synchronized(this) {
			File freeVnc = new File(getServletContext().getRealPath("/.freeServers"));
			BufferedWriter freeVncWriter = new BufferedWriter(new FileWriter(freeVnc, true));
			freeVncWriter.write(String.valueOf(machineProcess.getVncServer()));
			freeVncWriter.newLine();
			freeVncWriter.flush();
			freeVncWriter.close();
		};
	}
}
