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
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.eclipse.Activator;
import org.aw20.mongoworkbench.eclipse.dialog.TextInputPopup;
import org.aw20.mongoworkbench.eclipse.view.data.ScratchPads;
import org.aw20.util.MSwtUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class MCommandConsole extends MAbstractView {
	public TabFolder tabFolder;
	private	Button btnDelButton;
	
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
				
			} else
				throw new Exception("command not found");

		} catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, e);
		}
		
		new ScratchPads( tabFolder ).save( Activator.getDefault().getScratchPadsFile() );
	}

	public void init2() {
		parent.setLayout(new GridLayout(3, false));

		tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		

		// Set the scratch pads
		new ScratchPads( Activator.getDefault().getScratchPadsFile() ).reset( this );
		
		
		// The Add ScratchPad handling
		Button btnAddButton = new Button(parent, SWT.NONE);
		btnAddButton.setText("+ScratchPad");
		
		btnAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				TextInputPopup	popup	= new TextInputPopup(parent.getShell(), "Create ScratchPad");
				String newPad	= popup.open("Name of ScratchPad:");
				if ( newPad != null && newPad.trim().length() > 0 ){
					addPad( newPad, "" );
					
					new ScratchPads( tabFolder ).save( Activator.getDefault().getScratchPadsFile() );
				}

			}
		});

		
		// The Delete ScratchPad handling
		btnDelButton = new Button(parent, SWT.NONE);
		btnDelButton.setText("-ScratchPad");
		
		btnDelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if ( tabFolder.getItemCount() == 1 )
					return;
				
				TabItem tab = tabFolder.getItem( tabFolder.getSelectionIndex() );
				tab.dispose();

				if ( tabFolder.getItemCount() == 1 )
					btnDelButton.setEnabled(false);
				
				new ScratchPads( tabFolder ).save( Activator.getDefault().getScratchPadsFile() );
			}
		});
		
		
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Execute a command by highlighting and hitting SHIFT+ENTER");
	}

	public void addPad(String newPad, String text){
		TabItem tbtmPipe = new TabItem(tabFolder, SWT.NONE);
		tbtmPipe.setText( newPad );
		Text textPipe = MSwtUtil.createText( tabFolder );
		textPipe.setText(text);
		tbtmPipe.setControl(textPipe);
		
		if ( btnDelButton != null )
			btnDelButton.setEnabled(true);

		textPipe.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR && e.stateMask == SWT.SHIFT) {
					executeActionsOnText( (Text)e.getSource() );
					e.doit = false;
				}
			}
		});
		
	}
	
}