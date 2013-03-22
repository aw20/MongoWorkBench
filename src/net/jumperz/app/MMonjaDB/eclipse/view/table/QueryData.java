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
 */
package net.jumperz.app.MMonjaDB.eclipse.view.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aw20.util.DateUtil;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class QueryData extends Object {
	private static int MAX_CHAR_WIDTH = 24;
	
	private static String BLANK = "";
	private String[]	columns;
	private	List<Map>	data;
		
	public QueryData(DBCursor cursor){
		
		Set<String>	columnSet = new HashSet<String>();
		data	= new ArrayList<Map>();
		
		while ( cursor.hasNext() ){
			DBObject dbo	= cursor.next();
			columnSet.addAll( dbo.keySet() );
			data.add( dbo.toMap() );
		}

		columns	= columnSet.toArray( new String[0] );
		Arrays.sort(columns);
		
		// Move the _id to the front
		for ( int x=1; x < columns.length; x++ ){
			if ( columns[x].equals("_id") ){
				String column1 = columns[0];
				columns[0]	= columns[x];
				columns[x]	= column1;
				break;
			}
		}
		
	}

	public int size() {
		return data.size();
	}

	public String[] getColumns() {
		return columns;
	}

	public Map get(int r) {
		return data.get(r);
	}

	public String getString(Map rowMap, String key) {
		if ( !rowMap.containsKey(key) )
			return BLANK;
		
		Object	obj	= rowMap.get(key);
		if ( obj == null )
			return "null";
		else if ( obj instanceof Map )
			return "{" + ((Map)obj).size() + " keys}";
		else if ( obj instanceof List )
			return "[" + ((List)obj).size() + " items]";
		else if ( obj instanceof byte[] )
			return "[bin " + ((byte[])obj).length + " bytes]";
		else if ( obj instanceof Date )
			return DateUtil.getSQLDate( (Date)obj );
		else{
			String s = String.valueOf( obj );
			if ( s.length() > MAX_CHAR_WIDTH )
				return s.substring(0, MAX_CHAR_WIDTH);
			else
				return s;
		}
	}
	
}
