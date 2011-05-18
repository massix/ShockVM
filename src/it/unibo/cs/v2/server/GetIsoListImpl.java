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

package it.unibo.cs.v2.server;

import it.unibo.cs.v2.servlets.GetIsoList;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GetIsoListImpl extends RemoteServiceServlet implements GetIsoList {

	@Override
	public LinkedList<HashMap<String, String>> getIsoList() throws Exception {
		File isoList = new File(getServletContext().getRealPath("/isos/imagesList.xml"));
		LinkedList<HashMap<String, String>> ret = new LinkedList<HashMap<String,String>>();
		
		if (isoList.exists() && isoList.canRead()) {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document images = db.parse(isoList);
			
			images.normalize();
			
			NodeList imagesNdl = images.getElementsByTagName("Image");
			for (int i = 0; i < imagesNdl.getLength(); i++) {
				Element image = (Element) imagesNdl.item(i);
				
				HashMap<String, String> imageHM = new HashMap<String, String>();
				
				Element name = (Element) image.getElementsByTagName("name").item(0);
				Element version = (Element) image.getElementsByTagName("version").item(0);
				Element description = (Element) image.getElementsByTagName("description").item(0);
				Element web = (Element) image.getElementsByTagName("web").item(0);
				Element iso = (Element) image.getElementsByTagName("iso").item(0);
				
				imageHM.put("name", name.getTextContent());
				imageHM.put("version", version.getTextContent());
				imageHM.put("description", description.getTextContent());
				imageHM.put("web", web.getTextContent());
				imageHM.put("iso", iso.getAttribute("path"));

				ret.add(imageHM);
			}
			
			return ret;
		}
		
		return null;
	}

}
