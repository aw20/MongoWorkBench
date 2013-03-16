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
package net.jumperz.app.MMonjaDBCore;

import java.util.List;

import net.jumperz.app.MMonjaDBCore.action.MConnectAction;
import net.jumperz.app.MMonjaDBCore.action.MShowCollectionAction;
import net.jumperz.app.MMonjaDBCore.action.MShowDBAction;
import net.jumperz.app.MMonjaDBCore.action.MUseAction;
import net.jumperz.app.MMonjaDBCore.event.MEvent;
import net.jumperz.util.MCommand;

public class MStdoutView extends MAbstractLogAgent implements MOutputView, MCommand {

	public void execute() {
		try {
			execute2();
		} catch (Exception e) {
			warn(e);
		}
	}

	public void update(final Object e, final Object source) {
		
		final MEvent event = (MEvent) e;
		if (event.getEventName().indexOf(event_error) == 0) {
			final Object error = event.getData().get("error");
			debug(error);
		} else if (event.getEventName().indexOf(event_connect + "_end") == 0) {
			MConnectAction action = (MConnectAction) source;
			debug("connected:" + action.getMongo());
		} else if (event.getEventName().indexOf(event_showcollections + "_end") == 0) {
			MShowCollectionAction action = (MShowCollectionAction) source;
			debug(action.getCollSet());
		} else if (event.getEventName().indexOf(event_showdbs + "_end") == 0) {
			MShowDBAction action = (MShowDBAction) source;
			debug(action.getDBList());
		} else if (event.getEventName().indexOf(event_use + "_end") == 0) {
			MUseAction action = (MUseAction) source;
			debug("switched to db " + action.getDBName());
		} else if (event.getEventName().indexOf(event_find + "_end") == 0) {
			if (MMonjaDB.invoked) {
				List l = MDataManager.getInstance().getDocumentDataList();
				for (int i = 0; i < l.size(); ++i) {
					debug(l.get(i));
				}
			}
		}
	}

	private void execute2() throws Exception {
	}

	public void breakCommand() {
	}

}