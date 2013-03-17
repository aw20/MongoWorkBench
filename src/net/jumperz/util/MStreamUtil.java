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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import net.jumperz.net.MHttpData;

public final class MStreamUtil {
	private static final int BUFSIZE = 1024;

	public static byte[] objectToByteArray(Object o) throws IOException {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(buf);
		out.writeObject(o);
		out.close();
		return buf.toByteArray();
	}

	public static Object byteArrayToObject(byte[] buf) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf));
		try {
			return in.readObject();
		} finally {
			MStreamUtil.closeStream(in);
		}
	}

	public static Object stringToObject(String s) throws IOException, ClassNotFoundException {
		if (s == null || s.equals("")) {
			throw new IOException("Input String not found.");
		}
		return byteArrayToObject(Base64.decode(s));
	}

	public static String objectToString(Object o) throws IOException {
		return Base64.encodeBytes(objectToByteArray(o), false);
	}

	public static void read(InputStream in, byte[] buffer) throws IOException {
		// ensure that the data is read to the end of the buffer
		int received = 0;
		while (received < buffer.length) {
			int r = in.read(buffer, received, buffer.length - received);
			if (r == -1) {
				throw new IOException("read returns -1.");
			} else {
				received += r;
			}
		}
	}

	public static BufferedReader getReader(InputStream in) throws IOException {
		return new BufferedReader(new InputStreamReader(in, MCharset.CS_ISO_8859_1));
	}

	public static void sendHttpDataToStream(MHttpData data, OutputStream out) throws IOException {
		out.write(data.getHeader());
		if (data.hasBody()) {
			MStreamUtil.connectStream(data.getBodyInputStream(), out);
		}
	}

	public static void saveStreamToFile(InputStream in, String fileName) throws IOException {
		FileOutputStream out = new FileOutputStream(fileName);
		try {
			connectStream(in, out);
		} finally {
			closeStream(out);
		}
	}

	public static boolean containsNull(InputStream in) {
		boolean contains = false;
		try {
			while (true) {
				byte[] buf = new byte[4096];
				int r = in.read(buf);
				if (r <= 0) {
					break;
				}
				for (int i = 0; i < r; ++i) {
					if (buf[i] == (byte) 0x00) {
						contains = true;
						break;
					}
				}
			}
		} catch (IOException ignored) {
		} finally {
			closeStream(in);
		}
		return contains;
	}

	public static BufferedReader getReader() throws IOException {
		return getReader(System.in);
	}

	public static void writeStrToStream(String s, OutputStream out) {
		try {
			out.write(s.getBytes(MCharset.CS_ISO_8859_1));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeByteToFile(byte[] buffer, String fileName) throws IOException {
		OutputStream out = new FileOutputStream(fileName);
		try {
			out.write(buffer);
		} finally {
			closeStream(out);
		}
	}

	public static int connectStream(InputStream in, OutputStream out, boolean closeOut) throws IOException {
		try {
			int totalSize = 0;
			int readSize;
			byte[] buffer = new byte[BUFSIZE];
			while (true) {
				readSize = in.read(buffer);
				if (readSize <= 0) {
					break;
				}
				totalSize += readSize;
				out.write(buffer, 0, readSize);
			}
			return totalSize;
		} finally {
			closeStream(in);
			if (closeOut) {
				closeStream(out);
			}
		}
	}

	public static int connectStream(InputStream in, OutputStream out) throws IOException {
		return connectStream(in, out, false);
	}

	public static byte[] streamToBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(BUFSIZE);
		MStreamUtil.connectStream(in, out);
		return out.toByteArray();
	}

	public static String streamToString(InputStream in) throws IOException {
		String str = null;
		try {
			str = new String(MStreamUtil.streamToBytes(in), MCharset.CS_ISO_8859_1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static void closeStream(InputStream in) {
		try {
			if (in != null) {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeStream(OutputStream out) {
		if (out == null) {
			return;
		}
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static InputStream stringToStream(String s) {
		return getInputStreamFromString(s);
	}

	public static InputStream getInputStreamFromString(String s) {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(s.getBytes(MCharset.CS_ISO_8859_1));
		} catch (UnsupportedEncodingException e) {
		}
		return in;
	}

	public static InputStream getResourceStream(String resource, ClassLoader classLoader) throws IOException {
		URL url = classLoader.getResource(resource);
		return url.openStream();
	}

	public static InputStream getResourceStream(String resource) throws IOException {
		return getResourceStream(resource, MStreamUtil.class.getClassLoader());
	}

}