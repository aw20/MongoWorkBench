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
 */
package org.aw20.mongoworkbench.eclipse.view.table;

import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableSorter {
	private final Table table;

	public TableSorter(Table table) {
		this.table = table;
		addColumnSelectionListeners();
	}

	private void addColumnSelectionListeners() {
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			addColumnSelectionListener(columns[i]);
		}
	}

	private void addColumnSelectionListener(TableColumn column) {
		column.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event e) {
      	tableColumnClicked((TableColumn) e.widget);
      }
		});
	}

	private void tableColumnClicked(TableColumn column) {
		Table table = column.getParent();
		if (column.equals(table.getSortColumn())) {
			table.setSortDirection(table.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
		} else {
			table.setSortColumn(column);
			table.setSortDirection(SWT.UP);
		}
		
		// Now sort the table
		int index = Arrays.asList(table.getColumns()).indexOf(table.getSortColumn());
		TableItem[] items = table.getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());
		
		for (int i = 1; i < items.length; i++) {
			String value1 = items[i].getText(index);
			for (int j = 0; j < i; j++) {
				String value2 = items[j].getText(index);
				
				int result = table.getSortDirection() == SWT.UP ? collator.compare(value1, value2) : -collator.compare(value1, value2);
				
				if (result < 0) {
					String[] values = new String[ table.getColumnCount() ];
					for ( int x=0; x < values.length; x++ )
						values[x]	= items[i].getText(x);

					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
	}
}
