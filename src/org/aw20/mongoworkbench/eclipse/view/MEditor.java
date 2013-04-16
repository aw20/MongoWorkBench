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
 */
package org.aw20.mongoworkbench.eclipse.view;

import java.util.List;
import java.util.Map;


import org.aw20.mongoworkbench.Event;
import org.aw20.mongoworkbench.EventWorkBenchListener;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.EventWrapper;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.eclipse.view.table.TreeRender;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mongodb.DB;

public class MEditor extends MAbstractView implements EventWorkBenchListener {
	private Text textJSON;
	
	private Tree tree;
	private TreeRender treeRender;
	
	private Tree	wrTree;
	private TreeRender	wrTreeRender;
	
	private Button btnNewButton;
	private Map activeDocumentMap;
	private Button btnNewButton_1;

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
	}

	@Override
	public void onEventWorkBench(Event event, final Object data) {

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
	
			try {
				MongoCommand mcmd = MongoFactory.getInst().createCommand("db." + activeDocumentMap.get(EventWrapper.ACTIVE_COLL) + ".remove({_id:ObjectId(\"" + id + "\")},true)");
				mcmd.setConnection((String) activeDocumentMap.get(EventWrapper.ACTIVE_NAME));
				MongoFactory.getInst().submitExecution(mcmd);
			} catch (Exception e) {
				EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, e);
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
			EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, e);
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