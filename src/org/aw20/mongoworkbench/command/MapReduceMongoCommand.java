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
package org.aw20.mongoworkbench.command;

import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.util.StringUtil;
import org.bson.types.Code;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoClient;

public class MapReduceMongoCommand extends FindMongoCommand {

	protected BasicDBList dbListResult = null;
	
	@SuppressWarnings("deprecation")
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
		
		if ( !cmdMap.containsField("mapreduceArgs") )
			throw new Exception("no mapReduce document");

		DBCollection	collection	= db.getCollection(sColl);
		
		// Build the Map
		BasicDBObject	options	= (BasicDBObject)((BasicDBList)cmdMap.get("mapreduceArgs")).get(2);
		
		String outputCollection = null;
		String outputDB = null;
		MapReduceCommand.OutputType	outputType = MapReduceCommand.OutputType.INLINE;
		
		if ( options.get("out") instanceof String ){
			outputCollection 	= (String)options.get("out");
			outputType				= MapReduceCommand.OutputType.REPLACE;
		}else if ( options.get("out") instanceof BasicDBObject ) {
			BasicDBObject out	= (BasicDBObject)options.get("out");
			
			if ( out.containsField("inline") ){
				outputCollection = null;
			} else if ( out.containsField("replace") ){
				outputCollection	= (String)out.get("replace");
				outputType	= MapReduceCommand.OutputType.REPLACE;
			} else if ( out.containsField("merge") ){
				outputCollection	= (String)out.get("merge");
				outputType	= MapReduceCommand.OutputType.MERGE;
			} else if ( out.containsField("reduce") ){
				outputCollection	= (String)out.get("reduce");
				outputType	= MapReduceCommand.OutputType.REDUCE;
			}
			
			if ( out.containsField("db") )
				outputDB	= (String)out.get("db");
		}
		
		MapReduceCommand	mrc	= new MapReduceCommand(
			collection,
			((Code)((BasicDBList)cmdMap.get("mapreduceArgs")).get(0)).getCode(),
			((Code)((BasicDBList)cmdMap.get("mapreduceArgs")).get(1)).getCode(),
			outputCollection,
			outputType,
			(BasicDBObject)options.get("query")
				);
		
		if ( outputDB != null )
			mrc.setOutputDB(outputDB);
		
		if ( options.containsField("sort") && options.get("sort") instanceof DBObject )
			mrc.setSort( (DBObject)options.get("sort") );
		
		if ( options.containsField("scope") && options.get("scope") instanceof DBObject )
			mrc.setScope( ((DBObject)options.get("scope")).toMap() );
		
		if ( options.containsField("finalize") && options.get("scope") instanceof Code )
			mrc.setFinalize( ((Code)options.get("scope")).getCode() );
		
		if ( options.containsField("limit") )
			mrc.setLimit( StringUtil.toInteger( options.get("limit"), -1) );
	
		mrc.addExtraOption("jsMode", StringUtil.toBoolean( options.get("jsMode"), false) );
		mrc.setVerbose( StringUtil.toBoolean( options.get("verbose"), false) );
		
		// Run the actual mapreduce function
		MapReduceOutput	mro	= collection.mapReduce(mrc);
		

		// Pull the inline results
		if ( mro.getOutputCollection() == null ){
			dbListResult	= new BasicDBList();
			Iterable<DBObject> it = mro.results();
			for ( DBObject dbo : it ){
				dbListResult.add(dbo);
			}
		}
		
		BasicDBObject dbo = mro.getRaw();
		StringBuilder sb = new StringBuilder();

		if ( dbo.containsField("timeMillis") )
			sb.append("Time=").append( dbo.get("timeMillis") ).append("ms; ");
		
		if ( dbo.containsField("counts") ){
			BasicDBObject counts = (BasicDBObject)dbo.get("counts");
			sb.append( "Counts: input=" + counts.get("input") );
			sb.append( "; emit=" + counts.get("emit") );
			sb.append( "; reduce=" + counts.get("reduce") );
			sb.append( "; output=" + counts.get("output") );
		}

		setMessage( sb.toString() );
	}
	
	public BasicDBList getResults(){
		return dbListResult;
	}
	
	@Override
	public void parseCommandStr() throws Exception {
		sColl = getCollNameFromAction(cmd, "mapReduce");
		
		if ( sColl == null || sColl.length() == 0 )
			throw new Exception("failed to determine collection from command");
	}
}
