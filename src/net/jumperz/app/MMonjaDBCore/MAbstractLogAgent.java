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
package net.jumperz.app.MMonjaDBCore;

import net.jumperz.util.MLogServer;

public abstract class MAbstractLogAgent implements MConstants {
	public static boolean enabled = false;

	protected String className = "";
	protected String prefix = "";

	public void log(int logLevel, Object message) {
		if (enabled) {
			MLogServer.getInstance().log(className, logLevel, prefix, message);
		}
	}

	public void info(Object message) {
		log(MLogServer.log_info, message);
	}

	public void warn(Object message) {
		log(MLogServer.log_warn, message);
	}

	public void debug(Object message) {
		log(MLogServer.log_debug, message);
	}

}