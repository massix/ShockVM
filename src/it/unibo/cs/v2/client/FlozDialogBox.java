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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FlozDialogBox extends DialogBox {
	private VerticalPanel innerPanel = new VerticalPanel();
	private HorizontalPanel buttonsPanel = new HorizontalPanel();
	
	public FlozDialogBox(String caption) {
		super();
		setText(caption);
		innerPanel.add(buttonsPanel);
		setWidget(innerPanel);
		setAnimationEnabled(true);
		setGlassEnabled(true);
	}
	
	@Override
	public void add(Widget w) {
		if (w != null) {
			innerPanel.insert(w, innerPanel.getWidgetIndex(buttonsPanel));
		}
	}
	
	public void addButton(Button b) {
		buttonsPanel.add(b);
	}
}
