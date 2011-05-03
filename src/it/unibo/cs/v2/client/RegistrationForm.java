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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;


public class RegistrationForm extends HTMLPanel implements ClickHandler, KeyUpHandler {
	private final TextBox username;
	private final TextBox displayname;
	private final Button submitButton;
	private final PasswordTextBox password;
	private final FlexTable mainTable;
	
	private final IsUsernameValidAsync validProxy = (IsUsernameValidAsync) GWT.create(IsUsernameValid.class);
	private final RegisterUserAsync registerProxy = (RegisterUserAsync) GWT.create(RegisterUser.class);
	
	private final HTML resultLabel;
	
	public RegistrationForm() {
		super("");
		username = new TextBox();
		displayname = new TextBox();
		password = new PasswordTextBox();
		submitButton = new Button("Submit");
		resultLabel = new HTML();
		mainTable = new FlexTable();
		resultLabel.setVisible(false);
		
		mainTable.setWidget(0, 0, new HTML("Your (real) name"));
		mainTable.setWidget(0, 1, displayname);
		
		mainTable.setWidget(1, 0, new HTML("Desired username"));
		mainTable.setWidget(1, 1, username);
		
		mainTable.setWidget(2, 0, new HTML("Password"));
		mainTable.setWidget(2, 1, password);
		
		mainTable.setWidget(3, 0, submitButton);
		
		add(mainTable);
		add(resultLabel);
		
		displayname.addKeyUpHandler(this);
		password.addKeyDownHandler(new SimpleHandler());
		submitButton.addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		submit();
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			submit();
		}
		else {
			if (username.getText().length() < 8)
				username.setText(displayname.getText().toLowerCase().replace(' ', '_'));
		}
	}
	
	private void submit() {
		if (password.getText().length() < 8 || username.getText().length() < 4) {
			showLabel("Password or username too short, at least 8 characters must be used for password and 4 for username.", "red");
			return;
		}
		
		validProxy.isUserNameValid(username.getText().toLowerCase(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if (!result) 
					showLabel("Username " + username.getText() + " already taken", "red");
				
				else {
					hideLabel();
					registerProxy.registerUser(username.getText(), password.getText(), displayname.getText(), new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							showLabel(caught.getMessage(), "red");
						}

						@Override
						public void onSuccess(Boolean result) {
							showLabel(result.toString(), "green");
						}
					});
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}

	private void showLabel(String message, String color) {
		resultLabel.setVisible(true);
		resultLabel.getElement().getStyle().setColor(color);
		resultLabel.setHTML(message);
	}
	
	public void hideLabel() {
		resultLabel.setVisible(false);
	}
	
	class SimpleHandler implements KeyDownHandler {
		@Override
		public void onKeyDown(KeyDownEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				submit();
		}
	}
}
