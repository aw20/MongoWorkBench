package net.jumperz.app.MMonjaDB.eclipse.view;

import net.jumperz.app.MMonjaDB.eclipse.view.table.QueryData;
import net.jumperz.app.MMonjaDB.eclipse.view.table.TableManager;

import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.DBStatsMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;

public class MDBShowStatistics extends MAbstractView implements MongoCommandListener {
	private Table table;
	private TableManager	tableManager;


	public MDBShowStatistics() {
		MongoFactory.getInst().registerListener(this);
	}

	public void dispose() {
		MongoFactory.getInst().deregisterListener(this);
		super.dispose();
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public void init2() {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableManager	= new TableManager(table, false);
	}

	@Override
	public void onMongoCommandStart(MongoCommand mcmd) {}

	@Override
	public void onMongoCommandFinished(MongoCommand mcmd) {

		if ( mcmd instanceof DBStatsMongoCommand ){
			onDbStatsUpdate( (DBStatsMongoCommand)mcmd );
		}

	}
	
	private void onDbStatsUpdate(DBStatsMongoCommand mcmd){
		final QueryData qData = new QueryData(mcmd.getStatsListMap(),"db");
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
				tableManager.redraw( qData );
				table.setVisible(true);
			}
		});
	}
}
