package net.jumperz.app.MMonjaDBCore.action.mj;

import net.jumperz.app.MMonjaDBCore.action.MAbstractAction;

public class MPasteAction extends MAbstractAction {
	public MPasteAction() {
	}

	public boolean parse(String action) {
		return action.matches("^mj paste$");
	}

	public void executeFunction() throws Exception {
	}

	public int getActionCondition() {
		return action_cond_collection;
	}

	public String getEventName() {
		return event_mj_paste;
	}
}