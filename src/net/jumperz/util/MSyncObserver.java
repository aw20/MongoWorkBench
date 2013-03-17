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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MSyncObserver implements MObserver2 {
	private Map methodMap = new HashMap();

	private Object instance;

	public MSyncObserver(Object instance) {
		init(instance);
	}

	public void update(Object eventName, Object source) {
		try {
			if (methodMap.containsKey(eventName)) {
				Method method = (Method) methodMap.get(eventName);
				method.invoke(instance, new Object[] { source });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init(Object instance) {
		this.instance = instance;
		Class c = instance.getClass();

		Method[] methodArray = c.getDeclaredMethods();
		for (int i = 0; i < methodArray.length; ++i) {
			Method method = methodArray[i];
			String methodName = method.getName();
			methodMap.put(methodName, method);
		}
	}

}