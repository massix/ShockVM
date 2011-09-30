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

import it.unibo.cs.v2.servlets.ExportMachine;
import it.unibo.cs.v2.servlets.ExportMachineAsync;
import it.unibo.cs.v2.servlets.GetMachines;
import it.unibo.cs.v2.servlets.GetMachinesAsync;
import it.unibo.cs.v2.shared.MachineInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;

public class ExportMachineWizard extends HTMLPanel implements Wizard, AsyncCallback<LinkedList<MachineInfo>> {
	private GetMachinesAsync getMachinesProxy = (GetMachinesAsync) GWT.create(GetMachines.class);
	private ListBox	machinesList = new ListBox();
	private LinkedList<MachineInfo> machines;
	
	private final DisclosurePanel machineDescription = new DisclosurePanel("Machine's description");
	private final TextArea machineTextArea = new TextArea();
	private final Button exportButton = new Button("Export this machine");
	
	private boolean finished = false;
	
	private final HTML errorString = new HTML("<span style=\"color: red\">Error while getting the machines. Select another page or refresh.</span>");

	private final ExportMachineAsync exportMachineProxy = GWT.create (ExportMachine.class);
	
	private ChangeHandler getMachineDescription = new ChangeHandler() {
		
		@Override
		public void onChange(ChangeEvent event) {
			machineDescription.clear();
			
			for (MachineInfo m : machines) {
				if (m.getName().equals(machinesList.getItemText(machinesList.getSelectedIndex()))) {
					machineDescription.add(new HTML(m.getDescription()));
					break;
				}
			}
		}
	};
	
	private ClickHandler exportHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			MachineInfo machine = null;
			
			for (MachineInfo m : machines) {
				if (m.getName().equals(machinesList.getValue(machinesList.getSelectedIndex()))) {
					machine = m;
					break;
				}
			}
			
			if (machine != null)
				exportMachineProxy.exportMachine(machine, machineTextArea.getText(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						clear();
						add(new HTML("<span style=\"color:red\">Error while exporting the machine: " + caught.getMessage() + "</span>"));
						finished = true;
					}

					@Override
					public void onSuccess(Boolean result) {
						clear();
						add(new HTML("<span style=\"color: green\">Machine exported. The process may take a while, you will receive a notification when it'll be completed.</span>"));
						finished = true;
						
					}
				});
		}
	};
	
	public ExportMachineWizard() {
		// Set up the page
		super("<h2>Export a machine</h2>");
		add(new HTML("<h3>Select a machine to export</h3>"));
		add(machinesList);
		add(machineDescription);
		add(new HTML("<br /><br /><h3>Insert a more accurate description of the machine</h3>"));
		add(new HTML("<p>Don't forget to include all those informations that are needed by the other users. Such as the root password of this machine.</p>"));
		add(machineTextArea);
		add(new HTML("<br /><br />"));
		add(exportButton);
		
		// Set up the handlers
		getMachinesProxy.getMachines(this);
		machinesList.addChangeHandler(getMachineDescription);
		exportButton.addClickHandler(exportHandler);
		
		// Sets the animation on the Disclosure Panel
		machineDescription.setAnimationEnabled(true);
		
		// Configure the Text Area
		machineTextArea.setWidth("480px");
		machineTextArea.setHeight("240px");
		
	}

	@Override
	public boolean hasFinished() {
		return finished;
	}

	@Override
	public void onFailure(Throwable caught) {
		clear();
		add(errorString);
		add(new HTML(caught.getMessage()));
		
		finished = true;
	}

	@Override
	public void onSuccess(LinkedList<MachineInfo> result) {
		if (machines != null)
			machines.clear();
		
		if (result != null) {
			machines = result;
			
			boolean descriptionSet = false;
			
			for (MachineInfo machine : result) {
				if (machine.isUserOwner()) {
					if (!descriptionSet) {
						machineDescription.clear();
						machineDescription.add(new HTML(machine.getDescription()));
						machineDescription.setOpen(true);
						descriptionSet = true;
					}
					machinesList.addItem(machine.getName());
				}
			}
		}
		
		else {
			clear();
			add(errorString);
			
			finished = true;
		}
	}

}
