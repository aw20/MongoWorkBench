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
 *  
 *  March 2013
 */
package org.aw20.mongoworkbench.eclipse.view;


import org.aw20.mongoworkbench.EventWorkBenchListener;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.FindMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.eclipse.Activator;
import org.aw20.util.DateUtil;
import org.aw20.util.MSwtUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


/**
 * Manages the Command History
 */
public class MHistoryView extends MAbstractView implements MongoCommandListener, EventWorkBenchListener {

	public MHistoryView() {
		MongoFactory.getInst().registerListener(this);
		EventWorkBenchManager.getInst().registerListener(this);
	}

	private Table table;
	private Action redoAction, copyAction, copyMessageAction, toWizardAction;
	private Image	imgGood = null, imgBad = null;

	private static int	COLUMN_STATUS = 0;
	private static int	COLUMN_ORDER = 1;
	private static int	COLUMN_TIME = 2;
	private static int	COLUMN_DATABASE = 3;
	private static int	COLUMN_ACTION = 4;
	private static int	COLUMN_MESSAGE = 5;
	private static int	COLUMN_MS = 6;
	
	private static String PROP_NAME 		= "nm";
	private static String PROP_DB 			= "db";
	private static String PROP_SUCCESS 	= "sc";
	
	public void dispose() {
		MongoFactory.getInst().deregisterListener(this);
		EventWorkBenchManager.getInst().deregisterListener(this);
		super.dispose();
	}

	private TableColumn createColumn(Table table, int style, int width, String colname ){
		TableColumn column = new TableColumn(table, style);
		column.setWidth(width);
		column.setText(colname);
		return column;
	}
	
