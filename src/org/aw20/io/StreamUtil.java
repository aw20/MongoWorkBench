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
 *  $Id: StreamUtil.java 2605 2011-11-24 06:41:18Z alan $
 *
 *
 */
package org.aw20.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

public class StreamUtil extends Object {
	
	public static InputStream getResourceStream(String resource, ClassLoader classLoader) throws IOException {
		URL url = classLoader.getResource(resource);
		return url.openStream();
	}

	public static InputStream getResourceStream(String resource) throws IOException {
		return getResourceStream(resource, StreamUtil.class.getClassLoader());
	}
	
	/**
	 * Convienace method for loading the resource in the class path to a string
	 * 
	 * @param srcObj
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static String readToString( Object srcObj, String resource ) throws IOException{
		return readToString( srcObj.getClass().getResourceAsStream(resource) );
	}
	
	public static String readToString( InputStream in ) throws IOException{
		return readToString( in, null );
	}
	
	public static String readToString( InputStream in, String _encoding ) throws IOException{
		BufferedReader reader = null;
		InputStreamReader inreader = null; 
		CharArrayWriter writer = null;
		
		try {
			inreader = _encoding == null ? new InputStreamReader(in) : new InputStreamReader(in, _encoding );
			reader = new BufferedReader( inreader );
			writer = new CharArrayWriter();
			
			char [] chars = new char[2048];
			int read;
			while ( (read=reader.read(chars, 0, chars.length) ) != -1 ){
				writer.write( chars, 0, read );
			}
			
			return writer.toString();
		}finally{
			if ( in != null )try{ in.close(); }catch( IOException ignored ){}
			if ( inreader != null )try{ inreader.close(); }catch( IOException ignored ){}
		}
	}

	
	public static void copyTo( InputStream from, OutputStream to ) throws IOException {
		copyTo( from, to, true );
	}
	
	public static void copyTo( InputStream from, OutputStream to, boolean closeStreams ) throws IOException {
		try{
			BufferedInputStream	bis 	= new BufferedInputStream( from, 8192 );
			BufferedOutputStream	bos = new BufferedOutputStream( to );
			
			byte[] b = new byte[ 8192 ];
			int c = 0;
			while ( (c=bis.read(b, 0, 8192)) != -1 ){
				bos.write(b, 0, c);
				bos.flush();
			}
			
		}finally{
			if ( closeStreams ){
				closeStream( from );
				closeStream( to );
			}
		}
	}
	
	
	/**
	 * Closes the stream ignoring any erros
	 * @param is
	 */
	public static void closeStream( InputStream is ){
		if ( is == null )return;
		
		try{
			is.close();
		}catch(Exception ignore){}
	}
	
	
	/**
	 * Closes the stream ignoring any erros
	 * @param is
	 */
	public static void closeStream( OutputStream os ){
		if ( os == null ) return;
		
		try{
			os.close();
		}catch(Exception ignore){}
	}

	public static void closeStream(Writer writer) {
		if ( writer == null ) return;
		
		try{
			writer.close();
		}catch(Exception ignore){}
	}

	public static void closeStream(Reader reader) {
		if ( reader == null ) return;
		
		try{
			reader.close();
		}catch(Exception ignore){}
	}
	
	public static void copyTo(Reader from, Writer to) throws IOException {
		copyTo(from,to,true);
	}
	
	public static void copyTo(Reader from, Writer to, boolean closeStreams) throws IOException {
		try{
			BufferedReader	bis 	= new BufferedReader( from, 8192 );
			BufferedWriter	bos 	= new BufferedWriter( to );
			
			char[] b = new char[ 8192 ];
			int c = 0;
					
			while ( (c=bis.read(b, 0, 8192)) != -1 ){
				bos.write(b, 0, c);
				bos.flush();
			}
			
		}finally{
			if ( closeStreams ){
				closeStream( from );
				closeStream( to );
			}
		}
	}
}
