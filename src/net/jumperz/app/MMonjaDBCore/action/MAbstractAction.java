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

import net.jumperz.app.MMonjaDBCore.*;
import net.jumperz.app.MMonjaDBCore.event.*;


public abstract class MAbstractAction extends MAbstractLogAgent implements MAction {
	protected MEventManager eventManager = MEventManager.getInstance();

	protected MDataManager dataManager = MDataManager.getInstance();

	public abstract void executeFunction() throws Exception;

	public abstract int getActionCondition();

	public abstract String getEventName();

	// protected Object context;
	private MInputView originView;

	
	public final void setContextImpl(Object context) {
	}

	public void breakCommand() {
	}

	public final void execute() {
		try {
			if (checkCondition()) {
				eventManager.fireEvent(new MEvent(getEventName() + "_start"), this);
				executeFunction();
				eventManager.fireEvent(new MEvent(getEventName() + "_end"), this);
			}
		} catch (Exception e) {
			eventManager.fireErrorEvent(e, this);
		}
	}

	public MInputView getOriginView() {
		return originView;
	}

	public void setOriginView(MInputView v) {
		originView = v;
	}

	private boolean checkCondition() throws Exception {
		if (getActionCondition() == action_cond_none) {

		} else if (getActionCondition() == action_cond_not_connected_or_connected_to_different_host) {
			if (dataManager.isConnected()) {
				if (dataManager.connectedToSameHost(this)) {
					return false;
				}
			}
		} else if (getActionCondition() == action_cond_connected) {
			if (!dataManager.isConnected()) {
				throw new Exception("Not connected to MongoDB.");
			}
		} else if (getActionCondition() == action_cond_db) {
			if (dataManager.getDB() == null) {
				throw new Exception("Database is not choosed.");
			}
		} else if (getActionCondition() == action_cond_collection) {
			if (dataManager.getDocumentDataList() == null) {
				throw new Exception("collection is not choosed.");
			}
		}
		return true;
	}

}