	public void init2() {
		imgGood	= MSwtUtil.getImage(shell.getDisplay(), "bullet_green.png");
		imgBad	= MSwtUtil.getImage(shell.getDisplay(), "bullet_red.png");
		table 	= new Table(parent, SWT.BORDER | SWT.FULL_SELECTION );
		
		// Add in the listener
		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					e.doit = false;
					if ((e.stateMask & SWT.SHIFT) != 0){// Shift + Enter
						executeSelectedAction();
					}
				}
			}
		});
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		int columnWidths[] = new int[]{20,35,60,90,300,500,60};
		createColumn( table, SWT.CENTER, 	columnWidths[COLUMN_STATUS],	"" );
		createColumn( table, SWT.RIGHT, 	columnWidths[COLUMN_ORDER], 	"#" );
		createColumn( table, SWT.LEFT, 		columnWidths[COLUMN_TIME], 		"Time" );
		createColumn( table, SWT.LEFT, 		columnWidths[COLUMN_DATABASE],"DB" );
		createColumn( table, SWT.LEFT, 		columnWidths[COLUMN_ACTION], 	"Command" );
		createColumn( table, SWT.LEFT, 		columnWidths[COLUMN_MESSAGE], "Message" );
		createColumn( table, SWT.RIGHT,		columnWidths[COLUMN_MS], 			"ExeTime" );

		/**
		 * Resizes the column when the window resizes
		 */
		table.addControlListener(new ControlAdapter() {
	    public void controlResized(ControlEvent e) {
	      Rectangle area = table.getClientArea();
	      int width = area.width;

	      TableColumn[] columns = table.getColumns();
	      int columnWidth = 0;
	      for ( int x=0; x<columns.length; x++ )
	      	columnWidth += columns[x].getWidth();

	      int diff = width - columnWidth;
	      columns[COLUMN_ACTION].setWidth( columns[COLUMN_ACTION].getWidth() + diff );
	    }
	  });
		
		table.addListener(SWT.MouseDoubleClick, this);

		menuManager = new MenuManager();
		Menu contextMenu = menuManager.createContextMenu(table);
		table.setMenu(contextMenu);

		// executeTableAction
		redoAction = new Action() {
			public void run() {
				executeSelectedAction();
			}
		};
		redoAction.setToolTipText("ReRun Selected Command");
		redoAction.setText("ReRun Command\tShift+Enter");
		initAction(redoAction, "table_go.png", menuManager);
		redoAction.setEnabled(false);

		dropDownMenu.add(new Separator());
		menuManager.add(new Separator());

		// copyAction
		copyAction = new Action() {
			public void run() {
				copyActionToClipboard(COLUMN_ACTION);
			}
		};
		copyAction.setToolTipText("Copy Command to Clipboard");
		copyAction.setText("Copy Command");
		setActionImage(copyAction, "page_copy.png");
		addActionToToolBar(copyAction);
		copyAction.setEnabled(false);
		dropDownMenu.add(copyAction);
		menuManager.add(copyAction);

		
		// copyMessageAction
		copyMessageAction = new Action() {
			public void run() {
				copyActionToClipboard(COLUMN_MESSAGE);
			}
		};
		copyMessageAction.setToolTipText("Copy Message to Clipboard");
		copyMessageAction.setText("Copy Message");
		setActionImage(copyMessageAction, "page_white_paste_table.png");
		addActionToToolBar(copyMessageAction);
		copyMessageAction.setEnabled(false);
		
		dropDownMenu.add(copyMessageAction);
		menuManager.add(copyMessageAction);

		dropDownMenu.add(new Separator());
		menuManager.add(new Separator());
		

		// copyMessageAction
		toWizardAction = new Action() {
			public void run() {
				onToWizard();
			}
		};
		toWizardAction.setToolTipText("Open in Wizard");
		toWizardAction.setText("Open in Wizard");
		setActionImage(toWizardAction, "page_edit.png");
		addActionToToolBar(toWizardAction);
		toWizardAction.setEnabled(false);
		
		dropDownMenu.add(toWizardAction);
		menuManager.add(toWizardAction);
	}


	private void onToWizard() {
		int s = table.getSelectionIndex();
		if ( s < 0 )
			return;
		
		Activator.getDefault().showView( MCommandWizardView.class.getName() );
		
		TableItem	row	= table.getItem(s);
		EventWorkBenchManager.getInst().onEvent( org.aw20.mongoworkbench.Event.TOWIZARD, row.getText(COLUMN_ACTION) );
	}

	private void copyActionToClipboard(int column) {
		int s = table.getSelectionIndex();
		if ( s < 0 )
			return;
		
		TableItem	row	= table.getItem(s);
		MSwtUtil.copyToClipboard( row.getText(column) );
	}


	private void executeSelectedAction() {
		int s = table.getSelectionIndex();
		if ( s < 0 )
			return;
		
		TableItem	row	= table.getItem(s);
		
		// only want to do the successful commands
		if ( row.getData(PROP_SUCCESS) == null || !(Boolean)row.getData(PROP_SUCCESS) )
			return;
		
		String cmdText 	= row.getText(COLUMN_ACTION);
		try {

			MongoCommand cmd = MongoFactory.getInst().createCommand(cmdText);
			if (cmd != null) {
				cmd.setConnection( (String)row.getData(PROP_NAME), (String)row.getData(PROP_DB) );
				MongoFactory.getInst().submitExecution(cmd);
				
				if ( cmd instanceof FindMongoCommand ){
					Activator.getDefault().showView("org.aw20.mongoworkbench.eclipse.view.MDocumentView");		
				}

			} else
				throw new Exception("command not found");

		} catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent(org.aw20.mongoworkbench.Event.EXCEPTION, e);
		}
	}
	
	
	protected void handleEvent2(Event event) {
		if (event.widget == table) {
			switch (event.type) {
				case SWT.MouseDoubleClick:
					executeSelectedAction();
					break;
			}
		}
	}


	/**
	 * Update the table with the entry; if e == MEvent.MEVENT_EXECUTION_START then we are inserting
	 * into the table; otherwise update the entry
	 * 
	 * @param event
	 * @param maction
	 */
	private void updateTable(MongoCommand mcmd, boolean bStart) {
		
		// Enable the action icons
		if ( table.getItemCount() == 1 ){
			copyAction.setEnabled(true);
			redoAction.setEnabled(true);
			copyMessageAction.setEnabled(true);
			toWizardAction.setEnabled(true);
		}
		
		
		// Process the events
		if ( bStart ){
			
			TableItem item = new TableItem(table, SWT.NONE);
			
			item.setText( COLUMN_ORDER, String.valueOf( table.getItemCount() ) );
			item.setText( COLUMN_TIME, DateUtil.getDateString( System.currentTimeMillis(), "HH:mm:ss") );
			item.setText( COLUMN_DATABASE, (mcmd.getDB() != null) ? mcmd.getDB() : "" );
			item.setText( COLUMN_ACTION, mcmd.getCommandString() );
			item.setText( COLUMN_MESSAGE, "executing..." );
			item.setText( COLUMN_MS, "" );
			
			item.setData( mcmd.hashCode() );
			item.setData( PROP_NAME, mcmd.getName() );
			item.setData( PROP_DB, mcmd.getDB() );
			table.showItem(item);
			
		} else {

			// find the entry
			TableItem item = null;
			for ( int x=0; x < table.getItemCount(); x++ ){
				TableItem row = table.getItem(x);
				if ( row.getData() != null && (Integer)row.getData() == mcmd.hashCode() ){
					item = row;
					break;
				}
			}
			
			if ( item == null ) // we didn't find the row; this should never happen
				return;
			
			if ( !mcmd.isSuccess() ){
				item.setImage(COLUMN_STATUS, 	imgBad);
				item.setText( COLUMN_MESSAGE, mcmd.getExceptionMessage() );
			}else{
				item.setImage(COLUMN_STATUS, 	imgGood);
				item.setText( COLUMN_MESSAGE, mcmd.getMessage() );
			}

			item.setText( COLUMN_MS, String.valueOf(mcmd.getExecTime()) + " ms" );
			item.setData( PROP_SUCCESS, mcmd.isSuccess() );

			table.showItem(item);
		}

	}

	@Override
	public void onMongoCommandStart(final MongoCommand mcmd) {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				updateTable( mcmd, true );
			}
		});
	}

	@Override
	public void onMongoCommandFinished(final MongoCommand mcmd) {

		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				updateTable( mcmd, false );
			}
		});
		
	}

	@Override
	public void onEventWorkBench(org.aw20.mongoworkbench.Event event, Object data) {
		
		if ( event != org.aw20.mongoworkbench.Event.EXCEPTION )
			return;
		
		final Exception exception = (Exception)data;
		
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				
				TableItem item = new TableItem(table, SWT.NONE);
				
				item.setImage(COLUMN_STATUS, 	imgBad);
				item.setText( COLUMN_ORDER, String.valueOf( table.getItemCount() ) );
				item.setText( COLUMN_TIME, DateUtil.getDateString( System.currentTimeMillis(), "HH:mm:ss") );
				item.setText( COLUMN_DATABASE, "" );
				item.setText( COLUMN_ACTION, exception.getMessage() );
				item.setText( COLUMN_MESSAGE, "Exception" );
				item.setText( COLUMN_MS, "" );
				
				item.setData(0);
				table.showItem(item);
			}
		});
		
	}

}
