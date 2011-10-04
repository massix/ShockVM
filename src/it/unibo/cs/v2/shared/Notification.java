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

package it.unibo.cs.v2.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class Notification implements IsSerializable {
	protected NotificationType type;
	protected String from;
	protected String message;
	protected String fileName;
	
	public abstract void setType(NotificationType type);
	public abstract void setMessage(String message);
	public abstract void setFrom(String from);
	
	public final NotificationType getType() {
		return this.type;
	}
	
	public final String getFrom() {
		return this.from;
	}
	
	public final String getMessage() {
		return this.message;
	}
	
	public final void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public final String getFileName() {
		return this.fileName;
	}
	
	public static final NotificationType parseNotificationType(String type) {
		if (type == null || type.equals(""))
			return NotificationType.UNKNOWN;
		
		if (type.equals(NotificationType.SHAREMACHINE.toString()))
			return NotificationType.SHAREMACHINE;
		else if (type.equals(NotificationType.ACCEPTEDSHARE.toString()))
			return NotificationType.ACCEPTEDSHARE;
		else if (type.equals(NotificationType.REFUSEDSHARE.toString()))
			return NotificationType.REFUSEDSHARE;
		else if (type.equals(NotificationType.EXPORTCOMPLETE.toString()))
			return NotificationType.EXPORTCOMPLETE;
		else if (type.equals(NotificationType.IMPORTCOMPLETE.toString()))
			return NotificationType.IMPORTCOMPLETE;
		else if (type.equals(NotificationType.TIMEDJOB.toString()))
			return NotificationType.TIMEDJOB;
		else
			return NotificationType.UNKNOWN;
	}
}
