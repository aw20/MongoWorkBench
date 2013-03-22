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

import net.jumperz.app.MMonjaDB.eclipse.view.table.QueryData;
import net.jumperz.app.MMonjaDB.eclipse.view.table.TableManager;

import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.FindMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class MDocumentView extends MAbstractView implements MongoCommandListener {
	private Table table;
	private Text textJson;

	private TableManager	tableManager;
	private QueryData	queryData;
	
	public MDocumentView(){
		MongoFactory.getInst().registerListener(this);
	}
	
	public void dispose() {
		MongoFactory.getInst().deregisterListener(this);
		super.dispose();
	}
	
	public void init2() {
		parent.setLayout( new FillLayout(SWT.HORIZONTAL) );
		
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		
		TabItem tbtmTable = new TabItem(tabFolder, SWT.NONE);
		tbtmTable.setText("Table");
		
		table = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmTable.setControl(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableManager	= new TableManager( table );
		
		TabItem tbtmJson = new TabItem(tabFolder, SWT.NONE);
		tbtmJson.setText("JSON");
		
		textJson = new Text(tabFolder, SWT.BORDER | SWT.MULTI);
		tbtmJson.setControl(textJson);

	}

	@Override
	public void onMongoCommandStart(MongoCommand mcmd) {}

	@Override
	public void onMongoCommandFinished(MongoCommand mcmd) {
		
		if ( mcmd instanceof FindMongoCommand ){
			onFindCommand( (FindMongoCommand)mcmd );			
		}
		
	}

	private void onFindCommand(FindMongoCommand mcmd) {
		queryData	= new QueryData( mcmd.getCursor() );
		
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				tableManager.redraw( queryData );
			}
		});
		
	}
	
}
