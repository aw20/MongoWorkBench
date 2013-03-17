/* 
 *  Copyright (C) 2011,2012 AW2.0 Ltd
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
 *  $Id: StringUtil.java 3517 2012-12-19 16:33:25Z andy $
 */
package org.aw20.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class StringUtil extends Object {

	/**
	 * Takes a string filled with tokens and replaces them with their corresponding value
	 * 
	 * @param _template
	 * @param _keys
	 * @param _values
	 * @return
	 */
	public static String tokenReplace(String _template, String[] _tokens, String[] _values) {
		if ((_values != null && _tokens.length != _values.length) || _template == null)
			return _template;

		int c1 = -1, jump = 0;
		for (int x = 0; x < _tokens.length; x++) {

			c1 = _template.indexOf(_tokens[x]);
			while (c1 != -1) {
				if (_values != null && _values[x] != null) {
					_template = _template.substring(0, c1) + _values[x] + _template.substring(c1 + _tokens[x].length());
					jump = _values[x].length();
				} else {
					_template = _template.substring(0, c1) + _template.substring(c1 + _tokens[x].length());
					jump = 0;
				}

				c1 = _template.indexOf(_tokens[x], c1 + jump);
			}
		}

		return _template;
	}
	
	public static double toDouble(Object _value, double _default) {
		if (_value == null)
			return _default;
		else if (_value instanceof Integer)
			return ((Integer) _value).doubleValue();
		else if (_value instanceof Long)
			return ((Long) _value).doubleValue();
		else if (_value instanceof Double)
			return (Double) _value;
		else if (_value instanceof Float)
			return ((Float) _value).doubleValue();
		else
			return toDouble(_value.toString(), _default);
	}

	public static double toDouble(String _value, double _default) {
		if (_value == null || _value.length() == 0)
			return _default;
		else {
			try {
				return Double.parseDouble(_value);
			} catch (Exception e) {
				return _default;
			}
		}
	}


	/**
	 * Converts a string to an integer, and if unable to do so, returns the default value
	 * 
	 * @param _value
	 * @param _default
	 * @return
	 */
	public static int toInteger(Object _value, int _default) {
		if (_value == null)
			return _default;
		else if (_value instanceof Integer)
			return (Integer) _value;
		else if (_value instanceof Long)
			return ((Long) _value).intValue();
		else if (_value instanceof Double)
			return ((Double) _value).intValue();
		else if (_value instanceof Float)
			return ((Float) _value).intValue();
		else
			return toInteger(_value.toString(), _default, 10);
	}

	public static int toInteger(String _value, int _default) {
		return toInteger(_value, _default, 10);
	}

	public static int toInteger(String _value, int _default, int _radix) {
		if (_value == null || _value.length() == 0)
			return _default;
		else {
			try {
				return Integer.parseInt(_value, _radix);
			} catch (Exception e) {
				return _default;
			}
		}
	}

	public static boolean toBoolean(Object _value, boolean _default) {
		if ( _value == null )
			return _default;
		else if ( _value instanceof Boolean )
			return (Boolean)_value;
		else
			return toBoolean( _value.toString(), _default );
	}
	
	public static boolean toBoolean(String _value, boolean _default) {
		if (_value == null || _value.length() == 0)
			return _default;

		if (_value.equalsIgnoreCase("true"))
			return true;
		else if (_value.equalsIgnoreCase("false"))
			return false;
		else if (_value.equalsIgnoreCase("yes"))
			return true;
		else if (_value.equalsIgnoreCase("no"))
			return false;
		else if (StringUtil.toInteger(_value, 0) >= 1)
			return true;
		else if (StringUtil.toInteger(_value, 0) == 0)
			return false;

		return _default;
	}

	/**
	 * Converts a string to an integer, and if unable to do so, returns the default value
	 * 
	 * @param _value
	 * @param _default
	 * @return
	 */
	public static long toLong(Object _value, long _default) {
		if (_value == null)
			return _default;
		else if (_value instanceof Integer)
			return ((Integer) _value).longValue();
		else if (_value instanceof Long)
			return ((Long) _value);
		else if (_value instanceof Double)
			return ((Double) _value).longValue();
		else if (_value instanceof Float)
			return ((Float) _value).longValue();
		else
			return toLong(_value.toString(), _default, 10);
	}

	public static long toLong(String _value, long _default) {
		return toLong(_value, _default, 10);
	}

	public static long toLong(String _value, long _default, int _radix) {
		if (_value == null || _value.length() == 0)
			return _default;
		else {
			try {
				return Long.parseLong(_value, _radix);
			} catch (Exception e) {
				return _default;
			}
		}
	}

	/**
	 * Tries to cast Object to String. If object is null, it will return null.
	 * 
	 * @param _o
	 * @return String value of _o
	 */
	public static String toString(Object _o) {
		return toString(_o, null);
	}

	/**
	 * Tries to cast Object to String. If object is null, it will return the default value supplied..
	 * 
	 * @param _o
	 * @param _default
	 * @return String value of _o
	 */
	public static String toString(Object _o, String _default) {
		if (_o == null) {
			return _default;
		} else {
			try {
				return (String) _o;
			} catch (ClassCastException e) {
				return _default;
			}
		}
	}

	/**
	 * Removes the tags from a string
	 */
	public static String removeTags(String _str) {
		return _str.replaceAll("</?\\s*(\\w+)(\\s+[\\w-]+=([\\w\\.]+|\"[^\"]*\"|'[^']*'))*\\s*/?>", "");
	}

	/**
	 * Replaces all occurrences of specified characters in a String with the given String replacements
	 * 
	 * @param _src
	 *          The String in which the chars should be replaced
	 * @param _old
	 *          A list of characters to be replaced
	 * @param _new
	 *          A list of Strings to use as replacements. The first String will be used when an occurence of the first char is found, and so on.
	 * @throws IllegalArgumentException
	 *           if the size of the _old and _new arrays don't match
	 * @return String with replacements inserted
	 */
	public static String replaceChars(String _src, char[] _old, String[] _new) {
		if (_old.length != _new.length) {
			throw new IllegalArgumentException("Method misuse: _old.length != _new.length");
		}
		int strLen = _src.length();
		int charsLen = _old.length;
		StringBuffer buffer = new StringBuffer(_src);
		StringWriter writer = new StringWriter(strLen);
		char nextChar;
		boolean foundCh;

		for (int i = 0; i < strLen; i++) {
			nextChar = buffer.charAt(i);
			foundCh = false;

			for (int j = 0; j < charsLen; j++) {
				if (nextChar == _old[j]) {
					writer.write(_new[j]);
					foundCh = true;
				}
			}
			if (!foundCh) {
				writer.write(nextChar);
			}
		}

		return writer.toString();
	}

	/*
	 * Makes an HTML friendly version of the string, escaping the necessary tags > < " '
	 * 
	 * @param str The string of HTML @return String with HTML
	 */
	public static String escapeForHtml(String str) {
		return replaceChars(str, new char[] { '&', '<', '>', '\"' }, new String[] { "&amp;", "&lt;", "&gt;", "&quot;" });
	}

	/**
	 * Decodes back from HTML
	 * 
	 * @param str
	 * @return
	 */
	public static String escapeFromHtml(String str) {
		try {
			return tokenReplace(str, new String[] { "&amp;", "&lt;", "&gt;", "&quot;" }, new String[] { "&", "<", ">", "\"" });
		} catch (Exception e) {
			return str;
		}
	}

	/*
	 * Fully encodes a string for html, replacing characters outwith the a-zA-Z0-9 ranges with the html escaped version
	 * 
	 * from http://www.owasp.org/index.php/How_to_perform_HTML_entity_encoding_in_Java
	 * 
	 * @_html the html string to escape
	 */
	public static StringBuilder escapeHtmlFull(String _html) {
		StringBuilder b = new StringBuilder(_html.length());
		for (int i = 0; i < _html.length(); i++) {
			char ch = _html.charAt(i);
			if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9') {
				// safe
				b.append(ch);
			} else if (Character.isWhitespace(ch)) {
				// paranoid version: whitespaces are unsafe - escape
				// conversion of (int)ch is naive
				b.append("&#").append((int) ch).append(";");
			} else if (Character.isISOControl(ch)) {
				// paranoid version:isISOControl which are not isWhitespace removed !
				// do nothing do not include in output !
			} else {
				// paranoid version
				// the rest is unsafe, including <127 control chars
				b.append("&#" + (int) ch + ";");
			}
		}
		return b;
	}

	/*
	 * Takes a string of items and returns them back as a List object
	 * 
	 * @param str The string list @param delimitor the delimitor of the string list
	 */
	public static List<String> listToList(String str, String delimitor) {
		if (str == null || str.length() == 0)
			return new ArrayList<String>();

		String[] array = listToArray(str, delimitor);
		ArrayList<String> list = new ArrayList<String>();
		for (int x = 0; x < array.length; x++)
			list.add(array[x]);

		return list;
	}

	public static List<String> listToList(String str) {
		return listToList(str, ",");
	}

	/*
	 * Takes a string of items and returns them back as an Array of Strings
	 * 
	 * @param str The string list @param delimitor the delimitor of the string list
	 */
	public static String[] listToArray(String str, String delimitor) {
		if (str == null || str.length() == 0)
			return new String[0];

		return str.split(delimitor);
	}

	public static String[] listToArray(String str) {
		return listToArray(str, ",");
	}

	public static String readToString(Reader _reader) throws IOException {
		StringBuffer sb = new StringBuffer();
		char[] buff = new char[1024];
		int read;
		while ((read = _reader.read(buff)) != -1)
			sb.append(buff, 0, read);
		return sb.toString();

	}

	/**
	 * Takes a string representation of an IP address, a.b.c.d, and converts into a long
	 * 
	 * "192.168.1.1" --> 3232235777
	 * 
	 * @param ipAddress
	 * @return
	 */
	public static long ipStringToLong(String ipAddress) {
		String[] addressParts = ipAddress.split("\\.");
		if (addressParts.length != 4)
			return -1;

		String hexVal = "";
		for (int i = 0; i < 4; i++) {
			int intVal = StringUtil.toInteger(addressParts[i], 0);
			String hexTemp = Integer.toHexString(intVal);
			if (hexTemp.length() < 2) {
				hexTemp = "0" + hexTemp;
			}
			// Builds a String of the hex values for each byte
			hexVal = hexVal + hexTemp;
		}
		return Long.parseLong(hexVal, 16);
	}
}
