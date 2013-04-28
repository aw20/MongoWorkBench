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
 *  
 *  March 2013
 */
package org.aw20.mongoworkbench.eclipse.view.table;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.aw20.util.DateUtil;
import org.aw20.util.MSwtUtil;
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
		
		oidImage 			= MSwtUtil.getImage(device, "bullet_star.png");
		intImage 			= MSwtUtil.getImage(device, "bullet_blue.png");
		longImage 		= MSwtUtil.getImage(device, "bullet_red.png");
		doubleImage 	= MSwtUtil.getImage(device, "bullet_orange.png");
		stringImage 	= MSwtUtil.getImage(device, "bullet_green.png");
		dateImage 		= MSwtUtil.getImage(device, "bullet_white.png");
		boolImage 		= MSwtUtil.getImage(device, "bullet_yellow.png");
		listImage 		=	MSwtUtil.getImage(device, "stop_blue.png");
		mapImage 			= MSwtUtil.getImage(device, "stop_green.png");
		nullImage 		= MSwtUtil.getImage(device, "bullet_black.png");
		jsImage 			= MSwtUtil.getImage(device, "bullet_right.png");
		
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
		
		String[] keyArr = (String[])data.keySet().toArray(new String[0]);
		Arrays.sort(keyArr);
		
		for ( String key : keyArr ){
			if (!data.containsKey(key))
				continue;

			Object value = data.get(key);

			TreeItem newItem; 
			if ( parentItem == null )
				newItem = new TreeItem(tree, SWT.SINGLE);
			else
				newItem = new TreeItem(parentItem, SWT.SINGLE);

			String fieldName = parentFieldName + "." + key;
			if ( fieldName.startsWith(".") )
				fieldName = fieldName.substring(1);
			
			newItem.setData("fieldName", fieldName);
			newItem.setData("value", value);

			if (value == null) {
				newItem.setText(key);
				newItem.setImage(nullImage);
				setItemInfo(newItem, key, value);
			} else if (value instanceof Map) {
				newItem.setText(key);
				newItem.setImage(mapImage);
				newItem.setData("class", Map.class );
				drawItem(fieldName, newItem, (Map) value, expand);
			} else if (value instanceof List) {
				newItem.setText(key);
				newItem.setImage(listImage);
				newItem.setData("class", List.class );
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
			TreeItem newItem = new TreeItem(parentItem, SWT.SINGLE);
			Object value = data.get(i);

			String fieldName = parentFieldName + "." + i;
			if (fieldName.startsWith("."))
				fieldName = fieldName.substring(1);
			
			newItem.setData("fieldName", fieldName);
			newItem.setData("value", value);

			if (value == null) {
				newItem.setText("[" + i + "]");
				newItem.setImage(nullImage);
				setItemInfo(newItem, "[" + i + "]", value);
			} else if (value instanceof Map) {
				newItem.setText("[" + i + "]");
				newItem.setImage(mapImage);
				newItem.setData("class", Map.class );
				drawItem(fieldName, newItem, (Map) value, expand);
			} else if (value instanceof List) {
				newItem.setText("[" + i + "]");
				newItem.setImage(listImage);
				newItem.setData("class", List.class );
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
			treeItem.setData("class", Integer.class );
		} else if (value instanceof Long) {
			treeItem.setImage(longImage);
			treeItem.setText(2, "int64" );
			treeItem.setData("class", Long.class );
		} else if (value instanceof Double) {
			treeItem.setImage(doubleImage);
			treeItem.setText(2, "double" );	
			treeItem.setData("class", Double.class );
		} else if (value instanceof Date) {
			treeItem.setImage(dateImage);
			treeItem.setText(2, "date" );
			treeItem.setText(1, DateUtil.getSQLDate( (Date)value ) );
			treeItem.setData("class", Date.class );
		} else if (value instanceof String) {
			treeItem.setImage(stringImage);
			treeItem.setText(2, "string" );
			treeItem.setData("class", String.class );
		} else if (value instanceof ObjectId) {
			treeItem.setImage(oidImage);
			treeItem.setText(2, "objectId" );
			treeItem.setData("class", ObjectId.class );
		} else if (value instanceof Boolean) {
			treeItem.setImage(boolImage);
			treeItem.setText(2, "boolean" );
			treeItem.setData("class", Boolean.class );
		} else if (value instanceof Code) {
			treeItem.setImage(jsImage);
			treeItem.setText(2, "code" );
			treeItem.setData("class", Code.class );
		} else if (value instanceof Pattern) {
			treeItem.setImage(jsImage);
			treeItem.setText(2, "regex" );
			treeItem.setData("class", Pattern.class );
		} else if ( value instanceof byte[] ){
			treeItem.setImage(jsImage);
			treeItem.setText(2, "binary" );
			treeItem.setText(1, "[bin " + ((byte[])value).length + " bytes]" );
			treeItem.setData("class", Byte.class );
		}
	}
}