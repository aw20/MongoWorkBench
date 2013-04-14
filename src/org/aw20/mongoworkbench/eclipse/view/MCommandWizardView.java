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


import org.aw20.mongoworkbench.eclipse.view.wizard.AggregateWizard;
import org.aw20.mongoworkbench.eclipse.view.wizard.GroupWizard;
import org.aw20.mongoworkbench.eclipse.view.wizard.MapReduceWizard;
import org.aw20.mongoworkbench.eclipse.view.wizard.UpdateWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class MCommandWizardView extends MAbstractView {
	public MCommandWizardView() {
	}

	public void init2() {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);

		TabItem tbtmUpdate = new TabItem(tabFolder, SWT.NONE);
		tbtmUpdate.setText("db.col.update()");
		tbtmUpdate.setControl(new UpdateWizard(tabFolder, SWT.NONE));
		
		TabItem tbtmGroupItem = new TabItem(tabFolder, SWT.NONE);
		tbtmGroupItem.setText("db.col.group()");
		tbtmGroupItem.setControl(new GroupWizard(tabFolder, SWT.NONE));

		TabItem tbtmAggregate = new TabItem(tabFolder, SWT.NONE);
		tbtmAggregate.setText("db.col.aggregate()");
		tbtmAggregate.setControl(new AggregateWizard(tabFolder, SWT.NONE));
		
		TabItem tbtmMapReduce = new TabItem(tabFolder, SWT.NONE);
		tbtmMapReduce.setText("db.col.mapReduce()");
		tbtmMapReduce.setControl( new MapReduceWizard(tabFolder, SWT.NONE));
	}

}