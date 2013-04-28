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
 *  
 *  April 2013
 */
package org.aw20.mongoworkbench.eclipse.view;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.aw20.mongoworkbench.EventWorkBenchListener;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.EventWrapper;
import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.FindOneMongoCommand;
import org.aw20.mongoworkbench.command.GridFSRemoveFileCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.command.SaveMongoCommand;
import org.aw20.mongoworkbench.eclipse.view.table.TreeRender;
import org.aw20.mongoworkbench.eclipse.view.table.TreeWalker;
import org.aw20.util.DateUtil;
import org.aw20.util.JSONFormatter;
import org.aw20.util.StringUtil;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mongodb.DB;

public class MEditor extends MAbstractView implements EventWorkBenchListener, MongoCommandListener {
	private String[] allTypes = new String[]{"string","boolean","int32","int64","double","date","objectid","code","regex","binary"};
	
	private Text textJSON;
	
	private TabFolder tabFolder;
	private Tree tree;
	private TreeRender treeRender;
	
	private Tree	wrTree;
	private TreeRender	wrTreeRender;
	
	private Button btnSave;
	private Map activeDocumentMap;
	private Button btnDelete;

	private Color black;
	private TreeEditor treeeditor;
	private Composite compositeEditor;
	private Text cellEditorText;
	private boolean reSized = false;

	
	public MEditor() {
		EventWorkBenchManager.getInst().registerListener(this);
		MongoFactory.getInst().registerListener(this);
	}

	public void dispose() {
		EventWorkBenchManager.getInst().deregisterListener(this);
		MongoFactory.getInst().deregisterListener(this);
		super.dispose();
	}

