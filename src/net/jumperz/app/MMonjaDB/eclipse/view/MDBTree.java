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
import net.jumperz.app.MMonjaDB.eclipse.dialog.TextInputPopup;

import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.EventWrapper;
import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.CollectionCountMongoCommand;
import org.aw20.mongoworkbench.command.CollectionRemoveAllMongoCommand;
import org.aw20.mongoworkbench.command.CreateCollectionMongoCommand;
import org.aw20.mongoworkbench.command.CreateDbsMongoCommand;
import org.aw20.mongoworkbench.command.DropCollectionMongoCommand;
import org.aw20.mongoworkbench.command.DropDbsMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.command.ShowCollectionsMongoCommand;
import org.aw20.mongoworkbench.command.ShowDbsMongoCommand;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
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
		INDEX, JAVASCRIPT
	};
	
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

			} else if ( nodeType == NodeType.DATABASE ) {

				String sName	= selectedItem.getParentItem().getText();
				String sDb		=	selectedItem.getText();
				MongoFactory.getInst().setActiveServerDB( sName, sDb );
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
			
			}
		}
	}


	private boolean needUpdate(TreeItem parentItem, java.util.List dbNameList) {
		if (parentItem == null || dbNameList == null )
			return false;

		TreeItem[] dbItems = parentItem.getItems();
		if (dbItems.length != dbNameList.size())
			return true;

		for (int i = 0; i < dbNameList.size(); ++i) {
			Object o = dbNameList.get(i);
			
			if ( o instanceof String ){
			
				if (!o.equals(dbItems[i].getText()))
					return true;
				
			}else if ( o instanceof Map ){
				
				String k = (String)((Map)o).get( MongoCommand.KEY_NAME);
				if (!k.equals(dbItems[i].getText()))
					return true;

			}
		}

		return false;
	}


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
		drawServerNodes();
		
		// Set the menu up
		menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				if ( tree.getSelectionCount() != 0 )
					showContextMenu( tree.getSelection()[0] );
			}
		});
		
		
		Menu contextMenu = menuManager.createContextMenu(tree);
		tree.setMenu(contextMenu);
		createActions();

		// listeners
		tree.addListener(SWT.MouseDoubleClick, this);
		tree.addListener(SWT.KeyDown, this);
	}

	private void drawServerNodes() {
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
	}

	private Action	actionList[];
	
	private int	ACTION_SERVER_DISCONNECT 			= 0;
	private int	ACTION_SERVER_CREATEDATABASE 	= 1;
	private int	ACTION_SERVER_REFRESH 				= 2;
	private int	ACTION_SERVER_NAME 						= 3;
	private int	ACTION_SERVER_EDIT 						= 4;
	private int	ACTION_SERVER_DELETE 					= 5;
	private int	ACTION_SERVER_STATS 					= 6;
	private int	ACTION_DATABASE_DROP 					= 7;
	private int	ACTION_DATABASE_REFRESH				= 8;
	private int	ACTION_DATABASE_NAME					= 9;
	private int	ACTION_DATABASE_STATS					= 10;
	private int	ACTION_COLLECTIONMETADATA_NAME		= 11;
	private int	ACTION_COLLECTIONMETADATA_CREATE	= 12;
	private int	ACTION_COLLECTIONMETADATA_REFRESH	= 13;
	private int	ACTION_COLLECTION_NAME				= 14;
	private int	ACTION_COLLECTION_EMPTY				= 15;
	private int	ACTION_COLLECTION_DROP				= 16;
	private int	ACTION_COLLECTION_COUNT				= 17;
	private int	ACTION_SERVER_ADD							= 18;

	private void createActions(){
	
		actionList	= new Action[19];
		
		// Server
		actionList[ACTION_SERVER_ADD] = new Action() {public void run() {actionRun(ACTION_SERVER_ADD);}	};
		actionList[ACTION_SERVER_ADD].setText("Add Server");
		setActionImage( actionList[ACTION_SERVER_ADD], "database_gear.png" );
		toolBar.add(actionList[ACTION_SERVER_ADD]);

		
		actionList[ACTION_SERVER_DISCONNECT] = new Action() {public void run() {actionRun(ACTION_SERVER_DISCONNECT);}	};
		actionList[ACTION_SERVER_DISCONNECT].setText("Disconnect MongoDB");

		actionList[ACTION_SERVER_CREATEDATABASE] = new Action() {public void run() {actionRun(ACTION_SERVER_CREATEDATABASE);}	};
		actionList[ACTION_SERVER_CREATEDATABASE].setText("Create Database");

		actionList[ACTION_SERVER_REFRESH] = new Action() {public void run() {actionRun(ACTION_SERVER_REFRESH);}	};
		actionList[ACTION_SERVER_REFRESH].setText("Refresh");

		actionList[ACTION_SERVER_EDIT] = new Action() {public void run() {actionRun(ACTION_SERVER_EDIT);}	};
		actionList[ACTION_SERVER_EDIT].setText("Edit Connection");
		
		actionList[ACTION_SERVER_STATS] = new Action() {public void run() {actionRun(ACTION_SERVER_STATS);}	};
		actionList[ACTION_SERVER_STATS].setText("Show Statistics");
		
		actionList[ACTION_SERVER_DELETE] = new Action() {public void run() {actionRun(ACTION_SERVER_DELETE);}	};
		actionList[ACTION_SERVER_DELETE].setText("Delete Connection");

		actionList[ACTION_SERVER_NAME] = new Action() {public void run() {}	};
		actionList[ACTION_SERVER_NAME].setText("name");
		actionList[ACTION_SERVER_NAME].setEnabled(false);
		setActionImage( actionList[ACTION_SERVER_NAME], "server.png" );
		
		// Database
		actionList[ACTION_DATABASE_NAME] = new Action() {public void run() {}	};
		actionList[ACTION_DATABASE_NAME].setText("name");
		actionList[ACTION_DATABASE_NAME].setEnabled(false);
		setActionImage( actionList[ACTION_DATABASE_NAME], "database.png" );

		actionList[ACTION_DATABASE_DROP] = new Action() {public void run() {actionRun(ACTION_DATABASE_DROP);}	};
		actionList[ACTION_DATABASE_DROP].setText("Drop Database");

		actionList[ACTION_DATABASE_STATS] = new Action() {public void run() {actionRun(ACTION_DATABASE_STATS);}	};
		actionList[ACTION_DATABASE_STATS].setText("Show Statistics");

		actionList[ACTION_DATABASE_REFRESH] = new Action() {public void run() {actionRun(ACTION_DATABASE_REFRESH);}	};
		actionList[ACTION_DATABASE_REFRESH].setText("Refresh");

		// Collection metadata
		actionList[ACTION_COLLECTIONMETADATA_NAME] = new Action() {public void run() {}	};
		actionList[ACTION_COLLECTIONMETADATA_NAME].setText("name");
		actionList[ACTION_COLLECTIONMETADATA_NAME].setEnabled(false);
		setActionImage( actionList[ACTION_COLLECTIONMETADATA_NAME], "table_multiple.png" );

		actionList[ACTION_COLLECTIONMETADATA_CREATE] = new Action() {public void run() {actionRun(ACTION_COLLECTIONMETADATA_CREATE);}	};
		actionList[ACTION_COLLECTIONMETADATA_CREATE].setText("Create Collection");

		actionList[ACTION_COLLECTIONMETADATA_REFRESH] = new Action() {public void run() {actionRun(ACTION_COLLECTIONMETADATA_REFRESH);}	};
		actionList[ACTION_COLLECTIONMETADATA_REFRESH].setText("Refresh");

		// Collection
		actionList[ACTION_COLLECTION_NAME] = new Action() {public void run() {}	};
		actionList[ACTION_COLLECTION_NAME].setText("name");
		actionList[ACTION_COLLECTION_NAME].setEnabled(false);
		setActionImage( actionList[ACTION_COLLECTION_NAME], "table_multiple.png" );

		actionList[ACTION_COLLECTION_DROP] = new Action() {public void run() {actionRun(ACTION_COLLECTION_DROP);}	};
		actionList[ACTION_COLLECTION_DROP].setText("Drop Collection");

		actionList[ACTION_COLLECTION_EMPTY] = new Action() {public void run() {actionRun(ACTION_COLLECTION_EMPTY);}	};
		actionList[ACTION_COLLECTION_EMPTY].setText("Empty Collection");

		actionList[ACTION_COLLECTION_COUNT] = new Action() {public void run() {actionRun(ACTION_COLLECTION_COUNT);}	};
		actionList[ACTION_COLLECTION_COUNT].setText("Count Collection");
		
	}
	
	
	
	protected void showContextMenu(TreeItem selectedItem) {
		Map data = (Map) selectedItem.getData();
		NodeType nodeType = (NodeType)data.get(KEY_TYPE);
		
		if ( nodeType == NodeType.SERVER ){

			actionList[ACTION_SERVER_NAME].setText( selectedItem.getText() );
			menuManager.add( actionList[ACTION_SERVER_NAME] );
			menuManager.add( new Separator() );

			if ( selectedItem.getItemCount() > 0 ){
				menuManager.add( actionList[ACTION_SERVER_REFRESH] );
				menuManager.add( actionList[ACTION_SERVER_STATS] );
				menuManager.add( actionList[ACTION_SERVER_CREATEDATABASE] );
				menuManager.add( new Separator() );
				menuManager.add( actionList[ACTION_SERVER_DISCONNECT] );
			}
			menuManager.add( actionList[ACTION_SERVER_DELETE] );
			menuManager.add( actionList[ACTION_SERVER_EDIT] );
			
		} else if ( nodeType == NodeType.DATABASE ) {
			
			actionList[ACTION_DATABASE_NAME].setText( selectedItem.getText() );
			menuManager.add( actionList[ACTION_DATABASE_NAME] );
			menuManager.add( new Separator() );
			menuManager.add( actionList[ACTION_DATABASE_REFRESH] );
			menuManager.add( actionList[ACTION_DATABASE_DROP] );
			
		} else if ( nodeType == NodeType.METADATA && selectedItem.getText().equals("Collections") ) {
			
			actionList[ACTION_COLLECTIONMETADATA_NAME].setText( selectedItem.getParentItem().getText() );
			menuManager.add( actionList[ACTION_COLLECTIONMETADATA_NAME] );
			menuManager.add( new Separator() );
			menuManager.add( actionList[ACTION_COLLECTIONMETADATA_REFRESH] );
			menuManager.add( actionList[ACTION_COLLECTIONMETADATA_CREATE] );

		} else if ( nodeType == NodeType.COLLECTION ) {
			
			actionList[ACTION_COLLECTION_NAME].setText( selectedItem.getText() );
			menuManager.add( actionList[ACTION_COLLECTION_NAME] );
			menuManager.add( new Separator() );
			menuManager.add( actionList[ACTION_COLLECTION_COUNT] );
			menuManager.add( actionList[ACTION_COLLECTION_EMPTY] );
			menuManager.add( actionList[ACTION_COLLECTION_DROP] );
		
		}
		
	}

	
	
	/**
	 * Context Menu was clicked
	 * 
	 * @param type
	 */
	protected	void actionRun( int type ){
		if ( type == ACTION_SERVER_REFRESH || type == ACTION_DATABASE_REFRESH || type == ACTION_COLLECTIONMETADATA_REFRESH ){
			onReload();
		}else if ( type == ACTION_SERVER_DISCONNECT ){
			
			MessageBox messageBox = new MessageBox(parent.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
      messageBox.setMessage("Do you really want to disconnect from this server?\r\n\r\n     " + tree.getSelection()[0].getText() );
      messageBox.setText("Server Disconnect");
      int response = messageBox.open();
      if (response == SWT.YES){
      	String sName	= tree.getSelection()[0].getText();
      	Map m = Activator.getDefault().getServerMap(sName);
      	if ( m == null )
      		return;
      	
      	try {
					MongoFactory.getInst().registerMongo( m );
				} catch (UnknownHostException e) {
					EventWorkBenchManager.getInst().onEvent( org.aw20.mongoworkbench.Event.EXCEPTION, e );
				}

      	tree.getSelection()[0].removeAll();
      }
			
		}else if ( type == ACTION_SERVER_DELETE ){

			MessageBox messageBox = new MessageBox(parent.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
      messageBox.setMessage("Do you really want delete this server connection?\r\n\r\n     " + tree.getSelection()[0].getText() );
      messageBox.setText("Server Delete Confirmation");
      int response = messageBox.open();
      if (response == SWT.YES){
      	TreeItem	selectedItem	= tree.getSelection()[0];
      	String sName	= selectedItem.getText();
      	
      	MongoFactory.getInst().removeMongo(sName);
      	serverList	= Activator.getDefault().removeServerMap(sName);
      	Activator.getDefault().saveServerList(serverList);
      	selectedItem.removeAll();

      	tree.removeAll();
      	drawServerNodes();
      }
			
		}else if ( type == ACTION_SERVER_EDIT ){
			
			TreeItem	selectedItem	= tree.getSelection()[0];
    	String sName	= selectedItem.getText();
    	onConnectSelect( Activator.getDefault().getServerMap(sName) );
			
		}else if ( type == ACTION_SERVER_CREATEDATABASE ){
			
			TextInputPopup	popup	= new TextInputPopup(parent.getShell(), "Create Database");
			String newDbName	= popup.open("Database:");
			if ( newDbName != null ){
      	TreeItem	selectedItem	= tree.getSelection()[0];
      	
  			String sName	= selectedItem.getText();
  			MongoFactory.getInst().setActiveServerDB( sName, newDbName );
  			MongoFactory.getInst().submitExecution( new CreateDbsMongoCommand().setConnection(sName, newDbName) );
			}
			
		}else if ( type == ACTION_SERVER_STATS ){
			
		}else if ( type == ACTION_DATABASE_DROP ){
			
			MessageBox messageBox = new MessageBox(parent.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
      messageBox.setMessage("Do you really want drop this database?\r\n\r\n     " + tree.getSelection()[0].getText() );
      messageBox.setText("Drop Database Confirmation");
      int response = messageBox.open();
      if (response == SWT.YES){
      	
      	TreeItem	selectedItem	= tree.getSelection()[0];
  			String sName	= selectedItem.getParentItem().getText();
  			String sDB		= selectedItem.getText();
  			MongoFactory.getInst().setActiveServerDB( sName, sDB );
  			MongoFactory.getInst().submitExecution( new DropDbsMongoCommand().setConnection(sName, sDB) );

      }
		
		}else if ( type == ACTION_DATABASE_STATS ){
			
		}else if ( type == ACTION_COLLECTIONMETADATA_CREATE ){
			
			TextInputPopup	popup	= new TextInputPopup(parent.getShell(), "Create Collection");
			String newCollName	= popup.open("Collection:");
			if ( newCollName != null ){
				TreeItem	selectedItem	= tree.getSelection()[0];
	    	
				String sName	= selectedItem.getParentItem().getParentItem().getText();
				String sDb		=	selectedItem.getParentItem().getText();

				MongoFactory.getInst().setActiveServerDB( sName, sDb );
				MongoFactory.getInst().submitExecution( new CreateCollectionMongoCommand().setConnection(sName, sDb, newCollName) );

			}
			
		}else if ( type == ACTION_COLLECTION_DROP ){
			
			MessageBox messageBox = new MessageBox(parent.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
      messageBox.setMessage("Do you really want drop this collection?\r\n\r\n     " + tree.getSelection()[0].getText() );
      messageBox.setText("Drop Collection Confirmation");
      int response = messageBox.open();
      if (response == SWT.YES){
  			TreeItem	selectedItem	= tree.getSelection()[0];
      	
  			String sName	= selectedItem.getParentItem().getParentItem().getParentItem().getText();
  			String sDb		=	selectedItem.getParentItem().getParentItem().getText();
  			String sColl	=	selectedItem.getText();

  			MongoFactory.getInst().setActiveServerDB( sName, sDb );
  			MongoFactory.getInst().submitExecution( new DropCollectionMongoCommand().setConnection(sName, sDb, sColl) );
      }
		
		}else if ( type == ACTION_COLLECTION_COUNT ){
			
			TreeItem	selectedItem	= tree.getSelection()[0];
    	
			String sName	= selectedItem.getParentItem().getParentItem().getParentItem().getText();
			String sDb		=	selectedItem.getParentItem().getParentItem().getText();
			String sColl	=	selectedItem.getText();

			MongoFactory.getInst().setActiveServerDB( sName, sDb );
			MongoFactory.getInst().submitExecution( new CollectionCountMongoCommand().setConnection(sName, sDb, sColl) );
			
		}else if ( type == ACTION_COLLECTION_EMPTY ){
			
			MessageBox messageBox = new MessageBox(parent.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
      messageBox.setMessage("Do you really want remove all items from this collection?\r\n\r\n     " + tree.getSelection()[0].getText() );
      messageBox.setText("Empty Collection Confirmation");
      int response = messageBox.open();
      if (response == SWT.YES){
      	TreeItem	selectedItem	= tree.getSelection()[0];
      	
  			String sName	= selectedItem.getParentItem().getParentItem().getParentItem().getText();
  			String sDb		=	selectedItem.getParentItem().getParentItem().getText();
  			String sColl	=	selectedItem.getText();

  			MongoFactory.getInst().setActiveServerDB( sName, sDb );
  			MongoFactory.getInst().submitExecution( new CollectionRemoveAllMongoCommand().setConnection(sName, sDb, sColl) );
      }
      
		}else if ( type == ACTION_SERVER_ADD ){
			onConnectSelect( new HashMap() );		
		}

	}
	
	
	/**
	 * The refresh has been called
	 */
	public void onReload() {
		if ( tree.getSelectionCount() == 0 )
			return;
		
		TreeItem selectedItem = tree.getSelection()[0];
		if ( selectedItem == null )
			return;
		
		Map data = (Map) selectedItem.getData();
		NodeType nodeType = (NodeType)data.get(KEY_TYPE);
		
		if ( nodeType == NodeType.SERVER ){
			
			String sName	= (String)data.get("name");
			MongoFactory.getInst().submitExecution( new ShowDbsMongoCommand().setConnection(sName) );
			
		} else if ( nodeType == NodeType.DATABASE ) {

			String sName	= selectedItem.getParentItem().getText();
			String sDb		=	selectedItem.getText();
			MongoFactory.getInst().setActiveServerDB( sName, sDb );
			MongoFactory.getInst().submitExecution( new ShowCollectionsMongoCommand().setConnection(sName, sDb) );

		} else if ( nodeType == NodeType.METADATA ) {
		
			String sName	= selectedItem.getParentItem().getParentItem().getText();
			String sDb		=	selectedItem.getParentItem().getText();
			MongoFactory.getInst().setActiveServerDB( sName, sDb );
			MongoFactory.getInst().submitExecution( new ShowCollectionsMongoCommand().setConnection(sName, sDb) );
			
		}

	}

	
	private void onConnectSelect(Map serverProps) {
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
		else if ( mcmd instanceof CreateDbsMongoCommand )
			onShowDbs( (ShowDbsMongoCommand)mcmd );
	}
	
	private TreeItem createDbTreeItem(TreeItem mongoItem, String dbName) {
		TreeItem dbitem = new TreeItem(mongoItem, 0);
		dbitem.setText(dbName);
		dbitem.setImage(imageDatabase);

		Map data = new HashMap();
		data.put( KEY_TYPE, NodeType.DATABASE );
		dbitem.setData(data);
		
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
					EventWorkBenchManager.getInst().onEvent( org.aw20.mongoworkbench.Event.EXCEPTION, e );
				}

			}
		});
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
				
				
				dbItem.removeAll();
				
				// Put in the collections
				TreeItem	coll	= new TreeItem( dbItem, SWT.None );
				coll.setText("Collections");
				coll.setImage(imageMetaFolder);
				coll.setData( EventWrapper.createMap(KEY_TYPE, NodeType.METADATA) );
				
				List<String>	colList = mcmd.getCollectionNames();
				for (int i = 0; i < colList.size(); ++i) {
					TreeItem item = new TreeItem(coll, 0);
					item.setText( colList.get(i) );
					item.setImage(imageCollection);
					item.setData( EventWrapper.createMap(KEY_TYPE, NodeType.COLLECTION) );
				}
				

				TreeItem	js	= new TreeItem( dbItem, SWT.None );
				js.setText("Stored Javascript");
				js.setImage(imageMetaFolder);
				js.setData( EventWrapper.createMap(KEY_TYPE, NodeType.METADATA) );
				
				colList = mcmd.getJSNames();
				for (int i = 0; i < colList.size(); ++i) {
					TreeItem item = new TreeItem(js, 0);
					item.setText( colList.get(i) );
					item.setImage(imageCollection);
					item.setData( EventWrapper.createMap(KEY_TYPE, NodeType.JAVASCRIPT) );
				}
				
				
				TreeItem gridfs = new TreeItem( dbItem, SWT.None );
				gridfs.setText("GridFS");
				gridfs.setImage(imageMetaFolder);
				gridfs.setData( EventWrapper.createMap(KEY_TYPE, NodeType.METADATA) );

				colList = mcmd.getGridFSNames();
				for (int i = 0; i < colList.size(); ++i) {
					TreeItem item = new TreeItem(gridfs, 0);
					item.setText( colList.get(i) );
					item.setImage(imageCollection);
					item.setData( EventWrapper.createMap(KEY_TYPE, NodeType.GRIDFS) );
				}
				
				tree.showItem(coll);
			}
		});
	}
	
}