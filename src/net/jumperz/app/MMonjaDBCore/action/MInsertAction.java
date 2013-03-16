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
 */
package net.jumperz.app.MMonjaDBCore.action;

import net.jumperz.app.MMonjaDBCore.MDataManager;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

public class MInsertAction extends MAbstractAction {
	private String action;
	private DB db;
	private DBCollection coll;
	private WriteResult writeResult;

	public MInsertAction() {
	}

	public WriteResult getResult() {
		return writeResult;
	}

	public String getEventName() {
		return event_insert;
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
			this.action = action;
			return true;
		} catch (Exception e) {
			debug(e);
			return false;
		}
	}

	public void executeFunction() throws Exception {
		db = MDataManager.getInstance().getDB();
		db.eval(action, (Object[]) null);
	}

}
