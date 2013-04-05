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
package net.jumperz.app.MMonjaDB.eclipse.view.table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.jumperz.app.MMonjaDB.eclipse.MUtil;

import org.aw20.util.DateUtil;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class TreeRender {
	private Tree	tree;
	
	private Image oidImage;
	private Image intImage;
	private Image doubleImage;
	private Image stringImage;
	private Image dateImage;
	private Image longImage;
	private Image boolImage;
	private Image listImage;
	private Image mapImage;
	private Image nullImage;
	private Image jsImage;

	public TreeRender(Device device, Tree tree ){
		this( device, tree, true );
	}
	
	public TreeRender(Device device, Tree tree, boolean showType ){
		this.tree = tree;
		
		oidImage 			= MUtil.getImage(device, "bullet_star.png");
		intImage 			= MUtil.getImage(device, "bullet_blue.png");
		longImage 		= MUtil.getImage(device, "bullet_red.png");
		doubleImage 	= MUtil.getImage(device, "bullet_orange.png");
		stringImage 	= MUtil.getImage(device, "bullet_green.png");
		dateImage 		= MUtil.getImage(device, "bullet_white.png");
		boolImage 		= MUtil.getImage(device, "bullet_yellow.png");
		listImage 		=	MUtil.getImage(device, "stop_blue.png");
		mapImage 			= MUtil.getImage(device, "stop_green.png");
		nullImage 		= MUtil.getImage(device, "bullet_black.png");
		jsImage 			= MUtil.getImage(device, "bullet_right.png");
		
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		TreeColumn trclmnNewColumn = new TreeColumn(tree, SWT.NONE);
		trclmnNewColumn.setWidth(120);
		trclmnNewColumn.setText("Key");

		TreeColumn trclmnNewColumn2 = new TreeColumn(tree, SWT.NONE);
		trclmnNewColumn2.setWidth(150);
		trclmnNewColumn2.setText("Value");
		
		if ( showType ){
			TreeColumn trclmnNewColumn3 = new TreeColumn(tree, SWT.NONE);	
			trclmnNewColumn3.setWidth(55);
			trclmnNewColumn3.setText("Type");
		}
		
		tree.pack();
	}
	
	
	public void	render( Object data ){
		tree.removeAll();
		
		if ( data == null )
			return;
		
		if ( data instanceof Map ){
			
			Map	map	= (Map)data;
			if ( map.size() == 0 )
				return;
			drawItem("", null, map, (map.size() < 35) );
			
		}else if (data instanceof List){
			
			List list	= (List)data;
			if ( list.size() == 0 )
				return;
			
			drawItem("", null, list, (list.size() < 35) );

		}
	}
	
	
	private void drawItem(String parentFieldName, TreeItem parentItem, Map data, boolean expand) {
		Iterator p = data.keySet().iterator();
		while (p.hasNext()) {
			String key = (String) p.next();
			if (!data.containsKey(key))
				continue;

			Object value = data.get(key);

			TreeItem newItem; 
			if ( parentItem == null )
				newItem = new TreeItem(tree, SWT.NONE);
			else
				newItem = new TreeItem(parentItem, SWT.NONE);

			String fieldName = parentFieldName + "." + key;
			if ( fieldName.startsWith(".") )
				fieldName = fieldName.substring(1);
			
			newItem.setData("fieldName", fieldName);
			newItem.setData("value", value);
			//fieldNameTreeItemMap.put(fieldName, newItem);

			if (value == null) {
				newItem.setText(key + " : null");
				newItem.setImage(nullImage);
			} else if (value instanceof Map) {
				newItem.setText(key);
				newItem.setImage(mapImage);
				drawItem(fieldName, newItem, (Map) value, expand);
			} else if (value instanceof List) {
				newItem.setText(key);
				newItem.setImage(listImage);
				drawItem(fieldName, newItem, (List) value, expand);
			} else {
				setItemInfo(newItem, key, value);
			}

			if (expand) {
				if ( parentItem != null )
					parentItem.setExpanded(expand);
				
				newItem.setExpanded(expand);
			}
		}
	}
	
	private void drawItem(String parentFieldName, TreeItem parentItem, List data, boolean expand) {
		for (int i = 0; i < data.size(); ++i) {
			TreeItem newItem = new TreeItem(parentItem, SWT.NONE);
			Object value = data.get(i);

			String fieldName = parentFieldName + "." + i;
			if (fieldName.startsWith("."))
				fieldName = fieldName.substring(1);
			
			newItem.setData("fieldName", fieldName);
			newItem.setData("value", value);
			//fieldNameTreeItemMap.put(fieldName, newItem);

			if (value instanceof Map) {
				newItem.setText("[" + i + "]");
				newItem.setImage(mapImage);
				drawItem(fieldName, newItem, (Map) value, expand);
			} else if (value instanceof List) {
				newItem.setText("[" + i + "]");
				newItem.setImage(listImage);
				drawItem(fieldName, newItem, (List) value, expand);
			} else {
				setItemInfo(newItem, "[" + i + "]", value);
			}

			if (expand) {
				if ( parentItem != null )
					parentItem.setExpanded(expand);

				newItem.setExpanded(expand);
			}
		}
	}

	
	private void setItemInfo(TreeItem treeItem, String key, Object value) {
		treeItem.setText(0, key);
		treeItem.setText(1, String.valueOf(value) );
		
		if (value instanceof Integer) {
			treeItem.setImage(intImage);
			treeItem.setText(2, "int32" );	
		} else if (value instanceof Double) {
			treeItem.setImage(doubleImage);
			treeItem.setText(2, "double" );	
		} else if (value instanceof Long) {
			treeItem.setImage(longImage);
			treeItem.setText(2, "int64" );
		} else if (value instanceof Date) {
			treeItem.setImage(dateImage);
			treeItem.setText(2, "date" );
			treeItem.setText(1, DateUtil.getSQLDate( (Date)value ) );
		} else if (value instanceof String) {
			treeItem.setImage(stringImage);
			treeItem.setText(2, "string" );
		} else if (value instanceof ObjectId) {
			treeItem.setImage(oidImage);
			treeItem.setText(2, "objectId" );
		} else if (value instanceof Boolean) {
			treeItem.setImage(boolImage);
			treeItem.setText(2, "boolean" );
		} else if (value instanceof Code) {
			treeItem.setImage(jsImage);
			treeItem.setText(2, "code" );
		} else if (value instanceof Pattern) {
			treeItem.setImage(jsImage);
			treeItem.setText(2, "regex" );
		} else if ( value instanceof byte[] ){
			treeItem.setImage(jsImage);
			treeItem.setText(2, "binary" );
			treeItem.setText(1, "[bin " + ((byte[])value).length + " bytes]" );
		}
	}
}