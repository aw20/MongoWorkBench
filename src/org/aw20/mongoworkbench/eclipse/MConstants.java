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
package org.aw20.mongoworkbench.eclipse;

public interface MConstants {

	public static final int default_batch_size = 50;
	public static final int default_max_results = 50;

	public static final int default_ssh_port = 22;
	public static final int default_mongo_port = 27017;

	public static final String CONNECT_DIALOG_HOST = "connect_dialog_host";
	public static final String CONNECT_DIALOG_DB = "connect_dialog_db";
	public static final String CONNECT_DIALOG_PORT = "connect_dialog_port";
	public static final String CONNECT_DIALOG_SSH = "connect_dialog_ssh";
	public static final String CONNECT_DIALOG_SSH_KEY = "connect_dialog_ssh_key";
	public static final String CONNECT_DIALOG_NORMAL_CONNECTION = "connect_dialog_normal_connection";

	public static final String DEFAULT_CONFIG_FILE_NAME = "monjadb.conf";

	public static final String DBLIST_TABLE = "dblist_table";

	public static final String COLLLIST_TABLE = "colllist_table";

	public static final String DOCUMENTLIST_TABLE = "documentlist_table";

	public static final String CONSOLE_NAME = "MonjaDB Log";

	// eclipse pref
	public static final String PREF_MAX_FIND_RESULTS = "maxFindResults";

	public static final String PREF_REMEMBER_LAST_LOCATION = "rememberLastLocation";
}