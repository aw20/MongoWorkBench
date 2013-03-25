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
package org.aw20.mongoworkbench.command;

import net.jumperz.util.MRegEx;

import org.aw20.mongoworkbench.MongoFactory;

public class UseMongoCommand extends MongoCommand {

	private String dbName = null;
	
	@Override
	public void execute() throws Exception {
		if ( sName == null )
			throw new Exception("no server selected");
		
		dbName = MRegEx.getMatchIgnoreCase("^use\\s+(.*)$", cmd);
		if ( dbName == null )
			throw new Exception( "failed to retrieve database name" );

		// Not unusual for commands to end with a ; so chop it off
		dbName	= dbName.trim();
		if ( dbName.endsWith(";") )
			dbName	= dbName.substring(0, dbName.length()-1);

		// Check for bad characters
		for ( int x=0; x < dbName.length(); x++ ){
			if ( !Character.isLetterOrDigit( dbName.charAt(x) ) && dbName.charAt(x) != '_' )
				throw new Exception("database name contains invalid characters");
		}
		
		MongoFactory.getInst().setActiveDB(dbName);
	}

	@Override
	public String getCommandString() {
		return isSuccess() ? ("use " + dbName) : "invalid";
	}
}