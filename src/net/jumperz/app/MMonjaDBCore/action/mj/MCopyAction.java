package net.jumperz.app.MMonjaDBCore.action.mj;

import net.jumperz.app.MMonjaDBCore.action.MAbstractAction;

public class MCopyAction extends MAbstractAction {
	// --------------------------------------------------------------------------------
	public MCopyAction() {
	}

	// --------------------------------------------------------------------------------
	public boolean parse(String action) {
		return action.matches("^mj copy$");
	}

	// --------------------------------------------------------------------------------
	public void executeFunction() throws Exception {
	}

	// --------------------------------------------------------------------------------
	public int getActionCondition() {
		return action_cond_collection;
	}

	// --------------------------------------------------------------------------------
	public String getEventName() {
		return event_mj_copy;
	}
	// --------------------------------------------------------------------------------
}