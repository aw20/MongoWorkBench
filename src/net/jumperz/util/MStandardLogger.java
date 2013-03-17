/* 
 *  MongoWorkBench is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  Free Software Foundation,version 3.
 *  
 *  MongoWorkBench is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  If not, see http://www.gnu.org/licenses/
 *  
 *  Additional permission under GNU GPL version 3 section 7
 *  
 *  If you modify this Program, or any covered work, by linking or combining 
 *  it with any of the JARS listed in the README.txt (or a modified version of 
 *  (that library), containing parts covered by the terms of that JAR, the 
 *  licensors of this Program grant you additional permission to convey the 
 *  resulting work. 
 */
package net.jumperz.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class MStandardLogger implements MLogger {
	private final static int DEFAULT_BUFSIZE = 1024;

	private static MStandardLogger instance = new MStandardLogger();

	private List streamList;

	private int bufsize = DEFAULT_BUFSIZE;

	public MStandardLogger() {
		streamList = new ArrayList();
	}

	public static final MStandardLogger getInstance() {
		return instance;
	}

	public final synchronized void addStream(OutputStream stream) {
		streamList.add(stream);
	}

	public void setBufsize(int i) {
		bufsize = i;
	}

	public final void log(String prefix, String message) {
		try {
			StringBuffer strBuf = new StringBuffer(bufsize);
			strBuf.append(new Date());
			strBuf.append(" ");
			strBuf.append(prefix);
			strBuf.append(": ");
			strBuf.append(message);
			strBuf.append("\n");

			int count = streamList.size();
			for (int i = 0; i < count; ++i) {
				OutputStream stream = (OutputStream) streamList.get(i);
				synchronized (stream) {
					stream.write(strBuf.toString().getBytes(MCharset.CS_ISO_8859_1));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void log(String message) {
		log("", message);
	}

}
