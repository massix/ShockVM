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

import it.unibo.cs.v2.servlets.GetNotifications;
import it.unibo.cs.v2.shared.Notification;
import it.unibo.cs.v2.shared.NotificationType;
import it.unibo.cs.v2.shared.RefuseMachineNotification;
import it.unibo.cs.v2.shared.ShareMachineNotification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GetNotificationsImpl extends RemoteServiceServlet implements GetNotifications {

	@Override
	public LinkedList<Notification> getNotifications() {
		String home = (String) getThreadLocalRequest().getSession().getAttribute("home");
		String user = (String) getThreadLocalRequest().getSession().getAttribute("login");
		
		if (home.equals("") || user.equals(""))
			return null;
		
		File userDir = new File(home);
		
		File[] notifications = userDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().startsWith("notification-"))
					return true;
				
				return false;
			}
		});
		
		LinkedList<Notification> ret = new LinkedList<Notification>();
		
		for (File notification : notifications) {
			Notification add = null;
			try {
				BufferedReader notificationReader = new BufferedReader(new FileReader(notification));
				
				// First line is always the notification type
				NotificationType type = Notification.parseNotificationType(notificationReader.readLine());
				switch (type) {
				case ACCEPTEDSHARE:
				case REFUSEDSHARE:
					add = new RefuseMachineNotification();
					add.setType(type);
					add.setFrom(notificationReader.readLine());
					((RefuseMachineNotification) add).setMachineName(notificationReader.readLine());
					break;
				case SHAREMACHINE:
					add = new ShareMachineNotification();
					add.setType(type);
					add.setFrom(notificationReader.readLine());
					((ShareMachineNotification) add).setMachineName(notificationReader.readLine());
					break;
				default:
					return null;
				}
				
				add.setMessage(notificationReader.readLine());
				add.setFileName(notification.getName());
				
				notificationReader.close();
			} catch (FileNotFoundException e) {
				// Can't happen
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			ret.add(add);
		}
		
		return ret;
	}

}
