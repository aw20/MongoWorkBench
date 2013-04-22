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
 *  
 *  April 2013
 */
package org.aw20.mongoworkbench.eclipse.view;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Cursor;

public class MSystemJavaScript extends Composite {
	private Text textBox;
	private String HELPURL = "http://docs.mongodb.org/manual/tutorial/store-javascript-function-on-server/";

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MSystemJavaScript(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(4, true);
		setLayout(gridLayout);
		
		textBox = new Text(this, SWT.BORDER);
		GridData gd_textBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_textBox.widthHint = 1340;
		gd_textBox.heightHint = 236;
		textBox.setLayoutData(gd_textBox);
		new Label(this, SWT.NONE);
		
		Label urlLabel = new Label(this, SWT.WRAP);
		urlLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		urlLabel.setText(HELPURL);
		urlLabel.setToolTipText("click here to visit the java documentation");
		urlLabel.setCursor( new Cursor( this.getDisplay(), SWT.CURSOR_HAND ) );
		urlLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {}

			@Override
			public void mouseDown(MouseEvent e) {}

			@Override
			public void mouseUp(MouseEvent e) {
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(HELPURL));
				} catch (IOException e1) {}
			}
			
		});
		
		Button btnValidate = new Button(this, SWT.NONE);
		btnValidate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnValidate.setText("validate");
		btnValidate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onValidate();
			}

		});
		
		Button btnSave = new Button(this, SWT.NONE);
		btnSave.setText("save");
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSave();
			}
		});
		

	}
	private void onValidate() {
		
	}
	
	private void onSave() {
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
