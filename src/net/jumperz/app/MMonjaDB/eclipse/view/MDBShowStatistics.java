package net.jumperz.app.MMonjaDB.eclipse.view;

import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.DBStatsMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.command.ShowDbsMongoCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;

public class MDBShowStatistics extends MAbstractView implements MongoCommandListener {
	private Table table;

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
		System.out.println( mcmd.getStatsListMap() );
	}
}
