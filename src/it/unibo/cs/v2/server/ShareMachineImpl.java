package it.unibo.cs.v2.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import it.unibo.cs.v2.servlets.ShareMachine;
import it.unibo.cs.v2.shared.MachineInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ShareMachineImpl extends RemoteServiceServlet implements ShareMachine {

	@Override
	public boolean shareMachine(MachineInfo machine, String user) throws Exception {
		String login = (String) getThreadLocalRequest().getSession().getAttribute("login");
		String home = (String) getThreadLocalRequest().getSession().getAttribute("home");
		
		if (login.equals("") || home.equals(""))
			throw new Exception("Session expired, please refresh the page.");
		
		File usersDir = new File(getServletContext().getRealPath("users"));
		
		if (!usersDir.isDirectory())
			throw new Exception("Something went wrong. This is programmers' fault, not yours. Please contact the system administrator.");
		
		File[] singleDirs = usersDir.listFiles();
		
		File destDir = null;
		for (File f : singleDirs) {
			if (!f.isDirectory())
				continue;
			
			if (f.getName().equals(user)) {
				destDir = f;
				break;
			}
		}
		
		if (destDir == null)
			throw new Exception("Destination dir not found. Wrong user provided?");
		
		Date today = new Date();
		
		File newNotification = new File(destDir.getAbsolutePath() + "/notification-" + today.getTime());
		newNotification.createNewFile();
		
		BufferedWriter notificationWriter = new BufferedWriter(new FileWriter(newNotification));
		notificationWriter.write("Sender: " + login);
		notificationWriter.newLine();
		notificationWriter.write("Message: wanna share this machine?");
		notificationWriter.newLine();
		notificationWriter.flush();
		notificationWriter.close();
		
		return true;
	}

}
