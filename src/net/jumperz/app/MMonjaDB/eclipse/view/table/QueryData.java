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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jumperz.app.MMonjaDB.eclipse.view.MDocumentView.NAVITEM;

import org.aw20.mongoworkbench.command.FindMongoCommand;
import org.aw20.util.DateUtil;
import org.aw20.util.JSONFormatter;
import org.eclipse.swt.SWT;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class QueryData extends Object {
	private static int MAX_CHAR_WIDTH = 24;
	
	private static String BLANK = "";
	private String[]	columns;
	private	List<Map>	data = null;
	private Set<String>	rightJustified;
	
	private FindMongoCommand	findCommand;

	public QueryData( List<Map>	data, String firstColumn ){
		this.data = data;
		
		rightJustified	= new HashSet<String>();
		Set<String>	columnSet = new HashSet<String>();
		Iterator<Map>	it	= this.data.iterator();
		while ( it.hasNext() ){
			columnSet.addAll( it.next().keySet() );
		}
		setColumns(columnSet, firstColumn);
	}
	
	public QueryData( FindMongoCommand fmcmd ){
		if ( !fmcmd.isSuccess() )
			return;
		
		findCommand	= fmcmd;
		
		rightJustified	= new HashSet<String>();
		Set<String>	columnSet = new HashSet<String>();
		data	= new ArrayList<Map>();
		DBCursor	cursor	= fmcmd.getCursor();
		
		while ( cursor.hasNext() ){
			DBObject dbo	= cursor.next();
			columnSet.addAll( dbo.keySet() );
			data.add( dbo.toMap() );
		}

		findCommand.close();
		setColumns(columnSet,"_id");
	}
	
	public void addRightColumn(String str){
		rightJustified.add(str);
	}
	
	private void setColumns(Set<String>	columnSet, String firstColumn){
		columns	= columnSet.toArray( new String[0] );
		Arrays.sort(columns);
		
		// Move the _id to the front
		for ( int x=1; x < columns.length; x++ ){
			if ( columns[x].equals(firstColumn) ){
				String column1 = columns[0];
				columns[0]	= columns[x];
				columns[x]	= column1;
				break;
			}
		}
		
	}

	public int size() {
		return (data == null) ? 0 : data.size();
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

	public String getActiveName() {
		return findCommand.getName();
	}

	public String getActiveDB() {
		return findCommand.getDB();
	}

	public String getActiveColl() {
		return findCommand.getCollection();
	}

	
	/**
	 * Gets a new command to run
	 * 
	 * @param refresh
	 * @return
	 */
	public String getCommand(NAVITEM action) {
		
		StringBuilder	cmd	= new StringBuilder(128);
		cmd.append( "db." )
			.append( findCommand.getCollection() )
			.append( ".find(" )
			.append( findCommand.getQuery() )
			.append( ")");
			;

		if ( action == NAVITEM.PAGE_BACK ){
			
			int skip = findCommand.getExecutedSkip() - findCommand.getExecutedLimit();
			if ( skip < 0 )
				skip = 0;
			
			if ( skip > 0 )
				cmd.append( ".skip(" ).append( skip ).append( ")" );
			
		}else if ( action == NAVITEM.PAGE_FORWARD ){

			int skip = findCommand.getExecutedLimit() + findCommand.getExecutedSkip();
			if ( skip > findCommand.getCount() )
				skip	= findCommand.getCount() - (findCommand.getCount() % findCommand.getExecutedLimit());
			
			if ( skip > 0 )
				cmd.append( ".skip(" ).append( skip ).append( ")" );
			
		}else if ( action == NAVITEM.REFRESH ){

			if (  findCommand.getExecutedSkip() > 0 )
				cmd.append( ".skip(" ).append( findCommand.getExecutedSkip() ).append( ")" );

		}else if ( action == NAVITEM.PAGE_END ){

			int skip	= findCommand.getCount() - (findCommand.getCount() % findCommand.getExecutedLimit());
			if ( skip < 0 )
				skip = 0;
			
			if ( skip > 0 )
				cmd.append( ".skip(" ).append( skip ).append( ")" );

		} 
		
		// Append the limit
		cmd.append(".limit(")
			.append( findCommand.getExecutedLimit() )
			.append( ")" );
		
		// Append in the sort
		String sort	= findCommand.getSort();
		if ( sort.length() > 0 ){
			cmd.append(".sort(" )
				.append( sort )
				.append( ")" );
		}
			
		
		return cmd.toString();
	}

	public String getJSON() {
		if ( size() == 0 )
			return "";

		StringBuilder	sb	= new StringBuilder( 32000 );
		
		// Do the rows
		for ( int r=0; r < size(); r++ ){
			Map	rowMap	= get(r);
			
			sb.append( "/*___ " ).append( r+findCommand.getExecutedSkip() ).append(" ____________________________________*/\r\n" );
			sb.append( JSONFormatter.format(rowMap) );
			sb.append( "\r\n\r\n\r\n" );
		}

		sb.delete(sb.length()-6, sb.length());
		
		return sb.toString();
	}

	public int getColumnAlign(String columnName) {
		return (rightJustified.contains(columnName)) ? SWT.RIGHT : SWT.LEFT;
	}
}
