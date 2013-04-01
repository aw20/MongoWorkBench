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
package net.jumperz.app.MMonjaDB.eclipse.view;

import java.util.List;
import java.util.Map;

import net.jumperz.app.MMonjaDB.eclipse.view.table.TreeRender;

import org.aw20.mongoworkbench.Event;
import org.aw20.mongoworkbench.EventWorkBenchListener;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.EventWrapper;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mongodb.DB;

public class MEditor extends MAbstractView implements EventWorkBenchListener {
	private Text textJSON;
	private Button btnNewButton;
	
	private Tree tree;
	private TreeRender treeRender;
	
	private Map	activeDocumentMap;
	
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
		
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE );
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		TabItem tbtmTreeItem = new TabItem(tabFolder, SWT.NONE);
		tbtmTreeItem.setText("Document");
		
		tree = new Tree(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmTreeItem.setControl(tree);
		treeRender	= new TreeRender(parent.getDisplay(),tree);
		
		TabItem tbtmJSONItem = new TabItem(tabFolder, SWT.NONE);
		tbtmJSONItem.setText("JSON");
		
		textJSON = new Text(tabFolder, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		textJSON.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
		textJSON.setTabs(3);
		tbtmJSONItem.setControl(textJSON);
		
		btnNewButton = new Button(parent, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onUpdate();
			}
		});
		btnNewButton.setEnabled(false);
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnNewButton.setText("update");
	}

	@Override
	public void onEventWorkBench(Event event, final Object data) {

		switch ( event ){
			case DOCUMENT_VIEW:{
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						redraw( (Map)data );
					}
				});
				break;
			}
			case ELEMENT_VIEW:{
				
				break;
			}
			default:
				break;
		}
		
	}


	private void redraw(Map data){
		btnNewButton.setEnabled(false);

		// Update the JSON
		textJSON.setText("");

		activeDocumentMap	= data;
		
		if ( activeDocumentMap.get(EventWrapper.DOC_DATA) instanceof Map || activeDocumentMap.get(EventWrapper.DOC_DATA) instanceof List ){
			DB db = MongoFactory.getInst().getMongoActiveDB();
			textJSON.setText( (String) db.eval("tojson(arguments[0])", new Object[] { activeDocumentMap.get(EventWrapper.DOC_DATA) }) );
		}

		// Update the tree
		treeRender.render((Map)activeDocumentMap.get(EventWrapper.DOC_DATA));
		
		btnNewButton.setEnabled(true);
	}
	
	
	private void onUpdate() {
		
		try {
			MongoCommand mcmd = MongoFactory.getInst().createCommand( "db." + activeDocumentMap.get(EventWrapper.ACTIVE_COLL) + ".save(" + textJSON.getText() + ")" );
			mcmd.setConnection( (String)activeDocumentMap.get(EventWrapper.ACTIVE_NAME) );
			MongoFactory.getInst().submitExecution(mcmd);
		} catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent( Event.EXCEPTION, e);
		}
		
	}

}
