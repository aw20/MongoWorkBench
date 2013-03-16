package net.jumperz.app.MMonjaDBCore.action.mj;

import net.jumperz.app.MMonjaDBCore.action.MAbstractAction;
import net.jumperz.util.MRegEx;

public class MEditFieldAction extends MAbstractAction {
	private String fieldName;

	private String action;

	// --------------------------------------------------------------------------------
	public MEditFieldAction() {
	}

	// --------------------------------------------------------------------------------
	public String getAction() {
		return action;
	}

	// --------------------------------------------------------------------------------
	public String getFieldName() {
		return fieldName;
	}

	// --------------------------------------------------------------------------------
	public boolean parse(String action) {
		this.action = action;
		return action.matches("^mj edit field .*");
	}

	// --------------------------------------------------------------------------------
	public void executeFunction() throws Exception {
		fieldName = MRegEx.getMatch("mj edit field (.*)", action);
	}

	// --------------------------------------------------------------------------------
	public int getActionCondition() {
		return action_cond_collection;
	}

	// --------------------------------------------------------------------------------
	public String getEventName() {
		return event_mj_edit_field;
	}
	// --------------------------------------------------------------------------------
}