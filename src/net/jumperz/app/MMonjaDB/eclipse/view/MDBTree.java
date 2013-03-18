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
 *  March 2013
 */
package net.jumperz.app.MMonjaDB.eclipse.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.jumperz.app.MMonjaDB.eclipse.MUtil;
import net.jumperz.app.MMonjaDB.eclipse.dialog.ServerDialog;
import net.jumperz.app.MMonjaDBCore.MDataManager;
import net.jumperz.app.MMonjaDBCore.MOutputView;
import net.jumperz.app.MMonjaDBCore.action.MActionManager;
import net.jumperz.app.MMonjaDBCore.action.MConnectAction;
import net.jumperz.app.MMonjaDBCore.action.MFindAction;
import net.jumperz.app.MMonjaDBCore.action.MShowCollectionAction;
import net.jumperz.app.MMonjaDBCore.action.MShowDBAction;
import net.jumperz.app.MMonjaDBCore.action.MUseAction;
import net.jumperz.app.MMonjaDBCore.event.MEvent;
import net.jumperz.app.MMonjaDBCore.event.MEventManager;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.mongodb.DB;

public class MDBTree extends MAbstractView implements MOutputView {
	private Tree tree;

	private Action editAction;
	private Action disconnectAction;
	private Action reloadAction;
	private Action createDbAction;
	private Action removeDbAction;
	
	private Image imageServer, imageDatabase, imageCollection;

	public MDBTree() {
		MEventManager.getInstance().register2(this);
	}

	public void dispose() {
		eventManager.removeObserver2(this);
		super.dispose();
	}

	protected void handleEvent2(Event event) {
		if (event.widget == tree) {
			switch (event.type) {
				case SWT.KeyDown:
					break;
				case SWT.Selection:
					onTreeItemSelect();
					break;
			}
		}
	}

	private void onTreeItemSelect() {
		TreeItem[] selected = tree.getSelection();
		if (selected != null && selected.length == 1) {
			TreeItem selectedItem = selected[0];
			Map data = (Map) selectedItem.getData();
			String treeType = (String) data.get(data_type);
			if (treeType.equals(data_type_mongo)) {
				executeAction("show dbs");
			} else if (treeType.equals(data_type_db)) {
				if (isActive()) {
					String dbName = selectedItem.getText();
					executeAction("use " + dbName);
				}
				executeAction("show collections");
			} else if (treeType.equals(data_type_collection)) {
				if (isActive()) {
					String collName = selectedItem.getText();
					TreeItem dbItem = selectedItem.getParentItem();
					String dbName = dbItem.getText();
					if (!MDataManager.getInstance().getDB().getName().equals(dbName)) {
						executeAction("use " + dbName);
						executeAction("show collections");
					}
					actionManager.executeAction("db." + collName + ".find()");
				}
			}
		}
		updateGui();
	}

	private void onConnect(final MConnectAction ca) {
		drawRootItem(ca);

		actionManager.executeAction("show dbs");

		String dbName = dataManager.getDB().getName();
		java.util.List dbNameList = new ArrayList();
		dbNameList.add(dbName);
		drawDbItems(dbNameList);

		updateGui();
		// actionManager.executeAction( "use " + dbName );

	}

