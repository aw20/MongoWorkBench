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

import java.io.IOException;
import java.util.List;

import net.jumperz.app.MMonjaDB.eclipse.Activator;
import net.jumperz.app.MMonjaDBCore.MConstants;
import net.jumperz.util.MRegEx;

import org.aw20.io.StreamUtil;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.util.StringUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class FindMongoCommand extends MongoCommand {

	private DBCursor cursor;
	private	BasicDBObject cmdMap;
	private int skip, limit, count;
	
	@Override
	public void execute() throws Exception {
		MongoClient mdb = MongoFactory.getInst().getMongo( sName );
		if ( mdb == null )
			throw new Exception("no server selected");
		
		if ( sDb == null )
			throw new Exception("no database selected");
		
		MongoFactory.getInst().setActiveDB(sDb);

		DB db	= mdb.getDB(sDb);
		try {
			parseFindQuery(db);
		} catch (IOException e) {
			setException(e);
			return;
		}
		
		DBCollection	collection	= db.getCollection(sColl);
		
		// Construct the query
		int argSize	= ((List)cmdMap.get("findArg")).size();
		
		skip	= getSkip();
		limit	= getLimit();

		if ( argSize == 0 ){
			cursor = collection.find();
		}else if ( argSize == 1 ){
			DBObject ref = (DBObject)((List)cmdMap.get("findArg")).get(0);
			cursor = collection.find(ref);
		}else if (argSize >= 2) {
			List findArg = (List)cmdMap.get("findArg");
			DBObject ref = (DBObject) findArg.get(0);
			DBObject key = (DBObject) findArg.get(1);
			cursor = collection.find(ref, key);
		}

		// Apply the skip/limit
		if (skip > -1)
			cursor = cursor.skip(skip);
		if (limit > -1)
			cursor = cursor.limit(limit);

		// Apply the sort
		if (cmdMap.containsField("sortArg")) {
			cursor = cursor.sort( (BasicDBObject)cmdMap.get("sortArg") );
		}
		
		count	= cursor.count();
		
		setMessage( "count=" + count + ((skip > -1) ? ("; skip=" + skip) : "" ) + ((limit > -1) ? ("; limit=" + limit) : "" )  );
	}

	public String getQuery(){
		if (((List)cmdMap.get("findArg")).size() == 0)
			return "";
		else
			return ((List)cmdMap.get("findArg")).get(0).toString();
	}
	
	public String getSort(){
		if (cmdMap.containsField("sortArg")) 
			return cmdMap.get("sortArg").toString();
		else
			return "";
	}
	
	private int getLimit() {
		int limit = StringUtil.toInteger( cmdMap.get("limitArg"), -1);
		if ( limit == -1 )
			limit = Activator.getDefault().getPreferenceStore().getInt( MConstants.PREF_MAX_FIND_RESULTS );
		
		return limit;
	}

	private int getSkip() {
		return StringUtil.toInteger( cmdMap.get("skipArg"), -1);
	}
	
	public DBCursor getCursor(){
		return cursor;
	}

	@Override
	public void parseCommandStr() throws Exception {
		sColl = getCollNameFromAction(cmd, "find");
		
		if ( sColl == null || sColl.length() == 0 )
			throw new Exception("failed to determine collection from command");
	}
	
	protected void parseFindQuery(DB db) throws IOException {
		String newCmd = cmd.replaceFirst("db." + sColl, "a");

		String jsStr = StreamUtil.readToString( this.getClass().getResourceAsStream("parseFindQuery.txt") ); 
		jsStr = StringUtil.tokenReplace(jsStr, new String[]{"//_QUERY_"}, new String[]{newCmd} );
		
		cmdMap = (BasicDBObject)db.eval(jsStr, (Object[])null);
		cmdMap.remove("find");
		cmdMap.remove("limit");
		cmdMap.remove("skip");
		cmdMap.remove("sort");
	}

	public int getExecutedLimit(){
		return limit;
	}
	
	public int getExecutedSkip(){
		return skip;
	}

	public int getCount(){
		return count;
	}
	
	public BasicDBObject getDbObject(){
		return cmdMap;
	}
	
	public void close(){
		cursor.close();
		cursor = null;
	}
	
	/**
	 * db.service.find() -> service
	 * 
	 * @param actionStr
	 * @param actionName
	 * @return
	 */
	protected String getCollNameFromAction(String actionStr, String actionName) {
		return MRegEx.getMatchIgnoreCase("^db\\.([^\\(]+)\\." + actionName + "\\(", actionStr);
	}
}