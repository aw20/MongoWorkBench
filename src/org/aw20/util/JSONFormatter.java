/* 
 *  Copyright (C) 2013 AW2.0 Ltd
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
 *  $Id: JSONFormatter.java 3636 2013-02-27 16:05:12Z alan $
 */
package org.aw20.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;

public class JSONFormatter extends Object {

	private StringBuilder sb;
	private String tab;
	private String nl = System.getProperty("line.separator");

	public static String format( Object object ){
		return new JSONFormatter().parseObject(object);
	}
	
	public JSONFormatter() {
		this("  ");
	}
	
	public JSONFormatter(String _tab) {
		this.tab = _tab;
	}
	
	/**
	 * Treats a List, Map, String, Integer, Long, Float, Double, or Boolean as a JSON Object and formats it in JSON format.
	 * 
	 * Unrecognized Object Types will be marked "binary-object"
	 * 
	 * @param _o A valid object to be parsed may be a Boolean, Integer, Float, String, or Map or List containing any of the other object
	 * @return Formatted Json
	 */
	public String parseObject(Object _o) {
		sb = new StringBuilder(256);
		parseValue(_o);
		return sb.toString();
	}
	
	private void parseValue(Object _o) {
		parseValue(_o, 0);
	}
	
	private void parseValue(Object _o, int _depth) {
		
		if ( _o instanceof String || _o instanceof Character ){
			sb.append("\"" + _o.toString() + "\"");
		} else if (_o instanceof Map) {
			sb.append("{");
			parseObject((Map<String, Object>)_o, _depth + 1);
			sb.append("}");
		} else if (_o instanceof List) {
			sb.append("[");
			parseArray((List)_o, _depth + 1);
			sb.append("]");
		} else if (_o instanceof Float || _o instanceof Double) {
			String d = String.valueOf(_o);
			if ( d.endsWith(".0") )
				sb.append(d.substring(0,d.length()-2));
			else
				sb.append(d);
		} else if (_o instanceof Integer || _o instanceof Long || _o instanceof Boolean) {
			sb.append(_o.toString());
		} else if (_o == null) {
			sb.append("null");
		} else if ( _o instanceof Date ){
			sb.append( "ISODate(\"" );
			sb.append( DateUtil.getDateString( (Date)_o, "yyyy-MM-dd'T'HH:mm:ss.SSS") );
			sb.append( "Z\")");
		} else if ( _o instanceof ObjectId ){
			sb.append( "ObjectId(\"" );
			sb.append( (ObjectId)_o );
			sb.append( "\")");
		} else if ( _o instanceof Pattern ){
			sb.append( "/" );
			sb.append( (Pattern)_o );
			sb.append( "/");
		} else if ( _o instanceof byte[] ){	
			sb.append( "BinData(0,\"" );
			sb.append( Base64.encodeBytes((byte[])_o) );
			sb.append( "\")");
		} else {
			sb.append( _o.getClass().getName() );
		}
	}
	
	private void parseObject(Map<String, Object> _map, int _depth) {
		String margin = getMargin(_depth);
		
		String[] keyArr = (String[])_map.keySet().toArray(new String[0]);
		Arrays.sort(keyArr);
		for ( String key : keyArr ){
			Object value = _map.get(key);
			
			// Write out this key/value pair
			sb.append(nl)
				.append(margin)
				.append("\"")
				.append( key )
				.append("\" : ");
			parseValue(value, _depth);
			sb.append(",");
		}
		
		// Remove final comma
		if ( sb.charAt(sb.length()-1) == ','){
			sb.delete(sb.length() - 1, sb.length());
			sb.append(nl).append(getMargin(_depth - 1));
		} else {
			sb.append(" ");
		}
	}
	
	private void parseArray(List _list, int _depth) {
		
		// Parse through an array
		boolean empty = true;
		for( Object o : _list) {
			empty = false;
			parseValue(o, _depth);
			sb.append(", ");
		}

		// Remove final comma
		if (!empty)
			sb.delete(sb.length() - 2, sb.length());
		else
			sb.append(" ");
	}
	
	// Gets the margin for the specified _depth level
	private String getMargin(int _depth) {
		StringBuilder margin = new StringBuilder(32);
		for (int i = 0; i < _depth; i++)
			margin.append(this.tab);
		
		return margin.toString();
	}
}