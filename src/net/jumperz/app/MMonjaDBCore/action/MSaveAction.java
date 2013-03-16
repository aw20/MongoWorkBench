package net.jumperz.app.MMonjaDBCore.action;

import net.jumperz.app.MMonjaDBCore.MDataManager;
import net.jumperz.app.MMonjaDBCore.event.MEventManager;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;

public class MSaveAction extends MAbstractAction {
	private String action;

	private DB db;


	private DBCollection coll;

	public MSaveAction() {
	}

	public String getEventName() {
		return event_save;
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

	// -----------------------------------------------------------------------------
	public void executeFunction() throws Exception {
		db = MDataManager.getInstance().getDB();

		try {
			db.eval(action, (Object[]) null);
		} catch (MongoException e) {
			MEventManager.getInstance().fireErrorEvent(e);
		}
	}

}
