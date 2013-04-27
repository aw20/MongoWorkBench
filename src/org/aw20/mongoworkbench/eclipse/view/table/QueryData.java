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
 */
package org.aw20.mongoworkbench.eclipse.view.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aw20.mongoworkbench.command.FindMongoCommand;
import org.aw20.mongoworkbench.eclipse.view.MDocumentView.NAVITEM;
import org.aw20.util.DateUtil;
import org.aw20.util.JSONFormatter;
import org.eclipse.swt.SWT;

import com.mongodb.BasicDBList;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

public class QueryData extends Object {
	private static int MAX_CHAR_WIDTH = 24;
	
	private static String BLANK = "";
	private String[]	columns;
	private	List<Map>	data = null;
	private Set<String>	rightJustified;
	private int count = 0;
	
	private FindMongoCommand	findCommand = null;

	public QueryData( BasicDBList	listdata, String firstColumn ){
		List<Map> data	= new ArrayList<Map>(listdata.size());
		for ( int x=0; x < listdata.size(); x++ )
			data.add( ((DBObject)listdata.get(x)).toMap() );
		
		init(data, firstColumn);
	}
	
	public QueryData( List<Map>	data, String firstColumn ){
		init( data, firstColumn );
	}
	
	public QueryData( FindMongoCommand fmcmd ){
		if ( !fmcmd.isSuccess() )
			return;
		
		findCommand	= fmcmd;
		count	= findCommand.getCount();
		
		rightJustified	= new HashSet<String>();
		Set<String>	columnSet = new HashSet<String>();
		data	= new ArrayList<Map>();
		DBCursor	cursor	= fmcmd.getCursor();
		
		while ( cursor.hasNext() ){
			DBObject dbo	= cursor.next();
			columnSet.addAll( dbo.keySet() );
			
			if ( dbo instanceof GridFSDBFile ){
				Map map	= new HashMap();
				map.put("_id", ((GridFSDBFile)dbo).getId() );
				map.put("chunkSize", ((GridFSDBFile)dbo).getChunkSize() );
				map.put("md5", ((GridFSDBFile)dbo).getMD5() );
				map.put("length", ((GridFSDBFile)dbo).getLength() );
				map.put("filename", ((GridFSDBFile)dbo).getFilename() );
				map.put("contentType", ((GridFSDBFile)dbo).getContentType() );
				
				if ( ((GridFSDBFile)dbo).getAliases() != null )
					map.put("aliases", ((GridFSDBFile)dbo).getAliases() );
				
				if ( ((GridFSDBFile)dbo).getMetaData() != null )
					map.put("metadata", ((GridFSDBFile)dbo).getMetaData() );
				
				map.put("uploadDate", ((GridFSDBFile)dbo).getUploadDate() );
				data.add( map );
			}else
				data.add( dbo.toMap() );
		}

		findCommand.close();
		setColumns(columnSet,"_id");
	}

	private void init(List<Map> data, String firstColumn) {
		this.data = data;
		count	= this.data.size();

		rightJustified = new HashSet<String>();
		Set<String> columnSet = new HashSet<String>();
		Iterator<Map> it = this.data.iterator();
		while (it.hasNext()) {
			columnSet.addAll(it.next().keySet());
		}
		setColumns(columnSet, firstColumn);
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
	
	public boolean isGridFS(){
		return ( findCommand != null && findCommand.getCollection().endsWith(".files") );
	}

	public int getCount(){
		return count;
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
			
			if ( obj instanceof Double ){
				Double d = (Double)obj;
				
				if ( (double)d.longValue() == d )
					System.out.println( d.longValue() );
			}
			
			String s = String.valueOf( obj );
			if ( s.length() > MAX_CHAR_WIDTH )
				return s.substring(0, MAX_CHAR_WIDTH);
			else
				return s;
		}
	}

	public String getActiveName() {
		return (findCommand != null) ? findCommand.getName() : null;
	}

	public String getActiveDB() {
		return (findCommand != null) ? findCommand.getDB() : null;
	}

	public String getActiveColl() {
		return (findCommand != null) ? findCommand.getCollection() : null;
	}

	
	/**
	 * Gets a new command to run
	 * 
	 * @param refresh
	 * @return
	 */
	public String getCommand(NAVITEM action) {
		if (findCommand == null)
			return null;
		
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
			Map<String,Object>	rowMap	= get(r);
			
			sb.append( "/*___ " ).append( r+ ((findCommand != null ) ? findCommand.getExecutedSkip() : 0) ).append(" ____________________________________*/\r\n" );
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
