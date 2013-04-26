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
 *  April 2013
 */
package org.aw20.mongoworkbench.eclipse.view.table;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.aw20.util.DateUtil;
import org.aw20.util.StringUtil;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeWalker {

	/**
	 * Walks the Tree creating the object model 
	 * 
	 * @param tree
	 * @return
	 */
	public static Map<String, Object> toObject( Tree tree ){
		return fromMap( tree.getItems() );
	}
	
	
	private static Object toObject(TreeItem item){
		Class nodeClass = (Class)item.getData("class");
		
		if ( nodeClass == String.class ){
			return item.getText(1);
		} else if ( nodeClass == Boolean.class ){
			return StringUtil.toBoolean( item.getText(1), false );
		} else if ( nodeClass == Integer.class ){
			return StringUtil.toInteger( item.getText(1), Integer.MIN_VALUE);
		} else if ( nodeClass == Long.class ){
			return StringUtil.toLong(item.getText(1), Long.MIN_VALUE);
		} else if ( nodeClass == Double.class ){
			return StringUtil.toDouble(item.getText(1), Double.MIN_VALUE);
		} else if ( nodeClass == Date.class ){
			return DateUtil.parseDate(item.getText(1), "yyyy-MM-dd HH:mm:ss");
		} else if ( nodeClass == Code.class ){
			return new Code( item.getText(1) );
		} else if ( nodeClass == ObjectId.class ){
			return new ObjectId( item.getText(1) );
		} else if ( nodeClass == Pattern.class ){
			return Pattern.compile(item.getText(1));
		} else if ( nodeClass == Byte.class ){
		//TODO
			return null;
		} else
			return item.getText(1);
	}
	
	
	private static Map<String, Object> fromMap(TreeItem items[]){
		Map<String, Object>	map	= new HashMap<String, Object>();
		
		for ( int x=0; x<items.length; x++ ){
			TreeItem item	= items[x];
			
			Class nodeClass = (Class)item.getData("class");
			
			if ( nodeClass == null ){
				map.put( item.getText(0), null);
			} else if ( nodeClass == Map.class ){
				map.put( item.getText(0), fromMap(item.getItems()) );
			} else if ( nodeClass == List.class ){
				map.put( item.getText(0), fromList(item.getItems()) );
			} else {
				map.put( item.getText(0), toObject(item) );
			}

		}
		
		return map;
	}
	
	
	private static List<Object> fromList(TreeItem items[]){
		List<Object>	list = new ArrayList<Object>();
		
		for ( int x=0; x<items.length; x++ ){
			TreeItem item	= items[x];
			
			Class nodeClass = (Class)item.getData("class");
			
			if ( nodeClass == null ){
				list.add(null);
			} else if ( nodeClass == Map.class ){
				list.add( fromMap(item.getItems()) );
			} else if ( nodeClass == List.class ){
				list.add( fromList(item.getItems()) );
			} else {
				list.add( toObject(item) );
			}

		}

		return list;
	}
		
}