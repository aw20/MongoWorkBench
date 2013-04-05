package net.jumperz.app.MMonjaDB.eclipse.view;

import net.jumperz.app.MMonjaDB.eclipse.view.table.QueryData;
import net.jumperz.app.MMonjaDB.eclipse.view.table.TableManager;
import net.jumperz.app.MMonjaDB.eclipse.view.table.TreeRender;

import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.CollectionStatsMongoCommand;
import org.aw20.mongoworkbench.command.DBStatsMongoCommand;
import org.aw20.mongoworkbench.command.DBserverStatsMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
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
				if ( activeServer == null )
					return;
				
				MongoFactory.getInst().submitExecution( new DBStatsMongoCommand().setConnection(activeServer) );
			}
		};
		refreshStatsAction.setText("Refresh Database Status");
		refreshStatsAction.setToolTipText("Refresh Database Status");
		initAction( refreshStatsAction, "clock_link.png", null );
		
		refreshRuntimeAction	= new Action(){
			public void run(){
				if ( activeServer == null )
					return;

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
