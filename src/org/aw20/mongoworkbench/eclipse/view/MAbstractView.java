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
 */
package org.aw20.mongoworkbench.eclipse.view;


import org.aw20.mongoworkbench.eclipse.Activator;
import org.aw20.mongoworkbench.eclipse.MConstants;
import org.aw20.util.MSwtUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public abstract class MAbstractView extends ViewPart implements MConstants, Listener {
	protected Shell shell;

	protected FormLayout formLayout;
	protected boolean initialized = false;
	protected Composite parent;

	protected MenuManager menuManager;
	protected IMenuManager dropDownMenu;
	protected IToolBarManager toolBar;
	protected IActionBars actionBars;

	public MAbstractView() {
	}
	
	protected void setActionImage(Action action, String imageFileName) {
		Image image = MSwtUtil.getImage(parent.getShell().getDisplay(), imageFileName);
		action.setImageDescriptor(ImageDescriptor.createFromImage(image));
	}

	public String getViewTitle(){
		return getPartName();
	}
	
	public void setViewTitle(String t){
		setPartName(t);
	}
	
	protected void addActionToDropDownMenu(Action action) {
		dropDownMenu.add(action);
	}

	
	protected void addActionToToolBar(Action action) {
		toolBar.add(action);
	}

	
	protected void initAction(Action action, String imageFileName) {
		initAction(action, imageFileName, null);
	}

	
	protected void initAction(Action action, String imageFileName, MenuManager menuManager) {
		if (imageFileName != null)
			setActionImage(action, imageFileName);

		addActionToDropDownMenu(action);
		addActionToToolBar(action);

		if (menuManager != null)
			menuManager.add(action);
	}

	
	public void createPartControl(Composite parent) {
		this.parent = parent;
		shell = parent.getShell();

		if (Activator.getDefault() != null) {
			Activator.getDefault().setShell(shell);
		}

		formLayout = new FormLayout();

		actionBars = getViewSite().getActionBars();
		toolBar = actionBars.getToolBarManager();
		dropDownMenu = actionBars.getMenuManager();

		init2();

		initialized = true;
	}

	
	protected void init2() {
	}

	
	public final void handleEvent(Event event) {
		handleEvent2(event);
	}

	
	protected void handleEvent2(Event event) {
	}
	
	public void setFocus(){}
}
