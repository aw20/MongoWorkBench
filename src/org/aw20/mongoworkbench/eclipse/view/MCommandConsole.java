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


import org.aw20.mongoworkbench.Event;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.FindMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.eclipse.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class MCommandConsole extends MAbstractView {
	private Action executeAction;
	private Text ConsoleArea1;
	private Text ConsoleArea2;

	public MCommandConsole() {
	}

	private void executeActionsOnText(Text textArea) {
		String cmdText = textArea.getSelectionText();
		if (cmdText.length() == 0) {
			cmdText = textArea.getText();
		}

		cmdText = cmdText.trim();
		if (cmdText.length() == 0)
			return;

		try {

			MongoCommand cmd = MongoFactory.getInst().createCommand(cmdText);
			if (cmd != null) {
				cmd.setConnection(MongoFactory.getInst().getActiveServer(), MongoFactory.getInst().getActiveDB());
				MongoFactory.getInst().submitExecution(cmd);
				
				if ( cmd instanceof FindMongoCommand ){
					Activator.getDefault().showView("org.aw20.mongoworkbench.eclipse.view.MDocumentView");		
				}

			} else
				throw new Exception("command not found");

		} catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, e);
		}

		Activator.getDefault().saveWorkBench(1, ConsoleArea1.getText());
		Activator.getDefault().saveWorkBench(2, ConsoleArea2.getText());
	}

	public void init2() {
		menuManager = new MenuManager();

		SashForm sashForm = new SashForm(parent, SWT.NONE);

		ConsoleArea1 = new Text(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		ConsoleArea2 = new Text(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		sashForm.setWeights(new int[] { 1, 1 });

		ConsoleArea1.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
		ConsoleArea1.setTabs(2);
		ConsoleArea1.setText(Activator.getDefault().getWorkBench(1));

		ConsoleArea1.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR && e.stateMask == SWT.SHIFT) {
					executeActionsOnText(ConsoleArea1);
					e.doit = false;
				}
			}
		});

		ConsoleArea2.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
		ConsoleArea2.setTabs(2);
		ConsoleArea2.setText(Activator.getDefault().getWorkBench(2));

		ConsoleArea2.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR && e.stateMask == SWT.SHIFT) {
					executeActionsOnText(ConsoleArea2);
					e.doit = false;
				}
			}
		});

		menuManager.add(new Separator());

		// executeAction
		executeAction = new Action() {
			public void run() {
				if ( ConsoleArea2.isFocusControl() )
					executeActionsOnText(ConsoleArea2);
				else
					executeActionsOnText(ConsoleArea1);
			}
		};

		executeAction.setToolTipText("Execute selected (or all) as a MongoDB command (SHIFT+ENTER)");
		executeAction.setText("Execute");
		setActionImage(executeAction, "bullet_go.png");
		addActionToToolBar(executeAction);
		executeAction.setEnabled(true);
		menuManager.add(executeAction);
	}

	public void setFocus() {
		ConsoleArea1.setFocus();
	}
}