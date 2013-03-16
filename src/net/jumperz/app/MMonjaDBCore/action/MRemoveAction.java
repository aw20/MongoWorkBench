package net.jumperz.app.MMonjaDBCore.action;

import net.jumperz.app.MMonjaDBCore.MDataManager;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

public class MRemoveAction extends MAbstractAction {
	private String action;

	private DB db;

	private DBCollection coll;

	private WriteResult writeResult;

	// --------------------------------------------------------------------------------
	public MRemoveAction() {
	}

	// --------------------------------------------------------------------------------
	public WriteResult getResult() {
		return writeResult;
	}

	// --------------------------------------------------------------------------------
	public String getEventName() {
		return event_remove;
	}

	// --------------------------------------------------------------------------------
	public int getActionCondition() {
		return action_cond_connected;
	}

	// --------------------------------------------------------------------------------
	public DBCollection getCollection() {
		return coll;
	}

	// --------------------------------------------------------------------------------
	public DB getDB() {
		return db;
	}

	// --------------------------------------------------------------------------------
	public boolean parse(String action) {
		try {
			this.action = action;
			return true;
		} catch (Exception e) {
			debug(e);
			return false;
		}
	}

	// -----------------------------------------------------------------------------
	public void executeFunction() throws Exception {
		db = MDataManager.getInstance().getDB();
		db.eval(action, (Object[]) null);
	}
	// --------------------------------------------------------------------------------
}
