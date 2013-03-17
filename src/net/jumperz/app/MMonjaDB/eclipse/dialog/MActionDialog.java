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
package net.jumperz.app.MMonjaDB.eclipse.dialog;

import java.util.Map;

import net.jumperz.app.MMonjaDB.eclipse.MUtil;
import net.jumperz.app.MMonjaDBCore.MConstants;

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

public class MActionDialog extends Dialog implements MConstants {
	private Text nameText;
	private Text actionText;
	private Map savedAction;

	public MActionDialog(Shell parentShell, Map m) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		savedAction = m;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Saved Actions");
		Image image = MUtil.getImage(newShell.getDisplay(), "server_lightning.png");
		newShell.setImage(image);
	}

	protected void okPressed() {
		savedAction.put("name", nameText.getText());
		savedAction.put("actions", actionText.getText());

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

		Label nameLabel = new Label(group1, SWT.NONE);
		nameLabel.setText("Name :");
		FormData fd_nameLabel = new FormData();
		fd_nameLabel.top = new FormAttachment(0, 15);
		fd_nameLabel.left = new FormAttachment(0, 15);
		nameLabel.setLayoutData(fd_nameLabel);

		nameText = new Text(group1, SWT.BORDER);
		FormData d1 = new FormData();
		d1.left = new FormAttachment(nameLabel, 15);
		d1.top = new FormAttachment(nameLabel, -2, SWT.TOP);
		d1.right = new FormAttachment(nameLabel, 200, SWT.RIGHT);
		// d1.bottom = new FormAttachment( 100, -5 );
		nameText.setLayoutData(d1);

		Label actionLabel = new Label(group1, SWT.NONE);
		FormData fd_actionLabel = new FormData();
		fd_actionLabel.top = new FormAttachment(nameLabel, 20);
		fd_actionLabel.left = new FormAttachment(0, 8);
		actionLabel.setLayoutData(fd_actionLabel);
		actionLabel.setText("Actions :");

		actionText = new Text(group1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		FormData d2 = new FormData();
		d2.left = new FormAttachment(nameText, 0, SWT.LEFT);
		d2.bottom = new FormAttachment(100, -10);
		d2.top = new FormAttachment(actionLabel, -2, SWT.TOP);
		d2.right = new FormAttachment(100, -15);
		// d1.bottom = new FormAttachment( 100, -5 );
		actionText.setLayoutData(d2);

		Composite composite_1 = new Composite(group1, SWT.NONE);
		FormData fd_composite_1 = new FormData();
		fd_composite_1.bottom = new FormAttachment(100);
		fd_composite_1.right = new FormAttachment(0, 400);
		fd_composite_1.top = new FormAttachment(0, 200);
		composite_1.setLayoutData(fd_composite_1);

		if (savedAction.containsKey("name")) {
			nameText.setText(savedAction.get("name") + "");
		}
		if (savedAction.containsKey("actions")) {
			actionText.setText(savedAction.get("actions") + "");
		}

		return composite;
	}

}
