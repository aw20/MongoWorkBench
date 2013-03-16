package net.jumperz.app.MMonjaDB.eclipse;

import java.io.InputStream;

import net.jumperz.app.MMonjaDBCore.event.MEventManager;
import net.jumperz.util.MStreamUtil;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class MUtil {
	// --------------------------------------------------------------------------------
	public static Image getImage(Device device, String imageFileName) {
		InputStream in = null;
		try {
			in = MStreamUtil.getResourceStream("net/jumperz/app/MMonjaDB/eclipse/resources/" + imageFileName);
			ImageData imageData = new ImageData(in);
			return new Image(device, imageData);
		} catch (Exception e) {
			MEventManager.getInstance().fireErrorEvent(e);
			return null;
		} finally {
			MStreamUtil.closeStream(in);
		}
	}

	// --------------------------------------------------------------------------------
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

	// --------------------------------------------------------------------------------
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

	// --------------------------------------------------------------------------------
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
	// --------------------------------------------------------------------------------
}