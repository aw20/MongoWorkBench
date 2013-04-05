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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aw20.mongoworkbench.MongoFactory;

import com.mongodb.CommandResult;
import com.mongodb.MongoClient;

public class DBStatsMongoCommand extends ShowDbsMongoCommand {

	protected List<Map>	statsListMap = new ArrayList<Map>();
	private DecimalFormat df2places = new DecimalFormat("#0.00");
	private DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance();
	
	public DBStatsMongoCommand(){
		df2places.setGroupingUsed(true);
		
		DecimalFormatSymbols custom=new DecimalFormatSymbols();
		custom.setDecimalSeparator(',');
		nf.setDecimalFormatSymbols(custom);
	}
	
	@Override
	public void execute() throws Exception {
		MongoClient mdb = MongoFactory.getInst().getMongo( sName );
		
		if ( mdb == null )
			throw new Exception("no server selected");

		setDBNames(mdb);
		Iterator<String> it = dbNames.iterator();
		while ( it.hasNext() ){
			String db = it.next();
			
			CommandResult cmdr = mdb.getDB(db).getStats();
			statsListMap.add( transform(cmdr.toMap()) );
		}
		
		setMessage("Retrived Database Stats; db=" + statsListMap.size() );
	}

	protected Map transform(Map map) {
		
		if ( map.containsKey("avgObjSize") )
			map.put("avgObjSize", df2places.format(map.get("avgObjSize")) );
		
		if ( map.containsKey("paddingFactor") )
			map.put("paddingFactor", df2places.format(map.get("paddingFactor")) );
		
		if ( map.containsKey("fileSize") )
			map.put("fileSize", nf.format(map.get("fileSize")) );
		
		if ( map.containsKey("indexSize") )
			map.put("indexSize", nf.format(map.get("indexSize")) );
		
		if ( map.containsKey("size") )
			map.put("size", nf.format(map.get("size")) );
		
		if ( map.containsKey("lastExtentSize") )
			map.put("lastExtentSize", nf.format(map.get("lastExtentSize")) );
		
		if ( map.containsKey("totalIndexSize") )
			map.put("totalIndexSize", nf.format(map.get("totalIndexSize")) );
		
		if ( map.containsKey("objects") )
			map.put("objects", nf.format(map.get("objects")) );
		
		if ( map.containsKey("count") )
			map.put("count", nf.format(map.get("count")) );
		
		if ( map.containsKey("storageSize") )
			map.put("storageSize", nf.format(map.get("storageSize")) );
		
		if ( map.containsKey("dataSize") )
			map.put("dataSize", nf.format(map.get("dataSize")) );

		return map;
	}

	@Override
	public String getCommandString() {
		return "db.getStats()";
	}
	
	public List<Map> getStatsListMap(){
		return statsListMap;
	}
	
}
