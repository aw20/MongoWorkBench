package net.jumperz.app.MMonjaDBCore.action.mj;

import net.jumperz.app.MMonjaDBCore.action.MAbstractAction;

public class MPrevItemsAction extends MAbstractAction {
	public MPrevItemsAction() {
	}

	public boolean parse(String action) {
		return action.matches("^mj prev items$");
	}

	public void executeFunction() throws Exception {
	}

	public int getActionCondition() {
		return action_cond_collection;
	}

	public String getEventName() {
		return event_mj_prev_items;
	}
}