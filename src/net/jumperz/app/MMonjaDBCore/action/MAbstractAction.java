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
package net.jumperz.app.MMonjaDBCore.action;

import net.jumperz.app.MMonjaDBCore.MAbstractLogAgent;
import net.jumperz.app.MMonjaDBCore.MDataManager;
import net.jumperz.app.MMonjaDBCore.MInputView;
import net.jumperz.app.MMonjaDBCore.event.MEventManager;


public abstract class MAbstractAction extends MAbstractLogAgent implements MAction {
	protected MEventManager eventManager = MEventManager.getInstance();

	protected MDataManager dataManager = MDataManager.getInstance();

	public abstract void executeFunction() throws Exception;
	public abstract int getActionCondition();
	public abstract String getEventName();

	// protected Object context;
	private MInputView originView;
	private String		cmdStr = null;
	private String		message = null;
	private Exception	execException = null;
	
	public void setCmd( String _cmdstr ){
		this.cmdStr = _cmdstr;
	}
	
	public String getCmd(){
		return cmdStr;
	}
	
	public void setMessage(String m){
		message = m;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void setException(Exception e){
		execException = e;
	}
	
	public Exception getExecException(){
		return execException;
	}
	
	public final void setContextImpl(Object context) {}

	public void breakCommand() {
	}

	public final void execute() {
	}

	public MInputView getOriginView() {
		return originView;
	}

	public void setOriginView(MInputView v) {
		originView = v;
	}
}