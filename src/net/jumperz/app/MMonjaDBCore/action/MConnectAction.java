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

import net.jumperz.app.MMonjaDBCore.action.mj.MDisconnectAction;
import net.jumperz.util.MRegEx;
import net.jumperz.util.MStringUtil;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class MConnectAction extends MAbstractAction {
	protected String host;

	protected String dbName;
	protected int port;
	protected Mongo mongo;
	protected DB db;

	
	public String getEventName() {
		return event_connect;
	}

	
	public int getActionCondition() {
		return action_cond_not_connected_or_connected_to_different_host;
	}

	
	public String getName() {
		return host + ":" + port;
	}

	
	public boolean parse(String action) {
		if (action.indexOf("connect") == -1) {
			return false;
		}

		host = "127.0.0.1";
		dbName = null;
		port = 27017;

		String dbAddr = MRegEx.getMatch("^connect\\s+(.*)", action);
		if (dbAddr.indexOf('/') > -1) {
			String[] array = dbAddr.split("/");
			if (array.length != 2) {
				return false;
			}
			dbName = array[1];
			if (array[0].indexOf(':') > -1) {
				String[] array2 = array[0].split(":");
				if (array2.length != 2) {
					return false;
				}
				host = array2[0];
				port = MStringUtil.parseInt(array2[1], 27017);
			} else {
				host = array[0];
			}
		} else {
			dbName = dbAddr;
		}

		return true;
	}

	
	public boolean equals(Object o) {
		if (o.getClass().equals(this.getClass())) {
			MConnectAction c = (MConnectAction) o;
			if (this.host.equals(c.host) && this.port == c.port && this.dbName.equals(c.dbName)) {
				return true;
			}
		}
		return false;
	}

	
	public Mongo getMongo() {
		return mongo;
	}

	
	public DB getDB() {
		return db;
	}

	
	protected void checkExistingConnection() {
		if (dataManager.connectedToDifferentHost(this)) {
			(new MDisconnectAction()).execute();
		}
	}

	
	public void executeFunction() throws Exception {
		checkExistingConnection();
		mongo = new Mongo(host, port);
		db = mongo.getDB(dbName);
	}

	
	public void close() {
		mongo.close();
	}
	
}
