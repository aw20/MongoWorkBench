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

import java.util.ArrayList;
import java.util.List;

import net.jumperz.app.MMonjaDBCore.action.MAbstractAction;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class MShowAllDbStatsAction extends MAbstractAction {
	private List statsList = new ArrayList();

	public MShowAllDbStatsAction() {
	}

	public boolean parse(String action) {
		return action.equals("mj show all db stats");
	}

	public void executeFunction() throws Exception {
		Mongo mongo = dataManager.getMongo();
		List dbNameList = mongo.getDatabaseNames();
		for (int i = 0; i < dbNameList.size(); ++i) {
			String dbName = (String) dbNameList.get(i);
			DB db = mongo.getDB(dbName);
			statsList.add(db.getStats().toMap());
		}
	}

	public List getStatsList() {
		return statsList;
	}

	public int getActionCondition() {
		return action_cond_connected;
	}

	public String getEventName() {
		return event_mj_all_db_stats;
	}

}
