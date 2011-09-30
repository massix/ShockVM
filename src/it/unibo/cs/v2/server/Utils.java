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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Utils {
	public static void copy(File src, File dst) throws Exception {
		dst.createNewFile();
		
		FileReader srcReader = new FileReader(src);
		FileWriter dstWriter = new FileWriter(dst);
		
		int c;
		while ((c = srcReader.read()) != -1) 
			dstWriter.write(c);
		
		srcReader.close();
		dstWriter.flush();
		dstWriter.close();
	}
	
	public static void customCopy(File src, File dst, int chunkSize) throws Exception {
		dst.createNewFile();
		
		FileReader srcReader = new FileReader(src);
		FileWriter dstWriter = new FileWriter(dst);
		
		char[] chunk = new char[chunkSize];
		
		int len = 0;
		while ((len = srcReader.read(chunk, 0, chunkSize)) != -1)
			dstWriter.write(chunk, 0, len);
		
		srcReader.close();
		dstWriter.flush();
		dstWriter.close();
	}
}
