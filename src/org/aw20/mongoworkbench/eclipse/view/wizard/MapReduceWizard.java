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
import org.aw20.mongoworkbench.command.MapReduceMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.eclipse.view.WizardParentI;
import org.aw20.util.JSONFormatter;
import org.aw20.util.MSwtUtil;
import org.aw20.util.StringUtil;
import org.bson.types.Code;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class MapReduceWizard extends Composite implements WizardCommandI {
	private String HELPURL = "http://docs.mongodb.org/manual/reference/method/db.collection.mapReduce/";

	private String JS_FINALIZE = "function(key, reducedValue) {\r\n\r\n  return reducedValue;\r\n}";
	private String JS_MAP = "function() {\r\n\r\n  //emit(key, value);\r\n}";
	private String JS_REDUCE = "function(key, values) {\r\n\r\n  //return result;\r\n}";
	
	private Text textMRMap;
	private Text textMRReduce;
	private Text textMRFinalize;
	private Text textMRQuery;
	private Text textMRSort;
	private Text textMRLimit;
	private Text textMRScope;
	private Text textMRCollection;
	private Text textMRDatabase;

	private Combo combo;
	
	private Button btnSharded;
	private Button btnNonatomic;
	private Button btnJsmode;
	private Button btnVerbose;
	
	private WizardParentI wizardparent;
	
	public MapReduceWizard(WizardParentI wizardparent, Composite parent, int style) {
		super(parent, style);

		this.wizardparent = wizardparent;
		
		setLayout(new GridLayout(2, false));

		TabFolder tabFolder_2 = new TabFolder(this, SWT.NONE);
		tabFolder_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		
		//--
		TabItem tbtmQuery = new TabItem(tabFolder_2, SWT.NONE);
		tbtmQuery.setText("Input");

		Composite composite_7 = new Composite(tabFolder_2, SWT.NONE);
		tbtmQuery.setControl(composite_7);
		composite_7.setLayout(new GridLayout(2, false));

		Label lblQuery = new Label(composite_7, SWT.NONE);
		lblQuery.setText("Query*");
		lblQuery.setToolTipText("[optional] Specifies the selection criteria using query operators for determining the documents input to the map function");

		Label lblSort = new Label(composite_7, SWT.NONE);
		lblSort.setText("Sort*");
		lblSort.setToolTipText("[optional] Sorts the input documents. This option is useful for optimization. For example, specify the sort key to be the same as the emit key so that there are fewer reduce operations");

		textMRQuery = MSwtUtil.createText( composite_7 );
		textMRQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

		textMRSort = MSwtUtil.createText( composite_7 );
		textMRSort.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblLimit = new Label(composite_7, SWT.NONE);
		lblLimit.setText("Limit*");
		lblLimit.setToolTipText("[optional] Specifies a maximum number of documents to return from the collection");

		textMRLimit = new Text(composite_7, SWT.BORDER);
		textMRLimit.setToolTipText("number of documents to pull back");
		textMRLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblHelpText_2 = new Label(composite_7, SWT.NONE);
		lblHelpText_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblHelpText_2.setText("[optional] Determine the input criteria for your Map/Reduce job");

		
		//--
		TabItem tbtmScope = new TabItem(tabFolder_2, SWT.NONE);
		tbtmScope.setText("Scope");

		Composite composite_8 = new Composite(tabFolder_2, SWT.NONE);
		tbtmScope.setControl(composite_8);
		composite_8.setLayout(new GridLayout(1, false));

		textMRScope = MSwtUtil.createText( composite_8 );
		textMRScope.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textMRScope.setText("{\r\n\r\n}");

		Label lblHelp = new Label(composite_8, SWT.NONE);
		lblHelp.setText("[optional] Specifies global variables that are accessible in the map , reduce and the finalize functions");

		
		//--
		TabItem tbtmMap = new TabItem(tabFolder_2, SWT.NONE);
		tbtmMap.setText("Map");

		Composite composite_4 = new Composite(tabFolder_2, SWT.NONE);
		tbtmMap.setControl(composite_4);
		composite_4.setLayout(new GridLayout(1, false));

		textMRMap = MSwtUtil.createText( composite_4 );
		textMRMap.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textMRMap.setText(JS_MAP);

		Label lblSomeHelpText = new Label(composite_4, SWT.NONE);
		lblSomeHelpText.setText("A JavaScript function that associates or 'maps' a value with a key and emits the key and value pair");

		
		//--
		TabItem tbtmReduceTab = new TabItem(tabFolder_2, SWT.NONE);
		tbtmReduceTab.setText("Reduce");

		Composite composite_5 = new Composite(tabFolder_2, SWT.NONE);
		tbtmReduceTab.setControl(composite_5);
		composite_5.setLayout(new GridLayout(1, false));

		textMRReduce = MSwtUtil.createText( composite_5 );
		textMRReduce.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textMRReduce.setText(JS_REDUCE);
		
		Label lblHelpText = new Label(composite_5, SWT.NONE);
		lblHelpText.setText("A JavaScript function that 'reduces' to a single object all the values associated with a particular key");

		
		//--
		TabItem tbtmFinalize = new TabItem(tabFolder_2, SWT.NONE);
		tbtmFinalize.setText("Finalize");

		Composite composite_6 = new Composite(tabFolder_2, SWT.NONE);
		tbtmFinalize.setControl(composite_6);
		composite_6.setLayout(new GridLayout(1, false));

		textMRFinalize = MSwtUtil.createText( composite_6 );
		textMRFinalize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textMRFinalize.setText(JS_FINALIZE);
		
		Label lblHelpText_1 = new Label(composite_6, SWT.NONE);
		lblHelpText_1.setText("[optional] A JavaScript function that follows the reduce method and modifies the output");

		
		//--
		TabItem tbtmOutput_1 = new TabItem(tabFolder_2, SWT.NONE);
		tbtmOutput_1.setText("Output");

		Composite composite_9 = new Composite(tabFolder_2, SWT.NONE);
		tbtmOutput_1.setControl(composite_9);
		composite_9.setLayout(new GridLayout(3, false));

		Label lblType = new Label(composite_9, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Type");

		combo = new Combo(composite_9, SWT.DROP_DOWN | SWT.READ_ONLY );
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		combo.setItems( new String[]{ "inline","replace","merge","reduce" });
		combo.select(0);
		
		Label lblCollection = new Label(composite_9, SWT.NONE);
		lblCollection.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCollection.setText("Collection");

		textMRCollection = new Text(composite_9, SWT.BORDER);
		GridData gd_textMRCollection = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_textMRCollection.widthHint = 548;
		textMRCollection.setLayoutData(gd_textMRCollection);

		Label lblDatabase = new Label(composite_9, SWT.NONE);
		lblDatabase.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabase.setText("Database");

		textMRDatabase = new Text(composite_9, SWT.BORDER);
		GridData gd_textMRDatabase = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_textMRDatabase.widthHint = 549;
		textMRDatabase.setLayoutData(gd_textMRDatabase);
		new Label(composite_9, SWT.NONE);

		btnSharded = new Button(composite_9, SWT.CHECK);
		btnSharded.setText("sharded");

		btnNonatomic = new Button(composite_9, SWT.CHECK);
		btnNonatomic.setText("nonAtomic");
		new Label(composite_9, SWT.NONE);

		btnJsmode = new Button(composite_9, SWT.CHECK);
		btnJsmode.setText("jsMode");

		btnVerbose = new Button(composite_9, SWT.CHECK);
		btnVerbose.setText("verbose");

		Label lblNewLabel_2 = new Label(this, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText(HELPURL);
		lblNewLabel_2.addMouseListener(new MouseListener() {

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

		Button btnExecuteMapReduce = new Button(this, SWT.NONE);
		btnExecuteMapReduce.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnExecuteMapReduce.setText("execute");
		btnExecuteMapReduce.addSelectionListener(new SelectionAdapter() {
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
			.append( wizardparent.getActiveCollection() )
			.append(".mapReduce( ");
		
		
		// Map attribute
		if ( textMRMap.getText().trim().equals(JS_MAP) ){
			EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, new Exception("no map function supplied") );
			return;
		}else{
			sb.append( textMRMap.getText().trim() ).append(", ");
		}
		
		// Reduce attribute
		if ( textMRReduce.getText().trim().equals(JS_REDUCE) ){
			EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, new Exception("no reduce function supplied") );
			return;
		}else{
			sb.append( textMRReduce.getText().trim() ).append(", {");
		}
		
		
		// Output attribute
		String output	= combo.getText();
		if ( output == null || output.length() == 0 ){
			EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, new Exception("no output type specified") );
			return;
		}
		
		if ( output.equals("inline") ){
			sb.append( " out : { inline : 1}," );
		}else if (output.equals("replace") || output.equals("merge") || output.equals("reduce")){
			
			sb.append( " out : {");
			
			String collection	= textMRCollection.getText().trim();
			if ( collection.length() == 0 ){
				EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, new Exception("no collection was specified") );
				return;
			}else
				sb.append( output ).append( " : \"" ).append(collection).append("\",");
			
			String db	= textMRDatabase.getText().trim();
			if ( db.length() == 0 ){
				sb.append( "db : \"" ).append(db).append("\",");
			}

			sb.append( "sharded:" ).append( btnSharded.getSelection() ).append(",");
			sb.append( "nonAtomic:" ).append( btnNonatomic.getSelection() );

			sb.append("},");
		}
		
		
		// Query
		if ( textMRQuery.getText().trim().length() != 0 ){
			if ( !textMRQuery.getText().trim().startsWith("{") && !textMRQuery.getText().trim().endsWith("}") ){
				EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, new Exception("query does not look like a document") );
				return;
			}
			sb.append( " query : " ).append( textMRQuery.getText().trim() ).append(",");
		}
		
		
		// Sort
		if ( textMRSort.getText().trim().length() != 0 ){
			if ( !textMRSort.getText().trim().startsWith("{") && !textMRSort.getText().trim().endsWith("}") ){
				EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, new Exception("sort does not look like a document") );
				return;
			}
			sb.append( " sort : " ).append( textMRSort.getText().trim() ).append(",");
		}
		
		
		// limit
		if ( textMRLimit.getText().trim().length() != 0 ){
			if ( StringUtil.toInteger(textMRLimit.getText().trim(), -1) == -1 ){
				EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, new Exception("limit is not a number") );
				return;
			}
			sb.append( " limit : " ).append( textMRLimit.getText().trim() ).append(",");
		}
		
		
		// finalize
		if ( !textMRFinalize.getText().trim().equals(JS_FINALIZE) ){
			sb.append( " finalize : " ).append( textMRFinalize.getText().trim() ).append(",");
		}
		
		
		// scope
		if ( textMRScope.getText().trim().length() != 0 ){
			if ( !textMRScope.getText().trim().startsWith("{") && !textMRScope.getText().trim().endsWith("}") ){
				EventWorkBenchManager.getInst().onEvent(Event.EXCEPTION, new Exception("scope does not look like a document") );
				return;
			}
			sb.append( " scope : " ).append( textMRScope.getText().trim() ).append(",");
		}
		
		
		// flags
		sb.append( "jsMode:" ).append( btnJsmode.getSelection() ).append(",");
		sb.append( "verbose:" ).append( btnVerbose.getSelection() );

		sb.append( "} )" );
		
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
		if ( !cmd.getClass().getName().equals( MapReduceMongoCommand.class.getName() ) )
			return false;
		
		if ( !dbo.containsField("mapreduceArgs") )
			return false;
		
		BasicDBList dbList	= (BasicDBList)dbo.get("mapreduceArgs");
		
		BasicDBObject	options	= (BasicDBObject)dbList.get(2);
		
		
		// Input section
		if ( options.containsField("query") ){
			textMRQuery.setText( JSONFormatter.format( options.get("query") ) );
		}else{
			textMRQuery.setText("");
		}

		if ( options.containsField("sort") ){
			textMRSort.setText( JSONFormatter.format( options.get("sort") ) );
		}else{
			textMRSort.setText("");
		}

		if ( options.containsField("limit") ){
			textMRLimit.setText( (String)options.get("sort") );
		}else{
			textMRLimit.setText("");
		}
		
		// Scope section
		if ( options.containsField("scope") ){
			textMRScope.setText( JSONFormatter.format( options.get("scope") ) );
		}else{
			textMRScope.setText("");
		}
		
		// Map section
		textMRMap.setText( ((Code) (dbList.get(0))).getCode() );
		
		// Reduce Section
		textMRReduce.setText( ((Code) (dbList.get(1))).getCode() );
		
		// Finalize
		if ( options.containsField("finalize") ){
			textMRFinalize.setText( ((Code)options.get("finalize")).getCode() );
		}else{
			textMRFinalize.setText("");
		}

		// Output section
		combo.select(0);
		textMRCollection.setText("");
		textMRDatabase.setText("");
		btnSharded.setSelection(false);
		btnJsmode.setSelection(false);
		btnNonatomic.setSelection(false);
		btnVerbose.setSelection(false);
		
		if ( options.get("out") instanceof String ){
			textMRCollection.setText( (String)options.get("out") );
		} else if ( options.get("out") instanceof BasicDBObject ) {
			BasicDBObject out	= (BasicDBObject)options.get("out");
			
			if ( out.containsField("inline") ){
			} else if ( out.containsField("replace") ){
				combo.select(1);
				textMRCollection.setText( (String)out.get("replace") );
			} else if ( out.containsField("merge") ){
				combo.select(2);
				textMRCollection.setText( (String)out.get("merge") );
			} else if ( out.containsField("reduce") ){
				combo.select(3);
				textMRCollection.setText( (String)out.get("reduce") );
			}
			
			if ( out.containsField("sharded") )
				btnSharded.setSelection( StringUtil.toBoolean( out.get("sharded"), false) );

			if ( out.containsField("nonAtomic") )
				btnNonatomic.setSelection( StringUtil.toBoolean( out.get("nonAtomic"), false) );
			
			if ( out.containsField("db") )
				textMRDatabase.setText( (String)out.get("db") );
		}
		
		if ( options.containsField("jsMode") )
			btnJsmode.setSelection( StringUtil.toBoolean( options.get("jsMode"), false) );

		if ( options.containsField("verbose") )
			btnVerbose.setSelection( StringUtil.toBoolean( options.get("verbose"), false) );

		return true;
	}
}