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
package net.jumperz.app.MMonjaDB.eclipse;

import net.jumperz.app.MMonjaDB.eclipse.dialog.MConnectDialog;
import net.jumperz.app.MMonjaDBCore.MAbstractLogAgent;
import net.jumperz.app.MMonjaDBCore.MConstants;
import net.jumperz.app.MMonjaDBCore.MDataManager;
import net.jumperz.app.MMonjaDBCore.action.MActionManager;
import net.jumperz.app.MMonjaDBCore.event.MEvent;
import net.jumperz.app.MMonjaDBCore.event.MEventManager;
import net.jumperz.gui.MSwtUtil;
import net.jumperz.util.MObserver2;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class MMenuManager extends MAbstractLogAgent implements MConstants, Listener, MObserver2 {
	private static MMenuManager instance = new MMenuManager();

	MenuItem CascadeItem = null;
	MenuItem connectItem, disconnectItem, prefItem;
	Menu DropMenu, bar;

	public static MMenuManager getInstance() {
		return instance;
	}

	private MMenuManager() {
		MEventManager.getInstance().register2(this);
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

		connectItem = new MenuItem(DropMenu, SWT.PUSH);
		connectItem.setText("&Connect");

		disconnectItem = new MenuItem(DropMenu, SWT.PUSH);
		disconnectItem.setText("&Disconnect");
		disconnectItem.setEnabled(false);

		new MenuItem(DropMenu, SWT.SEPARATOR);

		prefItem = new MenuItem(DropMenu, SWT.PUSH);
		prefItem.setText("Preferen&ces");

		MSwtUtil.addListenerToMenuItems(DropMenu, this);

	}

	public void handleEvent(Event event) {
		if (event.type == SWT.Selection) {
			if (event.widget == connectItem) {
				(new MConnectDialog(Activator.getDefault().getShell())).open();
			} else if (event.widget == disconnectItem) {
				MActionManager.getInstance().executeAction("mj disconnect");
			} else if (event.widget == prefItem) {
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, "net.jumperz.app.MMonjaDB.eclipse.pref.MPrefPage", null, null);
				dialog.open();
			}
		}
	}

	private void updateMenu() {
		final boolean isConnected = MDataManager.getInstance().isConnected();

		Activator.getDefault().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {

				connectItem.setEnabled(!isConnected);
				disconnectItem.setEnabled(isConnected);

			}
		});
	}

	public void update(final Object e, final Object source) {
		final MEvent event = (MEvent) e;
		final String eventName = event.getEventName();
		if (eventName.indexOf(event_connect + "_end") == 0) {
			updateMenu();
		} else if (event.getEventName().indexOf(event_disconnect + "_end") == 0) {
			updateMenu();
		}
	}

}