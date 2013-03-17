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
 *  $Id: ByteArrayInputStreamRaw.java 2605 2011-11-24 06:41:18Z alan $
 */
package org.aw20.io;

import java.io.InputStream;

public class ByteArrayInputStreamRaw extends InputStream {
	protected byte[] buf = null;
	protected int count = 0;

	/**
	 * Number of bytes that have been read from the buffer
	 */
	protected int pos = 0;

	public ByteArrayInputStreamRaw(byte[] buf, int len) {
		this.buf = buf;
		this.count = len;
	}

	public final int available() {
		return count - pos;
	}

	public final int read() {
		return (pos < count) ? (buf[pos++] & 0xff) : -1;
	}

	public final int read(byte[] b, int off, int len) {
		if (pos >= count)
			return -1;

		if ((pos + len) > count)
			len = (count - pos);

		System.arraycopy(buf, pos, b, off, len);
		pos += len;
		return len;
	}

	public final long skip(long n) {
		if ((pos + n) > count)
			n = count - pos;
		if (n < 0)
			return 0;
		pos += n;
		return n;
	}

}
