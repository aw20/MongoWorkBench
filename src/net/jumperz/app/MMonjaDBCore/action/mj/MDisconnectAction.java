package net.jumperz.app.MMonjaDBCore.action.mj;

import net.jumperz.app.MMonjaDBCore.action.MAbstractAction;

public class MDisconnectAction extends MAbstractAction {
	// --------------------------------------------------------------------------------
	public String getEventName() {
		return event_disconnect;
	}

	// --------------------------------------------------------------------------------
	public int getActionCondition() {
		return action_cond_connected;
	}

	// --------------------------------------------------------------------------------
	public boolean parse(String action) {
		if (action.indexOf("mj disconnect") == -1) {
			return false;
		}
		return true;
	}

	// --------------------------------------------------------------------------------
	public void executeFunction() throws Exception {
		// MDataManager.getInstance().getMongo().close();
	}
	// --------------------------------------------------------------------------------
}
