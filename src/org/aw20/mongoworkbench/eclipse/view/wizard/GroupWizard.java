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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class GroupWizard extends Composite {
	
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

		TabItem tbtmKey = new TabItem(tabFolder_3, SWT.NONE);
		tbtmKey.setText("key");

		Composite composite_11 = new Composite(tabFolder_3, SWT.NONE);
		tbtmKey.setControl(composite_11);
		composite_11.setLayout(new GridLayout(1, false));

		textGroupKey = new Text(composite_11, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textGroupKey.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblHelp_2 = new Label(composite_11, SWT.NONE);
		lblHelp_2.setText("help");

		TabItem tbtmReduce = new TabItem(tabFolder_3, SWT.NONE);
		tbtmReduce.setText("reduce");

		Composite composite_12 = new Composite(tabFolder_3, SWT.NONE);
		tbtmReduce.setControl(composite_12);
		composite_12.setLayout(new GridLayout(1, false));

		textGroupReduce = new Text(composite_12, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textGroupReduce.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblHelp_3 = new Label(composite_12, SWT.NONE);
		lblHelp_3.setText("help");

		TabItem tbtmInitial2 = new TabItem(tabFolder_3, SWT.NONE);
		tbtmInitial2.setText("initial");

		Composite composite_13 = new Composite(tabFolder_3, SWT.NONE);
		tbtmInitial2.setControl(composite_13);
		composite_13.setLayout(new GridLayout(1, false));

		textGroupInitial = new Text(composite_13, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textGroupInitial.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblHelp_4 = new Label(composite_13, SWT.NONE);
		lblHelp_4.setText("help");

		TabItem tbtmKeyF2 = new TabItem(tabFolder_3, SWT.NONE);
		tbtmKeyF2.setText("key function");

		Composite composite_14 = new Composite(tabFolder_3, SWT.NONE);
		tbtmKeyF2.setControl(composite_14);
		composite_14.setLayout(new GridLayout(1, false));

		textGroupKeyF = new Text(composite_14, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textGroupKeyF.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblHelp_5 = new Label(composite_14, SWT.NONE);
		lblHelp_5.setText("help");

		TabItem tbtmCondition2 = new TabItem(tabFolder_3, SWT.NONE);
		tbtmCondition2.setText("condition");

		Composite composite_15 = new Composite(tabFolder_3, SWT.NONE);
		tbtmCondition2.setControl(composite_15);
		composite_15.setLayout(new GridLayout(1, false));

		textGroupCondition = new Text(composite_15, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textGroupCondition.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblHelp_6 = new Label(composite_15, SWT.NONE);
		lblHelp_6.setText("help");

		TabItem tbtmFinalize2 = new TabItem(tabFolder_3, SWT.NONE);
		tbtmFinalize2.setText("finalize");

		Composite composite_16 = new Composite(tabFolder_3, SWT.NONE);
		tbtmFinalize2.setControl(composite_16);
		composite_16.setLayout(new GridLayout(1, false));

		textGroupFinalize = new Text(composite_16, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textGroupFinalize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblHelp_7 = new Label(composite_16, SWT.NONE);
		lblHelp_7.setText("help");

		Label lblNewLabel_3 = new Label(this, SWT.NONE);
		lblNewLabel_3.setText("http://docs.mongodb.org/manual/reference/method/db.collection.group/");

		Button btnExecuteGroup = new Button(this, SWT.NONE);
		btnExecuteGroup.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnExecuteGroup.setText("execute");
	}

}
