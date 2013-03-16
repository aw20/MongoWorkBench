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
 */
package net.jumperz.app.MMonjaDB.eclipse.view;

import net.jumperz.app.MMonjaDB.eclipse.Activator;
import net.jumperz.app.MMonjaDB.eclipse.MUtil;
import net.jumperz.app.MMonjaDBCore.MAbstractLogAgent;
import net.jumperz.app.MMonjaDBCore.MConstants;
import net.jumperz.app.MMonjaDBCore.MDataManager;
import net.jumperz.app.MMonjaDBCore.MInputView;
import net.jumperz.app.MMonjaDBCore.action.MActionManager;
import net.jumperz.app.MMonjaDBCore.event.MEventManager;
import net.jumperz.util.MLogServer;
import net.jumperz.util.MProperties;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public abstract class MAbstractView extends ViewPart implements MConstants, Listener, MInputView {
	protected Shell shell;

	protected MProperties prop = MDataManager.getInstance().getProp();

	protected FormLayout formLayout;
	protected static FormData buttonFormData1, buttonFormData2, buttonFormData3;
	protected boolean initialized = false;
	protected Composite parent;
	protected MEventManager eventManager = MEventManager.getInstance();
	protected MActionManager actionManager = MActionManager.getInstance();
	protected MDataManager dataManager = MDataManager.getInstance();

	protected MenuManager menuManager;
	protected IMenuManager dropDownMenu;
	protected IToolBarManager toolBar;
	protected IActionBars actionBars;

	private MAbstractLogAgent logAgent = new MAbstractLogAgent() {};

	static {
		buttonFormData1 = new FormData(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonFormData1.right = new FormAttachment(100, BUTTON1_RIGHT);
		buttonFormData1.bottom = new FormAttachment(100, BUTTON1_BOTTOM);

		buttonFormData2 = new FormData(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonFormData2.right = new FormAttachment(100, BUTTON2_RIGHT);
		buttonFormData2.bottom = new FormAttachment(100, BUTTON2_BOTTOM);

		buttonFormData3 = new FormData(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonFormData3.right = new FormAttachment(100, BUTTON3_RIGHT);
		buttonFormData3.bottom = new FormAttachment(100, BUTTON3_BOTTOM);
	}

	
	public void executeAction(String actionStr) {
		actionManager.executeAction(actionStr, this);
	}

	
	public MAbstractView() {
	}

	
	protected boolean isActive() {
		return getSite().getPage().getActivePart().equals(this);
	}

	
	public void log(int logLevel, Object message) {
		logAgent.log(logLevel, message);
	}

	
	public void info(Object message) {
		log(MLogServer.log_info, message);
	}

	
	public void warn(Object message) {
		log(MLogServer.log_warn, message);
	}

	
	public void debug(Object message) {
		log(MLogServer.log_debug, message);
	}

	
	protected void setActionImage(Action action, String imageFileName) {
		Image image = MUtil.getImage(parent.getShell().getDisplay(), imageFileName);
		action.setImageDescriptor(ImageDescriptor.createFromImage(image));
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
		try {
			if (imageFileName != null) {
				setActionImage(action, imageFileName);
			}
			addActionToDropDownMenu(action);
			addActionToToolBar(action);

			if (menuManager != null) {
				menuManager.add(action);
			}
		} catch (Exception e) {
			eventManager.fireErrorEvent(e);
		}
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
	
}
