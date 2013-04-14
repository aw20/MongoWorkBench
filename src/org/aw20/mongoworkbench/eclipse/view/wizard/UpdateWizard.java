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
import org.eclipse.swt.widgets.Text;

public class UpdateWizard extends Composite {

	private Text textUpdateQuery;
	private Text textUpdateUpdate;
	
	public UpdateWizard(Composite parent, int style) {
		super(parent, style);
	
		setLayout(new GridLayout(3, false));

		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setText("Query");

		Label lblUpdate = new Label(this, SWT.NONE);
		lblUpdate.setText("Update");
		new Label(this, SWT.NONE);

		textUpdateQuery = new Text(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textUpdateQuery.setToolTipText("asdasdas");
		textUpdateQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

		textUpdateUpdate = new Text(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textUpdateUpdate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));

		Button btnUpdateUpsert = new Button(this, SWT.CHECK);
		btnUpdateUpsert.setToolTipText("If a record is not found, then create a new one");
		btnUpdateUpsert.setText("Upsert");

		Button btnUpdateMulti = new Button(this, SWT.CHECK);
		btnUpdateMulti.setToolTipText("Run this update on multiple records if matched");
		btnUpdateMulti.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnUpdateMulti.setText("Multi");

		Label lblTheUpdateMethod = new Label(this, SWT.WRAP);
		lblTheUpdateMethod.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblTheUpdateMethod.setText("http://docs.mongodb.org/manual/reference/method/db.collection.update/");

		Button btnUpdateExecute = new Button(this, SWT.NONE);
		btnUpdateExecute.setText("execute");
	}

	
	
}
