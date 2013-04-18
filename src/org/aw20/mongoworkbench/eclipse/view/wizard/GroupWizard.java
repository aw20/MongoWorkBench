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
import org.aw20.mongoworkbench.command.GroupMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class GroupWizard extends Composite implements WizardCommandI  {
	private String HELPURL = "http://docs.mongodb.org/manual/reference/method/db.collection.group/";
	private Text textGroupKey;
	private Text textGroupReduce;
	private Text textGroupKeyF;
	private Text textGroupInitial;
	private Text textGroupCondition;
	private Text textGroupFinalize;

	public GroupWizard(Composite parent, int style) {
		super(parent, style);

		setLayout(new GridLayout(2, false));

		TabFolder tabFolder_3 = new TabFolder(this, SWT.NONE);
		tabFolder_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		//---
		TabItem tbtmCondition2 = new TabItem(tabFolder_3, SWT.NONE);
		tbtmCondition2.setText("condition");

		Composite composite_15 = new Composite(tabFolder_3, SWT.NONE);
		tbtmCondition2.setControl(composite_15);
		composite_15.setLayout(new GridLayout(1, false));

		textGroupCondition = MSwtUtil.createText( composite_15 );
		textGroupCondition.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblHelp_6 = new Label(composite_15, SWT.NONE);
		lblHelp_6.setText("The selection criteria to determine which documents in the collection to process");


		//---
		TabItem tbtmInitial2 = new TabItem(tabFolder_3, SWT.NONE);
		tbtmInitial2.setText("initial");
		
		Composite composite_13 = new Composite(tabFolder_3, SWT.NONE);
		tbtmInitial2.setControl(composite_13);
		composite_13.setLayout(new GridLayout(1, false));

		textGroupInitial = MSwtUtil.createText( composite_13 );
		textGroupInitial.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textGroupInitial.setText("{\r\n\r\n}");

		Label lblHelp_4 = new Label(composite_13, SWT.NONE);
		lblHelp_4.setText("Initializes the aggregation result document");

		
		//---
		TabItem tbtmKey = new TabItem(tabFolder_3, SWT.NONE);
		tbtmKey.setText("key");

		Composite composite_11 = new Composite(tabFolder_3, SWT.NONE);
		tbtmKey.setControl(composite_11);
		composite_11.setLayout(new GridLayout(1, false));

		textGroupKey = MSwtUtil.createText( composite_11 );
		textGroupKey.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblHelp_2 = new Label(composite_11, SWT.NONE);
		lblHelp_2.setText("Specifies one or more document fields to group by.");

		
		//---
		TabItem tbtmKeyF2 = new TabItem(tabFolder_3, SWT.NONE);
		tbtmKeyF2.setText("key function");

		Composite composite_14 = new Composite(tabFolder_3, SWT.NONE);
		tbtmKeyF2.setControl(composite_14);
		composite_14.setLayout(new GridLayout(1, false));

		textGroupKeyF = MSwtUtil.createText( composite_14 );
		textGroupKeyF.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textGroupKeyF.setText("function(doc){\r\n\r\n}");

		Label lblHelp_5 = new Label(composite_14, SWT.NONE);
		lblHelp_5.setText("Alternative to the key field. A function that creates a 'key object' for use as the grouping key");


		//---
		TabItem tbtmReduce = new TabItem(tabFolder_3, SWT.NONE);
		tbtmReduce.setText("reduce function");

		Composite composite_12 = new Composite(tabFolder_3, SWT.NONE);
		tbtmReduce.setControl(composite_12);
		composite_12.setLayout(new GridLayout(1, false));

		textGroupReduce = MSwtUtil.createText( composite_12 );
		textGroupReduce.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textGroupReduce.setText("function(curr,result){\r\n\r\n\r\n}");
		
		Label lblHelp_3 = new Label(composite_12, SWT.NONE);
		lblHelp_3.setText("Function for the group operation perform on the documents during the grouping operation");

		
		//---
		TabItem tbtmFinalize2 = new TabItem(tabFolder_3, SWT.NONE);
		tbtmFinalize2.setText("finalize function");

		Composite composite_16 = new Composite(tabFolder_3, SWT.NONE);
		tbtmFinalize2.setControl(composite_16);
		composite_16.setLayout(new GridLayout(1, false));

		textGroupFinalize = MSwtUtil.createText( composite_16 );
		textGroupFinalize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textGroupFinalize.setText("function(result){\r\n\r\n\r\n}");
		
		Label lblHelp_7 = new Label(composite_16, SWT.NONE);
		lblHelp_7.setText("Function that runs each item in the result set before returning");

		
		
		//---
		Label urlLabel = new Label(this, SWT.NONE);
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

		Button btnExecuteGroup = new Button(this, SWT.NONE);
		btnExecuteGroup.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnExecuteGroup.setText("execute");
		btnExecuteGroup.addSelectionListener(new SelectionAdapter() {
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
		
		// Build up the command
		StringBuilder	sb = new StringBuilder();
		sb.append("db.")
			.append( MongoFactory.getInst().getActiveCollection() )
			.append(".group( {");
		
		if ( textGroupKey.getText().trim().length() > 0 ){
			sb.append( "key : " )
				.append( textGroupKey.getText().trim() )
				.append(",");
		}
			
		if ( textGroupReduce.getText().trim().length() > 0 ){
			sb.append( "reduce : " )
				.append( textGroupReduce.getText().trim() )
				.append(",");
		}
		
		if (textGroupKeyF.getText().trim().length() > 0){
			sb.append( "keyf : " )
				.append( textGroupKeyF.getText().trim() )
				.append(",");
		}
			
		if (textGroupInitial.getText().trim().length() > 0){
			sb.append( "initial : " )
				.append( textGroupInitial.getText().trim() )
				.append(",");
		}
		
		if (textGroupCondition.getText().trim().length() > 0){
			sb.append( "cond : " )
				.append( textGroupCondition.getText().trim() )
				.append(",");
		}
			
		if (textGroupFinalize.getText().trim().length() > 0){
			sb.append( "finalize : " )
				.append( textGroupFinalize.getText().trim() );
		}
		
		if ( sb.charAt(sb.length()-1) == ',')
			sb.deleteCharAt(sb.length()-1);
		
		sb.append( "})" );
		
		try {
			MongoCommand	mcmd	= MongoFactory.getInst().createCommand(sb.toString());
			if ( mcmd != null )
				MongoFactory.getInst().submitExecution( mcmd.setConnection( MongoFactory.getInst().getActiveServer(), MongoFactory.getInst().getActiveDB() ) );
		}catch (Exception e) {
			EventWorkBenchManager.getInst().onEvent( org.aw20.mongoworkbench.Event.EXCEPTION, e );
		}
	}

	@Override
	public boolean onWizardCommand(MongoCommand cmd, BasicDBObject dbo) {
		if ( !cmd.getClass().getName().equals( GroupMongoCommand.class.getName() ) )
			return false;
		
		if ( !dbo.containsField("groupArg") )
			return false;
		
		DBObject	gmap	= (DBObject)dbo.get("groupArg");

		if ( gmap.containsField("cond") ){
			textGroupCondition.setText( JSONFormatter.format(gmap.get("cond")) );
		}else{
			textGroupCondition.setText("");
		}

		if ( gmap.containsField("initial") ){
			textGroupInitial.setText( JSONFormatter.format(gmap.get("initial")) );
		}else{
			textGroupInitial.setText("");
		}

		if ( gmap.containsField("key") ){
			textGroupKey.setText( JSONFormatter.format(gmap.get("key")) );
		}else{
			textGroupKey.setText("");
		}

		if ( gmap.containsField("keyf") ){
			org.bson.types.Code c = (org.bson.types.Code)gmap.get("key");
			textGroupKeyF.setText( c.toString() );
		}else{
			textGroupKeyF.setText("");
		}

		if ( gmap.containsField("reduce") ){
			org.bson.types.Code c = (org.bson.types.Code)gmap.get("reduce");
			textGroupReduce.setText( c.toString() );
		}else{
			textGroupReduce.setText("");
		}

		if ( gmap.containsField("finalize") ){
			org.bson.types.Code c = (org.bson.types.Code)gmap.get("finalize");
			textGroupFinalize.setText( c.toString() );
		}else{
			textGroupFinalize.setText("");
		}
		
		return true;
	}

}