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

import org.aw20.mongoworkbench.command.MongoCommand;
import org.eclipse.swt.SWT;
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

import com.mongodb.BasicDBObject;

public class MapReduceWizard extends Composite implements WizardCommandI {

	private Text textMRMap;
	private Text textMRReduce;
	private Text textMRFinalize;
	private Text textMRQuery;
	private Text textMRSort;
	private Text textMRLimit;
	private Text textMRScope;
	private Text textMRCollection;
	private Text textMRDatabase;

	
	public MapReduceWizard(Composite parent, int style) {
		super(parent, style);

		setLayout(new GridLayout(2, false));

		TabFolder tabFolder_2 = new TabFolder(this, SWT.NONE);
		tabFolder_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		TabItem tbtmQuery = new TabItem(tabFolder_2, SWT.NONE);
		tbtmQuery.setText("Input");

		Composite composite_7 = new Composite(tabFolder_2, SWT.NONE);
		tbtmQuery.setControl(composite_7);
		composite_7.setLayout(new GridLayout(2, false));

		Label lblQuery = new Label(composite_7, SWT.NONE);
		lblQuery.setText("Query");

		Label lblSort = new Label(composite_7, SWT.NONE);
		lblSort.setText("Sort");

		textMRQuery = new Text(composite_7, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textMRQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

		textMRSort = new Text(composite_7, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textMRSort.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblLimit = new Label(composite_7, SWT.NONE);
		lblLimit.setText("Limit");

		textMRLimit = new Text(composite_7, SWT.BORDER);
		textMRLimit.setToolTipText("number of documents to pull back");
		textMRLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblHelpText_2 = new Label(composite_7, SWT.NONE);
		lblHelpText_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblHelpText_2.setText("help text");

		TabItem tbtmScope = new TabItem(tabFolder_2, SWT.NONE);
		tbtmScope.setText("Scope");

		Composite composite_8 = new Composite(tabFolder_2, SWT.NONE);
		tbtmScope.setControl(composite_8);
		composite_8.setLayout(new GridLayout(1, false));

		textMRScope = new Text(composite_8, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textMRScope.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblHelp = new Label(composite_8, SWT.NONE);
		lblHelp.setText("help");

		TabItem tbtmMap = new TabItem(tabFolder_2, SWT.NONE);
		tbtmMap.setText("Map");

		Composite composite_4 = new Composite(tabFolder_2, SWT.NONE);
		tbtmMap.setControl(composite_4);
		composite_4.setLayout(new GridLayout(1, false));

		textMRMap = new Text(composite_4, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textMRMap.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblSomeHelpText = new Label(composite_4, SWT.NONE);
		lblSomeHelpText.setText("some help text");

		TabItem tbtmReduceTab = new TabItem(tabFolder_2, SWT.NONE);
		tbtmReduceTab.setText("Reduce");

		Composite composite_5 = new Composite(tabFolder_2, SWT.NONE);
		tbtmReduceTab.setControl(composite_5);
		composite_5.setLayout(new GridLayout(1, false));

		textMRReduce = new Text(composite_5, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textMRReduce.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblHelpText = new Label(composite_5, SWT.NONE);
		lblHelpText.setText("help text");

		TabItem tbtmFinalize = new TabItem(tabFolder_2, SWT.NONE);
		tbtmFinalize.setText("Finalize");

		Composite composite_6 = new Composite(tabFolder_2, SWT.NONE);
		tbtmFinalize.setControl(composite_6);
		composite_6.setLayout(new GridLayout(1, false));

		textMRFinalize = new Text(composite_6, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textMRFinalize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblHelpText_1 = new Label(composite_6, SWT.NONE);
		lblHelpText_1.setText("help text");

		TabItem tbtmOutput_1 = new TabItem(tabFolder_2, SWT.NONE);
		tbtmOutput_1.setText("Output");

		Composite composite_9 = new Composite(tabFolder_2, SWT.NONE);
		tbtmOutput_1.setControl(composite_9);
		composite_9.setLayout(new GridLayout(3, false));

		Label lblType = new Label(composite_9, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Type");

		Combo combo = new Combo(composite_9, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

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

		Button btnSharded = new Button(composite_9, SWT.CHECK);
		btnSharded.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnSharded.setText("sharded");

		Button btnNonatomic = new Button(composite_9, SWT.CHECK);
		btnNonatomic.setText("nonAtomic");
		new Label(composite_9, SWT.NONE);

		Button btnJsmode = new Button(composite_9, SWT.CHECK);
		btnJsmode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnJsmode.setText("jsMode");

		Button btnVerbose = new Button(composite_9, SWT.CHECK);
		btnVerbose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnVerbose.setText("verbose");

		Label lblHelp_1 = new Label(composite_9, SWT.NONE);
		lblHelp_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblHelp_1.setText("help");

		Label lblNewLabel_2 = new Label(this, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("http://docs.mongodb.org/manual/reference/method/db.collection.mapReduce/");

		Button btnExecuteMapReduce = new Button(this, SWT.NONE);
		btnExecuteMapReduce.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnExecuteMapReduce.setText("execute");
	}

	@Override
	public boolean onWizardCommand(MongoCommand cmd, BasicDBObject dbo) {
		return false;
	}

}
