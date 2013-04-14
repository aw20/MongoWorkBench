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
