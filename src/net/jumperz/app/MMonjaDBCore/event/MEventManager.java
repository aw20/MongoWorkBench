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
 *  https://github.com/aw20/MonjaDB
 *  Original fork: https://github.com/Kanatoko/MonjaDB
 *  
 */
package net.jumperz.app.MMonjaDBCore.event;

import net.jumperz.app.MMonjaDBCore.MAbstractLogAgent;
import net.jumperz.util.MObserver2;
import net.jumperz.util.MSubject2;
import net.jumperz.util.MSubject2Impl;

public class MEventManager extends MAbstractLogAgent implements MSubject2 {
	private static final MEventManager instance = new MEventManager();

	private MSubject2 subject2 = new MSubject2Impl();

	public static MEventManager getInstance() {
		return instance;
	}

	public void fireErrorEvent(Exception e, Object source) {
		MEvent event = new MEvent(event_error);
		event.getData().put("error", e);
		event.getData().put("source", source);
		fireEvent(event, source);
	}

	public void fireErrorEvent(Exception e) {
		MEvent event = new MEvent(event_error);
		event.getData().put("error", e);
		fireEvent(event);
	}

	public synchronized void fireEvent(MEvent event) {
		notify2(event, null);
	}

	public synchronized void fireEvent(MEvent event, Object source) {
		notify2(event, source);
	}

	public void notify2(Object event, Object source) {
		subject2.notify2(event, source);
	}

	public void register2(MObserver2 observer) {
		subject2.register2(observer);
	}

	public void removeObservers2() {
		subject2.removeObservers2();
	}

	public void removeObserver2(MObserver2 observer) {
		subject2.removeObserver2(observer);
	}

}