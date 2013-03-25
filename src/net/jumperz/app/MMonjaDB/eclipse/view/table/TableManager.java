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
package net.jumperz.app.MMonjaDB.eclipse.view.table;

import java.util.Map;

import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.EventWrapper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableManager implements Listener {
	private Table table; 
	private QueryData queryData;
	
	public TableManager( Table table ){
		this.table = table;
		
		table.addListener(SWT.MouseDoubleClick, this);
		table.addListener(SWT.Selection, this);
		table.addListener(SWT.KeyDown, this);
	}
	

	/**
	 * Clear all the columns and data
	 */
	public void clear() {
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; ++i) {
			columns[i].dispose();
		}
		table.removeAll();
		queryData	= null;
	}


	/**
	 * Repopulate the table
	 * @param queryData
	 */
	public void redraw(QueryData queryData) {
		table.setVisible(false);

		clear();

		this.queryData = queryData;

		if ( queryData.size() == 0 )
			return;
		
		// Do the columns
		String[]	columns	= queryData.getColumns();
		for (int i = 0; i < columns.length; ++i) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(columns[i]);
			column.pack();
		}

		// Do the rows
		for ( int r=0; r < queryData.size(); r++ ){
			Map	rowMap	= queryData.get(r);
			TableItem	item	= new TableItem( table, SWT.NONE );
			
			for (int i = 0; i < columns.length; ++i) {
				item.setText(i, queryData.getString( rowMap, columns[i] ) );
			}
			
		}
		
		for (int i = 0; i < columns.length; ++i) {
			table.getColumn(i).pack();
		}

		table.setVisible(true);
	}


	@Override
	public void handleEvent(Event event) {
		switch (event.type) {
			case SWT.KeyDown:
				break;
			case SWT.Selection:

				Map	rowMap	= queryData.get(table.getSelectionIndex());
				if ( rowMap != null ){
					Map eventMap	= EventWrapper.createMap( 
							EventWrapper.ACTIVE_NAME, queryData.getActiveName(), 
							EventWrapper.ACTIVE_DB, queryData.getActiveDB(),
							EventWrapper.ACTIVE_COLL, queryData.getActiveColl(),
							EventWrapper.DOC_DATA, rowMap
							);
					
					EventWorkBenchManager.getInst().onEvent( org.aw20.mongoworkbench.Event.DOCUMENT_VIEW, eventMap );
				}

				break;
		}
	}
	
}
