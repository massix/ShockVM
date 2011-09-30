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

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MachineInfo implements IsSerializable {
	private String configurationFile;
	private String iso;
	private String name;
	private String description;
	private String hda;
	private String hdaSize;
	private boolean hdbEnabled;
	private String hdb;
	private String hdbSize;
	private boolean virtuacluster;
	private boolean secondNetwork;
	private String macAddress;
	private String socketPath;
	private boolean bootCdrom;
	private String realOwner;
	private boolean userOwner;
	private boolean tabletDevice;
	private LinkedList<String> sharedWith = new LinkedList<String>();
	private LinkedList<String> pendingShares = new LinkedList<String>();
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setHda(String hda) {
		this.hda = hda;
	}
	
	public String getHda() {
		return hda;
	}
	
	public void setHdaSize(String hdaSize) {
		this.hdaSize = hdaSize;
	}

	public String getHdaSize() {
		return hdaSize;
	}

	public void setHdb(String hdb) {
		this.hdb = hdb;
	}
	
	public String getHdb() {
		return hdb;
	}
	
	public void setHdbSize(String hdbSize) {
		this.hdbSize = hdbSize;
	}

	public String getHdbSize() {
		return hdbSize;
	}

	public void setVirtuacluster(boolean virtuacluster) {
		this.virtuacluster = virtuacluster;
	}
	
	public boolean isVirtuacluster() {
		return virtuacluster;
	}
	
	public void setSecondNetwork(boolean secondNetwork) {
		this.secondNetwork = secondNetwork;
	}
	
	public boolean isSecondNetwork() {
		return secondNetwork;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public String getMacAddress() {
		return macAddress;
	}
	
	public void setSocketPath(String socketPath) {
		this.socketPath = socketPath;
	}
	
	public String getSocketPath() {
		return socketPath;
	}

	public void setIso(String iso) {
		this.iso = iso;
	}

	public String getIso() {
		return iso;
	}

	public void setHdbEnabled(boolean hdbEnabled) {
		this.hdbEnabled = hdbEnabled;
	}

	public boolean isHdbEnabled() {
		return hdbEnabled;
	}

	public void setBootCdrom(boolean bootCdrom) {
		this.bootCdrom = bootCdrom;
	}

	public boolean isBootCdrom() {
		return bootCdrom;
	}
	
	public void addShare(String user) {
		if (!sharedWith.contains(user))
			sharedWith.add(user);
	}
	
	public void removeShare(String user) {
		if (sharedWith.contains(user))
			sharedWith.remove(user);
	}
	
	public String[] getShares() {
		String[] ret = new String[sharedWith.size()];
		for (int i = 0; i < sharedWith.size(); i++) 
			ret[i] = sharedWith.get(i);
		
		return ret;
	}
	
	public void addPendingShare(String user) {
		if (!pendingShares.contains(user))
			pendingShares.add(user);
	}
	
	public void removePendingShare(String user) {
		if (pendingShares.contains(user))
			pendingShares.remove(user);
	}
	
	public String[] getPendingShares() {
		String[] ret = new String[pendingShares.size()];
		for (int i = 0; i < pendingShares.size(); i++)
			ret[i] = pendingShares.get(i);
		
		return ret;
	}
	
	public int getTotalShare() {
		return sharedWith.size() + pendingShares.size();
	}

	public void setRealOwner(String realOwner) {
		this.realOwner = realOwner;
	}

	public String getRealOwner() {
		return realOwner;
	}

	public void setConfigurationFile(String configurationFile) {
		this.configurationFile = configurationFile;
	}

	public String getConfigurationFile() {
		return configurationFile;
	}

	public void setTabletDevice(boolean tabletDevice) {
		this.tabletDevice = tabletDevice;
	}

	public boolean isTabletDevice() {
		return tabletDevice;
	}

	public boolean isUserOwner() {
		return userOwner;
	}

	public void setUserOwner(boolean userOwner) {
		this.userOwner = userOwner;
	}
}
