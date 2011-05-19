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

public class MachineProcessInfo implements IsSerializable {
	private int pid;
	private int vncServer;
	private String machineName;
	private boolean owned;
	
	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getPid() {
		return pid;
	}

	public void setVncServer(int vncServer) {
		this.vncServer = vncServer;
	}

	public int getVncServer() {
		return vncServer;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setOwned(boolean owned) {
		this.owned = owned;
	}

	public boolean isOwned() {
		return owned;
	}
}
