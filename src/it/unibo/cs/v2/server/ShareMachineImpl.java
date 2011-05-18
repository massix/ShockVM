package it.unibo.cs.v2.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.unibo.cs.v2.servlets.ShareMachine;
import it.unibo.cs.v2.shared.MachineInfo;
import it.unibo.cs.v2.shared.NotificationType;

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
		
		// Write out the notification
		BufferedWriter notificationWriter = new BufferedWriter(new FileWriter(newNotification));

		// First line is the notification type
		notificationWriter.write(NotificationType.SHAREMACHINE.toString());
		notificationWriter.newLine();
		
		// Second line is the sender
		notificationWriter.write(login);
		notificationWriter.newLine();

		// Third line is the Object of the notification (machineName in this case)
		notificationWriter.write(machine.getName());
		notificationWriter.newLine();

		notificationWriter.write("User " + login + " wants to share the following machine with you.<br />");
		notificationWriter.write("<b>Name</b>: " + machine.getName() + "<br />");
		notificationWriter.write("<b>Description</b>: " + machine.getDescription() + "<br />");
		notificationWriter.write("<b>VirtuaCluster " + (machine.isVirtuacluster()? "enabled" : "disabled") + "</b><br />");
		notificationWriter.newLine();
		
		notificationWriter.flush();
		notificationWriter.close();
		
		// Write out the pending user in the XML File
		File machineFile = new File(machine.getConfigurationFile());
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(machineFile);
		
		Element root = doc.getDocumentElement();
		
		Element newShare = doc.createElement("share");
		newShare.setAttribute("status", "pending");
		newShare.setAttribute("user", user);
		
		root.appendChild(newShare);
		
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.transform(new DOMSource(doc), new StreamResult(machineFile));
		
		return true;
	}

}
