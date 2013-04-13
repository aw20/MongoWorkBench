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

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class MPerspectiveFactory1 implements IPerspectiveFactory {
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		layout.createFolder("top", 		IPageLayout.TOP, 0.3f, editorArea);
		layout.createFolder("middle", IPageLayout.TOP, 0.75f, editorArea);
		layout.createFolder("bottom", IPageLayout.TOP, 0.1f, editorArea);
		
		IFolderLayout topleftFolder 		= layout.createFolder("topleft", 		IPageLayout.LEFT, 0.2f, 	"top");
		IFolderLayout topcenterFolder 	= layout.createFolder("topcenter", 	IPageLayout.LEFT, 0.8f, 	"top");
		
		IFolderLayout bodyleft 		= layout.createFolder("bodyleft", 	IPageLayout.LEFT, 0.75f, 	"middle" );
		IFolderLayout bodyright 	= layout.createFolder("bodyright", 	IPageLayout.LEFT, 0.3f, 	"middle" );
		IFolderLayout bottomleft 	= layout.createFolder("bottomleft", IPageLayout.BOTTOM,	0.2f, "bottom"  );

		topleftFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MDBTree.class.getName());

		topcenterFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MCommandConsole.class.getName());
		topcenterFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MJavaScriptView.class.getName());

		bodyright.addView(net.jumperz.app.MMonjaDB.eclipse.view.MEditor.class.getName());

		bodyleft.addView(net.jumperz.app.MMonjaDB.eclipse.view.MDocumentView.class.getName());
		bodyleft.addView(net.jumperz.app.MMonjaDB.eclipse.view.MDBShowStatistics.class.getName());
		bodyleft.addView(net.jumperz.app.MMonjaDB.eclipse.view.MCollectionShowStatus.class.getName());

		bottomleft.addView(net.jumperz.app.MMonjaDB.eclipse.view.MHistoryView.class.getName());

		//bodyleft.addPlaceholder( net.jumperz.app.MMonjaDB.eclipse.view.MDocumentView.class.getName() + ":*" );
	
	}
}