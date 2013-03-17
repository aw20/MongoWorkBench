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
 *  $Id: MD5.java 2605 2011-11-24 06:41:18Z alan $
 */
package org.aw20.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 extends Object {

	public static String getDigest( byte [] _b ) {
		return asHex( getDigestAsBytes( _b ) );
	}
	
	public static String getDigest(String _s) {
		return getDigest( _s.getBytes() );
	}
	
	public static byte[] getDigestAsBytes(String _s) {
		return getDigestAsBytes( _s.getBytes() );
	}

	public static byte[] getDigestAsBytes(byte [] _b) {
		try {
			return MessageDigest.getInstance("MD5").digest( _b );
		} catch (NoSuchAlgorithmException e) {
		}
		return new byte[0];
	}

	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', };

	public static String asHex(byte hash[]) {
		char buf[] = new char[hash.length * 2];
		for (int i = 0, x = 0; i < hash.length; i++) {
			buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
			buf[x++] = HEX_CHARS[hash[i] & 0xf];
		}
		return new String(buf);
	}

}
