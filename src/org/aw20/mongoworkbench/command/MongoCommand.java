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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aw20.io.StreamUtil;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.util.NumberUtil;
import org.aw20.util.StringUtil;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public abstract class MongoCommand extends Object {

	public static String KEY_NAME = "_name";

	public static String KEY_COUNT = "_count";

	protected String sName = null, sDb = null, sColl = null, rteMessage = "", cmd = null;

	protected Exception lastException = null;

	protected boolean hasRun = false;

	private long execTime = -1;

	/**
	 * Sets the connection details to which this command pertains to
	 * 
	 * @param mongoName
	 * @param database
	 * @param collection
	 */
	public MongoCommand setConnection(String mongoName, String database, String collection) {
		this.sName = mongoName;
		this.sDb = database;
		this.sColl = collection;
		return this;
	}

	public MongoCommand setConnection(String mongoName, String database) {
		this.sName = mongoName;
		this.sDb = database;
		return this;
	}

	public MongoCommand setConnection(String mongoName) {
		this.sName = mongoName;
		return this;
	}

	public MongoCommand setConnection(MongoCommand mcmd) {
		this.sName = mcmd.sName;
		this.sDb = mcmd.sDb;
		this.sColl = mcmd.sColl;
		return this;
	}

	public boolean isSuccess() {
		return lastException == null;
	}
	
	public boolean hasQueryData(){
		return false;
	}

	public boolean hasRun() {
		return hasRun;
	}

	public void markAsRun() {
		hasRun = true;
	}

	public Exception getException() {
		return lastException;
	}

	public void setException(Exception e) {
		lastException = e;
	}

	public String getExceptionMessage() {
		if (lastException == null)
			return "";

		if (lastException instanceof MongoException) {
			String e = lastException.getMessage();
			if (e.startsWith("command failed [$eval]: {") && e.endsWith("}")) {
				try {
					Map m = (Map) JSON.parse(e.substring(e.indexOf("{")));
					return (String) m.get("errmsg");
				} catch (Exception ee) {
					return lastException.getMessage();
				}
			}
		}

		return lastException.getMessage();
	}

	public long getExecTime() {
		return execTime;
	}

	protected void setMessage(String string) {
		rteMessage = string;
	}

	public String getMessage() {
		return rteMessage;
	}

	public String getName() {
		return sName;
	}

	public String getDB() {
		return sDb;
	}

	public String getCollection() {
		return sColl;
	}

	/**
	 * Override this function to provide the body of the command
	 */
	public abstract void execute() throws Exception;

	public String getCommandString() {
		return this.cmd;
	}

	public void setExecTime(long l) {
		execTime = l;
	}

	public void setCommandStr(String cmd) {
		this.cmd = cmd;
	}

	public void parseCommandStr() throws Exception {}

	public String getMatchIgnoreCase(String patternStr, String target) {
		Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(target);
		if (matcher.find()) {
			if (matcher.groupCount() > 0) {
				return matcher.group(1);
			} else {
				return target.substring(matcher.start(), matcher.end());
			}
		} else {
			return "";
		}
	}
	
	
	public BasicDBObject parse() throws Exception{
		MongoClient mdb = MongoFactory.getInst().getMongo( sName );
		
		if ( mdb == null )
			throw new Exception("no server selected");
		
		if ( sDb == null )
			throw new Exception("no database selected");
		
		MongoFactory.getInst().setActiveDB(sDb);
		
		DB db	= mdb.getDB(sDb);
		return parseMongoCommandString(db, cmd);
	}
	
	
	protected BasicDBObject parseMongoCommandString(DB db, String cmd) throws Exception {
		
		String newCmd = cmd.replaceFirst("db." + sColl, "a");
		String jsStr = StreamUtil.readToString( this.getClass().getResourceAsStream("parseCommand.txt") ); 
		jsStr = StringUtil.tokenReplace(jsStr, new String[]{"//_QUERY_"}, new String[]{newCmd} );

		Context cx = Context.enter();
		cx.setLanguageVersion(Context.VERSION_1_7);
		Scriptable scope = cx.initStandardObjects();
		Object returnObj = cx.evaluateString(scope, jsStr, "CustomJS", 1, null);
		
		Map cmdMap = (Map) jsConvert2cfData( (IdScriptableObject)returnObj );
		
		// Remove the helper methods
		cmdMap.remove("find");
		cmdMap.remove("findOne");
		cmdMap.remove("group");
		cmdMap.remove("aggregate");
		cmdMap.remove("save");
		cmdMap.remove("mapReduce");
		cmdMap.remove("update");
		cmdMap.remove("remove");
		cmdMap.remove("limit");
		cmdMap.remove("skip");
		cmdMap.remove("sort");
		
		return new BasicDBObject( cmdMap );
	}

	
	public static Object jsConvert2cfData(IdScriptableObject obj) throws Exception {

		if (obj instanceof NativeObject) {
			Map struct = new HashMap();

			NativeObject nobj = (NativeObject) obj;
			Object[] elements = nobj.getAllIds();

			Object cfdata;
			
			for (int x = 0; x < elements.length; x++) {
				Object jsObj = nobj.get(elements[x]);

				if (jsObj == null)
					cfdata = null;
				else if (jsObj instanceof NativeObject || jsObj instanceof NativeArray)
					cfdata = jsConvert2cfData((IdScriptableObject) jsObj);
				else
					cfdata = jsObj;

				struct.put((String) elements[x], cfdata);
			}

			return struct;
		} else if (obj instanceof NativeArray) {
			List array = new ArrayList();

			NativeArray nobj = (NativeArray) obj;
			Object cfdata;
			int len = (int) nobj.getLength();

			for (int x = 0; x < len; x++) {
				Object jsObj = nobj.get(x);

				if (jsObj == null)
					cfdata = null;
				else if (jsObj instanceof NativeObject || jsObj instanceof NativeArray)
					cfdata = jsConvert2cfData((IdScriptableObject) jsObj);
				else
					cfdata = jsObj;
				
				array.add(cfdata);
			}

			return array;
		} else {
			return null;
		}
	}

	
	protected BasicDBObject	fixNumbers( BasicDBObject dbo ){
		Iterator<String> it	= dbo.keySet().iterator();
		while (it.hasNext()){
			String field	= it.next();
			Object o	= dbo.get(field);
			if ( o instanceof Double ){
				dbo.put(field, NumberUtil.fixDouble( (Double)o) );
			}else if ( o instanceof BasicDBObject ){
				dbo.put(field, fixNumbers((BasicDBObject)o) );
			}else if ( o instanceof BasicDBList ){
				dbo.put(field, fixNumbers((BasicDBList)o) );
			}
		}
		
		return dbo;
	}
	
	
	protected BasicDBList	fixNumbers( BasicDBList dbo ){
		for ( int x=0; x < dbo.size(); x++ ){
			Object o =	dbo.get(x);
			
			if ( o instanceof Double ){
				dbo.set(x, NumberUtil.fixDouble( (Double)o) );
			}else if ( o instanceof BasicDBObject ){
				dbo.set(x, fixNumbers((BasicDBObject)o) );
			}else if ( o instanceof BasicDBList ){
				dbo.set(x, fixNumbers((BasicDBList)o) );
			}
		}

		return dbo;
	}
}