/* 
 *  Copyright (C) 2011 AW2.0 Ltd
 *
 *  org.aw20 is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  Free Software Foundation,version 3.
 *  
 *  OpenBD is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with org.aw20.  If not, see http://www.gnu.org/licenses/
 *  
 *  Additional permission under GNU GPL version 3 section 7
 *  
 *  If you modify this Program, or any covered work, by linking or combining 
 *  it with any of the JARS listed in the README.txt (or a modified version of 
 *  (that library), containing parts covered by the terms of that JAR, the 
 *  licensors of this Program grant you additional permission to convey the 
 *  resulting work. 
 *  
 *  $Id: ByteArrayOutputStreamRaw.java 2605 2011-11-24 06:41:18Z alan $
 *
 * Slightly modified version of ByteArraOutputStream (1.6), tuned:
 * 
 *   - No synchronized methods
 *   - Does not copy the bytes internally
 *   - String write method
 *
 */
package org.aw20.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ByteArrayOutputStreamRaw extends OutputStream {

	protected byte buf[];

	protected int count;

	public ByteArrayOutputStreamRaw() {
		this(32);
	}

	public ByteArrayOutputStreamRaw(int size) {
		buf = new byte[size];
	}

	public void write(String s) {
		byte[] b = s.getBytes();
		int len = b.length;
		if (len == 0) {
			return;
		}

		int newcount = count + len;
		if (newcount > buf.length) {
			byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
			System.arraycopy(buf, 0, newbuf, 0, count);
			buf = newbuf;
		}
		System.arraycopy(b, 0, buf, count, len);
		count = newcount;
	}
	
	public void write(byte[] b) {
		int len = b.length;
		if (len == 0) {
			return;
		}

		int newcount = count + len;
		if (newcount > buf.length) {
			byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
			System.arraycopy(buf, 0, newbuf, 0, count);
			buf = newbuf;
		}
		System.arraycopy(b, 0, buf, count, len);
		count = newcount;
	}
	
	public void write(int b) {
		int newcount = count + 1;
		if (newcount > buf.length) {
			byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
			System.arraycopy(buf, 0, newbuf, 0, count);
			buf = newbuf;
		}
		buf[count] = (byte) b;
		count = newcount;
	}

	public void write(byte b[], int off, int len) {
		if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length)
				|| ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		int newcount = count + len;
		if (newcount > buf.length) {
			byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
			System.arraycopy(buf, 0, newbuf, 0, count);
			buf = newbuf;
		}
		System.arraycopy(b, off, buf, count, len);
		count = newcount;
	}

	public synchronized void writeTo(OutputStream out) throws IOException {
		out.write(buf, 0, count);
	}

	public void reset() {
		count = 0;
	}

	public byte toByteArray()[] {
		byte newbuf[] = new byte[count];
		System.arraycopy(buf, 0, newbuf, 0, count);
		return newbuf;
	}

	public byte[] getByteArray(){
		return buf;
	}
	
	public int size() {
		return count;
	}

	public String toString() {
		return new String(buf, 0, count);
	}

	public String toString(String enc) throws UnsupportedEncodingException {
		return new String(buf, 0, count, enc);
	}

	@SuppressWarnings("deprecation")
	public String toString(int hibyte) {
		return new String(buf, hibyte, 0, count);
	}

	public void close() throws IOException {}

}
