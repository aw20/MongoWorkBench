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


import java.util.Map;

import org.aw20.mongoworkbench.Event;
import org.aw20.mongoworkbench.EventWorkBenchListener;
import org.aw20.mongoworkbench.EventWorkBenchManager;
import org.aw20.mongoworkbench.EventWrapper;
import org.aw20.mongoworkbench.MongoFactory;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.eclipse.view.wizard.AggregateWizard;
import org.aw20.mongoworkbench.eclipse.view.wizard.FindWizard;
import org.aw20.mongoworkbench.eclipse.view.wizard.GroupWizard;
import org.aw20.mongoworkbench.eclipse.view.wizard.MapReduceWizard;
import org.aw20.mongoworkbench.eclipse.view.wizard.UpdateWizard;
import org.aw20.mongoworkbench.eclipse.view.wizard.WizardCommandI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.mongodb.BasicDBObject;

public class MCommandWizardView extends MAbstractView implements EventWorkBenchListener, WizardParentI {
	
	private TabFolder tabFolder;
	private String activeDB = null, activeColl = null;
	
	public MCommandWizardView() {
		EventWorkBenchManager.getInst().registerListener(this);
	}

	public void dispose() {
		EventWorkBenchManager.getInst().deregisterListener(this);
		super.dispose();
	}
	
	public void init2() {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder = new TabFolder(parent, SWT.NONE);
		
		TabItem tbtmFind = new TabItem(tabFolder, SWT.NONE);
		tbtmFind.setText("db.col.find()");
		tbtmFind.setControl(new FindWizard(this, tabFolder, SWT.NONE));
	

		TabItem tbtmUpdate = new TabItem(tabFolder, SWT.NONE);
		tbtmUpdate.setText("db.col.update()");
		tbtmUpdate.setControl(new UpdateWizard(this, tabFolder, SWT.NONE));
		
		TabItem tbtmGroupItem = new TabItem(tabFolder, SWT.NONE);
		tbtmGroupItem.setText("db.col.group()");
		tbtmGroupItem.setControl(new GroupWizard(this, tabFolder, SWT.NONE));

		TabItem tbtmAggregate = new TabItem(tabFolder, SWT.NONE);
		tbtmAggregate.setText("db.col.aggregate()");
		tbtmAggregate.setControl(new AggregateWizard(this, tabFolder, SWT.NONE));
		
		TabItem tbtmMapReduce = new TabItem(tabFolder, SWT.NONE);
		tbtmMapReduce.setText("db.col.mapReduce()");
		tbtmMapReduce.setControl( new MapReduceWizard(this, tabFolder, SWT.NONE));
	}

	@Override
	public void onEventWorkBench(Event event, final Object data) {
		if ( event != Event.TOWIZARD )
			return;
		
		final MongoCommand cmd	= (MongoCommand)((Map)data).get(EventWrapper.COMMAND);
		final BasicDBObject dbo = (BasicDBObject)((Map)data).get(EventWrapper.DBOBJECT);

		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				
				Control[]	childControls	= tabFolder.getChildren();
				for ( int x=0; x < childControls.length; x++ ){
					if ( ((WizardCommandI)childControls[x]).onWizardCommand(cmd, dbo) ){
						tabFolder.setSelection(x);
						activeDB 		= cmd.getDB();
						activeColl 	= cmd.getCollection();
						break;
					}
				}
				
				// Set the tabitems
				TabItem[] tabItems = tabFolder.getItems();
				tabItems[0].setText("db." + activeColl + ".find()");
				tabItems[1].setText("db." + activeColl + ".update()");
				tabItems[2].setText("db." + activeColl + ".group()");
				tabItems[3].setText("db." + activeColl + ".aggregate()");
				tabItems[4].setText("db." + activeColl + ".mapReduce()");
			}
		});

	}

	@Override
	public String getActiveDB() {
		return ( activeDB == null ) ? MongoFactory.getInst().getActiveDB() : activeDB;
	}

	@Override
	public String getActiveCollection() {
		return ( activeColl == null ) ? MongoFactory.getInst().getActiveCollection() : activeColl;
	}
}