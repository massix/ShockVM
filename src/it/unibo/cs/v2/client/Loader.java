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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public abstract class Loader<T> implements AsyncCallback<T> {
	public Widget widget;
	private boolean startingStatus = true;
	
	public Loader(Widget widget) {
		if (widget != null) {
			this.widget = widget;
			this.widget.setVisible(startingStatus);
		}
	}
	
	public Loader(Widget widget, boolean inverted) {
		if (inverted)
			startingStatus = false;
		
		if (widget != null) {
			this.widget = widget;
			this.widget.setVisible(startingStatus);
		}
	}
	
	@Override
	public final void onSuccess(T result) {
		if (widget != null)
			widget.setVisible(!startingStatus);
		
		onCustomSuccess(result);
	}
	
	@Override
	public final void onFailure(Throwable caught) {
		if (widget != null)
			widget.setVisible(!startingStatus);
		
		onCustomFailure(caught);
	}
	
	public abstract void onCustomSuccess(T result);
	public abstract void onCustomFailure(Throwable caught);
}
