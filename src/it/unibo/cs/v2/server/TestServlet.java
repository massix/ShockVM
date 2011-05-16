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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			String firstParam = request.getParameter("command");

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("Ciao, io sono una servlet di prova :) <br />" + firstParam == null? "NOOO" : firstParam);
			
			ProcessBuilder t = new ProcessBuilder("dir.exe");
			Process exec = t.start();

			InputStream in = exec.getInputStream();
			
			String path = getServletContext().getContextPath();
			String sInfo = getServletContext().getServerInfo();
			
			int c;
			out.println("Server is: " + sInfo);
			out.println("<pre><code>" + path + "$ ls");
			while ((c = in.read()) != -1) 
					out.print((char) c);
			
			out.println("</code></pre>");
			
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html");
		try {
			PrintWriter out = response.getWriter();
			String param = request.getParameter("testparam");
			out.println("No way, boy. " + param);
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
