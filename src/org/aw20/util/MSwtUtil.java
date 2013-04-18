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
package org.aw20.util;


import java.io.InputStream;

import org.aw20.io.StreamUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class MSwtUtil extends Object {

	public static Text createText( Composite comp ){
		Text txt = new Text(comp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txt.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
		txt.setTabs(2);
		return txt;
	}
	
	public static void addListenerToMenuItems(Menu menu, Listener listener) {
		MenuItem[] itemArray = menu.getItems();
		for (int i = 0; i < itemArray.length; ++i) {
			itemArray[i].addListener(SWT.Selection, listener);
		}
	}

	public static void copyToClipboard(String s) {
		Display display = Display.findDisplay(Thread.currentThread());
		Clipboard clipboard = new Clipboard(display);
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { s }, new Transfer[] { textTransfer });
		clipboard.dispose();
	}

	public static Image getImage(Device device, String imageFileName) {
		InputStream in = null;
		try {
			in = StreamUtil.getResourceStream("org/aw20/mongoworkbench/eclipse/resources/" + imageFileName);
			ImageData imageData = new ImageData(in);
			return new Image(device, imageData);
		} catch (Exception e) {
			return null;
		} finally {
			StreamUtil.closeStream(in);
		}
	}
	
}