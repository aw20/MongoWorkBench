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
 *  April 2013
 */
package org.aw20.mongoworkbench.command;

import org.aw20.mongoworkbench.MongoFactory;
import org.bson.types.ObjectId;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;

public class GridFSRemoveFileCommand extends MongoCommand {

	private String id;
	
	public GridFSRemoveFileCommand(String id) {
		this.id = id;
	}

	@Override
	public void execute() throws Exception {
		MongoClient mdb = MongoFactory.getInst().getMongo( sName );
		
		if ( mdb == null )
			throw new Exception("no server selected");
		
		if ( sDb == null )
			throw new Exception("no database selected");
		
		MongoFactory.getInst().setActiveDB(sDb);
		DB db	= mdb.getDB(sDb);
		
		GridFS	gfs	= new GridFS( db, sColl.substring(0,sColl.lastIndexOf(".")) );
		gfs.remove( new ObjectId(id) );

		setMessage("fileRemoved="+id);
	}

	public String getCommandString() {
		return "aw20.gridfs." + sColl.substring(0,sColl.lastIndexOf(".")) + ".remove(" + id + ")";
	}
	
}
