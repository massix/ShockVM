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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTMLPanel;

public class MainPage extends HTMLPanel {
	private HashMap<String, String> userInfo;
	
	
	public MainPage(HashMap<String, String> userInfo) {
		super("<h2>FLOZ Configurator welcomes you!</h2>");
		this.userInfo = userInfo;
		getElement().getStyle().setPaddingLeft(3, Unit.EM);
	}

}
