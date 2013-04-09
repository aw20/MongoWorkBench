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
package net.jumperz.app.MMonjaDBCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.jumperz.app.MMonjaDBCore.action.MActionManager;
import net.jumperz.mongo.MMongoUtil;
import net.jumperz.util.MHistory;
import net.jumperz.util.MObserver2;
import net.jumperz.util.MProperties;
import net.jumperz.util.MThreadPool;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.Mongo;

//import net.jumperz.app.MMonjaDBCore.exception.MConnectedToWrongHostException;

public class MDataManager extends MAbstractLogAgent implements MObserver2, MConstants {
	private static MDataManager instance = new MDataManager();

	private volatile Mongo mongo;

	private volatile DB db;

	private volatile String collName = "";

	// private volatile List dbNameList;
	private volatile List documentDataList;

	private volatile Map documentDataMap;

	private volatile List columnNameList;

	private volatile Object document;

	// private volatile String lastFindActionString;
	private volatile String lastEditActionString;


	private int sortOrder = sort_order_default;

	private MThreadPool threadPool = new MThreadPool(5);

	private MThreadPool actionThreadPool = new MThreadPool(1);

	private MProperties prop;

	private boolean numberIntEnabled = false;

	private MHistory findHistory = new MHistory();

	// private int batchSize = default_batch_size;
	private int maxFindResults = default_max_results;


	// copy paste
	private volatile List copiedDocumentList = new ArrayList();

	private volatile String copiedCollName;

	
	public String getCopiedCollName() {
		return copiedCollName;
	}

	
	public void setCopiedCollName(String copiedCollName) {
		this.copiedCollName = copiedCollName;
	}

	
	public List getCopiedDocumentList() {
		return copiedDocumentList;
	}

	public void setCopiedDocumentList(List copiedDocumentList) {
		this.copiedDocumentList = copiedDocumentList;
	}

	public void setProp(MProperties p) {
		prop = p;
	}

	
	
	public String getCollName() {
		return collName;
	}

	
	public int getMaxResults() {
		return maxFindResults;
	}

	
	public int getSortOrder() {
		// sort order
		if (sortOrder == sort_order_asc) {
			sortOrder = sort_order_desc;
		} else {
			sortOrder = sort_order_asc;
		}
		return sortOrder;
	}

	
	public List getColumnNameList() {
		return columnNameList;
	}

	
	public List getDocumentDataList() {
		return documentDataList;
	}

	public Map getDocumentDataMap() {
		return documentDataMap;
	}

	
	public MProperties getProp() {
		return prop;
	}

	
	public static MDataManager getInstance() {
		return instance;
	}

	
	public MThreadPool getActionThreadPool() {
		return actionThreadPool;
	}

	
	public boolean isConnected() {
		return mongo != null;
	}

	
	public synchronized DB getDB() {
		return db;
	}

	
	public synchronized Mongo getMongo() {
		return mongo;
	}

	
	public synchronized void setDB(DB db) {
		this.db = db;
	}

	
	private MDataManager() {
		instance = this;
	}

	
	public MThreadPool getThreadPool() {
		return threadPool;
	}

	
	public boolean numberIntEnabled() {
		return numberIntEnabled;
	}

	public void stopThreadPools() {
		threadPool.slowStop();
		actionThreadPool.slowStop();
	}

	
	public String getLastEditActionString() {
		return lastEditActionString;
	}

	
	public MHistory getFindHistory() {
		return findHistory;
	}

	
	public Object getLastEditedDocument() {
		return document;
	}


	public void updateDocument(Object _id, String editingFieldName, Object newValue) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		BasicDBObject update = new BasicDBObject("$set", new BasicDBObject(editingFieldName, newValue));

		String updateStr = null;
		if (newValue instanceof Integer) {
			if (!numberIntEnabled()) {
				int intValue = ((Integer) newValue).intValue();
				updateIntField(_id.toString(), editingFieldName, intValue);
				return;
			} else {
				updateStr = "{ \"$set\" : { \"" + editingFieldName + "\" : NumberInt( " + newValue + " ) } }";
			}
		} else {
			updateStr = MMongoUtil.toJson(getDB(), update);
		}

		String collName = getCollName();

		MActionManager.getInstance().executeAction("db." + collName + ".update(" + MMongoUtil.toJson(getDB(), query) + "," + updateStr + ",false, false )");

		reloadDocument();
	}

	
	public void updateIntField(String oidStr, String fieldName, int value) {
		MActionManager.getInstance().executeAction("mj update int field " + getCollName() + " " + oidStr + " " + fieldName + " " + value);
		reloadDocument();
	}

	
	public void setMaxFindResults(int i) {
		maxFindResults = i;
	}

	
	public void reloadDocument() {
	}

	
	public boolean hasPrevItems() {
		return true;
	}

	
	public boolean hasNextItems() {
		return false;
	}


	@Override
	public void update(Object event, Object source) {
		// TODO Auto-generated method stub
		
	}

	
}
