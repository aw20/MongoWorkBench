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

import java.util.List;
import java.util.Map;


import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.CollectionStatsMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.eclipse.view.table.QueryData;
import org.aw20.mongoworkbench.eclipse.view.table.TableManager;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;

public class MCollectionShowStatus extends MAbstractView implements MongoCommandListener  {
	private Table table;
	private TableManager	tableManager;
	
	public MCollectionShowStatus() {
		MongoFactory.getInst().registerListener(this);
	}

	public void dispose() {
		MongoFactory.getInst().deregisterListener(this);
		super.dispose();
	}

	public void init2() {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableManager	= new TableManager( table, false );
		
		Action refreshRuntimeAction	= new Action(){
			public void run(){
				MongoFactory.getInst().submitExecution( new CollectionStatsMongoCommand().setConnection( MongoFactory.getInst().getActiveServer(), MongoFactory.getInst().getActiveDB()) );
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
		
		if ( mcmd instanceof CollectionStatsMongoCommand ){
			onCollectionStats( (CollectionStatsMongoCommand)mcmd );
		}
		
	}

	private void onCollectionStats(CollectionStatsMongoCommand mcmd) {
		List<Map>	data	= mcmd.getStatsListMap();

		final QueryData	qData	= new QueryData( data, "ns" );
		qData.addRightColumn("avgObjSize");
		qData.addRightColumn("lastExtentSize");
		qData.addRightColumn("indexSizes");
		qData.addRightColumn("count");
		qData.addRightColumn("storageSize");
		qData.addRightColumn("dataSize");
		qData.addRightColumn("numExtents");
		qData.addRightColumn("indexes");
		qData.addRightColumn("size");
		qData.addRightColumn("totalIndexSize");

		
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				table.setVisible(false);
				tableManager.redraw(qData);
				table.setVisible(true);
			}
		});
	}
}
