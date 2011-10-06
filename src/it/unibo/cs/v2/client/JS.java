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

public class JS {
	public static native boolean restoreContainer() /*-{
		// Reset containers
		$wnd.$D('dummycontainer').appendChild($wnd.$D('error_div'));
		$wnd.$D('dummycontainer').appendChild($wnd.$D('canvasContainer'));
		
		$wnd.$D('error_div').style.visibility = 'hidden';
		
		$wnd.rfb.disconnect();
		
		return false;
	}-*/;
	
	public static native boolean showVNC(String host, String port) /*-{
		$wnd.rfb.connect(host, port, '', '');

		$wnd.$D('empty').appendChild($wnd.$D('error_div'));
		$wnd.$D('empty').appendChild($wnd.$D('canvasContainer'));
		
		$wnd.$D('error_div').style.visibility = 'visible';

		var controls;
		controls = $wnd.$D('controls');
		
		// Add controls
		var chtml = '<b>VNC Controls</b><br />'; 
		chtml += '<input type="button" onclick="ungrab_keyboard();" value="Release Keyboard" class="gwt-Button"/> ';
		chtml += '<input type="button" onclick="grab_keyboard();" value="Grab Keyboard" class="gwt-Button"/> ';
		chtml += '<input type="button" onclick="send_cad();" value="Send C-A-D" class="gwt-Button"/> ';
		chtml += '<input type="button" onclick="disconnect();" value="Disconnect" class="gwt-Button"/> ';
		chtml += '<input type="button" onclick="scale_view(0.3);" value="30%" class="gwt-Button"/> ';
		chtml += '<input type="button" onclick="scale_view(0.6);" value="60%" class="gwt-Button"/> ';
		chtml += '<input type="button" onclick="scale_view(0.9);" value="90%" class="gwt-Button"/> ';
		chtml += '<input type="button" onclick="scale_view(1);" value="100%" class="gwt-Button"/> ';
		
		controls.innerHTML = chtml;
		
		return false;
	}-*/;
}
