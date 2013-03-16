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
 *  https://github.com/aw20/MonjaDB
 *  Original fork: https://github.com/Kanatoko/MonjaDB
 *  
 */
package net.jumperz.app.MMonjaDBCore.action;

import net.jumperz.app.MMonjaDBCore.MDataManager;
import net.jumperz.mongo.MFindQuery;
import net.jumperz.mongo.MMongoUtil;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MFindAction extends MAbstractAction {
	private String actionStr;

	private DBCursor cursor;
	private DB db;
	private boolean used = false;
	private DBCollection coll;
	private MFindQuery findQuery;

	public String getActionStr() {
		return actionStr;
	}

	public MFindAction() {
	}

	public String getEventName() {
		return event_find;
	}

	public int getActionCondition() {
		return action_cond_connected;
	}

	public DBCollection getCollection() {
		return coll;
	}

	public DB getDB() {
		return db;
	}

	public boolean parse(String action) {
		try {
			this.actionStr = action;
			return true;
		} catch (Exception e) {
			debug(e);
			return false;
		}
	}

	public DBCursor getCursor() {
		if (used) {
			throw new MContextException("The cursor has used by another object.");
		}
		used = true;
		return cursor;
	}

	public MFindQuery getFindQuery() {
		return findQuery;
	}

	public void executeFunction() throws Exception {
		db = MDataManager.getInstance().getDB();
		String collName = MMongoUtil.getCollNameFromAction(actionStr, "find");
		coll = db.getCollection(collName);

		findQuery = MMongoUtil.parseFindQuery(db, actionStr);

		BasicDBList findArgList = (BasicDBList) findQuery.get("findArg");

		// check skip & limit
		int skip = (int) findQuery.getSkipArg();
		int limit = (int) findQuery.getLimitArg();

		if (findArgList.size() == 0) { // db.test.find()
			cursor = coll.find();
			if (skip > -1) {
				cursor = cursor.skip(skip);
			}
			if (limit > -1) {
				cursor = cursor.limit(limit);
			}
		} else if (findArgList.size() == 1) {
			DBObject ref = (DBObject) findArgList.get(0);
			cursor = coll.find(ref);

			if (skip > -1) {
				cursor = cursor.skip(skip);
			}
			if (limit > -1) {
				cursor = cursor.limit(limit);
			}
		} else if (findArgList.size() >= 2) {
			DBObject ref = (DBObject) findArgList.get(0);
			DBObject key = (DBObject) findArgList.get(1);
			cursor = coll.find(ref, key);
			if (skip > -1) {
				cursor = cursor.skip(skip);
			}
			if (limit > -1) {
				cursor = cursor.limit(limit);
			}
		}

		if (findQuery.getInvokedFunctionNameList().contains("sort")) {
			cursor = cursor.sort(findQuery.getSortArg());
		}
	}
	// --------------------------------------------------------------------------------
}
