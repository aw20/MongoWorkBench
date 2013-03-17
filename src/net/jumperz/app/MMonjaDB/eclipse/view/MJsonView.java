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
 */
package net.jumperz.app.MMonjaDB.eclipse.view;

import net.jumperz.app.MMonjaDBCore.MInputView;
import net.jumperz.app.MMonjaDBCore.MOutputView;
import net.jumperz.app.MMonjaDBCore.action.mj.MEditAction;
import net.jumperz.app.MMonjaDBCore.event.MEvent;
import net.jumperz.mongo.MMongoUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Text;

import com.mongodb.DB;
import com.mongodb.DBObject;

public class MJsonView extends MAbstractView implements MOutputView, MInputView {
	private Text text;
	private Action saveAction;
	
	public MJsonView() {
		eventManager.register2(this);
	}

	
	public void init2() {
		parent.setLayout(new FormLayout());
		text = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(0, 1);
		fd_text.left = new FormAttachment(0, 1);
		fd_text.bottom = new FormAttachment(100, -1);
		fd_text.right = new FormAttachment(100, -1);
		text.setLayoutData(fd_text);

		saveAction = new Action() {
			public void run() {
				onSave();
			}
		};
		saveAction.setToolTipText("Save To Database");
		saveAction.setText("Save");
		initAction(saveAction, "disk.png", null);
		saveAction.setEnabled(false);
	}

	
	private void onSave() {
		// DB db = dataManager.getDB();
		// Object o = db.eval( text.getText(), null );
		String collName = dataManager.getCollName();

		String s = text.getText();
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\n", "");
		s = s.replaceAll("\t", "");

		executeAction("db." + collName + ".save(" + s + ")");
		dataManager.reloadDocument();
	}

	
	public void dispose() {
		eventManager.removeObserver2(this);
		super.dispose();
	}

	
	private void onFind() {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				text.setText("");
				saveAction.setEnabled(false);
			}
		});

	}

	
	private void onUse() {
		onFind();
	}

	
	private void onDisconnect() {
		onFind();
	}

	
	private void drawJson(DBObject data) {
		DB db = dataManager.getDB();
		text.setText(MMongoUtil.toJson(db, data));
		saveAction.setEnabled(true);
	}

	
	private void onEdit(MEditAction action) {
		final DBObject data = dataManager.getDocumentDataByAction(action);
		if (data == null) {
			return;
		}

		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				drawJson(data);
			}
		});
	}

	
	public void update(final Object e, final Object source) {
		final MEvent event = (MEvent) e;
		
		if (event.getEventName().indexOf(event_mj_edit + "_end") == 0) {
			MEditAction action = (MEditAction) source;
			onEdit(action);
		} else if (event.getEventName().indexOf(event_find + "_end") == 0) {
			onFind();
		} else if (event.getEventName().indexOf(event_use + "_end") == 0) {
			onUse();
		} else if (event.getEventName().indexOf(event_disconnect + "_end") == 0) {
			onDisconnect();
		}
	}
	
}
