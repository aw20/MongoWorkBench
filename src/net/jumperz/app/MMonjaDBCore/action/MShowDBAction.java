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
package net.jumperz.app.MMonjaDBCore.action;

import java.util.List;

import net.jumperz.util.MRegEx;

public class MShowDBAction extends MAbstractAction {
	private List dbList;

	public MShowDBAction() {
	}

	public boolean parse(String action) {
		return MRegEx.containsIgnoreCase(action, "^show\\s+dbs$");
	}

	public String getEventName() {
		return event_showdbs;
	}

	public int getActionCondition() {
		return action_cond_connected;
	}

	public void executeFunction() throws Exception {
		dbList = dataManager.getMongo().getDatabaseNames();
		setMessage("databases=" + dbList.size() );
	}

	public List getDBList() {
		return dbList;
	}

}