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

import java.util.HashMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FlozConfigurator extends DockLayoutPanel implements EntryPoint {
	public FlozConfigurator() {
		super(Unit.EM);
	}


	private final String COPYRIGHT = "<b>FLOZ Configurator is part of the VirtuaCluster, "
		+ "Copyright (C) 2011 - Massimo Gengarelli "
		+ "&lt;gengarel@cs.unibo.it&gt;</b>";

	private GetUserInfoAsync userInfoProxy = (GetUserInfoAsync) GWT.create(GetUserInfo.class);
	
	
	private HashMap<String, String> userInfo;

	/**
	 * It's nice to keep'em in memory for low-costs accesses
	 */
	private MainPage mainPage;
	private LeftPanel leftPanel;
	
	/**
	 * Points to the widget that is set in the center of the DockLayoutPanel
	 */
	private Panel centerWidget;

	
	/**
	 * This is called by the GWT EntryPoint interface
	 */
	@Override
	public void onModuleLoad() {
//		mainPanel = new DockLayoutPanel(Unit.EM);
		getElement().setAttribute("id", "mainPanel");

		addNorth(new HTML("<div id=\"Header\"> "
				+ "<h1>FLOZ Configurator</h1>" + "</div>"), 6);
		addSouth(new HTML("<div id=\"footer\">" + COPYRIGHT
				+ "</div>"), 4);

		RootLayoutPanel.get().add(this);

		showLoginTable();
	}

	/**
	 * Shows a nice Login form
	 */
	private void showLoginTable() {
		final LoginPage login = new LoginPage();
		centerWidget = (Panel) login;
		centerWidget.getElement().setAttribute("id", "loginPage");
		final Image loadingImage = new Image("/loading.gif");
		
		login.add(loadingImage);


		login.setAsyncCallback(new Loader<Boolean>(loadingImage) {
			@Override
			public void onCustomSuccess(Boolean result) {
				if (result == true) {
					remove(login);

					Cookies.setCookie("login", login.getProvidedLogin());

					userInfoProxy.getUserInfo(login.getProvidedLogin(), new Loader<HashMap<String,String>>(loadingImage) {
						@Override
						public void onCustomFailure(Throwable caught) {
							Window.alert("RPC Failure while getting user infos, refresh the page");
						}

						@Override
						public void onCustomSuccess(HashMap<String, String> result) {
							userInfo = result;
							showMainPage();
						}
					});

				}

				else
					login.showError("Login failed");
			}

			@Override
			public void onCustomFailure(Throwable caught) {
				Window.alert("RPC Failed, try reloading the page");
			}
		});



		loadingImage.setVisible(false);
		add(centerWidget);
	}
	
	
	/**
	 * Main Page (if user is logged in)
	 */
	private void showMainPage() {
		mainPage = new MainPage(userInfo);
		leftPanel = new LeftPanel(userInfo);
		addWest(leftPanel, 15);
		add(mainPage);

		leftPanel.setVisible(true);
		mainPage.setVisible(true);
		
		
		animate(1500);
	}
}
