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

import java.util.List;

import org.aw20.mongoworkbench.MongoFactory;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class PassThruMongoCommand extends MongoCommand {
	private Object resultObj;
	
	@Override
	public void execute() throws Exception {
		MongoClient mdb = MongoFactory.getInst().getMongo( sName );
		if ( mdb == null )
			throw new Exception("no server selected");
		
		if ( sDb == null )
			throw new Exception("no database selected");
		
		MongoFactory.getInst().setActiveDB(sDb);
		DB db	= mdb.getDB(sDb);
		
		resultObj = db.eval( cmd, (Object[])null);
	}
	
	public boolean isViewable(){
		if ( resultObj == null )
			return false;
		else if ( resultObj instanceof List )
			return true;
		else
			return false;
	}

	public BasicDBList getResults(){
		BasicDBList list = (BasicDBList)resultObj;
		
		for ( int x=0; x < list.size(); x++ ){
			Object o	= list.get(x);
			if ( !(o instanceof DBObject) ){
				BasicDBObject	dbo	= new BasicDBObject("1", o );
				list.set(x, fixNumbers(dbo) );
			}
		}
		
		return list;
	}
	
}