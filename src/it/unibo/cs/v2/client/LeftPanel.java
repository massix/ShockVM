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

import it.unibo.cs.v2.servlets.DeleteMachine;
import it.unibo.cs.v2.servlets.DeleteMachineAsync;
import it.unibo.cs.v2.servlets.GetMachines;
import it.unibo.cs.v2.servlets.GetMachinesAsync;
import it.unibo.cs.v2.servlets.StartMachine;
import it.unibo.cs.v2.servlets.StartMachineAsync;
import it.unibo.cs.v2.shared.MachineInfo;
import it.unibo.cs.v2.shared.MachineProcessInfo;

import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class LeftPanel extends StackLayoutPanel {
	private final int HEADERSIZE = 2;
	
	private final String WELCOME_STRING = "Welcome to <b>Live VirtuaCluster</b>. <br/>" +
			"Using this simple web app you can easily create a personal virtual machine to use whenever you want, " +
			"you can also share it with your friends and see the progresses they're making with your machines. Creating a " +
			"new machine is as simple as clicking some buttons and following some instructions. If you should encounter a " +
			"problem, please contact <a href=\"mailto:gengarel@cs.unibo.it\">the webmaster</a>. <b>Enjoy!</b>";
	
	private final HTMLPanel firstPanel = new HTMLPanel(WELCOME_STRING);
	private final HTMLPanel machines = new HTMLPanel("");
	private final Widget serverNews = new HTML("Fixed");

	private final GetMachinesAsync getMachinesProxy = (GetMachinesAsync) GWT.create(GetMachines.class);
	private final DeleteMachineAsync deleteMachineProxy = (DeleteMachineAsync) GWT.create(DeleteMachine.class);
	private final StartMachineAsync startMachineProxy = (StartMachineAsync) GWT.create(StartMachine.class);
	
	public LeftPanel(HashMap<String, String> userInfo) {
		super(Unit.EM);

		buildMachinesList();
		
		firstPanel.add(new HTML("<h2>Navigation Menu</h2>"));
		firstPanel.add(new Hyperlink("HomePage", HistoryTokens.START));
		
		firstPanel.add(new HTML("<h2>New machine</h2>"));
		firstPanel.add(new Hyperlink("Create from scratch", HistoryTokens.NEWMACHINE));
		firstPanel.add(new Hyperlink("Create from an existing one", HistoryTokens.PREBUILT));
		
		firstPanel.add(new HTML("<h2>Credits</h2>"));
		firstPanel.add(new Anchor("VirtualSquare", "http://wiki.virtualsquare.org", "_blank")); 
		firstPanel.add(new HTML("&nbsp;&nbsp;Home of VDE"));
		
		firstPanel.add(new Anchor("KVM", "http://www.linux-kvm.org", "_blank")); 
		firstPanel.add(new HTML("&nbsp;&nbsp;Kernel Virtual Machine"));
		
		firstPanel.add(new Anchor("jBCrypt", "http://www.mindrot.org/projects/jBCrypt/", "_blank")); 
		firstPanel.add(new HTML("&nbsp;&nbsp;internally used for hashing passwords"));
		
		firstPanel.add(new Anchor("Google Web Toolkit", "http://code.google.com/webtoolkit/", "_blank")); 
		firstPanel.add(new HTML("&nbsp;&nbsp;the framework used for this webapp"));
		
		add(new ScrollPanel(firstPanel), userInfo.get("displayname"), HEADERSIZE);
		add(new ScrollPanel(machines), "Your Machines", HEADERSIZE);
		add(new ScrollPanel(serverNews), "Latest Infos", HEADERSIZE);
	}
	
	private final void buildMachinesList() {
		machines.clear();
		final Button refresh = new Button("Refresh");
		refresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				buildMachinesList();
			}
		});
		
		machines.add(refresh);

		getMachinesProxy.getMachines(new AsyncCallback<LinkedList<MachineInfo>>() {
			@Override
			public void onSuccess(LinkedList<MachineInfo> result) {
				if (result != null) {
					for (final MachineInfo machineInfo : result) {
						final DisclosurePanel disclosure = new DisclosurePanel(machineInfo.getName());
						final HTMLPanel machinePanel = new HTMLPanel(machineInfo.getDescription() + "<br />");
						
						machinePanel.add(new HTML("<br /><b>Basic settings</b>"));
						machinePanel.add(new HTML("Base system: " + machineInfo.getIso() + "<br />"));
						
						machinePanel.add(new HTML("<br /><b>Storage</b><br />"));
						machinePanel.add(new HTML("Hard Disk: " + machineInfo.getHda() + " (" + machineInfo.getHdaSize() + ")<br />"));
						
						if (machineInfo.isHdbEnabled()) 
							machinePanel.add(new HTML("Hard Disk: " + machineInfo.getHdb() + " (" + machineInfo.getHdbSize() + ")<br />"));
						
						machinePanel.add(new HTML("<br /><b>Networking</b><br />"));
						if (machineInfo.isVirtuacluster())
							machinePanel.add(new HTML("VirtuaCluster support <i>enabled</i><br />"));
						else
							machinePanel.add(new HTML("VirtuaCluster support <i>disabled</i><br />"));
						
						if (machineInfo.isSecondNetwork()) {
							String mac = machineInfo.getMacAddress().equals("")? "default" : machineInfo.getMacAddress();
							machinePanel.add(new HTML("Network interface: " + machineInfo.getSocketPath() + " (" + mac + ")"));
						}
						
						HorizontalPanel buttonsPanel = new HorizontalPanel();
						final ListBox bootFrom = new ListBox();

						machinePanel.add(new HTML("<br /><b>Boot from</b><br />"));

						bootFrom.addItem("Hard Disk", "c");
						bootFrom.addItem("CD-Rom", "d");
						machinePanel.add(bootFrom);
						machinePanel.add(buttonsPanel);
						
						
						final Button startButton = new Button("Start/View machine");
						final Button deleteButton = new Button("Delete machine");
						
						// TODO: start machine
						startButton.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								startButton.setEnabled(false);
								machineInfo.setBootCdrom(bootFrom.getValue(bootFrom.getSelectedIndex()).equals("d"));
								final Image loadingGif = new Image("loading.gif");
								machinePanel.add(loadingGif);
								
								startMachineProxy.startViewMachine(machineInfo, new AsyncCallback<MachineProcessInfo>() {

									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught.getMessage());
										machinePanel.remove(loadingGif);
										startButton.setEnabled(true);
									}

									@Override
									public void onSuccess(MachineProcessInfo result) {
										MainPage.getInstance().showApplet(result);
										startButton.setEnabled(true);
										machinePanel.remove(loadingGif);
									}
								});
							}
						});
						
						// TODO: delete machine
						deleteButton.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								final FlozDialogBox confirmDelete = new FlozDialogBox("Delete " + machineInfo.getName() + "?");
								final Button okButton = new Button("Yes, do it!");
								final Button cancelButton = new Button("No please! Don't!");
								confirmDelete.add(new HTML("<h3>Confirmation box</h3>Are you sure you want to delete " + machineInfo.getName() + "?"));
								
								okButton.addClickHandler(new ClickHandler() {
									
									@Override
									public void onClick(ClickEvent event) {
										deleteMachineProxy.deleteMachine(machineInfo.getName(), new AsyncCallback<Boolean>() {

											@Override
											public void onFailure(Throwable caught) {
												Window.alert(caught.getMessage());
												buildMachinesList();
												confirmDelete.hide();
											}

											@Override
											public void onSuccess(Boolean result) {
												buildMachinesList();
												confirmDelete.hide();
											}
										});
										
									}
								});
								
								cancelButton.addClickHandler(new ClickHandler() {
									
									@Override
									public void onClick(ClickEvent event) {
										confirmDelete.hide();
									}
								});
								
								confirmDelete.addButton(okButton);
								confirmDelete.addButton(cancelButton);
								
								confirmDelete.showRelativeTo(machinePanel);
							}
						});
						
						buttonsPanel.add(startButton);
						buttonsPanel.add(deleteButton);
						
						machinePanel.add(buttonsPanel);
						
						disclosure.setContent(machinePanel);
						disclosure.setAnimationEnabled(true);
						
						disclosure.addStyleName("bottomlimited");
						disclosure.getElement().getStyle().setMarginBottom(2, Unit.EM);
						
						machines.add(disclosure);
						
						machines.getElement().getStyle().setHeight(100, Unit.PCT);
						machines.getElement().getStyle().setWidth(100, Unit.PCT);

					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				machines.add(new HTML("General error"));
			}
		});
	}

}
