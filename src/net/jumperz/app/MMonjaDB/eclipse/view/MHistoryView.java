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
package net.jumperz.app.MMonjaDB.eclipse.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.jumperz.app.MMonjaDBCore.MOutputView;
import net.jumperz.app.MMonjaDBCore.action.MActionManager;
import net.jumperz.app.MMonjaDBCore.event.MEvent;
import net.jumperz.app.MMonjaDBCore.event.MEventManager;
import net.jumperz.gui.MSwtUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class MHistoryView extends MAbstractView implements MOutputView {

	public MHistoryView() {
		actionManager.register2(this);
	}

	protected FormLayout formLayout;

	private Table table;
	private Action redoAction;
	private Action clearAction;
	private Action copyAction;
	private Action saveAction;
	private List actionLogList;

	public void dispose() {
		actionManager.removeObserver2(this);

		if (actionLogList.size() > MAX_SAVED_ACTION_LOG) {
			actionLogList = actionLogList.subList(0, MAX_SAVED_ACTION_LOG);
		}
		String savedStr = JSON.serialize(actionLogList);
		prop.setProperty(ACTION_LOG_LIST, savedStr);

		super.dispose();
	}

	public void init2() {
		parent.setLayout(new FormLayout());

		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					e.doit = false;
					if ((e.stateMask & SWT.SHIFT) != 0)// Shift + Enter
					{
						repeatActionsOnTable();
					} else {

					}
				}
			}
		});

		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(0, 0);
		fd_table.bottom = new FormAttachment(100, 0);
		fd_table.left = new FormAttachment(0, 0);
		fd_table.right = new FormAttachment(100, 0);

		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn actionColumn = new TableColumn(table, SWT.NONE);
		actionColumn.setWidth(100);
		actionColumn.setText("Action");
		TableColumn dateColumn = new TableColumn(table, SWT.NONE);
		dateColumn.setWidth(100);
		dateColumn.setText("Date");

		FormData fd_text = new FormData();
		fd_text.bottom = new FormAttachment(100, -40);
		fd_text.right = new FormAttachment(100);
		fd_text.top = new FormAttachment(0);
		fd_text.left = new FormAttachment(0);

		MSwtUtil.getTableColumnWidthFromProperties("actionListTable", table, prop, new int[] { 200, 100 });
		MSwtUtil.addListenerToTableColumns2(table, this);

		// table.addListener( SWT.KeyDown, this );
		table.addListener(SWT.MouseDoubleClick, this);

		menuManager = new MenuManager();
		Menu contextMenu = menuManager.createContextMenu(table);
		table.setMenu(contextMenu);

		// executeTableAction
		redoAction = new Action() {
			public void run() {
				repeatActionsOnTable();
			}
		};
		redoAction.setToolTipText("Redo Selected Actions");
		redoAction.setText("Redo\tShift+Enter");
		initAction(redoAction, "table_go.png", menuManager);
		redoAction.setEnabled(false);

		dropDownMenu.add(new Separator());
		menuManager.add(new Separator());

		// copyAction
		copyAction = new Action() {
			public void run() {// ------------
				copyActions();
			}
		};// ------------
		copyAction.setToolTipText("Copy Actions to Clipboard");
		copyAction.setText("Copy");
		setActionImage(copyAction, "page_copy.png");
		addActionToToolBar(copyAction);
		copyAction.setEnabled(false);
		dropDownMenu.add(copyAction);
		menuManager.add(copyAction);

		dropDownMenu.add(new Separator());
		menuManager.add(new Separator());

		// clearAction
		clearAction = new Action() {
			public void run() {// ------------
				clearActions();
			}
		};// ------------
		clearAction.setToolTipText("Clear All");
		clearAction.setText("Clear All");
		initAction(clearAction, "table_delete.png", menuManager);
		clearAction.setEnabled(false);

		// saveAction
		saveAction = new Action() {
			public void run() {// ------------
				saveActions();
			}
		};// ------------
		saveAction.setToolTipText("Save Action");
		saveAction.setText("Save");
		initAction(saveAction, "cog_add.png", menuManager);
		saveAction.setEnabled(false);

		// load actionLogList
		if (prop.containsKey(ACTION_LOG_LIST)) {
			String savedStr = prop.getProperty(ACTION_LOG_LIST);
			actionLogList = (java.util.List) JSON.parse(savedStr);
			for (int i = 0; i < actionLogList.size(); ++i) {
				Map actionLog = (Map) actionLogList.get(i);
				addActionToTable(actionLog);
			}
		} else {
			actionLogList = new LinkedList();
		}
	}

	private void saveActions() {
		// activate MSavedActionsView
		try {
			MSavedActionsView savedActionsView = (MSavedActionsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("net.jumperz.app.MMonjaDB.eclipse.view.MSavedActionsView");
			savedActionsView.setFocus();
		} catch (Exception e) {
			MEventManager.getInstance().fireErrorEvent(e);
			return;
		}

		MEvent e = new MEvent(event_save_actions);
		Map data = new HashMap();

		StringBuffer buf = new StringBuffer();

		TableItem[] items = table.getSelection();
		if (items != null) {
			for (int i = 0; i < items.length; ++i) {
				Map actionLog = (Map) items[i].getData();
				if (actionLog != null) {
					if (buf.length() > 0) {
						buf.append("\r\n");
					}
					buf.append(actionLog.get("actionStr"));
				}
			}
		}

		String actions = buf.toString();
		if (actions.length() > 0) {
			data.put("actions", actions);
			e.setData(data);
			eventManager.fireEvent(e);
		}
	}

	private void copyActions() {
		// copy selected actions on the table to clipboard
		TableItem[] selectedItems = table.getSelection();
		StringBuffer buf = new StringBuffer(1024);
		for (int i = 0; i < selectedItems.length; ++i) {
			buf.append(selectedItems[i].getText(0));
			buf.append("\r\n");
		}
		MSwtUtil.copyToClipboard(buf.toString());
	}

	private void clearActions() {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				table.removeAll();
				actionLogList.clear();
			}
		});
	}

	private void repeatActionsOnTable() {
		final MHistoryView actionLogView = this;
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {// *****
				TableItem[] items = table.getSelection();
				if (items != null) {
					for (int i = 0; i < items.length; ++i) {
						Map actionLog = (Map) items[i].getData();
						if (actionLog != null) {
							MActionManager.getInstance().executeAction(actionLog.get("actionStr") + "", actionLogView);
						}
					}
				}
			}
		});// ********
	}

	private void onTableDoubleClick(Event event) {
		// repeatActionsOnTable();
	}

	private void onTableColumnSelect(TableColumn column) {

	}

	private void onTableColumnResize() {
		MSwtUtil.setTableColumnWidthToProperties("actionListTable", table, prop);
	}

	protected void handleEvent2(Event event) {
		if (event.widget == table) {
			switch (event.type) {
				case SWT.MouseDoubleClick:
					onTableDoubleClick(event);
					break;
			}
		} else if (MSwtUtil.getTableColumns(table).contains(event.widget)) {
			switch (event.type) {
				case SWT.Selection:
					onTableColumnSelect((TableColumn) event.widget);
					break;
				case SWT.Resize:
					onTableColumnResize();
					break;
			}
		}
	}

	private void addActionToTable(Map actionLog) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, (String) actionLog.get("actionStr"));
		item.setText(1, actionLog.get("t") + "");
		table.showItem(item);
		item.setData(actionLog);
	}

	private void onAction(final String actionStr) {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {// *****

				java.util.Date now = new java.util.Date();

				BasicDBObject actionLog = new BasicDBObject();
				actionLog.put("actionStr", actionStr);
				actionLog.put("t", now);
				addActionToTable(actionLog);

				actionLogList.add(actionLog);

			}
		});// ********
	}

	public void update(final Object e, final Object source) {
		if (e instanceof String) {
			onAction((String) e);
		} else if (e instanceof MEvent) {
			debug("====" + e);
		}
	}

	public void setFocus() {

	}

}
