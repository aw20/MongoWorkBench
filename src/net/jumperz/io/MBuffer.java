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
package net.jumperz.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import net.jumperz.util.MStreamUtil;

/*
 * - NOT multi-thread ready
 * - cleanup() must be called ( especially for large data )
 */
public final class MBuffer extends OutputStream {
	private static Set tmpFileSet = new HashSet();

	// private static Set streamSet = new HashSet();
	private static final int DEFAULT_MAX_MEM_SIZE = 1024 * 1024 * 10; // 10MByte

	private static final int BUFSIZE = 2048;

	private static int staticMaxMemSize = DEFAULT_MAX_MEM_SIZE;

	public static boolean debug = false;

	private int totalSize = 0;

	private OutputStream activeStream;

	private OutputStream fileStream;

	private ByteArrayOutputStream byteStream;

	private boolean isSmall;

	private File tmpFile;

	private File file;

	private int maxMemSize;

	private boolean closed = false;

	private boolean isNull = false;

	public static void setStaticMaxMemSize(int i) {
		staticMaxMemSize = i;
	}

	public MBuffer(int size) {
		maxMemSize = size;
		init();
	}

	public MBuffer() {
		maxMemSize = staticMaxMemSize;
		init();
	}

	public void write(int i) throws IOException {
		activeStream.write(i);
		totalSize++;
	}

	public boolean isClosed() {
		return closed;
	}

	public void close() {
		closed = true;
		MStreamUtil.closeStream(activeStream);
	}

	private void init() {
		/*
		 * if( bufStream != null ) { try { bufStream.close(); } catch( IOException e ) { e.printStackTrace(); } }
		 * 
		 * if( tmpFile != null ) { tmpFile.delete(); }
		 */

		tmpFile = null;
		isSmall = true;
		byteStream = new ByteArrayOutputStream(BUFSIZE);
		activeStream = byteStream;
	}

	public void setNull() {
		isSmall = false;
		isNull = true;
		activeStream = new MNullOutputStream();
	}

	public void write(byte[] buffer, int len) throws IOException {
		write(buffer, 0, len);
	}

	public int getSize() {
		return totalSize;
	}

	public void write(byte[] buffer, int offset, int len) throws IOException {
		if (isSmall && totalSize < maxMemSize && (totalSize + len) >= maxMemSize) {
			changeStreamToTmpFile();
		}

		activeStream.write(buffer, offset, len);
		totalSize += len;
	}

	public byte[] getBytes() throws IOException {
		return MStreamUtil.streamToBytes(getInputStream());
	}

	public void write(byte[] buffer) throws IOException {
		int len = buffer.length;
		write(buffer, len);
	}

	private void changeStreamToTmpFile() throws IOException {
		isSmall = false;

		// copy data
		tmpFile = File.createTempFile("mbuffer_", ".buf");
		file = tmpFile;
		synchronized (MBuffer.tmpFileSet) {
			MBuffer.tmpFileSet.add(tmpFile);
		}
		tmpFile.deleteOnExit();

		fileStream = new FileOutputStream(tmpFile);
		byteStream.writeTo(fileStream);
		byteStream = null;

		activeStream = fileStream;

		if (debug) {
			new Exception("create:" + tmpFile.getAbsolutePath()).printStackTrace();
		}
	}

	public InputStream getInputStream() {
		InputStream inputStream = null;
		if (isSmall) {
			byte[] buffer = byteStream.toByteArray();
			inputStream = new ByteArrayInputStream(buffer);
		} else if (isNull) {
			byte[] buffer = new byte[] {};
			inputStream = new ByteArrayInputStream(buffer);
		} else {
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return inputStream;
	}

	public void clear() {
		try {
			if (!closed) {
				close();
			}

			deleteTmpFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteTmpFile() {
		if (!isSmall) {
			if (debug) {
				new Exception("delete:" + tmpFile.getAbsolutePath()).printStackTrace();
			}

			MStreamUtil.closeStream(fileStream);
			if (tmpFile != null) {
				synchronized (tmpFileSet) {
					tmpFileSet.remove(tmpFile);
				}

				tmpFile.delete();
			}
		}
	}

	public void setFile(File f) throws IOException {
		file = f;
		isSmall = false;
		activeStream = new FileOutputStream(f);
	}
}