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

import java.util.List;

import org.aw20.mongoworkbench.MongoFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class FindOneMongoCommand extends MongoCommand {
	private	DBObject dbo;
	
	@Override
	public void execute() throws Exception {
		MongoClient mdb = MongoFactory.getInst().getMongo( sName );
		if ( mdb == null )
			throw new Exception("no server selected");
		
		if ( sDb == null )
			throw new Exception("no database selected");
		
		MongoFactory.getInst().setActiveDB(sDb);

		DB db	= mdb.getDB(sDb);
		DBObject cmdMap = parseMongoCommandString(db, cmd);
		
		DBCollection	collection	= db.getCollection(sColl);
		
		// Construct the query
		int argSize	= ((List)cmdMap.get("findOneArg")).size();
		
		if ( argSize == 1 ){
			DBObject ref = (DBObject)((List)cmdMap.get("findOneArg")).get(0);
			dbo = collection.findOne(ref);
		}else if (argSize >= 2) {
			List findArg = (List)cmdMap.get("findOneArg");
			DBObject ref = (DBObject) findArg.get(0);
			DBObject key = (DBObject) findArg.get(1);
			dbo = collection.findOne(ref, key);
		}else
			throw new Exception("no query specified");

		setMessage( "loaded: " + (dbo != null) );
	}

	@Override
	public void parseCommandStr() throws Exception {
		sColl = getCollNameFromAction(cmd, "findOne");
		
		if ( sColl == null || sColl.length() == 0 )
			throw new Exception("failed to determine collection from command");
	}

	public DBObject getDbObject(){
		return dbo;
	}
	
	protected String getCollNameFromAction(String actionStr, String actionName) {
		return getMatchIgnoreCase("^db\\.([^\\(]+)\\." + actionName + "\\(", actionStr);
	}
}