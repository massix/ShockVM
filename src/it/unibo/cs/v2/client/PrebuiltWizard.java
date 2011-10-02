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

package it.unibo.cs.v2.client;

import java.util.LinkedList;
import java.util.HashMap;

import it.unibo.cs.v2.servlets.GetPrebuiltMachines;
import it.unibo.cs.v2.servlets.GetPrebuiltMachinesAsync;
import it.unibo.cs.v2.shared.MachineInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;

public class PrebuiltWizard extends HTMLPanel implements Wizard {
	private GetPrebuiltMachinesAsync getPrebuiltMachinesProxy = GWT.create(GetPrebuiltMachines.class);
	
	public PrebuiltWizard() {
		super("<h2>Import an existing machine</h2>");
		add(new HTML("<p>Following is a list of machines exported by the other users. The list is ordered by the users' name.</p>"));
		
		getPrebuiltMachinesProxy.getPrebuiltMachines(new AsyncCallback<HashMap<String,LinkedList<MachineInfo>>>() {
			
			@Override
			public void onSuccess(HashMap<String, LinkedList<MachineInfo>> result) {
				if (result == null) {
					System.err.println("No machines found.");
					return;
				}
				
				for (String user : result.keySet()) {
					DisclosurePanel userPanel = new DisclosurePanel(user + " exported a total of " + result.get(user).size() + " machines");
					userPanel.setAnimationEnabled(true);
					HTMLPanel userHTMLPanel = new HTMLPanel("");
					userPanel.add(userHTMLPanel);
					
					for (final MachineInfo m : result.get(user)) {
						DisclosurePanel machinePanel = new DisclosurePanel(m.getName());
						Button importButton = new Button("Import this machine");
						machinePanel.setAnimationEnabled(true);
						HTMLPanel machineHTMLPanel = new HTMLPanel("");
						machinePanel.add(machineHTMLPanel);
						
						userHTMLPanel.add(machinePanel);
						machineHTMLPanel.add(new HTML(m.getDescription()));
						machineHTMLPanel.add(new HTML("\n" + m.getLongDescription()
								.replace("<", "&lt;")
								.replace(">", "&gt;")
								.replace("\n", "<br />")
								.replace("\\n", "<br />")
								));
						machineHTMLPanel.add(importButton);
						
						importButton.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								GWT.log("Requested to import machine: " + m.getConfigurationFile());
							}
						});
					}
					
					add(userPanel);
					add(new HTML("<br />"));
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
			
			}
		});
	}
	
	@Override
	public boolean hasFinished() {
		return false;
	}

}
