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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class AggregateWizard extends Composite {
	
	private Text textPipe;

	public AggregateWizard(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new GridLayout(5, false));

		TabFolder tabFolder_1 = new TabFolder(this, SWT.NONE);
		tabFolder_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));

		TabItem tbtmPipe = new TabItem(tabFolder_1, SWT.NONE);
		tbtmPipe.setText("Pipe#1");

		textPipe = new Text(tabFolder_1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		tbtmPipe.setControl(textPipe);

		Label lblHttpdocs = new Label(this, SWT.NONE);
		GridData gd_lblHttpdocsmongodborgmanualreferencemethoddbcollectionaggrega = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_lblHttpdocsmongodborgmanualreferencemethoddbcollectionaggrega.widthHint = 425;
		lblHttpdocs.setLayoutData(gd_lblHttpdocsmongodborgmanualreferencemethoddbcollectionaggrega);
		lblHttpdocs.setText("http://docs.mongodb.org/manual/reference/method/db.collection.aggregate");

		Button btnAddPipeline = new Button(this, SWT.NONE);
		btnAddPipeline.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnAddPipeline.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnAddPipeline.setText("+pipe");

		Button btnRemovePipe = new Button(this, SWT.NONE);
		btnRemovePipe.setEnabled(false);
		btnRemovePipe.setText("-pipe");

		Button btnExecuteAggregation = new Button(this, SWT.NONE);
		btnExecuteAggregation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnExecuteAggregation.setText("execute");

		
	}

}
