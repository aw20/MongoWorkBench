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
package org.aw20.mongoworkbench.eclipse.dialog;

import java.util.Set;


import org.aw20.mongoworkbench.eclipse.MConstants;
import org.aw20.util.MSwtUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MPasswordDialog extends Dialog implements MConstants {
	private Text passwordTExt;

	private Set passwordSet;

	private String title;

	public MPasswordDialog(Shell parentShell, String _title, Set s) {
		super(parentShell);
		passwordSet = s;
		title = _title;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
		Image image = MSwtUtil.getImage(newShell.getDisplay(), "server_lightning.png");
		newShell.setImage(image);
	}

	protected void okPressed() {
		String pass = passwordTExt.getText();
		passwordSet.add(pass);
		setReturnCode(OK);
		close();
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new FormLayout());

		Group group1 = new Group(composite, SWT.SHADOW_ETCHED_OUT);
		FormData d7 = new FormData();
		d7.top = new FormAttachment(0, 6);
		d7.left = new FormAttachment(0, 3);
		d7.right = new FormAttachment(100, -3);
		d7.bottom = new FormAttachment(100, -3);
		group1.setLayoutData(d7);
		group1.setLayout(new FormLayout());

		Label passwdLabel = new Label(group1, SWT.NONE);
		passwdLabel.setText("Password :");
		FormData fd_passwdLabel = new FormData();
		fd_passwdLabel.top = new FormAttachment(0, 15);
		fd_passwdLabel.left = new FormAttachment(0, 8);
		passwdLabel.setLayoutData(fd_passwdLabel);

		passwordTExt = new Text(group1, SWT.BORDER | SWT.PASSWORD);
		FormData d1 = new FormData();
		d1.top = new FormAttachment(passwdLabel, -2, SWT.TOP);
		d1.left = new FormAttachment(passwdLabel, 10, SWT.RIGHT);
		d1.right = new FormAttachment(passwdLabel, 200, SWT.RIGHT);
		// d1.bottom = new FormAttachment( 100, -5 );
		passwordTExt.setLayoutData(d1);

		Composite composite_1 = new Composite(group1, SWT.NONE);
		composite_1.setLayout(new FormLayout());
		FormData fd_composite_1 = new FormData();
		fd_composite_1.right = new FormAttachment(passwordTExt, 20, SWT.RIGHT);
		fd_composite_1.bottom = new FormAttachment(0, 50);
		fd_composite_1.top = new FormAttachment(0, 3);
		fd_composite_1.left = new FormAttachment(passwordTExt, 14);
		composite_1.setLayoutData(fd_composite_1);

		return composite;
	}
}
