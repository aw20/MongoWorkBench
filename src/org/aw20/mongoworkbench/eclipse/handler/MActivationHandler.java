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
 *  https://github.com/aw20/MonjaDB
 *  Original fork: https://github.com/Kanatoko/MonjaDB
 *  
 */
package org.aw20.mongoworkbench.eclipse.handler;

import org.eclipse.core.commands.*;
import org.eclipse.ui.*;

public class MActivationHandler extends AbstractHandler {
	private boolean enabled = true;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow[] windowArray = wb.getWorkbenchWindows();
		if (windowArray.length > 0) {
			IWorkbenchWindow window = windowArray[0];
			try {
				wb.showPerspective("org.aw20.mongoworkbench.eclipse.MPerspectiveFactory1", window);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
