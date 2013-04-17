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
package org.aw20.mongoworkbench.eclipse.view.wizard;

import java.io.IOException;

import org.aw20.mongoworkbench.Event;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.AggregateMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.util.JSONFormatter;
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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class AggregateWizard extends Composite implements WizardCommandI {
	private String HELPURL = "http://docs.mongodb.org/manual/reference/method/db.collection.aggregate";
		
	private Text textPipe;
	private TabFolder tabFolder;
	private Button btnRemovePipe;
	
	public AggregateWizard(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new GridLayout(5, false));

		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));

		TabItem tbtmPipe = new TabItem(tabFolder, SWT.NONE);
		tbtmPipe.setText("Pipe#1");

		textPipe = new Text(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		tbtmPipe.setControl(textPipe);

		Label lblHttpdocs = new Label(this, SWT.NONE);
		GridData gd_lblHttpdocsmongodborgmanualreferencemethoddbcollectionaggrega = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_lblHttpdocsmongodborgmanualreferencemethoddbcollectionaggrega.widthHint = 425;
		lblHttpdocs.setLayoutData(gd_lblHttpdocsmongodborgmanualreferencemethoddbcollectionaggrega);
		lblHttpdocs.setText(HELPURL);
		lblHttpdocs.setCursor( new Cursor( this.getDisplay(), SWT.CURSOR_HAND ) );
		lblHttpdocs.addMouseListener(new MouseListener() {

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
		
		Button btnAddPipeline = new Button(this, SWT.NONE);
		btnAddPipeline.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onAddTab();
			}
		});
		btnAddPipeline.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnAddPipeline.setText("+pipe");

		btnRemovePipe = new Button(this, SWT.NONE);
		btnRemovePipe.setEnabled(false);
		btnRemovePipe.setText("-pipe");
		btnRemovePipe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onRemoveTab();
			}
		});

		Button btnExecuteAggregation = new Button(this, SWT.NONE);
		btnExecuteAggregation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnExecuteAggregation.setText("execute");
		btnExecuteAggregation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onExecute();
			}
		});
	}


	protected void onExecute() {
		if ( MongoFactory.getInst().getActiveCollection() == null ){
			EventWorkBenchManager.getInst().onEvent( Event.EXCEPTION, new Exception("no active collection selected") );
			return;
		}
		
		// Build up the command
		StringBuilder	sb = new StringBuilder();
		sb.append("db.")
			.append( MongoFactory.getInst().getActiveCollection() )
			.append(".aggregate( ");

		
		TabItem[] tabs = tabFolder.getItems();
		for ( int x=0; x < tabs.length; x++ ){
			String text = ((Text)tabs[x].getControl()).getText().trim();
			
			if ( text.length() == 0 )
				continue;
			else if ( !text.startsWith("{") && !text.endsWith("}") ){
				EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, new Exception("invalid Pipe#" + (x+1)) );
				return;
			}else{
				sb.append( text ).append(",");
			}
		}
		
		if ( sb.charAt(sb.length()-1) == ',')
			sb.deleteCharAt(sb.length()-1);

		sb.append( ")" );
		
		try {
			MongoCommand	mcmd	= MongoFactory.getInst().createCommand(sb.toString());
			if ( mcmd != null )
				MongoFactory.getInst().submitExecution( mcmd.setConnection( MongoFactory.getInst().getActiveServer(), MongoFactory.getInst().getActiveDB() ) );
		}catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent( org.aw20.mongoworkbench.Event.EXCEPTION, e );
		}
	}


	protected void onRemoveTab() {
		if ( tabFolder.getItemCount() == 1 )
			return;
		
		TabItem tab = tabFolder.getItem( tabFolder.getItemCount()-1 );
		tab.dispose();
		
		if ( tabFolder.getItemCount() == 1 )
			btnRemovePipe.setEnabled(false);
	}


	protected void onAddTab() {
		TabItem tbtmPipe = new TabItem(tabFolder, SWT.NONE);
		tbtmPipe.setText("Pipe#" + (tabFolder.getItemCount()) );
		Text textPipe = new Text(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		tbtmPipe.setControl(textPipe);
		btnRemovePipe.setEnabled(true);
	}


	@Override
	public boolean onWizardCommand(MongoCommand cmd, BasicDBObject dbo) {
		if ( !cmd.getClass().getName().equals( AggregateMongoCommand.class.getName() ) )
			return false;
		
		if ( !dbo.containsField("aggregateArg") )
			return false;
		
		BasicDBList	args = (BasicDBList)dbo.get("aggregateArg");
		if (args.size() == 0)
			return false;
		
		// remove all the tabs
		while ( tabFolder.getItemCount() > 0 )
			tabFolder.getItem(0).dispose();
		
		
		for ( int x=0; x < args.size(); x++ ){
			TabItem tbtmPipe = new TabItem(tabFolder, SWT.NONE);
			tbtmPipe.setText("Pipe#" + (x+1) );
			Text textPipe = new Text(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
			textPipe.setText( JSONFormatter.format(args.get(x)) );
			tbtmPipe.setControl(textPipe);
		}
		
		if ( tabFolder.getItemCount() > 1 )
			btnRemovePipe.setEnabled(true);
		else
			btnRemovePipe.setEnabled(false);
		
		return true;
	}

}
