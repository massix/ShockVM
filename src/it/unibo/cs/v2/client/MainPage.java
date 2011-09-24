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

import it.unibo.cs.v2.servlets.ShutdownMachine;
import it.unibo.cs.v2.servlets.ShutdownMachineAsync;
import it.unibo.cs.v2.shared.MachineProcessInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;

public class MainPage extends HTMLPanel implements ValueChangeHandler<String> {
	private static MainPage instance;
	private final String WELCOME = "<h2>Welcome to Live ShockVM!</h2>" +
			"This is the main interface of <b>Live ShockVM</b>, use the left panel to create a new machine or to start up" +
			" the machines you've already created. The left panel is divided into three menus: the one with your name is the main menu, where you can create " +
			"a new machine; the second one is a short brief of the machines you already own while the latter one displays the MOTD of Live ShockVM.";
	
	private final HTML label = new HTML(WELCOME);
	private HTML vncApplet = new HTML();
	private NewMachineWizard wizard = new NewMachineWizard();
	
	private final ShutdownMachineAsync shutdownMachineProxy = (ShutdownMachineAsync) GWT.create(ShutdownMachine.class);
	
	protected MainPage() {
		super("");
		getElement().getStyle().setPaddingLeft(3, Unit.EM);
		
		add(label);
	}
	
	public static MainPage getInstance() {
		if (instance == null)
			instance = new MainPage();
		
		return instance;
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		clear();
		String historyToken = event.getValue();
		if (historyToken.equals(HistoryTokens.START)) {
			label.setHTML(WELCOME);
			add(label);	
		}
	
		else if (historyToken.equals(HistoryTokens.NEWMACHINE)) {
			if (wizard.hasFinished()) 
				wizard = new NewMachineWizard();
			
			wizard.setVisible(true);
			add(wizard);
		}
		
		else if (historyToken.equals(HistoryTokens.PREBUILT)) {
			add(new HTML("New machine from prebuilt huh?"));
		}
		
		History.newItem("", false);
	}

	public void showApplet(final MachineProcessInfo mpi, final boolean newWindow) {
		clear();
		
		add(vncApplet);

		vncApplet.setHTML("<h2>Viewing " + mpi.getMachineName() + " (" + mpi.getPid() + ")</h2>" +
				"<b>To shutdown this machine, freeing the VNC server, " +
				"please use the \"Shutdown this machine\" button positioned on the bottom of the page</b><br />" +
				"<applet code=\"VncViewer.class\" archive=\"VncViewer.jar\" width=\"1024\" height=\"768\">" +
				"<param name=\"port\" value=\"" + (5900+mpi.getVncServer()) + "\"/>" +
				"<param name=\"open new window\" value=\"" + (newWindow? "yes" : "no") + "\"/>" +
				"<param name=\"view only\" value=\"" + (mpi.isOwned()? "no" : "yes") + "\"/>" +
				"</applet>");
		final Button shutdown = new Button("Shutdown this machine");
		add(shutdown);
		
		shutdown.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				shutdownMachineProxy.shutdownMachine(mpi, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						vncApplet.setHTML("<h2>Thank you</h2><span style=\"color: green\">" +
								"Machine successfully shut down.</span>");
						remove(shutdown);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
			}
		});
	}
}
