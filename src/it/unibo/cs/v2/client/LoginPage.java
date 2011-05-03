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
 * along with Foobar. If not, see http://www.gnu.org/licenses/.
 */

package it.unibo.cs.v2.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class LoginPage extends HTMLPanel {
	private TextBox userName;
	private PasswordTextBox password;
	private Button submitButton;

	private LoginUserAsync userProxy = (LoginUserAsync) GWT.create(LoginUser.class);	
	
	private HTML errorLabel;
	
	private DisclosurePanel disclosure;
	
	public LoginPage() {
		super("");

		FlexTable mainTable = new FlexTable();
		errorLabel = new HTML();
		errorLabel.setVisible(false);
		errorLabel.getElement().getStyle().setColor("red");
		
		userName = new TextBox();
		password = new PasswordTextBox();
		
		disclosure = new DisclosurePanel("Register a new user");
		disclosure.setContent(new RegistrationForm());
		
		if (Cookies.getCookie("login") != null) {
			userName.setText(Cookies.getCookie("login"));
			password.setFocus(true);
		}
		else
			userName.setFocus(true);
		
		submitButton = new Button("Submit");

		mainTable.clear();

		/* First row */
		mainTable.setWidget(0, 0, new HTML("<b>Username</b>"));
		mainTable.setWidget(0, 1, userName);

		mainTable.setWidget(1, 0, new HTML("<b>Password</b>"));
		mainTable.setWidget(1, 1, password);

		mainTable.setWidget(2, 0, submitButton);
		mainTable.getElement().setAttribute("align", "center");

		add(mainTable);
		add(errorLabel);
		add(disclosure);
		
		disclosure.getElement().setAttribute("align", "center");
		disclosure.setAnimationEnabled(true);
		
		disclosure.addCloseHandler(new CloseHandler<DisclosurePanel>() {
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				((RegistrationForm) disclosure.getContent()).hideLabel();
			}
		});
	}

	public void setAsyncCallback(final AsyncCallback<Boolean> callback) {
		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				userProxy.loginUser(userName.getText(), password.getText(), callback);
			}
		});
	}
	
	public void showError(String error)	{
		errorLabel.setHTML(error);
		errorLabel.setVisible(true);
	}
	
	public void hideError() {
		errorLabel.setVisible(false);
	}

	public String getProvidedLogin() {
		return userName.getText();
	}

	public String getProvidedPassword() {
		return password.getText();
	}
}
