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
 */
package net.jumperz.app.MMonjaDB.eclipse.pref;

import net.jumperz.app.MMonjaDB.eclipse.Activator;
import net.jumperz.app.MMonjaDBCore.MConstants;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class MPrefPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, MConstants {
	
	public MPrefPage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		// setDescription( "MonjaDB Preferences" );

		// Activator.getDefault().getPreferenceStore().setDefault( RUN_ON_STARTUP, false );
	}

	
	public void createFieldEditors() {
		// addField( new StringFieldEditor( BATCH_SIZE, "Default 'limit' size:", getFieldEditorParent() ) );
		addField(new StringFieldEditor(PREF_MAX_FIND_RESULTS, "Maximum number of items shown in the Document List View:", getFieldEditorParent()));
		// addField( new BooleanFieldEditor( PREF_REMEMBER_LAST_LOCATION, "Remember last location", getFieldEditorParent() ) );
		/*
		 * addField( new StringFieldEditor( PORT, "Port:", getFieldEditorParent() ) );
		 * 
		 * addField( new BooleanFieldEditor( RUN_ON_STARTUP, RUN_ON_STARTUP, getFieldEditorParent() ) );
		 */
		/*
		 * addField( new BooleanFieldEditor( PreferenceConstants.P_BOOLEAN, "&An example of a boolean preference", getFieldEditorParent()));
		 */

		/*
		 * addField( new DirectoryFieldEditor(PreferenceConstants.P_PATH, "&Directory preference:", getFieldEditorParent()));
		 * 
		 * 
		 * addField(new RadioGroupFieldEditor( PreferenceConstants.P_CHOICE, "An example of a multiple-choice preference", 1, new String[][] { { "&Choice 1", "choice1" }, { "C&hoice 2", "choice2" } }, getFieldEditorParent()));
		 */
	}

	
	public void init(IWorkbench workbench) {
	}
	
}