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
package net.jumperz.app.MMonjaDB.eclipse.view;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Text;

public class MJavaScriptView extends MAbstractView {
	private Text text1, text2;
	private Action executeAction;
	private Action clearAction;
	private SashForm sashForm;
	private long initializedTime;

	public MJavaScriptView() {
	}

	public void init2() {
		parent.setLayout(new FormLayout());

		sashForm = new SashForm(parent, SWT.SMOOTH | SWT.VERTICAL);

		FormData fd_sashForm1 = new FormData();
		fd_sashForm1.top = new FormAttachment(0, 1);
		fd_sashForm1.left = new FormAttachment(0, 1);
		fd_sashForm1.right = new FormAttachment(100, -1);
		fd_sashForm1.bottom = new FormAttachment(100, -1);
		sashForm.setLayoutData(fd_sashForm1);

		text1 = new Text(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text1.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				onSashResize();
			}
		});
		text1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

			}
		});
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(0, 1);
		fd_text.left = new FormAttachment(0, 1);
		fd_text.bottom = new FormAttachment(100, -1);
		fd_text.right = new FormAttachment(100, -1);
		text1.setLayoutData(fd_text);

		text2 = new Text(sashForm, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		FormData fd_text2 = new FormData();
		fd_text2.top = new FormAttachment(0, 1);
		fd_text2.left = new FormAttachment(0, 1);
		fd_text2.bottom = new FormAttachment(100, -1);
		fd_text2.right = new FormAttachment(100, -1);
		text2.setLayoutData(fd_text2);

		executeAction = new Action() {
			public void run() {// -----------
				onExecute();
			}
		};// -----------
		executeAction.setToolTipText("Execute JavaScript 'eval()' on MongoDB Server");
		executeAction.setText("Execute");
		initAction(executeAction, "database_go.png", null);
		executeAction.setEnabled(false);

		clearAction = new Action() {
			public void run() {// -----------
				text1.setText("");
				text2.setText("");
			}
		};// -----------
		clearAction.setToolTipText("Clear");
		clearAction.setText("Clear");
		initAction(clearAction, "bullet_delete.png", null);
		clearAction.setEnabled(true);

		initializedTime = System.currentTimeMillis();
	}

	private void onSashResize() {
		if (System.currentTimeMillis() >= initializedTime + 3000) {
		}
	}

	private void onExecute() {
	}

	public void setFocus() {
	}

	public void update(final Object e, final Object source) {
	}

}
