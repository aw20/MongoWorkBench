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
package net.jumperz.app.MMonjaDB.eclipse.view;

import net.jumperz.app.MMonjaDB.eclipse.Activator;
import net.jumperz.app.MMonjaDBCore.MOutputView;
import net.jumperz.app.MMonjaDBCore.action.MAction;
import net.jumperz.app.MMonjaDBCore.action.MActionManager;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;

public class MActionView extends MAbstractView implements MOutputView {
	private Action executeAction;
	private Text textArea;

	public MActionView() {
		actionManager.register2(this);
	}

	public void dispose() {
		actionManager.removeObserver2(this);
		super.dispose();
	}

	private void executeActionsOnText() {
		String cmdText = textArea.getSelectionText();
		if ( cmdText.length() == 0 ){
			cmdText = textArea.getText();
		}
		
		cmdText = cmdText.trim();
		if ( cmdText.length() == 0 )
			return;

		MAction maction = MActionManager.getInstance().getAction(cmdText);
		if ( maction != null ){
			MActionManager.getInstance().submitForExecution(maction, this);
		}

		Activator.getDefault().saveWorkBench(textArea.getText());
	}

	public void init2() {
		menuManager = new MenuManager();
		textArea = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);

		textArea.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR && e.stateMask == SWT.SHIFT) {
					executeActionsOnText();
					e.doit = false;
				}
			}
		});
		
		String txt = Activator.getDefault().getWorkBench();
		if ( txt != null )
			textArea.setText(txt);
		
		menuManager.add(new Separator());

		// executeAction
		executeAction = new Action() {
			public void run() {
				executeActionsOnText();
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
		textArea.setFocus();
	}

	public void update(Object event, Object source) {}
}