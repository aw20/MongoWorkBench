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

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.jumperz.app.MMonjaDB.eclipse.Activator;
import net.jumperz.app.MMonjaDB.eclipse.MUtil;
import net.jumperz.app.MMonjaDB.eclipse.dialog.ServerDialog;
import net.jumperz.app.MMonjaDBCore.action.MActionManager;
import net.jumperz.app.MMonjaDBCore.action.MFindAction;
import net.jumperz.app.MMonjaDBCore.action.MUseAction;

import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.command.ShowCollectionsMongoCommand;
import org.aw20.mongoworkbench.command.ShowDbsMongoCommand;
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

public class MDBTree extends MAbstractView implements MongoCommandListener {
	private Tree tree;

	private static 	String KEY_TYPE = "_datakey";
	private enum NodeType {
		SERVER,
		DATABASE,
		METADATA,
		COLLECTION,
		GRIDFS,
		INDEX
	};
	
	private Action editAction;
	private Action disconnectAction;
	private Action reloadAction;
	private Action createDbAction;
	private Action removeDbAction;
	
	private Image imageServer, imageDatabase, imageCollection, imageMetaFolder;
	private List<Map>		serverList;

	public MDBTree() {
		serverList	= Activator.getDefault().getServerList();
		
		Iterator<Map>	it	= serverList.iterator();
		while ( it.hasNext() ){
			try {
				MongoFactory.getInst().registerMongo( it.next() );
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		MongoFactory.getInst().registerListener(this);
	}

	public void dispose() {
		MongoFactory.getInst().deregisterListener(this);
		super.dispose();
	}

	protected void handleEvent2(Event event) {
		if (event.widget == tree) {
			switch (event.type) {
				case SWT.KeyDown:
					break;
				case SWT.MouseDoubleClick:
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
			NodeType nodeType = (NodeType)data.get(KEY_TYPE);
			
			if ( nodeType == NodeType.SERVER ) {
				
				String sName	= (String)data.get("name");
				MongoFactory.getInst().submitExecution( new ShowDbsMongoCommand().setConnection(sName) );
				
			} else if ( nodeType == NodeType.METADATA && selectedItem.getText().equals("Collections") ) {
				
				String sName	= (String)((Map)selectedItem.getParentItem().getParentItem().getData()).get("name");
				String sDb		=	selectedItem.getParentItem().getText();
				MongoFactory.getInst().setActiveDB(sDb);
				MongoFactory.getInst().submitExecution( new ShowCollectionsMongoCommand().setConnection(sName, sDb) );

			} else if ( nodeType == NodeType.COLLECTION ){
				
				String sName	= (String)((Map)selectedItem.getParentItem().getParentItem().getParentItem().getData()).get("name");
				String sDb		=	selectedItem.getParentItem().getParentItem().getText();
				String sColl	= selectedItem.getText();
				try {
					MongoCommand	mcmd	= MongoFactory.getInst().createCommand("db." + sColl + ".find()");
					if ( mcmd != null )
						MongoFactory.getInst().submitExecution( mcmd.setConnection(sName, sDb) );
				}catch (Exception e) {
					EventWorkBenchManager.getInst().onEvent( org.aw20.mongoworkbench.Event.EXCEPTION, e );
				}
				
				/*
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
				*/
			}
		}
		updateGui();
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
	
	
/*
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
*/
	
	private boolean needUpdate(TreeItem parentItem, java.util.List dbNameList) {
		if (parentItem == null)
			return false;

		TreeItem[] dbItems = parentItem.getItems();
		if (dbItems.length != dbNameList.size())
			return true;

		for (int i = 0; i < dbNameList.size(); ++i) {
			if (!dbNameList.get(i).equals(dbItems[i].getText())) {
				return true;
			}
		}

		return false;
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

	/*
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
	*/

	public void init2() {
		imageServer			= MUtil.getImage(parent.getShell().getDisplay(), "server.png");
		imageDatabase 	= MUtil.getImage(parent.getShell().getDisplay(), "database.png");
		imageCollection	= MUtil.getImage(parent.getShell().getDisplay(), "table_multiple.png");
		imageMetaFolder	= MUtil.getImage(parent.getShell().getDisplay(), "folder.png");
		
		parent.setLayout(formLayout);

		tree = new Tree(parent, SWT.BORDER | SWT.SINGLE );
		tree.setLinesVisible(true);
		
		FormData d1 = new FormData();
		d1.top 		= new FormAttachment(0, 1);
		d1.left 	= new FormAttachment(0, 1);
		d1.right 	= new FormAttachment(100, -1);
		d1.bottom = new FormAttachment(100, -1);
		tree.setLayoutData(d1);

		// Add in the servers
		Iterator<Map>	it	= serverList.iterator();
		while ( it.hasNext() ){
			Map	map	= it.next();
			
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText( (String)map.get("name") );
			item.setImage(imageServer);

			Map data = new HashMap();
			data.put( KEY_TYPE, NodeType.SERVER);
			data.putAll( map );
			item.setData(data );
		}
		
		
		// Set the menu up
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
		//tree.addListener(SWT.Selection, this);
		tree.addListener(SWT.KeyDown, this);

		if (dataManager.isConnected()) {
			//onConnect(dataManager.getConnectAction());
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
			final Map	newProps	= serverDialog.getAttributes();
			
			// Run around to see if this is a new one
			String newName = (String)newProps.get("name");
			boolean bFound = false;
			for ( int x=0; x < serverList.size(); x++ ){
				if ( serverList.get(x).get("name").equals(newName) ){
					serverList.set( x, newProps );
					bFound = true;
				}
			}
			
			if ( !bFound ){
				serverList.add(newProps);

				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						TreeItem item = new TreeItem(tree, SWT.NONE);
						item.setText( (String)newProps.get("name") );
						item.setImage(imageServer);

						Map data = new HashMap();
						data.put( KEY_TYPE, NodeType.SERVER );
						data.putAll( newProps );
						item.setData(data );
					}
				});

			}
			
			// Save the serverList
			Activator.getDefault().saveServerList(serverList);
		}
	}


	@Override
	public void onMongoCommandStart(MongoCommand mcmd) {}

	@Override
	public void onMongoCommandFinished(MongoCommand mcmd) {
		if ( mcmd instanceof ShowDbsMongoCommand )
			onShowDbs( (ShowDbsMongoCommand)mcmd );
		else if ( mcmd instanceof ShowCollectionsMongoCommand )
			onShowCollections( (ShowCollectionsMongoCommand)mcmd );
	}
	
	private TreeItem createDbTreeItem(TreeItem mongoItem, String dbName) {
		TreeItem dbitem = new TreeItem(mongoItem, 0);
		dbitem.setText(dbName);
		dbitem.setImage(imageDatabase);

		Map data = new HashMap();
		data.put( KEY_TYPE, NodeType.DATABASE );
		dbitem.setData(data);

		// Add the subfolders
		TreeItem	coll	= new TreeItem( dbitem, 0 );
		coll.setText("Collections");
		coll.setImage(imageMetaFolder);
		data = new HashMap();
		data.put( KEY_TYPE, NodeType.METADATA );
		coll.setData(data);
		
		/*
		 * Not Yet Implemented; but when i do it will rock!
		coll	= new TreeItem( dbitem, 0 );
		coll.setText("Stored Javascript");
		coll.setImage(imageMetaFolder);
		data = new HashMap();
		data.put( KEY_TYPE, NodeType.METADATA );
		coll.setData(data);
		
		coll	= new TreeItem( dbitem, 0 );
		coll.setText("GridFS");
		coll.setImage(imageMetaFolder);
		data = new HashMap();
		data.put( KEY_TYPE, NodeType.METADATA );
		coll.setData(data);
		*/
		
		tree.showItem(dbitem);
		return dbitem;
	}


	private void onShowDbs( final ShowDbsMongoCommand mcmd ) {
		
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {

				TreeItem	item	= findTreeItem( tree.getParentItem(), NodeType.SERVER, mcmd.getName() );
				List<String>	dbList = mcmd.getDBNames();
				
				try {
					if (needUpdate(item, dbList)) {
						item.removeAll();

						for (int i = 0; i < dbList.size(); ++i) {
							createDbTreeItem(item, (String)dbList.get(i) );
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		updateGui();
	}

	
	private TreeItem findTreeItem(TreeItem treeitem, NodeType nodetype, String id) {
		if ( treeitem != null && treeitem.getItemCount() == 0 )
			return null;
		
		TreeItem[] items = (treeitem != null ) ? treeitem.getItems() : tree.getItems();
		if ( items == null )
			return null;
		
		for (int i = 0; i < items.length; ++i) {
			Map data	= (Map)items[i].getData();
			
			if ( (NodeType)data.get(KEY_TYPE) == nodetype ){
				if ( nodetype == NodeType.SERVER ){
					if ( data.get("name").equals(id) )
						return items[i];
				}else if ( nodetype == NodeType.DATABASE || nodetype == NodeType.COLLECTION ){
					if ( items[i].getText().equals(id) )
						return items[i];
				}
			}
		}
		
		return null;
	}
	
	
	private void onShowCollections( final ShowCollectionsMongoCommand mcmd ) {
		
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {

				TreeItem	serverItem	= findTreeItem( tree.getParentItem(), NodeType.SERVER, mcmd.getName() );
				if (serverItem == null)
					return;

				TreeItem	dbItem	= findTreeItem( serverItem, NodeType.DATABASE, mcmd.getDB() );
				if (dbItem == null)
					return;
				
				TreeItem	collectionsItem	= dbItem.getItem(0);
				
				List<String>	colList = mcmd.getCollectionNames();
				
				try {
					if (needUpdate(collectionsItem, colList)) {
						collectionsItem.removeAll();

						for (int i = 0; i < colList.size(); ++i) {
							
							TreeItem item = new TreeItem(collectionsItem, 0);
							item.setText( (String)colList.get(i) );
							tree.showItem(item);
							item.setImage(imageCollection);

							Map data = new HashMap();
							data.put( KEY_TYPE, NodeType.COLLECTION );
							item.setData(data);

						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		updateGui();
	}
	
}