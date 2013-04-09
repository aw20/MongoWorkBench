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
 *  
 *  https://github.com/aw20/MongoWorkBench
 *  Original fork: https://github.com/Kanatoko/MonjaDB
 *  
 */
package net.jumperz.app.MMonjaDBCore.action;

import java.util.LinkedHashMap;
import java.util.Map;

import net.jumperz.app.MMonjaDBCore.MAbstractLogAgent;
import net.jumperz.app.MMonjaDBCore.MConstants;
import net.jumperz.app.MMonjaDBCore.MDataManager;
import net.jumperz.app.MMonjaDBCore.MInputView;
import net.jumperz.app.MMonjaDBCore.action.mj.MCopyAction;
import net.jumperz.app.MMonjaDBCore.action.mj.MPasteAction;
import net.jumperz.app.MMonjaDBCore.action.mj.MSortAction;
import net.jumperz.app.MMonjaDBCore.action.mj.MUpdateIntFieldAction;
import net.jumperz.util.MObserver2;
import net.jumperz.util.MSubject2;
import net.jumperz.util.MSubject2Impl;
import net.jumperz.util.MThreadPool;

public class MActionManager extends MAbstractLogAgent implements MConstants, MSubject2 {
	private static final MActionManager instance = new MActionManager();

	private Map actionMap;
	private MThreadPool threadPool = MDataManager.getInstance().getActionThreadPool();
	private MSubject2 subject2 = new MSubject2Impl();

	public static MActionManager getInstance() {
		return instance;
	}

	public void addAction(String regex, Class clazz) {
		actionMap.put(regex, clazz);
	}

	private MActionManager() {
		actionMap = new LinkedHashMap();
		actionMap.put("^show\\s+dbs", MShowDBAction.class);
		actionMap.put("^mj sort .*", MSortAction.class);
		actionMap.put("^mj update int field.*", MUpdateIntFieldAction.class);
		actionMap.put("^mj copy$", MCopyAction.class);
		actionMap.put("^mj paste$", MPasteAction.class);
	}

	public MAction getAction(String actionStr) {
		return null;
	}

	public void submitForExecution(MAction action, MInputView view){
		action.setOriginView(view);
		threadPool.addCommand(action);
	}
	
	public MAction executeAction(String actionStr, MInputView view) {
		return null;
	}

	public MAction executeAction(String actionStr) {
		return executeAction(actionStr, null);
	}

	public void notify2(Object event, Object source) {
		subject2.notify2(event, source);
	}

	public void register2(MObserver2 observer) {
		subject2.register2(observer);
	}

	public void removeObservers2() {
		subject2.removeObservers2();
	}

	public void removeObserver2(MObserver2 observer) {
		subject2.removeObserver2(observer);
	}
}