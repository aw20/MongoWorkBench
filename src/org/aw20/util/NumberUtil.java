/* 
 *  Copyright (C) 2013 AW2.0 Ltd
 *
 *  org.aw20 is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  Free Software Foundation,version 3.
 *  
 *  OpenBD is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with org.aw20.  If not, see http://www.gnu.org/licenses/
 *  
 *  Additional permission under GNU GPL version 3 section 7
 *  
 *  If you modify this Program, or any covered work, by linking or combining 
 *  it with any of the JARS listed in the README.txt (or a modified version of 
 *  (that library), containing parts covered by the terms of that JAR, the 
 *  licensors of this Program grant you additional permission to convey the 
 *  resulting work. 
 *  
 *  $Id: FileUtil.java 2981 2012-08-08 21:01:27Z alan $
 */
package org.aw20.util;

public class NumberUtil {

	public static Object fixDouble( Double d ){
		long l = d.longValue();
		if ( (double)l == d ){
			if ( l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE )
				return (int)l;
			else
				return l;
		}else
			return d;
	}
	
}
