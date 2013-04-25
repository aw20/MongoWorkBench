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
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.GridFSRemoveFileCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.eclipse.view.table.TreeRender;
import org.aw20.util.DateUtil;
import org.aw20.util.StringUtil;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mongodb.DB;

public class MEditor extends MAbstractView implements EventWorkBenchListener {
	private String[] allTypes = new String[]{"string","boolean","int32","int64","double","date","objectid","code","regex","binary"};
	
	private Text textJSON;
	
	private Tree tree;
	private TreeRender treeRender;
	
	private Tree	wrTree;
	private TreeRender	wrTreeRender;
	
	private Button btnNewButton;
	private Map activeDocumentMap;
	private Button btnNewButton_1;

	private Color black;
	private TreeEditor treeeditor;
	private Composite compositeEditor;
	private Text cellEditorText;
	private boolean reSized = false;

	
	public MEditor() {
		EventWorkBenchManager.getInst().registerListener(this);
	}

	public void dispose() {
		EventWorkBenchManager.getInst().deregisterListener(this);
		super.dispose();
	}

	public void init2() {
		GridLayout gridLayout = new GridLayout(2, true);
		parent.setLayout(gridLayout);


		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
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
		tbtmJSONItem.setControl(textJSON);

		tbtmTreeItem = new TabItem(tabFolder, SWT.NONE);
		tbtmTreeItem.setText("WriteResult");
		wrTree = new Tree(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmTreeItem.setControl(wrTree);
		wrTreeRender = new TreeRender(parent.getDisplay(), wrTree);
		
		btnNewButton_1 = new Button(parent, SWT.NONE);
		btnNewButton_1.setText("delete document");
		btnNewButton_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onDelete();
			}
		});

		btnNewButton = new Button(parent, SWT.NONE);
		btnNewButton.setText("save document");
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onUpdate();
			}
		});
		

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
					if ( item.getBounds(x).contains( e.x, e.y) ){

						// don't let them edit the value/type for Map/List items
						if ( x > 0 && nodeClass == Map.class || nodeClass == List.class )
							return;
						
						setupCellEditor( item, x );
						return;
					}
				}
			}
		});

	}
	
	protected void setupCellEditor(final TreeItem item, final int column) {
		compositeEditor = new Composite(tree, SWT.NONE);
		compositeEditor.setBackground(black);
		reSized = false;
		
		cellEditorText	= new Text( compositeEditor, SWT.NONE );
		
		compositeEditor.addListener (SWT.Resize, new Listener () {
			public void handleEvent (Event e) {
				Rectangle rect = compositeEditor.getClientArea();
				cellEditorText.setBounds (rect.x + 1, rect.y + 1, rect.width - 1 * 2, rect.height - 1 * 2);
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
			newtext	= newtext.toLowerCase().trim();
			if ( !isValidType(newtext) )
				return;
			
			
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
	
	private void onUpdate() {
		if (activeDocumentMap == null)
			return;
		
		try {
			MongoCommand mcmd = MongoFactory.getInst().createCommand("db." + activeDocumentMap.get(EventWrapper.ACTIVE_COLL) + ".save(" + textJSON.getText() + ")");
			mcmd.setConnection((String) activeDocumentMap.get(EventWrapper.ACTIVE_NAME));
			MongoFactory.getInst().submitExecution(mcmd);
		} catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent(org.aw20.mongoworkbench.Event.EXCEPTION, e);
		}
	}

	private void redraw(Map data) {
		btnNewButton.setEnabled(false);
		btnNewButton_1.setEnabled(false);

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

		if ( activeDocumentMap.get(EventWrapper.ACTIVE_DB) != null ){
			btnNewButton.setEnabled(true);
			btnNewButton_1.setEnabled(true);
		}
	}

}