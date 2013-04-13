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
 */
package net.jumperz.gui;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class MSwtUtil {
	
	public static void removeMenuItems(Menu menu) {
		MenuItem[] items = menu.getItems();
		for (int i = 0; i < items.length; ++i) {
			items[i].dispose();
		}
	}

	
	public static void addListenerToMenuItems(Menu menu, Listener listener) {
		MenuItem[] itemArray = menu.getItems();
		for (int i = 0; i < itemArray.length; ++i) {
			itemArray[i].addListener(SWT.Selection, listener);
		}
	}

	public static void addListenerToTreeColumns2(Tree tree, Listener listener) {
		TreeColumn[] columns = tree.getColumns();
		for (int i = 0; i < columns.length; ++i) {
			columns[i].addListener(SWT.Resize, listener);
			columns[i].addListener(SWT.Selection, listener);
		}
	}

	
	public static void addListenerToTableColumns2(Table table, Listener listener) {
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; ++i) {
			columns[i].addListener(SWT.Resize, listener);
			columns[i].addListener(SWT.Selection, listener);
		}
	}

	
	public static void addListenerToTableColumns(Table table, Listener listener) {
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; ++i) {
			columns[i].addListener(SWT.Resize, listener);
		}
	}

	
	public static java.util.List getTreeColumns(Tree tree) {
		return Arrays.asList(tree.getColumns());
	}

	
	public static java.util.List getTableColumns(Table table) {
		return Arrays.asList(table.getColumns());
	}

	
	public static void copyToClipboard(String s) {
		Display display = Display.findDisplay(Thread.currentThread());
		Clipboard clipboard = new Clipboard(display);
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { s }, new Transfer[] { textTransfer });
		clipboard.dispose();
	}
	
}