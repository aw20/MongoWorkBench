/* 
 *  Copyright (C) 2011 AW2.0 Ltd
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
 *  $Id: SystemClockMinuteWrapper.java 2608 2011-11-24 07:08:23Z alan $
 */
package org.aw20.util;

public class SystemClockMinuteWrapper {
	public int minuteLeap;
	public SystemClockEvent handler;
	public boolean bRunOnce;
	
	public SystemClockMinuteWrapper(SystemClockEvent _handler, int _minuteLeap, boolean _bRunOnce ){
		handler 		= _handler;
		minuteLeap	= _minuteLeap;
		bRunOnce		= _bRunOnce;
	}
}