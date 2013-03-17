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
package net.jumperz.app.MMonjaDB.eclipse.view;

import net.jumperz.app.MMonjaDB.eclipse.MUtil;
import net.jumperz.app.MMonjaDBCore.MOutputView;
import net.jumperz.app.MMonjaDBCore.action.MAction;
import net.jumperz.app.MMonjaDBCore.action.MActionManager;
import net.jumperz.app.MMonjaDBCore.event.MEvent;
import net.jumperz.gui.MSwtUtil;

import org.aw20.util.DateUtil;
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
 * 
 * @author alan
 *
 */
public class MHistoryView extends MAbstractView implements MOutputView {

	public MHistoryView() {
		actionManager.register2(this);
	}

	private Table table;
	private Action redoAction;
	private Action copyAction;
	private Image	imgGood = null, imgBad = null;

	private static int	COLUMN_STATUS = 0;
	private static int	COLUMN_ORDER = 1;
	private static int	COLUMN_TIME = 2;
	private static int	COLUMN_ACTION = 3;
	private static int	COLUMN_MESSAGE = 4;
	private static int	COLUMN_MS = 5;
	
	
	public void dispose() {
		actionManager.removeObserver2(this);
		super.dispose();
	}

	private TableColumn createColumn(Table table, int style, int width, String colname ){
		TableColumn column = new TableColumn(table, style);
		column.setWidth(width);
		column.setText(colname);
		return column;
	}
	
	public void init2() {
		imgGood	= MUtil.getImage(shell.getDisplay(), "bullet_green.png");
		imgBad	= MUtil.getImage(shell.getDisplay(), "bullet_red.png");
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

		int columnWidths[] = new int[]{20,35,60,400,300,60};
		createColumn( table, SWT.CENTER, 	columnWidths[COLUMN_STATUS],	"" );
		createColumn( table, SWT.RIGHT, 	columnWidths[COLUMN_ORDER], 	"#" );
		createColumn( table, SWT.LEFT, 		columnWidths[COLUMN_TIME], 		"Time" );
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
		redoAction.setToolTipText("Redo Selected Command");
		redoAction.setText("Redo\tShift+Enter");
		initAction(redoAction, "table_go.png", menuManager);
		redoAction.setEnabled(false);

		dropDownMenu.add(new Separator());
		menuManager.add(new Separator());

		// copyAction
		copyAction = new Action() {
			public void run() {
				copyActionToClipboard();
			}
		};
		copyAction.setToolTipText("Copy Command to Clipboard");
		copyAction.setText("Copy");
		setActionImage(copyAction, "page_copy.png");
		addActionToToolBar(copyAction);
		copyAction.setEnabled(false);
		
		dropDownMenu.add(copyAction);
		menuManager.add(copyAction);
	}


	private void copyActionToClipboard() {
		// 
		int s = table.getSelectionIndex();
		if ( s < 0 )
			return;
		
		TableItem	row	= table.getItem(s);
		
		StringBuffer buf = new StringBuffer(256)
			.append( row.getText(COLUMN_ACTION) )
			.append( "\r\n" )
			.append( row.getText(COLUMN_MESSAGE) )
			.append( "\r\nTime " )
			.append( row.getText(COLUMN_TIME) )
			.append( "; " )
			.append( row.getText(COLUMN_MS) )
			;

		MSwtUtil.copyToClipboard(buf.toString());
	}


	private void executeSelectedAction() {
		int s = table.getSelectionIndex();
		if ( s < 0 )
			return;
		
		TableItem	row	= table.getItem(s);
		String action = row.getText(COLUMN_ACTION);
		
		MAction maction = MActionManager.getInstance().getAction(action);
		if ( maction != null ){
			MActionManager.getInstance().submitForExecution(maction, this);
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
	private void updateTable(MEvent event, MAction maction) {
		
		// Enable the action icons
		if ( table.getItemCount() == 1 ){
			copyAction.setEnabled(true);
			redoAction.setEnabled(true);
		}
		
		
		// Process the events
		if ( event == MEvent.MEVENT_EXECUTION_START ){
			
			TableItem item = new TableItem(table, SWT.NONE);
			
			item.setText( COLUMN_ORDER, String.valueOf( table.getItemCount() ) );
			item.setText( COLUMN_TIME, DateUtil.getDateString( System.currentTimeMillis(), "HH:mm:ss") );
			item.setText( COLUMN_ACTION, maction.getCmd() );
			item.setText( COLUMN_MESSAGE, "executing..." );
			item.setText( COLUMN_MS, "" );
			
			item.setData( maction.hashCode() );
			table.showItem(item);
			
		} else if ( event == MEvent.MEVENT_EXECUTION_FINISHED ){

			// find the entry
			TableItem item = null;
			for ( int x=0; x < table.getItemCount(); x++ ){
				TableItem row = table.getItem(x);
				if ( (int)row.getData() == maction.hashCode() ){
					item = row;
					break;
				}
			}
			
			if ( item == null ) // we didn't find the row; this should never happen
				return;
			
			if ( maction.getExecException() != null ){
				item.setImage(COLUMN_STATUS, imgBad);
				item.setText( COLUMN_MESSAGE, maction.getExecException().getMessage() );
			}else{
				item.setImage(COLUMN_STATUS, imgGood);
				item.setText( COLUMN_MESSAGE, maction.getMessage() == null ? "" : maction.getMessage() );
			}

			item.setText( COLUMN_MS, String.valueOf(maction.getTimeMS()) + " ms" );
			
			table.showItem(item);
		}

	}
	

	
	/**
	 * An event has been triggered, so we must now update our table with this data
	 */
	public void update(final Object e, final Object source) {

		if ( e == MEvent.MEVENT_EXECUTION_START ){
			
			shell.getDisplay().asyncExec(new Runnable() {
				public void run() {
					updateTable( MEvent.MEVENT_EXECUTION_START, (MAction)source );
				}
			});

		}else if ( e == MEvent.MEVENT_EXECUTION_FINISHED ){

			shell.getDisplay().asyncExec(new Runnable() {
				public void run() {
					updateTable( MEvent.MEVENT_EXECUTION_FINISHED, (MAction)source );
				}
			});

		}
	}

}
