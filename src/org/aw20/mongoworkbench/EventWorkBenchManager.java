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
 *  March 2013
 */
package org.aw20.mongoworkbench;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class EventWorkBenchManager extends Object {
	private static EventWorkBenchManager thisInst;
	
	public static synchronized EventWorkBenchManager getInst(){
		if ( thisInst == null )
			thisInst = new EventWorkBenchManager();

		return thisInst;
	}

	private Set<EventWorkBenchListener>	listeners;
	
	private EventWorkBenchManager(){
		listeners	= new HashSet<EventWorkBenchListener>();
	}
	
	public void registerListener(EventWorkBenchListener listener){
		listeners.add(listener);
	}
	
	public void deregisterListener(EventWorkBenchListener listener){
		listeners.remove(listener);
	}

	public void onEvent( final Event event, final Object data ){
		
		new Thread("EventWorkBenchEventFire"){ 
			public void run(){
				Iterator<EventWorkBenchListener>	it	= listeners.iterator();
				while ( it.hasNext() ){
					try{
						it.next().onEventWorkBench(event, data);
					}catch(Exception e){
						System.out.println(e);
					}
				}
			}
		}.start();

	}
	
}
