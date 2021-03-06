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
 *  
 *  https://github.com/aw20/MongoWorkBench
 *  Original fork: https://github.com/Kanatoko/MonjaDB
 *  
 *  March 2013
 */
package org.aw20.mongoworkbench;

import java.util.HashMap;
import java.util.Map;


public class EventWrapper {

	public static String	ACTIVE_NAME = "sname";
	public static String	ACTIVE_DB 	= "sdb";
	public static String	ACTIVE_COLL = "scoll";
	public static String	DOC_DATA 		= "data";
	
	public static String	COMMAND			= "command";
	public static String	DBOBJECT		= "dbobject";
	
	public static	Map createMap( Object... args ){
		HashMap	map	= new HashMap();
		
		for ( int x=0; x < args.length; x += 2 ){
			map.put( args[x], args[x+1] );
		}
		
		return map;
	}
}
