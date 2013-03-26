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
package org.aw20.mongoworkbench.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aw20.mongoworkbench.MongoFactory;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class ShowCollectionsMongoCommand extends MongoCommand {

	private List<String>	colNames = null, jsNames = null, gridfsNames = null;
	
	@Override
	public void execute() throws Exception {
		MongoClient mdb = MongoFactory.getInst().getMongo( sName );

		if ( mdb == null )
			throw new Exception("no server selected");
		
		if ( sDb == null )
			throw new Exception("no database selected");

		MongoFactory.getInst().setActiveDB(sDb);
		DB db	= mdb.getDB(sDb);
		Set<String>	colSet	= db.getCollectionNames();
		
		colNames		= new ArrayList<String>(colSet.size());
		jsNames			= new ArrayList<String>(1);
		gridfsNames	= new ArrayList<String>(1);
		
		
		Iterator<String> it = colSet.iterator();
		while ( it.hasNext() ){
			String colName = it.next();
			
			if ( colName.endsWith(".js") )
				jsNames.add( colName );
			else if ( colName.endsWith(".chunks") )
				gridfsNames.add( colName.substring(0, colName.lastIndexOf(".") ) );
			else if ( colName.endsWith(".files") || colName.endsWith("system.indexes") )
				;
			else
				colNames.add( colName );
		}
		
		setMessage("# Collections=" + colNames.size() + "; GridFS=" + gridfsNames.size() + "; Javascript=" + jsNames.size() );
	}

	@Override
	public String getCommandString() {
		return "show collections";
	}
	
	public List<String>	getCollectionNames(){
		return colNames;
	}
	
	public List<String>	getGridFSNames(){
		return gridfsNames;
	}
	
	public List<String>	getJSNames(){
		return jsNames;
	}

}