	private void updateGui() {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				boolean hasItem = false;
				if (tree.getItemCount() > 0) {
					hasItem = true;
				}
				reloadAction.setEnabled(hasItem);
			}
		});
	}

	private void drawRootItem(final MConnectAction ca) {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (tree.getItemCount() == 0) {
					TreeItem item = new TreeItem(tree, SWT.NONE);
					item.setText(ca.getName());
					item.setImage(imageServer);
					Map data = new HashMap();
					data.put(data_type, data_type_mongo);
					item.setData(data);

					editAction.setEnabled(false);
					disconnectAction.setEnabled(true);
				}
			}
		});
	}

	private boolean needUpdate(TreeItem parentItem, java.util.List dbNameList) {
		if (parentItem == null) {
			return false;
		}

		TreeItem[] dbItems = parentItem.getItems();
		if (dbItems.length != dbNameList.size()) {
			return true;
		}

		for (int i = 0; i < dbNameList.size(); ++i) {
			if (!dbNameList.get(i).equals(dbItems[i].getText())) {
				return true;
			}
		}

		return false;
	}

	private void drawDbItems(final java.util.List dbNameList) {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				
				try {
					TreeItem mongoItem = tree.getItem(0);
					if (needUpdate(mongoItem, dbNameList)) {
						mongoItem.removeAll();
						for (int i = 0; i < dbNameList.size(); ++i) {
							createDbTreeItem(mongoItem, (String) dbNameList.get(i));
						}

						DB db = MDataManager.getInstance().getDB();
						if (db != null) {
							selectDbItem(db.getName());
						}
					} else {
						debug("no need to update");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					MEventManager.getInstance().fireErrorEvent(e);
				}

			}
		});
	}

	private synchronized void onShowDbs(MShowDBAction action) {
		// add tree items on the swt thread
		final java.util.List dbNameList = action.getDBList();
		drawDbItems(dbNameList);
	}

	private TreeItem createDbTreeItem(TreeItem mongoItem, String dbName) {
		TreeItem item = new TreeItem(mongoItem, 0);
		item.setText(dbName);
		item.setImage(imageDatabase);

		Map data = new HashMap();
		data.put(data_type, data_type_db);
		item.setData(data);

		tree.showItem(item);
		return item;
	}

	private synchronized void onShowCollections(MShowCollectionAction action) {
		final String activeDbName = MDataManager.getInstance().getDB().getName();


		// add tree items on the swt thread
		final java.util.List collNameList = new ArrayList(action.getCollSet());

		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					TreeItem parentItem = MUtil.getTreeItemByDbName(tree, activeDbName);
					if (needUpdate(parentItem, collNameList)) {
						parentItem.removeAll();
						for (int i = 0; i < collNameList.size(); ++i) {
							String dbName = (String) collNameList.get(i);
							TreeItem item = new TreeItem(parentItem, 0);
							item.setText(dbName);
							tree.showItem(item);
							item.setImage(imageCollection);

							Map data = new HashMap();
							data.put(data_type, data_type_collection);
							item.setData(data);
						}
					} else {
						debug("no need to update");
					}
				} catch (Exception e) {
					e.printStackTrace();
					MEventManager.getInstance().fireErrorEvent(e);
				}

			}
		});
		
	}

	private void selectDbItem(String dbName) {

		TreeItem[] selectedItems = tree.getSelection();
		if (selectedItems != null && selectedItems.length == 1) {
			Map map = (Map) selectedItems[0].getData();
			if (map.containsKey(data_type)) {
				if (map.get(data_type).equals(data_type_collection)) {
					if (selectedItems[0].getParentItem().getText().equals(dbName)) {
						// collection in the db is selected. nothing to do.
						return;
					}
				}
			}
		}

		TreeItem dbItem = MUtil.getTreeItemByDbName(tree, dbName);
		if (dbItem == null) {
			TreeItem mongoItem = tree.getItem(0);
			if (mongoItem != null) {
				dbItem = createDbTreeItem(mongoItem, dbName);
				// tree.setSelection( item );
			}
		}

		if (!MUtil.treeItemSelected(tree, dbItem)) {
			tree.select(dbItem);
			onTreeItemSelect();// TODO: :p
		}
	}

	private void selectItem(String dbName, String collName) {
		TreeItem item = MUtil.getTreeItemByDbAndCollName(tree, dbName, collName);
		if (item == null) {
			return;
		}
		if (!MUtil.treeItemSelected(tree, item)) {
			tree.select(item);
			// onTreeItemSelect();//TODO: :p
		}
	}

	private void onUse(MUseAction action) {
		if (action.getOriginView() == this) {
			return;
		}

		final String dbName = action.getDBName();

		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {// *****

				selectDbItem(dbName);

			}
		});// *****
	}

	private void onFind(MFindAction action) {
		final String dbName = action.getDB().getName();
		final String collName = action.getCollection().getName();

		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {// *****

				selectItem(dbName, collName);

			}
		});// *****
	}

	private void onDisconnect() {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {// *****

				tree.removeAll();
				disconnectAction.setEnabled(false);

				updateGui();
			}
		});// *****

	}

	public void update(final Object e, final Object source) {
		final MEvent event = (MEvent) e;
		final String eventName = event.getEventName();
		if (eventName.indexOf(event_connect + "_end") == 0) {
			// draw tree item
			MConnectAction ca = (MConnectAction) source;
			onConnect(ca);
		} else if (eventName.indexOf(event_showdbs + "_end") == 0) {
			MShowDBAction action = (MShowDBAction) source;
			onShowDbs(action);
		} else if (eventName.indexOf(event_showcollections + "_end") == 0) {
			MShowCollectionAction action = (MShowCollectionAction) source;
			onShowCollections(action);
		} else if (eventName.indexOf(event_use + "_end") == 0) {
			MUseAction action = (MUseAction) source;
			onUse(action);
		} else if (event.getEventName().indexOf(event_find + "_end") == 0) {
			MFindAction action = (MFindAction) source;
			onFind(action);
		} else if (event.getEventName().indexOf(event_disconnect + "_end") == 0) {
			onDisconnect();
		}
	}

	public void init2() {
		imageServer			= MUtil.getImage(parent.getShell().getDisplay(), "server.png");
		imageDatabase 	= MUtil.getImage(parent.getShell().getDisplay(), "database.png");
		imageCollection	= MUtil.getImage(parent.getShell().getDisplay(), "table_multiple.png");
		
		parent.setLayout(formLayout);

		tree = new Tree(parent, SWT.BORDER);
		
		FormData d1 = new FormData();
		d1.top = new FormAttachment(0, 1);
		d1.left = new FormAttachment(0, 1);
		d1.right = new FormAttachment(100, -1);
		d1.bottom = new FormAttachment(100, -1);
		tree.setLayoutData(d1);

		menuManager = new MenuManager();
		Menu contextMenu = menuManager.createContextMenu(tree);
		tree.setMenu(contextMenu);

		final MDBTree dbTree = this;

		editAction = new Action() {
			public void run() {// -----------
				dbTree.onConnectSelect();
			}
		};// -----------
		editAction.setToolTipText("Add/Edit MongoDB Connection");
		editAction.setText("Add/Edit");
		editAction.setEnabled(true);

		disconnectAction = new Action() {
			public void run() {
				dbTree.onDisconnectSelect();
			}
		};
		disconnectAction.setToolTipText("Disconnect from MongoDB");
		disconnectAction.setText("Disconnect");

		initAction(editAction, "server_lightning.png", menuManager);
		initAction(disconnectAction, "server_delete.png", menuManager);

		disconnectAction.setEnabled(false);

		reloadAction = new Action() {
			public void run() {
				executeAction("show dbs");
			}
		};
		reloadAction.setToolTipText("Reload Databases ( show dbs  )");
		reloadAction.setText("Reload Databases");
		initAction(reloadAction, "arrow_refresh.png", menuManager);
		reloadAction.setEnabled(false);

		// listeners
		tree.addListener(SWT.MouseDoubleClick, this);
		tree.addListener(SWT.Selection, this);
		tree.addListener(SWT.KeyDown, this);

		if (dataManager.isConnected()) {
			onConnect(dataManager.getConnectAction());
		}
	}

	public void onDisconnectSelect() {
		MActionManager.getInstance().executeAction("mj disconnect");
	}

	public void onConnectSelect() {
		Map serverProps	= new HashMap();
		
		ServerDialog	serverDialog	= new ServerDialog(parent.getShell());
		
		Object result = serverDialog.open(serverProps);
		if ( (result instanceof Boolean) && (boolean)result ){
			Map	newProps	= serverDialog.getAttributes();
			
			System.out.println( newProps );
			
		}
		
	}
}
