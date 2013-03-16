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
package net.jumperz.app.MMonjaDBCore.action.mj;

import net.jumperz.app.MMonjaDBCore.MCoreUtil;
import net.jumperz.app.MMonjaDBCore.MDataManager;
import net.jumperz.app.MMonjaDBCore.action.MAbstractAction;
import net.jumperz.util.MStringUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

public class MUpdateIntFieldAction extends MAbstractAction {

	private String action;

	public MUpdateIntFieldAction() {
	}

	public String getAction() {
		return action;
	}

	public boolean parse(String action) {
		this.action = action;
		String[] array = action.split("\\s+");
		if (array.length == 8) {
			if (action.indexOf("mj update int field") == 0) {
				return true;
			}
		}
		return false;
	}

	public void executeFunction() throws Exception {
		String[] array = action.split("\\s+");
		String collName = array[4];
		String oidStr = array[5];
		String fieldName = array[6];
		int value = MStringUtil.parseInt(array[7]);

		Object oid = MCoreUtil.getObjectIdFromString(oidStr);
		DBObject query = new BasicDBObject("_id", oid);
		DBObject update = new BasicDBObject("$set", new BasicDBObject(fieldName, new Integer(value)));

		DB db = MDataManager.getInstance().getDB();

		DBCollection coll = db.getCollection(collName);

		coll.update(query, update, false, false, WriteConcern.SAFE);
	}

	public int getActionCondition() {
		return action_cond_collection;
	}

	public String getEventName() {
		return event_mj_update_int;
	}

}