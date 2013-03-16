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

import net.jumperz.app.MMonjaDBCore.action.MAbstractAction;

public class MNextItemsAction extends MAbstractAction {

	public MNextItemsAction() {
	}

	public boolean parse(String action) {
		return action.matches("^mj next items$");
	}

	public void executeFunction() throws Exception {
	}

	public int getActionCondition() {
		return action_cond_collection;
	}

	public String getEventName() {
		return event_mj_next_items;
	}

}