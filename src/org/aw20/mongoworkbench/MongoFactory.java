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
package org.aw20.mongoworkbench;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aw20.mongoworkbench.command.FindMongoCommand;
import org.aw20.mongoworkbench.command.MongoCommand;
import org.aw20.mongoworkbench.command.PassThruMongoCommand;
import org.aw20.mongoworkbench.command.SaveMongoCommand;
import org.aw20.mongoworkbench.command.UseMongoCommand;
import org.aw20.util.StringUtil;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoFactory extends Thread {

	private static MongoFactory	thisInst = null;
	
	public static synchronized MongoFactory getInst(){
		if ( thisInst == null )
			thisInst = new MongoFactory();

		return thisInst;
	}
	
	private Map<String,MongoClient>	mongoMap;
	private Set<MongoCommandListener>	mongoListeners;
	private List<MongoCommand>	commandQueue;
	private Map<String, Class>	commandMap;
	private boolean bRun = true;

	
	private String activeServer = null, activeDB = null;
	
	public MongoFactory(){
		mongoMap				= new HashMap<String,MongoClient>();
		commandQueue		=	new ArrayList<MongoCommand>(); 
		mongoListeners	= new HashSet<MongoCommandListener>();
		commandMap			= new HashMap<String,Class>();

		// Register the Commands
		commandMap.put("^db\\.[^\\(]+\\.find\\(.*", FindMongoCommand.class);
		commandMap.put("^db\\.[^\\(]+\\.save\\(.*", SaveMongoCommand.class);
		commandMap.put("^use\\s+.*", UseMongoCommand.class);

		setName( "MongoFactory" );
		start();
	}
	
	public void registerListener(MongoCommandListener listener){
		mongoListeners.add(listener);
	}
	
	public void deregisterListener(MongoCommandListener listener){
		mongoListeners.remove(listener);
	}
	
	public void registerMongo( Map<String,Object> mongoDetails ) throws UnknownHostException{
		String name	= (String)mongoDetails.get("name");
		
		MongoClient	mclient	= mongoMap.get(name);
		if ( mclient != null )
			mclient.close();
		
		mclient	= new MongoClient( (String)mongoDetails.get("host"), StringUtil.toInteger(mongoDetails.get("port"), 27017) );
		mongoMap.put(name, mclient);
	}
	
	public void removeMongo(String sName){
		MongoClient	mclient	= mongoMap.remove(sName);
		if ( mclient != null )
			mclient.close();
	}
	
	public void destroy(){
		bRun = false;
	}
	
	public String getActiveServer(){
		return activeServer;
	}
	
	public String getActiveDB(){
		return activeDB;
	}
	
	public void setActiveDB(String db){
		this.activeDB = db;
	}
	
	public MongoCommand	createCommand( String cmd ) throws Exception {
		
		// remove line breaks
		cmd = cmd.replaceAll("(\\r|\\n)", "");
		cmd = cmd.replaceAll("\\t+", " ");
		cmd	= cmd.trim();

		Iterator p = commandMap.keySet().iterator();
		while (p.hasNext()) {
			String patternStr = (String) p.next();
			if (cmd.matches(patternStr)) {
				Class clazz = (Class) commandMap.get(patternStr);
				try {
					MongoCommand mcmd = (MongoCommand)clazz.newInstance();
					mcmd.setConnection(activeServer, activeDB);
					mcmd.setCommandStr( cmd );
					mcmd.parseCommandStr();
					return mcmd;
				} catch (Exception e) {
					EventWorkBenchManager.getInst().onEvent( Event.EXCEPTION, e);
					return null;
				}
			}
		}
		
		// Determine if this is a command
		if ( cmd.startsWith("db.") || cmd.startsWith("sh.") || cmd.startsWith("rs.") ){
			MongoCommand mcmd = new PassThruMongoCommand();
			mcmd.setConnection(activeServer, activeDB);
			mcmd.setCommandStr(cmd);
			return mcmd;
		}
			
		
		return null;
	}
	
	public void submitExecution( MongoCommand mcmd ){
		synchronized(commandQueue){
			commandQueue.add(mcmd);
			commandQueue.notify();
		}
	}
	
	public MongoClient getMongo(String sName) {
		activeServer = sName;
		return mongoMap.get(sName);
	}

	public MongoClient getMongoActive() {
		return mongoMap.get(activeServer);
	}

	public DB getMongoActiveDB() {
		return mongoMap.get(activeServer).getDB(activeDB);
	}
	
	public void run(){
		MongoCommand	cmd;
		
		while (bRun){
			
			synchronized(commandQueue){
				try {
					commandQueue.wait();
					cmd	= commandQueue.remove(0);

					if ( cmd == null )
						continue;
					
				} catch (InterruptedException e) {
					bRun = false;
					break;
				}
			}


			// Now let the listeners know we are about to start
			Iterator<MongoCommandListener>	it	= mongoListeners.iterator();
			while ( it.hasNext() ){
				try{
					it.next().onMongoCommandStart(cmd);
				}catch(Exception e){
					EventWorkBenchManager.getInst().onEvent( Event.EXCEPTION, e);
				}
			}
			
			// Execute the command
			long startTime	= System.currentTimeMillis();
			try{
				cmd.execute();
			}catch(Exception e){
				cmd.setException( e );
			}finally{
				cmd.setExecTime( System.currentTimeMillis() - startTime );
				cmd.hasRun();
			}


			// Now let the listeners know we have finished
			it	= mongoListeners.iterator();
			while ( it.hasNext() ){
				try{
					it.next().onMongoCommandFinished(cmd);
				}catch(Exception e){
					EventWorkBenchManager.getInst().onEvent( Event.EXCEPTION, e);
				}
				
			}
			
		}
	}

	public void setActiveServerDB(String name, String db) {
		activeServer = name;
		activeDB = db;
	}
}
