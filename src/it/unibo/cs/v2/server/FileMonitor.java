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

import it.unibo.cs.v2.shared.NotificationType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FileMonitor extends Timer {
	private File srcFile, dstFile;
	private File notificationFile;
	private String message;
	private BufferedWriter bw;

	private TimerTask innerTask = new TimerTask() {
		
		@Override
		public void run() {
			try {
				bw = new BufferedWriter(new FileWriter(notificationFile));
				bw.write(NotificationType.TIMEDJOB.toString());
				bw.newLine();
				
				bw.write(message + " " + srcFile.getName());
				bw.newLine();
				
				bw.write(new Double(((double) dstFile.length() / (double) srcFile.length()) * 100.0).toString());
				bw.newLine();
				
				bw.flush();
				bw.close();
			} catch (Exception e) {
				System.out.println("Exception in FileMonitor: " + e.getMessage());
			}
		}
		
		@Override
		public boolean cancel() {
			try {
				bw.flush();
				bw.close();
			} catch (Exception e) {}
			
			notificationFile.delete();
			return super.cancel();
		}
	};
	
	
	public FileMonitor(File srcFile, File dstFile, String home, String msg) {
		this.srcFile = srcFile;
		this.dstFile = dstFile;
		notificationFile = new File(home + "/notification-" + new Date().getTime());
		message = msg;
	}
	
	public void start() {
		scheduleAtFixedRate(innerTask, 1500, 10000);
	}
	
	@Override
	public void cancel() {
		innerTask.cancel();
		super.cancel();
	}
}
