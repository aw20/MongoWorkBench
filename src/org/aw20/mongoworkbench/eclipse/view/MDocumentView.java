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


import org.aw20.mongoworkbench.Event;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.AggregateMongoCommand;
import org.aw20.mongoworkbench.command.FindMongoCommand;
import org.aw20.mongoworkbench.command.GroupMongoCommand;
import org.aw20.mongoworkbench.command.MapReduceMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.eclipse.view.table.QueryData;
import org.aw20.mongoworkbench.eclipse.view.table.TableManager;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class MDocumentView extends MAbstractView implements MongoCommandListener {
	public static enum NAVITEM {
		REFRESH, PAGE_BACK, PAGE_FORWARD, PAGE_START, PAGE_END
	};
	
	private Table table;
	private Text textJson;
	private TabFolder tabFolder;

	private TableManager	tableManager;
	private QueryData	queryData;
	private Action backAction, forwardAction, startAction, endAction, refreshAction;
	
	public MDocumentView(){
		MongoFactory.getInst().registerListener(this);
	}
	
	public void dispose() {
		MongoFactory.getInst().deregisterListener(this);
		super.dispose();
	}
	
	public void init2() {
		parent.setLayout( new FillLayout(SWT.HORIZONTAL) );
		
		tabFolder = new TabFolder(parent, SWT.NONE);

		TabItem tbtmTable = new TabItem(tabFolder, SWT.NONE);
		tbtmTable.setText("Table");
		
		table = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmTable.setControl(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableManager	= new TableManager( table, true );
		
		TabItem tbtmJson = new TabItem(tabFolder, SWT.NONE);
		tbtmJson.setText("JSON");
		
		textJson = new Text(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textJson.setTabs(2);
		textJson.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
		tbtmJson.setControl(textJson);

		
		refreshAction = new Action() {
			public void run() {
				onAction( NAVITEM.REFRESH );
			}
		};
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refresh");
		initAction(refreshAction, "control_repeat.png", null);

		startAction = new Action() {
			public void run() {
				onAction( NAVITEM.PAGE_START );
			}
		};
		startAction.setText("Start Page");
		startAction.setToolTipText("Start Page");
		initAction(startAction, "control_start.png", null);

		backAction = new Action() {
			public void run() {
				onAction( NAVITEM.PAGE_BACK );
			}
		};
		backAction.setText("Previous Page");
		backAction.setToolTipText("Previous Page");
		initAction(backAction, "control_rewind.png", null);

		forwardAction = new Action() {
			public void run() {
				onAction( NAVITEM.PAGE_FORWARD );
			}
		};
		forwardAction.setText("Next Page");
		forwardAction.setToolTipText("Next Page");
		initAction(forwardAction, "control_fastforward.png", null);
		

		endAction = new Action() {
			public void run() {
				onAction( NAVITEM.PAGE_END );
			}
		};
		endAction.setText("Last Page");
		endAction.setToolTipText("Last Page");
		initAction(endAction, "control_end.png", null);
		
		
		setActionStatus( false );
	}

	
	private void setActionStatus(boolean b) {
		refreshAction.setEnabled(b);
		backAction.setEnabled(b);
		forwardAction.setEnabled(b);
		startAction.setEnabled(b);
		endAction.setEnabled(b);
	}

	/**
	 * The buttons at the top of the window are being clicked
	 * @param refresh
	 */
	protected void onAction(NAVITEM refresh) {
		String cmd	= queryData.getCommand( refresh );
		if ( cmd == null )
			return;
		
		try {
			setActionStatus(false);
			MongoCommand mcmd = MongoFactory.getInst().createCommand(cmd);
			mcmd.setConnection( queryData.getActiveName(), queryData.getActiveDB() );
			MongoFactory.getInst().submitExecution(mcmd);
		} catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent( Event.EXCEPTION, e);
		}
	}

	@Override
	public void onMongoCommandStart(MongoCommand mcmd) {}

	@Override
	public void onMongoCommandFinished(MongoCommand mcmd) {
		if ( mcmd.getClass().getName().equals( FindMongoCommand.class.getName() ) ){
			onCommand( new QueryData( ((FindMongoCommand)mcmd) ), true );
		}	else if ( mcmd.getClass().getName().equals( GroupMongoCommand.class.getName() )
				|| mcmd.getClass().getName().equals( AggregateMongoCommand.class.getName() ) ){
			onCommand( new QueryData( ((GroupMongoCommand)mcmd).getResults(), null ), false );
		}else if ( mcmd.getClass().getName().equals( MapReduceMongoCommand.class.getName() ) ){
			if ( ((MapReduceMongoCommand)mcmd).getResults() != null )
				onCommand( new QueryData( ((MapReduceMongoCommand)mcmd).getResults(), null ), false );
		}
	}

	private void onCommand(QueryData data, final boolean enableButtons ) {
		queryData	= data;

		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if ( tabFolder.getSelectionIndex() == 0 )
					table.setVisible(false);
				
				textJson.setText( queryData.getJSON() );
				tableManager.redraw( queryData );
				
				if ( tabFolder.getSelectionIndex() == 0 )
					table.setVisible(true);

				setActionStatus(enableButtons);
			}
		});

	}
	
}