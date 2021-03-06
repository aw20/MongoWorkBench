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


import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.CollectionStatsMongoCommand;
import org.aw20.mongoworkbench.command.DBStatsMongoCommand;
import org.aw20.mongoworkbench.command.DBserverStatsMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.eclipse.view.table.QueryData;
import org.aw20.mongoworkbench.eclipse.view.table.TableManager;
import org.aw20.mongoworkbench.eclipse.view.table.TreeRender;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

public class MDBShowStatistics extends MAbstractView implements MongoCommandListener {
	private TableManager tableManager;
	private TreeRender treeRender;
	private Table table;
	private Action refreshStatsAction, refreshRuntimeAction;
	private String activeServer = null;

	public MDBShowStatistics() {
		MongoFactory.getInst().registerListener(this);
	}

	public void dispose() {
		MongoFactory.getInst().deregisterListener(this);
		super.dispose();
	}

	public void init2() {
		parent.setLayout(new FillLayout(SWT.VERTICAL));
		
		SashForm sashForm = new SashForm(parent, SWT.SMOOTH);
		
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText("Database Status");
		
		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableManager	= new TableManager(table, false);
		
		
		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));

		Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
		lblNewLabel_1.setText("Server Status");
		
		Tree tree = new Tree(composite_1, SWT.BORDER);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeRender	= new TreeRender(parent.getDisplay(), tree, false );
		
		refreshStatsAction	= new Action(){
			public void run(){
				if ( activeServer == null ){
					activeServer	= MongoFactory.getInst().getActiveServer();
					if ( activeServer == null )
						return;
				}
				
				MongoFactory.getInst().submitExecution( new DBStatsMongoCommand().setConnection(activeServer) );
			}
		};
		refreshStatsAction.setText("Refresh Database Status");
		refreshStatsAction.setToolTipText("Refresh Database Status");
		initAction( refreshStatsAction, "clock_link.png", null );
		
		refreshRuntimeAction	= new Action(){
			public void run(){
				if ( activeServer == null ){
					activeServer	= MongoFactory.getInst().getActiveServer();
					if ( activeServer == null )
						return;
				}

				MongoFactory.getInst().submitExecution( new DBserverStatsMongoCommand().setConnection(activeServer) );
			}
		};
		refreshRuntimeAction.setText("Refresh Server Status");
		refreshRuntimeAction.setToolTipText("Refresh Server Status");
		initAction( refreshRuntimeAction, "clock.png", null );
	}

	@Override
	public void onMongoCommandStart(MongoCommand mcmd) {}

	@Override
	public void onMongoCommandFinished(MongoCommand mcmd) {

		if (mcmd instanceof DBStatsMongoCommand && !(mcmd instanceof CollectionStatsMongoCommand)) {
			onDbStatsUpdate((DBStatsMongoCommand) mcmd);
		} else if (mcmd instanceof DBserverStatsMongoCommand) {
			onDbServerStatus((DBserverStatsMongoCommand) mcmd);
		}

	}

	private void onDbServerStatus(final DBserverStatsMongoCommand mcmd) {
		activeServer	= mcmd.getName();
		
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				treeRender.render( mcmd.getStatus() );
			}
		});
		
	}

	private void onDbStatsUpdate(DBStatsMongoCommand mcmd) {
		activeServer	= mcmd.getName();

		final QueryData qData = new QueryData(mcmd.getStatsListMap(), "db");
		qData.addRightColumn("avgObjSize");
		qData.addRightColumn("fileSize");
		qData.addRightColumn("indexSize");
		qData.addRightColumn("objects");
		qData.addRightColumn("storageSize");
		qData.addRightColumn("dataSize");
		qData.addRightColumn("numExtents");
		qData.addRightColumn("indexes");
		qData.addRightColumn("nsSizeMB");
		qData.addRightColumn("collections");

		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				table.setVisible(false);
				tableManager.redraw(qData);
				table.setVisible(true);
			}
		});
	}
}
