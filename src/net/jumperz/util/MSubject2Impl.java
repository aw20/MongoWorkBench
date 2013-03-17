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
package net.jumperz.util;

import java.util.*;

public class MSubject2Impl implements MSubject2 {
	private List Observers;

	public MSubject2Impl() {
		Observers = new ArrayList();
	}

	public void notify2(Object event, Object source) {
		List tmpList = null;
		synchronized (Observers) {
			tmpList = new ArrayList(Observers);
		}

		int size = tmpList.size();
		for (int i = 0; i < size; ++i) {
			MObserver2 Observer = (MObserver2) tmpList.get(i);
			Observer.update(event, source);
		}
	}

	public void register2(MObserver2 observer) {
		synchronized (Observers) {
			Observers.add(observer);
		}
	}

	public void removeObservers2() {
		synchronized (Observers) {
			Observers.clear();
		}
	}

	public void removeObserver2(MObserver2 observer) {
		synchronized (Observers) {
			Observers.remove(observer);
		}
	}
}
