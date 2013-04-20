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
 */
package org.aw20.mongoworkbench.eclipse.view.wizard;

import java.io.IOException;

import org.aw20.mongoworkbench.Event;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.command.UpdateMongoCommand;
import org.aw20.mongoworkbench.eclipse.view.WizardParentI;
import org.aw20.util.JSONFormatter;
import org.aw20.util.MSwtUtil;
import org.aw20.util.StringUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class UpdateWizard extends Composite implements WizardCommandI {

	private Text textUpdateQuery;
	private Text textUpdateUpdate;
	private Button btnUpdateMulti, btnUpdateUpsert;
	private String HELPURL	= "http://docs.mongodb.org/manual/reference/method/db.collection.update/";
	
	private WizardParentI wizardparent;
	
	public UpdateWizard(WizardParentI wizardparent, Composite parent, int style) {
		super(parent, style);
	
		this.wizardparent = wizardparent;
		
		setLayout(new GridLayout(3, false));

		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setText("Query");
		lblNewLabel.setToolTipText("This is the query document to query on.  Should be { }");

		Label lblUpdate = new Label(this, SWT.NONE);
		lblUpdate.setText("Update");
		lblUpdate.setToolTipText("This is the update document to apply.  Should be { }");
		new Label(this, SWT.NONE);

		textUpdateQuery = MSwtUtil.createText( this );
		textUpdateQuery.setToolTipText("asdasdas");
		textUpdateQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

		textUpdateUpdate = MSwtUtil.createText( this );
		textUpdateUpdate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));

		btnUpdateUpsert  = new Button(this, SWT.CHECK);
		btnUpdateUpsert.setToolTipText("If a record is not found, then create a new one");
		btnUpdateUpsert.setText("Upsert");

		btnUpdateMulti = new Button(this, SWT.CHECK);
		btnUpdateMulti.setToolTipText("Run this update on multiple records if matched");
		btnUpdateMulti.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnUpdateMulti.setText("Multi");

		Label urlLabel = new Label(this, SWT.WRAP);
		urlLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		urlLabel.setText(HELPURL);
		urlLabel.setToolTipText("click here to visit MongoDB documentation");
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
		
		Button btnUpdateExecute = new Button(this, SWT.NONE);
		btnUpdateExecute.setText("execute");
		btnUpdateExecute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onExecute();
			}
		});
	}

	private void onExecute() {
		if ( MongoFactory.getInst().getActiveCollection() == null ){
			EventWorkBenchManager.getInst().onEvent( Event.EXCEPTION, new Exception("no active collection selected") );
			return;
		}
		
		String queryText	= textUpdateQuery.getText().trim();
		if ( queryText.length() == 0 )
			return;
		
		String updateText	= textUpdateUpdate.getText().trim();
		if ( updateText.length() == 0 )
			return;
		
		// Build up the command
		StringBuilder	sb = new StringBuilder();
		sb.append("db.")
			.append( wizardparent.getActiveCollection() )
			.append(".update(")
			.append( queryText )
			.append(",")
			.append( updateText )
			.append(",")
			.append( btnUpdateUpsert.getSelection() )
			.append(",")
			.append( btnUpdateMulti.getSelection() )
			.append( ")" );
		
		try {
			MongoCommand	mcmd	= MongoFactory.getInst().createCommand(sb.toString());
			if ( mcmd != null )
				MongoFactory.getInst().submitExecution( mcmd.setConnection( MongoFactory.getInst().getActiveServer(), wizardparent.getActiveDB() ) );
		}catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent( org.aw20.mongoworkbench.Event.EXCEPTION, e );
		}
	}

	@Override
	public boolean onWizardCommand(MongoCommand cmd, BasicDBObject dbo) {
		if ( !cmd.getClass().getName().equals( UpdateMongoCommand.class.getName() ) )
			return false;
		
		if ( !dbo.containsField("updateArg") )
			return false;
		
		BasicDBList	list	= (BasicDBList)dbo.get("updateArg");	
		
		// Set the fields of the wizard from the command
		textUpdateQuery.setText( JSONFormatter.format( ((BasicDBObject)list.get(0)).toMap() ) );
		textUpdateUpdate.setText( JSONFormatter.format( ((BasicDBObject)list.get(1)).toMap() ) );
		
		if ( list.size() >= 3 )
			btnUpdateUpsert.setSelection( StringUtil.toBoolean( list.get(2), false ) );
		else
			btnUpdateUpsert.setSelection( false );

		if ( list.size() >= 4 )
			btnUpdateMulti.setSelection( StringUtil.toBoolean( list.get(3), false ) );
		else
			btnUpdateMulti.setSelection( false );

		return true;
	}
}
