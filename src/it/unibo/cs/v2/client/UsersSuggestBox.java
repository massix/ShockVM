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

import it.unibo.cs.v2.servlets.GetUsersList;
import it.unibo.cs.v2.servlets.GetUsersListAsync;

import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;

public class UsersSuggestBox extends SuggestBox implements ValueChangeHandler<String> {
	private final MultiWordSuggestOracle oracle;
	private LinkedList<String> usersList;
	private final Timer updateListTimer = new Timer() {

		@Override
		public void run() {
			doUpdate();
		}
	};
	
	private final GetUsersListAsync getUsersListProxy = (GetUsersListAsync) GWT.create(GetUsersList.class);
	
	
	public UsersSuggestBox() {
		oracle = (MultiWordSuggestOracle) getSuggestOracle();
		addValueChangeHandler(this);
		
		// Update first when launched
		doUpdate();
		
		// Update every ten seconds
		updateListTimer.scheduleRepeating(10000);
	}

	private void doUpdate() {
		getUsersListProxy.getUsersList(new AsyncCallback<LinkedList<String>>() {
			
			@Override
			public void onSuccess(LinkedList<String> result) {
				usersList = result;
				oracle.addAll(usersList);
			}			
			
			@Override
			public void onFailure(Throwable caught) {
				usersList = new LinkedList<String>();
				
			}
		});
		
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if (!usersList.contains(event))
			setText("");
	}
}
