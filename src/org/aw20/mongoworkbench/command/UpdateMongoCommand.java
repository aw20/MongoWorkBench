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
import java.util.List;
import java.util.Map;

import org.aw20.mongoworkbench.Event;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.util.StringUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;


public class UpdateMongoCommand extends FindMongoCommand {

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
		
		if ( !cmdMap.containsField("updateArg") )
			throw new Exception("no update document");
		
		
		List	argList	= (List)cmdMap.get("updateArg");
		if ( argList.size() == 1 )
			throw new Exception("not enough parameters; db.collection.update(query, update, <upsert>, <multi>)");
		
		DBCollection	collection	= db.getCollection(sColl);

		db.requestStart();
		WriteResult writeresult = null;
		try{
			
			if ( argList.size() == 2 ){
				
				writeresult			= collection.update( (DBObject)argList.get(0), fixNumbers( (BasicDBObject)argList.get(1) ) );
				
			}else if ( argList.size() == 3 ){
				
				boolean upsert 	= StringUtil.toBoolean( argList.get(2), false );
				writeresult			= collection.update( (DBObject)argList.get(0), fixNumbers( (BasicDBObject)argList.get(1) ), upsert, false );
				
			}else if ( argList.size() == 4 ){
				
				boolean upsert 	= StringUtil.toBoolean( argList.get(2), false );
				boolean multi 	= StringUtil.toBoolean( argList.get(3), false );
				writeresult			= collection.update( (DBObject)argList.get(0), fixNumbers( (BasicDBObject)argList.get(1) ), upsert, multi );
				
			}else
				throw new Exception("too many parameters; db.collection.update(query, update, <upsert>, <multi>)");

		}finally{
			db.requestDone();
		}

		// Get the result
		Map mwriteresult	= (Map)JSON.parse( writeresult.toString() );
		mwriteresult.put("exeDate", new Date() );
		
		EventWorkBenchManager.getInst().onEvent( Event.WRITERESULT, mwriteresult );
		
		setMessage( "Updated: updatedExisting=" + mwriteresult.get("updatedExisting") + "; documentsUpdated=" + mwriteresult.get("n") );
	}
	
	@Override
	public void parseCommandStr() throws Exception {
		sColl = getCollNameFromAction(cmd, "update");
		
		if ( sColl == null || sColl.length() == 0 )
			throw new Exception("failed to determine collection from command");
	}
}