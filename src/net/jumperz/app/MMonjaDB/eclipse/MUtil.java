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
 *  https://github.com/aw20/MonjaDB
 *  Original fork: https://github.com/Kanatoko/MonjaDB
 *  
 */
package net.jumperz.app.MMonjaDB.eclipse;

import java.io.InputStream;

import org.aw20.io.StreamUtil;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class MUtil {

	public static Image getImage(Device device, String imageFileName) {
		InputStream in = null;
		try {
			in = StreamUtil.getResourceStream("net/jumperz/app/MMonjaDB/eclipse/resources/" + imageFileName);
			ImageData imageData = new ImageData(in);
			return new Image(device, imageData);
		} catch (Exception e) {
			return null;
		} finally {
			StreamUtil.closeStream(in);
		}
	}

	public static TreeItem getTreeItemByDbName(Tree tree, String dbName) {
		if (tree.getItemCount() == 0) {
			return null;
		}
		TreeItem mongoItem = tree.getItem(0);
		TreeItem[] items = mongoItem.getItems();
		if (items != null) {
			for (int i = 0; i < items.length; ++i) {
				if (items[i].getText().equals(dbName)) {
					return items[i];
				}
			}
		}
		return null;
	}

	public static TreeItem getTreeItemByDbAndCollName(Tree tree, String dbName, String collName) {
		TreeItem dbItem = getTreeItemByDbName(tree, dbName);
		if (dbItem != null) {
			TreeItem[] items = dbItem.getItems();
			for (int i = 0; i < items.length; ++i) {
				if (items[i].getText().equals(collName)) {
					return items[i];
				}
			}
		}
		return null;
	}

	public static boolean treeItemSelected(Tree tree, TreeItem treeItem) {
		if (treeItem == null) {
			return false;
		}
		if (tree == null) {
			return false;
		}
		TreeItem[] selected = tree.getSelection();
		if (selected != null && selected.length == 1 && selected[0] == treeItem) {
			return true;
		} else {
			return false;
		}
	}

}