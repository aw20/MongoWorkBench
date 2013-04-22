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

import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.MongoCommandListener;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.command.SystemJavaScriptReadCommand;
import org.aw20.mongoworkbench.command.SystemJavaScriptValidateCommand;
import org.aw20.mongoworkbench.command.SystemJavaScriptWriteCommand;
import org.aw20.util.MSwtUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MSystemJavaScript extends MAbstractView implements MongoCommandListener {
	private Text textBox;
	private String HELPURL = "http://docs.mongodb.org/manual/tutorial/store-javascript-function-on-server/";

	private Button btnValidate, btnSave;
	private SystemJavaScriptReadCommand readCommand;
	
	public MSystemJavaScript() {
		MongoFactory.getInst().registerListener(this);
	}
		
	public void dispose() {
		MongoFactory.getInst().deregisterListener(this);
		super.dispose();
	}

	
	public void init2() {
		parent.setLayout(new GridLayout(3, false));
		
		textBox = MSwtUtil.createText(parent);
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd_text.widthHint = 438;
		textBox.setLayoutData(gd_text);
		
		Label urlLabel = new Label(parent, SWT.NONE);
		urlLabel.setText("url");
		urlLabel.setText(HELPURL);
		urlLabel.setToolTipText("click here to visit the MongoDB documentation");
		urlLabel.setCursor( new Cursor( parent.getDisplay(), SWT.CURSOR_HAND ) );
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
		
		
		btnValidate = new Button(parent, SWT.NONE);
		btnValidate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnValidate.setText("validate");
		btnValidate.setEnabled(false);
		btnValidate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onValidate();
			}
		});
		
		
		btnSave = new Button(parent, SWT.NONE);
		btnSave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnSave.setText("save");
		btnSave.setEnabled(false);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSave();
			}
		});
	}
	
	private void onValidate() {
		try {
			MongoCommand	mcmd	= new SystemJavaScriptValidateCommand(readCommand.getJSName(), textBox.getText());
			MongoFactory.getInst().submitExecution( mcmd.setConnection(readCommand.getName(), readCommand.getDB()) );
			btnSave.setEnabled(false);
			btnValidate.setEnabled(false);
		}catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent( org.aw20.mongoworkbench.Event.EXCEPTION, e );
		}
	}
	
	private void onSave() {
		try {
			MongoCommand	mcmd	= new SystemJavaScriptWriteCommand(readCommand.getJSName(), textBox.getText());
			MongoFactory.getInst().submitExecution( mcmd.setConnection(readCommand.getName(), readCommand.getDB()) );
			btnSave.setEnabled(false);
			btnValidate.setEnabled(false);
		}catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent( org.aw20.mongoworkbench.Event.EXCEPTION, e );
		}
	}

	@Override
	public void onMongoCommandStart(MongoCommand mcmd) {}

	@Override
	public void onMongoCommandFinished(MongoCommand mcmd) {
		if ( mcmd instanceof SystemJavaScriptReadCommand ){
			readCommand	= (SystemJavaScriptReadCommand)mcmd;
			final String jsCode = readCommand.getCode();
			
			shell.getDisplay().asyncExec(new Runnable() {
				public void run() {
					textBox.setText(jsCode);
					btnSave.setEnabled(true);
					btnValidate.setEnabled(true);
				}
			});
			
		} else if ( mcmd instanceof SystemJavaScriptWriteCommand || mcmd instanceof SystemJavaScriptValidateCommand ){
			
			shell.getDisplay().asyncExec(new Runnable() {
				public void run() {
					btnSave.setEnabled(true);
					btnValidate.setEnabled(true);
				}
			});
			
		}
	}

}