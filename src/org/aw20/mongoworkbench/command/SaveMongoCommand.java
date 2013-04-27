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
 *  April 2013
 */
package org.aw20.mongoworkbench.command;

import java.util.Date;
import java.util.Map;

import org.aw20.mongoworkbench.Event;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.MongoFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

public class SaveMongoCommand extends FindMongoCommand {

	private Object	id = null;
	
	@Override
	public void execute() throws Exception {
		MongoClient mdb = MongoFactory.getInst().getMongo( sName );
		
		if ( mdb == null )
			throw new Exception("no server selected");
		
		if ( sDb == null )
			throw new Exception("no database selected");
		
		MongoFactory.getInst().setActiveDB(sDb);
		
		DB db	= mdb.getDB(sDb);
		BasicDBObject cmdMap	= parseMongoCommandString(db, cmd);
		
		if ( !cmdMap.containsField("saveArg") )
			throw new Exception("no save document");
		
		DBObject	document				= fixNumbers( (BasicDBObject)cmdMap.get("saveArg") );
		DBCollection	collection	= db.getCollection(sColl);
		
		// Run the command
		db.requestStart();
		WriteResult writeresult;
		try{
			writeresult = collection.save(document, WriteConcern.JOURNAL_SAFE);
			id	= document.get("_id");
		}finally{
			db.requestDone();
		}

		// Get the result
		Map mwriteresult	= (Map)JSON.parse( writeresult.toString() );
		mwriteresult.put("exeDate", new Date() );
		
		EventWorkBenchManager.getInst().onEvent( Event.WRITERESULT, mwriteresult );
		
		setMessage( "Saved: updatedExisting=" + mwriteresult.get("updatedExisting") + "; documentsUpdated=" + mwriteresult.get("n") );
	}
	
	public Object	getObjectId(){
		return id;
	}
	
	@Override
	public void parseCommandStr() throws Exception {
		sColl = getCollNameFromAction(cmd, "save");
		
		if ( sColl == null || sColl.length() == 0 )
			throw new Exception("failed to determine collection from command");
	}
}