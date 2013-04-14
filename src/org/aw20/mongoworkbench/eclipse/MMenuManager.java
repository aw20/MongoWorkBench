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
package org.aw20.mongoworkbench.eclipse;


import org.aw20.util.MSwtUtil;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class MMenuManager implements MConstants, Listener {
	private static MMenuManager instance = new MMenuManager();

	MenuItem CascadeItem = null;
	MenuItem prefItem;
	Menu DropMenu, bar;

	public static MMenuManager getInstance() {
		return instance;
	}

	private MMenuManager() {
	}

	public void initMenus() {
		Shell shell = Activator.getDefault().getShell();
		bar = shell.getMenuBar();
		MenuItem[] items = bar.getItems();

		for (int i = 0; i < items.length; ++i) {
			MenuItem item = items[i];
			if (item.getText().indexOf("MongoWorkBench") > -1) {
				CascadeItem = item;
			}
		}

		DropMenu = new Menu(shell, SWT.DROP_DOWN);
		CascadeItem.setMenu(DropMenu);

		prefItem = new MenuItem(DropMenu, SWT.PUSH);
		prefItem.setText("Preferen&ces");

		MSwtUtil.addListenerToMenuItems(DropMenu, this);
	}

	public void handleEvent(Event event) {
		if (event.type == SWT.Selection) {
			if (event.widget == prefItem) {
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, "org.aw20.mongoworkbench.eclipse.pref.MPrefPage", null, null);
				dialog.open();
			}
		}
	}

}