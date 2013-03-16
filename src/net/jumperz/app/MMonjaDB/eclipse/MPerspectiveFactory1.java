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

import net.jumperz.app.MMonjaDBCore.MAbstractLogAgent;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class MPerspectiveFactory1 extends MAbstractLogAgent implements IPerspectiveFactory {
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		layout.createFolder("bottom", IPageLayout.BOTTOM, 0.35f, editorArea);
		IFolderLayout leftFolder 		= layout.createFolder("left", IPageLayout.LEFT, 0.22f, editorArea);
		IFolderLayout centerFolder 	= layout.createFolder("center", IPageLayout.LEFT, 0.68f, editorArea);
		//IFolderLayout rightFolder 	= layout.createFolder("right", IPageLayout.LEFT, 0.10f, editorArea);

		//IFolderLayout bottomLeftFolder 		= layout.createFolder("bottomLeft", IPageLayout.LEFT, 0.35f, "bottom");
		IFolderLayout bottomCenterFolder 	= layout.createFolder("bottomCenter", IPageLayout.LEFT, 0.75f, "bottom");
		IFolderLayout bottomRightFolder 	= layout.createFolder("bottomRight", IPageLayout.LEFT, 0.25f, "bottom");

		// IFolderLayout rightBottomFolder = layout.createFolder( "rightBottom", IPageLayout.BOTTOM, 0.75f, "right" );

		/*
		leftFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MDBTree.class.getName());
		centerFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MDocumentList.class.getName());
		rightFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MDocumentEditor.class.getName());

		bottomLeftFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MActionView.class.getName());

		bottomCenterFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MSavedActionsView.class.getName());

		bottomFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MJavaScriptView.class.getName());
		bottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);

		rightFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MJsonView.class.getName());

		centerFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MDBList.class.getName());
		centerFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MCollectionList.class.getName());
		*/
		
		leftFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MDBTree.class.getName());
		centerFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MActionView.class.getName());
		centerFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MHistoryView.class.getName());
		centerFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MJavaScriptView.class.getName());
	
		bottomCenterFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MDocumentList.class.getName());
		bottomCenterFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MDBList.class.getName());
		bottomCenterFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MCollectionList.class.getName());

		bottomRightFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MDocumentEditor.class.getName());
		bottomRightFolder.addView(net.jumperz.app.MMonjaDB.eclipse.view.MJsonView.class.getName());
		
	}
	
}
