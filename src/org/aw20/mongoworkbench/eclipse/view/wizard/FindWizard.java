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
package org.aw20.mongoworkbench.eclipse.view.wizard;

import java.io.IOException;

import org.aw20.mongoworkbench.Event;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.FindMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.eclipse.view.WizardParentI;
import org.aw20.util.JSONFormatter;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class FindWizard extends Composite implements WizardCommandI {
	private Text textQuery;
	private Text textKeys;
	private Text textSort;
	private String HELPURL	= "http://docs.mongodb.org/manual/reference/method/db.collection.find/";
	
	private WizardParentI wizardparent;

	public FindWizard(WizardParentI wizardparent, Composite parent, int style) {
		super(parent, style);
		
		this.wizardparent = wizardparent;
		
		GridLayout gridLayout = new GridLayout(3, true);
		setLayout(gridLayout);
		
		Label lblQuery = new Label(this, SWT.NONE);
		lblQuery.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblQuery.setToolTipText("This is the query document to query on. Should be { }");
		lblQuery.setText("Query");
		
		Label lblKeys = new Label(this, SWT.NONE);
		lblKeys.setText("Keys");
		
		Label lblSort = new Label(this, SWT.NONE);
		lblSort.setToolTipText("Sorts the input documents. This option is useful for optimization. For example, specify the sort key to be the same as the emit key so that there are fewer reduce operations.");
		lblSort.setText("Sort");
		
		textQuery = MSwtUtil.createText( this );
		textQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		textKeys = MSwtUtil.createText( this );
		textKeys.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		textSort = MSwtUtil.createText( this );
		textSort.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
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
		
		Button btnExecute = new Button(this, SWT.NONE);
		btnExecute.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnExecute.setText("execute");
		btnExecute.addSelectionListener(new SelectionAdapter() {
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
		
		String queryText  = textQuery.getText().trim();
		if ( queryText.length() == 0 )
			return;
		
		String sortText  = textSort.getText().trim();
		String keysText  = textKeys.getText().trim();
		
		if (queryText.charAt(0) != '{' || queryText.charAt(queryText.length() - 1) != '}') {
			return;
		}
		
		if (keysText.length() != 0){
			if (keysText.charAt(0) != '{' || keysText.charAt(keysText.length() - 1) != '}') {
				return;
			}
		}
		
		if (sortText.length() != 0){
			if (sortText.charAt(0) != '{' || sortText.charAt(sortText.length() - 1) != '}') {
				return;
			}
		}
	
		StringBuilder sb = new StringBuilder();
		sb.append("db.")
			.append( wizardparent.getActiveCollection() )
			.append(".find(")
			.append( queryText );
		
		if ( keysText.length() > 0 ){
			sb.append(",")
				.append( keysText );
		}
			
		sb.append(")");
		
		if (sortText.length() != 0){
			sb.append(".sort(");
			sb.append( sortText );
			sb.append(");");
		}
		
		
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
		if ( !cmd.getClass().getName().equals( FindMongoCommand.class.getName() ) )
			return false;

		if ( !dbo.containsField("findArg") )
			return false;

		BasicDBList dbList	= (BasicDBList)dbo.get("findArg");
		
		if ( dbList.size() == 1 ){
			textKeys.setText("");
			textQuery.setText( JSONFormatter.format( dbList.get(0)) );
		}else if ( dbList.size() == 2 ){
			textKeys.setText( JSONFormatter.format( dbList.get(1)) );
			textQuery.setText( JSONFormatter.format( dbList.get(0)) );
		}else{
			textKeys.setText("");
			textQuery.setText("");
		}
		
		if ( dbo.containsField("sort") ){
			textSort.setText( JSONFormatter.format( dbo.get("sort")) );
		}else{
			textSort.setText("");
		}
		
		return true;
	}

}