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

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class MListComparator implements Comparator, Serializable {
	private static final long serialVersionUID = 1L;
	List list;

	public MListComparator(List l) {
		list = l;
	}

	public int compare(Object o1, Object o2) {
		int pos1 = list.indexOf(o1);
		int pos2 = list.indexOf(o2);

		if (pos1 == -1 && pos2 == -1) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s1.compareTo(s2);
		}

		if (pos1 == -1) {
			pos1 = Integer.MAX_VALUE;
		}
		if (pos2 == -1) {
			pos2 = Integer.MAX_VALUE;
		}
		return (pos1 - pos2);
	}

}