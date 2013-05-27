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
 *  May 2013
 */
package org.aw20.mongoworkbench.eclipse.view.data;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.aw20.mongoworkbench.eclipse.view.MCommandConsole;
import org.aw20.util.FileUtil;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class ScratchPads implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 * Inner class to wrap up the name/text
	 */
	private class Pad implements Serializable {
		private static final long serialVersionUID = 1L;
		public String name = null, text = null;
		
		public Pad(String name, String text){
			this.name = name;
			this.text = text;
		}
		
	}
	
	private List<Pad>	padList;
	
	public ScratchPads(File file){
		ScratchPads	sp	= (ScratchPads) FileUtil.loadClass(file);
		if ( sp != null ){
			padList	= sp.padList;
		}else{
			padList	= new ArrayList<Pad>();
			padList.add( new Pad( "MyPad", "" ) );
		}
	}

	public ScratchPads(TabFolder tabFolder){
		padList	= new ArrayList<Pad>();
		
		TabItem[] tabs = tabFolder.getItems();
		for ( int x=0; x < tabs.length; x++ ){
			Pad	pad	= new Pad( tabs[x].getText(), ((Text)tabs[x].getControl()).getText().trim() );
			padList.add(pad);
		}
	}
	
	public void reset( MCommandConsole mconsole ){
		while ( mconsole.tabFolder.getItemCount() > 0 )
			mconsole.tabFolder.getItem(0).dispose();
		
		Iterator<Pad>	it	= padList.iterator();
		while ( it.hasNext() ){
			Pad	pad = it.next();
			mconsole.addPad( pad.name, pad.text );
		}
	}
	
	public void save( File file ){
		FileUtil.saveClass(file, this);
	}
}