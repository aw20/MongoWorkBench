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
 *  $Id: SystemClock.java 2608 2011-11-24 07:08:23Z alan $
 */
package org.aw20.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class SystemClock extends Object {

	private static SystemClock thisInst = new SystemClock();

	public static int	DAY_EVENT 		= 0;
	public static int	HOUR_EVENT 		= 1;
	public static int	MONTH_EVENT 	= 2;
	public static int	MINUTE_EVENT	= 3;

	private long	startTime	= System.currentTimeMillis();
	private int currentYear = -1; 
	private int currentMonth = -1; 
	private int currentDay = -1, previousDay = -1, lastDayMonth = -1;
	private int currentDayOfYear = -1;
	private int	currentDayOfWeek = -1;
	private int currentHour = -1;
	private int currentMinute = -1;
	
	private ArrayList	listenersHour 	= new ArrayList(); 
	private ArrayList	listenersMinute = new ArrayList(); 
	private ArrayList	listenersDay 		= new ArrayList(); 
	private ArrayList	listenersMonth 	= new ArrayList(); 
	
	private Timer timer;
	
	private SystemClock(){
		setPreviousTime();
		setTime();
		
		timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					setTime();
				}
			}, 500, 1000);
    
	}
	
	
	private void setPreviousTime(){
		Calendar thisDate = new GregorianCalendar();
		thisDate.add( Calendar.DAY_OF_MONTH, -1 );
		
		previousDay = thisDate.get( Calendar.DAY_OF_MONTH );
	}

	private synchronized void setTime(){
		Calendar thisDate = new GregorianCalendar();
		currentYear 			= thisDate.get( Calendar.YEAR );
		currentDayOfYear	= thisDate.get( Calendar.DAY_OF_YEAR );
		currentDayOfWeek	= thisDate.get( Calendar.DAY_OF_WEEK );
		lastDayMonth 			= thisDate.getActualMaximum(Calendar.DAY_OF_MONTH);

		
		/* Handle the Monthly change */
		if ( thisDate.get( Calendar.MONTH ) != currentMonth ){
			currentMonth = thisDate.get( Calendar.MONTH );
			Iterator it = listenersMonth.iterator();
			while ( it.hasNext() ){
				final SystemClockEvent	handlerMonth = (SystemClockEvent)it.next();
				
				new Thread("SystemClockMonthEvent_" + currentYear + "-" + currentMonth + "-" + currentDay + "_" + currentHour + ":" + currentMinute ){
					public void run(){
						try{
							handlerMonth.clockEvent(MONTH_EVENT);
						}catch(Throwable ignore){}
					}
				}.start();
			}
		}
		
		
		/* Handle the HOURLY change */
		if ( thisDate.get( Calendar.HOUR_OF_DAY ) != currentHour ){
			currentHour = thisDate.get( Calendar.HOUR_OF_DAY );
			Iterator it = listenersHour.iterator();
			while ( it.hasNext() ){
				final SystemClockEvent	handlerHour = (SystemClockEvent)it.next();
				
				new Thread( "SystemClockHourEvent_" + currentYear + "-" + currentMonth + "-" + currentDay + "_" + currentHour + ":" + currentMinute ){
					public void run(){
						try{
							handlerHour.clockEvent(HOUR_EVENT);
						}catch(Throwable ignore){}
					}
				}.start();
			}
		}
		
		
		/* Handle the MINUTE change */
		if ( thisDate.get( Calendar.MINUTE ) != currentMinute ){
			currentMinute = thisDate.get( Calendar.MINUTE );
			Iterator it = listenersMinute.iterator();
			while ( it.hasNext() ){
				SystemClockMinuteWrapper wrapper = (SystemClockMinuteWrapper)it.next();
				
				if ( wrapper.bRunOnce )
					it.remove();
				
				if ( wrapper.minuteLeap == 1 || currentMinute%wrapper.minuteLeap == 0 ){
					final SystemClockEvent minHandler = wrapper.handler;

					new Thread("SystemClockMinuteEvent_" + currentYear + "-" + currentMonth + "-" + currentDay + "_" + currentHour + ":" + currentMinute ){
						public void run(){
							try{
								minHandler.clockEvent( MINUTE_EVENT );
							}catch(Throwable ignore){}
						}
					}.start();
				
				}
			}
		}
		
		
		/* Handle the DAY change */
		if ( thisDate.get( Calendar.DAY_OF_MONTH ) != currentDay ){
			previousDay	= currentDay;
			currentDay	= thisDate.get( Calendar.DAY_OF_MONTH );
			
			Iterator it = listenersDay.iterator();
			while ( it.hasNext() ){
				final SystemClockEvent	handlerDay = (SystemClockEvent)it.next();

				new Thread("SystemClockDayEvent_" + currentYear + "-" + currentMonth + "-" + currentDay + "_" + currentHour + ":" + currentMinute){
					public void run(){
						try{
							handlerDay.clockEvent( DAY_EVENT );
						}catch(Throwable ignore){}
					}
				}.start();
			}
		}		
	}
	
	public static void shutdown(){
		thisInst.timer.cancel();
		removeAllListeners();
	}
	
	public static void removeAllListeners(){
		synchronized( thisInst ){
			thisInst.listenersMonth.clear();
			thisInst.listenersDay.clear();
			thisInst.listenersHour.clear();
			thisInst.listenersMinute.clear();
		}
	}
	
	public static void removeListenerHour( SystemClockEvent _handler ){
		synchronized( thisInst ){
			thisInst.listenersHour.remove( _handler );
		}
	}
	
	public static void removeListenerMonth( SystemClockEvent _handler ){
		synchronized( thisInst ){
			thisInst.listenersMonth.remove( _handler );
		}
	}
	
	public static void removeListenerDay( SystemClockEvent _handler ){
		synchronized( thisInst ){
			thisInst.listenersDay.remove( _handler );
		}
	}
	
	public static void removeListenerMinute( SystemClockEvent _handler ){
		synchronized( thisInst ){
			
			Iterator<SystemClockMinuteWrapper> it = thisInst.listenersMinute.iterator();
			while ( it.hasNext() ){
				SystemClockMinuteWrapper s = it.next();
				if ( s.handler == _handler ){
					it.remove();
					return;
				}
			}
			
		}
	}
		
	public static void setListenerHour( SystemClockEvent _handler ){
		synchronized( thisInst ){
			thisInst.listenersHour.add( _handler );
		}
	}
	
	public static void setListenerMinute( SystemClockEvent _handler ){
		synchronized( thisInst ){
			setListenerMinute( _handler, 1 );
		}
	}
	
	public static void setListenerMinute( SystemClockEvent _handler, int minuteLeap ){
		synchronized( thisInst ){
			SystemClockMinuteWrapper mW	= new SystemClockMinuteWrapper( _handler, minuteLeap, false );
			thisInst.listenersMinute.add( mW );
		}
	}
		
	public static void setListenerMinute( SystemClockEvent _handler, int minuteLeap, boolean bRunOnce ){
		synchronized( thisInst ){
			SystemClockMinuteWrapper mW	= new SystemClockMinuteWrapper( _handler, minuteLeap, bRunOnce );
			thisInst.listenersMinute.add( mW );
		}
	}
	
	public static void setListenerDay( SystemClockEvent _handler ){
		synchronized( thisInst ){
			thisInst.listenersDay.add( _handler );
		}
	}
	
	public static void setListenerMonth( SystemClockEvent _handler ){
		synchronized( thisInst ){
			thisInst.listenersMonth.add( _handler );
		}
	}
	
	public static int getCurrentHour(){
		return thisInst.currentHour;
	}
	
	public static int getPreviousHour(){
		if ( thisInst.currentHour == 0 )
			return 23;
		else
			return thisInst.currentHour - 1;
	}
	
	public static int getNextHour(){
		if ( thisInst.currentHour == 23 )
			return 0;
		else
			return thisInst.currentHour + 1;
	}

	public static int getCurrentDay() {
		return thisInst.currentDay;
	}

	public static int getPreviousDay() {
		return thisInst.previousDay;
	}
	
	public static int getLastDayMonth() {
		return thisInst.lastDayMonth;	
	}

	public static int getCurrentDayOfYear() {
		return thisInst.currentDayOfYear;
	}

	public static int getCurrentMinute() {
		return thisInst.currentMinute;
	}

	public static int getCurrentDailyMinutes(){
		return (thisInst.currentHour * 60) + thisInst.currentMinute;
	}
	
	public static int getCurrentMonth() {
		return thisInst.currentMonth + 1;	//- java is 0 based
	}

	public static int getCurrentYear() {
		return thisInst.currentYear;
	}

	public static int getPreviousYear() {
		return thisInst.currentYear - 1;
	}
	
	public static int getCurrentDayOfWeek(){
		return thisInst.currentDayOfWeek;
	}
	
	/**
	 * Returns the number of seconds since we started this JVM; never returns 0
	 * @return
	 */
	public static int getElapsedSeconds(){
		int seconds = (int)( (System.currentTimeMillis()-thisInst.startTime)/1000 );
		if ( seconds == 0 )
			return 1;
		else 
			return seconds;
	}
}
