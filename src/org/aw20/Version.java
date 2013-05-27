/*
 * (c) Copyright 2012 AW2.0 Ltd
 * 
 * Original Author: AW2.0Ltd
 * 
 * Licensed for use in AW2.0 Ltd projects. Perpetual runtime licence.
 * 
 * $Id: Version.java 2818 2012-03-24 15:55:29Z andy $
 *
 */
package org.aw20;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {

	private static String release = "0";
	
	static{
		InputStream in = null;
		try{
			in = Version.class.getResourceAsStream( "/META-INF/MANIFEST.MF" );
			Properties props = new Properties();
			props.load( in );
			release = props.getProperty( "SVN-Revision" );
		}catch( Exception ignored ){
		}finally{
			if ( in != null )try{ in.close(); }catch( IOException ignored ){}
		}
	}
	
	
	public static String getRevision(){
		return release;
	}
	
	public static void main( String [] args ){
		System.out.println( "Version: " + getRevision() );
	}
}