	public void init2() {
		GridLayout gridLayout = new GridLayout(2, true);
		parent.setLayout(gridLayout);

		tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		TabItem tbtmTreeItem = new TabItem(tabFolder, SWT.NONE);
		tbtmTreeItem.setText("Document");

		tree = new Tree(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmTreeItem.setControl(tree);
		treeRender = new TreeRender(parent.getDisplay(), tree);

		TabItem tbtmJSONItem = new TabItem(tabFolder, SWT.NONE);
		tbtmJSONItem.setText("JSON");

		textJSON = new Text(tabFolder, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		textJSON.setTabs(2);
		textJSON.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
		textJSON.addKeyListener( new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if ( e.keyCode == SWT.RIGHT
						|| e.keyCode == SWT.LEFT 
						|| e.keyCode == SWT.UP 
						|| e.keyCode == SWT.DOWN 
						|| e.keyCode == SWT.PAGE_DOWN 
						|| e.keyCode == SWT.PAGE_UP 
						|| e.keyCode == SWT.END 
						){
					return;
				}
				
				if (isEditable() && !btnSave.isEnabled() ){
					enableButtons(true);
				}
			}
			
		});
		tbtmJSONItem.setControl(textJSON);

		tbtmTreeItem = new TabItem(tabFolder, SWT.NONE);
		tbtmTreeItem.setText("WriteResult");
		wrTree = new Tree(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmTreeItem.setControl(wrTree);
		wrTreeRender = new TreeRender(parent.getDisplay(), wrTree);
		
		btnDelete = new Button(parent, SWT.NONE);
		btnDelete.setText("delete document");
		btnDelete.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onDelete();
			}
		});

		btnSave = new Button(parent, SWT.NONE);
		btnSave.setText("save document");
		btnSave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onUpdate();
			}
		});
		
		enableButtons(false);

		black  = parent.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		treeeditor = new TreeEditor(tree);
		
		tree.addMouseListener( new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {}
			
			@Override
			public void mouseDown(MouseEvent e) {}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TreeItem item = tree.getSelection()[0];
				
				Class nodeClass = (Class)item.getData("class");
				
				// we are not allowing the main ObjectId to be messed around with
				if ( nodeClass == ObjectId.class )
					return;

				for ( int x=0; x < tree.getColumnCount(); x++ ){
					if ( isEditable() && item.getBounds(x).contains( e.x, e.y) ){

						// don't let them edit the value/type for Map/List items
						if ( x > 0 && nodeClass == Map.class || nodeClass == List.class )
							return;
						
						setupCellEditor( item, x );
						return;
					}
				}
			}
		});

		
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
	}
	
	private boolean isEditable(){
		return (activeDocumentMap.get(EventWrapper.ACTIVE_DB) != null);
	}

	private void enableButtons(boolean t){
		btnSave.setEnabled(t);
		btnDelete.setEnabled(t);
	}
	
	private Action	actionList[];
	private int	ACTION_INFO = 0;
	private int ACTION_DELETE = 1;
	
	private void createActions() {
		actionList	= new Action[2];
		
		actionList[ACTION_INFO] = new Action() {public void run() {actionRun(ACTION_INFO);}	};
		actionList[ACTION_INFO].setText("");
		actionList[ACTION_INFO].setEnabled(false);

		actionList[ACTION_DELETE] = new Action() {public void run() {actionRun(ACTION_DELETE);}	};
		actionList[ACTION_DELETE].setText("remove key");
		setActionImage(actionList[ACTION_DELETE], "bullet_delete.png");
	}

	protected void showContextMenu(TreeItem treeItem) {
		
		menuManager.add( actionList[ACTION_INFO] );
		actionList[ACTION_INFO].setText( treeItem.getText(0) );
		
		menuManager.add( new Separator() );
		menuManager.add( actionList[ACTION_DELETE] );
		menuManager.add( new Separator() );

		// we are not allowing the main ObjectId to be messed around with
		Class nodeClass = (Class)treeItem.getData("class");
		if ( nodeClass == ObjectId.class || nodeClass == Map.class || nodeClass == List.class )
			return;

		Action action;
		for (int x=0; x < allTypes.length; x++ ){
			final int y = x;
			
			if ( allTypes[x].equals("objectid") || allTypes[x].equals("binary") )
				continue;

			action = new Action() {public void run() {actionRun(100+y);}	};
			action.setText( allTypes[x] );
			
			if ( nodeClass == String.class && allTypes[x].equals("string") )
				setActionImage(action, "tick.png");
			else if ( nodeClass == Double.class && allTypes[x].equals("double") )
				setActionImage(action, "tick.png");
			else if ( nodeClass == Integer.class && allTypes[x].equals("int32") )
				setActionImage(action, "tick.png");
			else if ( nodeClass == Long.class && allTypes[x].equals("int64") )
				setActionImage(action, "tick.png");
			else if ( nodeClass == Date.class && allTypes[x].equals("date") )
				setActionImage(action, "tick.png");
			else if ( nodeClass == Code.class && allTypes[x].equals("code") )
				setActionImage(action, "tick.png");
			else if ( nodeClass == Pattern.class && allTypes[x].equals("regex") )
				setActionImage(action, "tick.png");
			else if ( nodeClass == Boolean.class && allTypes[x].equals("boolean") )
				setActionImage(action, "tick.png");
			
			menuManager.add(action);
		}
	}


	protected void actionRun(int actionId) {
		if ( actionId >= 100 ){
			TreeItem	item	= tree.getSelection()[0];
			setCellValue( item, 2, allTypes[actionId-100] );
		} else if ( actionId == ACTION_DELETE ){
     	tree.getSelection()[0].removeAll();
     	tree.getSelection()[0].dispose();
     	enableButtons(true);
		}
	}

	
	protected void setupCellEditor(final TreeItem item, final int column) {
		compositeEditor = new Composite(tree, SWT.NONE);
		compositeEditor.setBackground(black);
		reSized = false;
		
		cellEditorText	= new Text( compositeEditor, SWT.NONE );
		
		compositeEditor.addListener (SWT.Resize, new Listener () {
			public void handleEvent (Event e) {
				Rectangle rect = compositeEditor.getClientArea();
				cellEditorText.setBounds(rect.x + 1, rect.y + 1, rect.width - 1 * 2, rect.height - 1 * 2);
			}
		});

		Listener textListener = new Listener () {
			public void handleEvent(final Event e) {
				switch (e.type) {
					case SWT.FocusOut:
						setCellValue( item, column, cellEditorText.getText() );
						compositeEditor.dispose();
						break;
						
					case SWT.Verify:
						if ( !reSized ){
							GC gc 			= new GC(cellEditorText);
							Point size 	= gc.textExtent( cellEditorText.getText() );
							gc.dispose();

							size = cellEditorText.computeSize(size.x, SWT.DEFAULT);
							treeeditor.horizontalAlignment = SWT.LEFT;

							Rectangle itemRect = item.getBounds(column), rect = tree.getClientArea();
							treeeditor.minimumWidth = itemRect.width + 1 * 2;

							int left = itemRect.x, right = rect.x + rect.width;
							treeeditor.minimumWidth 	= Math.min(treeeditor.minimumWidth, right - left);
							treeeditor.minimumHeight 	= size.y + 1 * 2;

							treeeditor.layout();
							reSized = true;
						}
						break;
						
					case SWT.Traverse:
						switch (e.detail) {
							case SWT.TRAVERSE_RETURN:
								setCellValue( item, column, cellEditorText.getText() );
								//FALL THROUGH
							case SWT.TRAVERSE_ESCAPE:
								compositeEditor.dispose();
								e.doit = false;
						}
						break;
				}
			}

		};
		
		cellEditorText.addListener(SWT.FocusOut, textListener);
		cellEditorText.addListener(SWT.Traverse, textListener);
		cellEditorText.addListener(SWT.Verify, textListener);
		
		cellEditorText.setText( item.getText(column) );
		treeeditor.setEditor(compositeEditor, item, column);
		cellEditorText.selectAll();
		cellEditorText.setFocus();
	}

	
	
	/**
	 * The key/value/type has been edited, now we have to determine if this is a valid operation to have done
	 * given the overall state of the row.
	 * 
	 * @param item
	 * @param column
	 * @param newtext
	 */
	private void setCellValue( TreeItem item, int column, String newtext ){
		Class nodeClass = (Class)item.getData("class");
		
		// we are not allowing the main ObjectId to be messed around with
		if ( nodeClass == ObjectId.class || nodeClass == Map.class || nodeClass == List.class )
			return;
		
		enableButtons(true);
		
		if ( column == 0 ){	
			/**
			 * Changing the key name; we have to make sure it contains no invalid characters and does not clash with any
			 * other key name that is on the same level of this key
			 */
			newtext	= newtext.trim();
			if ( !isKeyLegal(newtext) )
				return;

			// Check for same keys
			TreeItem[]	peers;
			if ( item.getParentItem() == null )
				peers	= tree.getItems();
			else
				peers	= item.getParentItem().getItems();

			for ( int x=0; x<peers.length;x++){
				if ( peers[x] != item && peers[x].getText(0).equals(newtext) ){
					return;
				}
			}
			
			item.setText( column, newtext );
			
		}else if (column == 1){
			/**
			 * Changing the value; Got to make sure the value is a valid type
			 */
			if ( nodeClass == String.class ){
				item.setText(column, newtext);
			} else if ( nodeClass == Boolean.class ){
				if ( newtext.equals("true") || newtext.equals("false") ){
					item.setText( column, newtext );
				}
			} else if ( nodeClass == Integer.class ){
				if ( StringUtil.toInteger(newtext, Integer.MIN_VALUE) != Integer.MIN_VALUE ){
					item.setText( column, newtext );
				}
			} else if ( nodeClass == Long.class ){
				if ( StringUtil.toLong(newtext, Long.MIN_VALUE) != Long.MIN_VALUE ){
					item.setText( column, newtext );
				}
			} else if ( nodeClass == Double.class ){
				if ( StringUtil.toDouble(newtext, Double.MIN_VALUE) != Double.MIN_VALUE ){
					item.setText( column, newtext );
				}
			} else if ( nodeClass == Date.class ){
				if ( DateUtil.parseDate(newtext, "yyyy-MM-dd HH:mm:ss") != null ){
					item.setText( column, newtext );
				}
			} else if ( nodeClass == Code.class ){
				item.setText( column, newtext );
			} else if ( nodeClass == Pattern.class ){
				item.setText( Pattern.compile(newtext).toString() );
			} else if ( nodeClass == Byte.class ){
				
			}
			
		}else if (column == 2){
			/**
			 * Changing the type of the object; we need to make sure it is valid and we change the value to this type
			 */
			String newtype	= newtext.toLowerCase().trim();
			if ( !isValidType(newtype) )
				return;
			
			if ( newtype.equals("string") ){
				
				item.setData( "class", String.class );
				item.setText(2,"string");
				
			} else if ( newtype.equals("boolean") ){

				String d = item.getText(1);
				if ( !d.equals("true") && !d.equals("false") ){
					item.setText(1,"false");
				}
				item.setData( "class", Boolean.class );
				item.setText(2,"boolean");
				
			} else if ( newtype.equals("int32") ){
				
				String d = item.getText(1);
				if ( StringUtil.toInteger(d, Integer.MIN_VALUE) == Integer.MIN_VALUE ){
					item.setText( 1, "0" );
				}
				item.setData( "class", Integer.class );
				item.setText(2,"int32");
				
			} else if ( newtype.equals("int64") ){

				String d = item.getText(1);
				if ( StringUtil.toLong(d, Long.MIN_VALUE) == Long.MIN_VALUE ){
					item.setText( 1, "0" );
				}
				item.setData( "class", Long.class );
				item.setText(2,"int64");
				
			} else if ( newtype.equals("double") ){

				String d = item.getText(1);
				if ( StringUtil.toDouble(d, Double.MIN_VALUE) == Double.MIN_VALUE ){
					item.setText( 1, "0.0" );
				}
				item.setData( "class", Double.class );
				item.setText(2,"double");

			} else if ( newtype.equals("date") ){
				
				String d = item.getText(1);
				if ( DateUtil.parseDate(d, "yyyy-MM-dd HH:mm:ss") == null ){
					item.setText( 1,  DateUtil.getSQLDate( new Date() ) );
				}
				item.setData( "class", Date.class );
				item.setText(2,"date");
				
			} else if ( newtype.equals("code") ){
				
				item.setData( "class", String.class );
				item.setText( 2,"code");
				
			} else if ( newtype.equals("regex") ){

				item.setData( "class", Pattern.class );
				item.setText( 2,"regex");

			} else if ( nodeClass == Byte.class ){
				
			}

		}
	}
	
	
	private boolean isKeyLegal( String t ){
		if ( t.length() == 0 )
			return false;
		
		for ( int x=0; x < t.length(); x++ ){
			char c = t.charAt(x);
			
			if ( !Character.isLetterOrDigit(c) && c != '_' )
				return false;
		}
		
		return true;
	}
	
	
	private boolean isValidType( String t ){
		for ( int x=0; x < allTypes.length; x++ ){
			if ( t.equals(allTypes[x] ) )
				return true;
		}
		return false;
	}
	
	
	@Override
	public void onEventWorkBench(org.aw20.mongoworkbench.Event event, final Object data) {

		switch (event) {
			case DOCUMENT_VIEW: {
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						redraw((Map) data);
					}
				});
				break;
			}
			case ELEMENT_VIEW: {
				break;
			}
			case WRITERESULT: {
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						writeResult((Map) data);
					}
				});
				break;
			}
			default:
				break;
		}

	}

	protected void writeResult(Map data) {
		wrTreeRender.render(data);
	}

	private void onDelete() {
		if (activeDocumentMap == null)
			return;

		Map m = (Map)activeDocumentMap.get(EventWrapper.DOC_DATA);
		String id = m.get("_id").toString();
		
		MessageBox messageBox = new MessageBox(parent.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
    messageBox.setMessage("Do you delete this document?\r\n\r\n     " + id );
    messageBox.setText("Document Delete");
    int response = messageBox.open();
    if (response == SWT.YES){
	
    	if ( activeDocumentMap.get(EventWrapper.ACTIVE_COLL).toString().endsWith(".files") ){

    		try {
					MongoCommand mcmd = new GridFSRemoveFileCommand(id);
					mcmd.setConnection((String) activeDocumentMap.get(EventWrapper.ACTIVE_NAME),  (String)activeDocumentMap.get(EventWrapper.ACTIVE_DB),  (String)activeDocumentMap.get(EventWrapper.ACTIVE_COLL));
					MongoFactory.getInst().submitExecution(mcmd);
				} catch (Exception e) {
					EventWorkBenchManager.getInst().onEvent(org.aw20.mongoworkbench.Event.EXCEPTION, e);
				}
    		
    	}else{
    	
				try {
					MongoCommand mcmd = MongoFactory.getInst().createCommand("db." + activeDocumentMap.get(EventWrapper.ACTIVE_COLL) + ".remove({_id:ObjectId(\"" + id + "\")})");
					mcmd.setConnection((String) activeDocumentMap.get(EventWrapper.ACTIVE_NAME));
					MongoFactory.getInst().submitExecution(mcmd);
				} catch (Exception e) {
					EventWorkBenchManager.getInst().onEvent(org.aw20.mongoworkbench.Event.EXCEPTION, e);
				}
			
    	}
    }
	}

	
	private void redraw(Map data) {
		btnSave.setEnabled(false);
		btnDelete.setEnabled(false);

		// Update the JSON
		textJSON.setText("");

		activeDocumentMap = data;

		if (activeDocumentMap.get(EventWrapper.DOC_DATA) instanceof Map || activeDocumentMap.get(EventWrapper.DOC_DATA) instanceof List) {
			DB db = MongoFactory.getInst().getMongoActiveDB();
			textJSON.setText((String) db.eval("tojson(arguments[0])", new Object[] { activeDocumentMap.get(EventWrapper.DOC_DATA) }));
		}

		// Update the tree
		Map map = (Map) activeDocumentMap.get(EventWrapper.DOC_DATA);
		treeRender.render(map);

		enableButtons(false);
	}

	
	private void onUpdate() {
		if (activeDocumentMap == null)
			return;

		if ( tabFolder.getSelectionIndex() == 0 ){
			Object	obj	= TreeWalker.toObject(tree);
			saveDocument( JSONFormatter.format(obj) );
		}else if ( tabFolder.getSelectionIndex() == 1 ){
			saveDocument(textJSON.getText());
		}
	}
	
	
	private void saveDocument(String json){
		try {
			MongoCommand mcmd = MongoFactory.getInst().createCommand("db." + activeDocumentMap.get(EventWrapper.ACTIVE_COLL) + ".save(" + json + ")");
			mcmd.setConnection((String) activeDocumentMap.get(EventWrapper.ACTIVE_NAME));
			MongoFactory.getInst().submitExecution(mcmd);
		} catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent(org.aw20.mongoworkbench.Event.EXCEPTION, e);
		}
	}

	@Override
	public void onMongoCommandStart(MongoCommand mcmd) {}

	@Override
	public void onMongoCommandFinished(MongoCommand inmcmd) {
		if ( inmcmd instanceof SaveMongoCommand ){
			Object id 	= ((Map)activeDocumentMap.get(EventWrapper.DOC_DATA)).get("_id");
			Object sid	= ((SaveMongoCommand)inmcmd).getObjectId();
			
			if ( id.equals(sid) ){

				/*
				 * This is the save command; we want to make sure this is our SAVE command so we can trigger a reload
				 */
				String queryJson;
				if ( sid instanceof ObjectId ){
					queryJson	= "ObjectId(\"" + sid.toString() + "\")";
				}else{
					queryJson = "\"" + sid.toString() + "\"";
				}

				try {
					MongoCommand mcmd = MongoFactory.getInst().createCommand("db." + activeDocumentMap.get(EventWrapper.ACTIVE_COLL) + ".findOne({_id:" + queryJson + "})");
					mcmd.setConnection((String) activeDocumentMap.get(EventWrapper.ACTIVE_NAME));
					MongoFactory.getInst().submitExecution(mcmd);
				} catch (Exception e) {
					EventWorkBenchManager.getInst().onEvent(org.aw20.mongoworkbench.Event.EXCEPTION, e);
				}
			}
			
		} else if ( inmcmd instanceof FindOneMongoCommand ){
			
			/*
			 * This is the FindOneMongoCommand that we triggered on the savecommand; however we want to make sure we are reloading
			 * the one that was editing in this particular one and not one from a command console.
			 */
			Object id 	= ((Map)activeDocumentMap.get(EventWrapper.DOC_DATA)).get("_id");
			Object sid	= ((FindOneMongoCommand)inmcmd).getDbObject().get("_id");
			
			if ( id.equals(sid) ){

				final Map eventMap	= EventWrapper.createMap( 
						EventWrapper.ACTIVE_NAME, inmcmd.getName(), 
						EventWrapper.ACTIVE_DB, inmcmd.getDB(),
						EventWrapper.ACTIVE_COLL, inmcmd.getCollection(),
						EventWrapper.DOC_DATA, ((FindOneMongoCommand)inmcmd).getDbObject().toMap()
						);
				
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						redraw(eventMap);
					}
				});
			}

		}
	}
}