package net.jumperz.app.MMonjaDBCore.action;

import net.jumperz.app.MMonjaDBCore.MDataManager;

import com.mongodb.DB;
import com.mongodb.DBCollection;

public class MUpdateAction extends MAbstractAction {
	private String action;

	private DB db;
	
	private DBCollection coll;

	// --------------------------------------------------------------------------------
	public MUpdateAction() {
	}

	// --------------------------------------------------------------------------------
	public String getEventName() {
		return event_update;
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
			// MMongoUtil.getListFromAction( MMongoUtil.getArgStrFromAction( action, "update" ), "update" );
			return true;
		} catch (Exception e) {
			debug(e);
			return false;
		}
	}

	// -----------------------------------------------------------------------------
	public void executeFunction() throws Exception {
		db = MDataManager.getInstance().getDB();
		db.eval(action, (Object[])null);
	}
	// --------------------------------------------------------------------------------
}
