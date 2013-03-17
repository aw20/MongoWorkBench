/* 
 *  Copyright (C) 2012 AW2.0 Ltd
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.aw20.io.ByteArrayInputStreamRaw;
import org.aw20.io.ByteArrayOutputStreamRaw;
import org.aw20.io.StreamUtil;


public final class FileUtil extends Object {

	
	/**
	 * Unzips the file into the directory
	 * 
	 * @param zipfile
	 * @param dest
	 * @throws IOException
	 */
	public static void unzipFile(File zipfile, File destination) throws IOException {

		ZipFile zipFile = null;

		try{
			zipFile = new ZipFile(zipfile);
			
			Enumeration files = zipFile.entries();
			File f = null;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			InputStream eis = null;
	
			while (files.hasMoreElements()) {
				try {
					ZipEntry entry = (ZipEntry) files.nextElement();
					eis = zipFile.getInputStream(entry);
	
					f = new File(destination.getAbsolutePath() + File.separator + entry.getName());
	
					if (entry.isDirectory()) {
						f.mkdirs();
						continue;
					} else {
						f.getParentFile().mkdirs();
						f.createNewFile();
					}
	
					// Read/Write file
					byte[] buffer = new byte[4096];
					int bytesRead = 0;
					fos = new FileOutputStream(f);
					bos	= new BufferedOutputStream(fos, 32000);
					while ((bytesRead = eis.read(buffer)) != -1)
						bos.write(buffer, 0, bytesRead);

					bos.flush();
					bos.close();

				} finally {
					StreamUtil.closeStream(fos);
					StreamUtil.closeStream(eis);
				}
			}

		}finally{
			if ( zipFile != null )
				zipFile.close();
		}
	}



	/**
	 * Zips all the files in the list and puts it into the zipFile
	 * 
	 * @param srcFiles
	 * @param zipFile
	 * @throws IOException
	 */
	public static void gzipFile( File[] srcFiles, File zipFile ) throws IOException {
		List<File>	list	= new ArrayList<File>(srcFiles.length);
		for (int x=0; x < srcFiles.length; x++ )
			list.add( srcFiles[x] );
		
		gzipFile( list, zipFile );
	}
	
	
	
	/** 
	 * Zips all the files in the list and puts it into the zipFile
	 * 
	 * @param srcFiles
	 * @param zipFile
	 */
	public static void gzipFile( List<File> srcFiles, File zipFile ) throws IOException {
		if ( zipFile.exists() )
			zipFile.delete();
		
		FileOutputStream fos 			= null;
		FileInputStream fis 			= null;
		BufferedInputStream bis 	= null;
		ZipOutputStream zos 			= null;
		byte[] buffer 						= new byte[32000];
		
		try{
			fos 	= new FileOutputStream(zipFile);
			zos 	= new ZipOutputStream(fos);
			
			Iterator<File> fit	= srcFiles.iterator();
			while ( fit.hasNext() ){
				File file	= fit.next();
				
				if ( file.isDirectory() )
					continue;
				
				zos.putNextEntry(new ZipEntry(file.getName()));

				fis = new FileInputStream(file);
				bis	= new BufferedInputStream( fis, 32000 );

	      int length;
	      while((length = bis.read(buffer)) > 0)
	       	zos.write(buffer, 0, length);
	
	      zos.flush();
	      zos.closeEntry();
	      StreamUtil.closeStream( bis );
	      StreamUtil.closeStream( fis );
			}

			zos.close();
		}finally{
			StreamUtil.closeStream(fos);
		}
	}
	
	
	
	/*
	 * This method takes a file and creates compressed version
	 */
	public static void gzipFile(String oldFile, String newFile) throws IOException {
		try {
			gzipFile(new File(oldFile), new File(newFile));
		} catch (Exception e) {
			return;
		}
	}

	
	public static void gzipFile(File oldFile, File newFile) throws IOException {
		FileInputStream in = null;
		FileOutputStream fout = null;
		GZIPOutputStream out = null;
		byte[] buffer = new byte[32000];
		int bytesRead;

		try {
			in = new FileInputStream(oldFile);
			fout = new FileOutputStream(newFile);
			out = new GZIPOutputStream(fout);

			while ((bytesRead = in.read(buffer)) != -1)
				out.write(buffer, 0, bytesRead);

			out.flush();

		} finally {
			StreamUtil.closeStream(in);
			StreamUtil.closeStream(out);
			StreamUtil.closeStream(fout);
		}
	}

	/*
	 * This method copies one file to another
	 */
	public static void copyFile(String oldFile, String newFile) throws IOException {
		FileInputStream inFile = null;
		FileOutputStream outFile = null;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			inFile = new FileInputStream(oldFile);
			outFile = new FileOutputStream(newFile);

			in = new BufferedInputStream(inFile, 32000);
			out = new BufferedOutputStream(outFile, 32000);
			byte[] buffer = new byte[32000];
			int bytesRead;

			while ((bytesRead = in.read(buffer)) != -1)
				out.write(buffer, 0, bytesRead);

			out.flush();
		} finally {
			StreamUtil.closeStream(in);
			StreamUtil.closeStream(out);
			StreamUtil.closeStream(inFile);
			StreamUtil.closeStream(outFile);
		}
	}

	public static void writeToFile(File _file, String _contents) throws IOException {
		BufferedWriter writer = null;
		FileWriter fwriter = null;

		try {
			fwriter = new FileWriter(_file);
			writer = new BufferedWriter(fwriter);

			writer.write(_contents);
			writer.flush();

		} finally {
			StreamUtil.closeStream(writer);
			StreamUtil.closeStream(fwriter);
		}
	}

	public static void writeToFile(File _file, String _contents, String _encoding) throws IOException {
		OutputStreamWriter writer = null;
		FileOutputStream fwriter = null;

		try {
			fwriter = new FileOutputStream(_file);
			writer = new OutputStreamWriter(fwriter, _encoding);

			writer.write(_contents);
			writer.flush();

		} finally {
			StreamUtil.closeStream(writer);
			StreamUtil.closeStream(fwriter);
		}

	}

	public static String readToString(File _file) throws IOException {
		return readToString(_file, null);
	}

	public static String readToString(File _file, String _encoding) throws IOException {
		BufferedReader reader = null;
		InputStreamReader inreader = null;
		CharArrayWriter writer = null;
		FileWriter fwriter = null;
		FileInputStream fin = null;

		try {
			fin = new FileInputStream(_file);
			inreader = _encoding == null ? new InputStreamReader(fin) : new InputStreamReader(fin, _encoding);
			reader = new BufferedReader(inreader);
			writer = new CharArrayWriter();

			char[] chars = new char[2048];
			int read;
			while ((read = reader.read(chars, 0, chars.length)) != -1) {
				writer.write(chars, 0, read);
			}

			return writer.toString();
		} finally {
			StreamUtil.closeStream(fin);
			StreamUtil.closeStream(inreader);
			StreamUtil.closeStream(fwriter);
		}
	}

	public static byte[] readBytes(File _file, int _bufferSize) throws IOException {
		ByteArrayOutputStream writer = null;
		FileInputStream fin = null;

		try {
			fin = new FileInputStream(_file);
			writer = new ByteArrayOutputStream();

			byte[] byteBuffer = new byte[_bufferSize];
			int read;
			while ((read = fin.read(byteBuffer)) != -1) {
				writer.write(byteBuffer, 0, read);
			}

			return writer.toByteArray();
		} finally {
			StreamUtil.closeStream(fin);
		}
	}

	public static String getFileExtension(File inFile) {
		if (inFile != null) {
			String name = inFile.getName();
			int c1 = name.lastIndexOf(".");
			if (c1 == -1)
				return "";
			else
				return name.substring(c1 + 1).toLowerCase();
		} else {
			return null;
		}

	}

	/**
	 * This method write a specified class object to a specified file.
	 * 
	 * @param _filename
	 *          The file which the object going to save to.
	 * @param _class
	 *          The specified object
	 */
	public static void saveClass(File _filename, Object _class) {
		saveClass(_filename, _class, false);
	}

	public static void saveClass(String _filename, Object _class) {
		saveClass(new File(_filename), _class, false);
	}

	public static void saveClass(File _file, Object _class, boolean _compress) {
		FileOutputStream FS = null;
		try {
			FS = new FileOutputStream(_file);
			saveClass(FS, _class, _compress);
		} catch (Exception E) {
			throw new IllegalArgumentException("Failed to save class");
		} catch (Throwable E) {
		} finally {
			StreamUtil.closeStream(FS);
		}
	}

	public static void saveClass(OutputStream _out, Object _class, boolean _compress) throws IOException {
		if (_compress) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream OOS = new ObjectOutputStream(bos);
			OOS.writeObject(_class);

			byte[] dataArray = bos.toByteArray();
			byte[] test = new byte[dataArray.length]; // this is where the byte array gets compressed to
			Deflater def = new Deflater(Deflater.BEST_COMPRESSION);
			def.setInput(dataArray);
			def.finish();
			def.deflate(test);
			_out.write(test, 0, def.getTotalOut());
		} else {
			ObjectOutputStream OS = new ObjectOutputStream(_out);
			OS.writeObject(_class);
		}
	}

	/**
	 * This method read a specified class object from a specified file and return this object.
	 * 
	 * @param _filename
	 *          the specified file
	 */

	public static Object loadClass(String _filename) {
		return loadClass(_filename, false);
	}

	public static Object loadClass(File _filename) {
		return loadClass(_filename, false);
	}

	public static Object loadClass(String _filename, boolean _uncompress) {
		try {
			return loadClass(new File(_filename), _uncompress);
		} catch (Exception e) {
			return null;
		}
	}

	public static Object loadClass(File _file, boolean _uncompress) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(_file);
			return loadClass(fis, _uncompress);
		} catch (Exception E) {
			return null;
		} finally {
			StreamUtil.closeStream(fis);
		}
	}

	public static Object loadClass(InputStream _inStream) {
		if (_inStream == null) {
			return null;
		}
		return loadClass(_inStream, false);
	}

	public static Object loadClass(InputStream _inStream, boolean _uncompress) {
		ObjectInputStream ois;
		if (_inStream == null) {
			return null;
		}
		try {
			if (_uncompress) {
				// we need to get the input as a byte [] so we can decompress (inflate) it.
				Inflater inflater = new Inflater();
				ByteArrayOutputStream bos;
				int bytesAvail = _inStream.available();
				if (bytesAvail > 0) {
					bos = new ByteArrayOutputStream(bytesAvail);
				} else {
					bos = new ByteArrayOutputStream();
				}

				byte[] buffer = new byte[1024];
				int read = _inStream.read(buffer);
				while (read > 0) {
					bos.write(buffer, 0, read);
					read = _inStream.read(buffer);
				}
				bos.flush();
				inflater.setInput(bos.toByteArray());

				bos.reset();
				buffer = new byte[32000];
				int inflated = inflater.inflate(buffer);
				while (inflated > 0) {
					bos.write(buffer, 0, inflated);
					inflated = inflater.inflate(buffer);
				}

				inflater.end();
				bos.flush();
				ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

				ois = new ObjectInputStream(bis);

			} else {
				ois = new ObjectInputStream(_inStream);
			}

			Object newObj = ois.readObject();
			_inStream.close();
			return newObj;
		} catch (Exception E) {
			System.err.println("awcommons.FileUtil.loadClass() Exception: " + E);
			return null;
		}
	}

	public static Object loadClass(byte[] src, boolean _uncompress) throws IOException, ClassNotFoundException, DataFormatException {
		return loadClass(src, src.length, _uncompress, new ByteArrayOutputStreamRaw(src.length * 7));
	}

	public static Object loadClass(byte[] src, int len, boolean _uncompress, ByteArrayOutputStreamRaw bos) throws IOException, ClassNotFoundException, DataFormatException {
		ObjectInputStream ois = null;

		try {
			if (_uncompress) {
				bos.reset();
				Inflater inflater = new Inflater();
				inflater.setInput(src, 0, len);

				byte[] buffer = new byte[32000];
				int inflated = inflater.inflate(buffer);
				while (inflated > 0) {
					bos.write(buffer, 0, inflated);
					inflated = inflater.inflate(buffer);
				}
				inflater.end();

				ois = new ObjectInputStream(new ByteArrayInputStreamRaw(bos.getByteArray(), bos.size()));
			} else {
				ois = new ObjectInputStream(new ByteArrayInputStreamRaw(src, src.length));
			}

			return ois.readObject();
		} finally {
			StreamUtil.closeStream(ois);
		}
	}

	public static Object getObject(ByteArrayOutputStream bos) throws IOException, ClassNotFoundException {
		if (bos == null) {
			return null;
		}

		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		return new ObjectInputStream(bis).readObject();
	}

	public static boolean recursiveDelete( File _f ) throws IOException{
		boolean result = true;
		File[] dirContent = _f.listFiles();
		if ( dirContent != null ){ // will be null if it's not a directory
			for (int i = 0; i < dirContent.length; i++) {
				if ( dirContent[i].isDirectory() ){
					if ( !recursiveDelete( dirContent[i] ) ){
						result = false;
					}
				}else if ( !dirContent[i].delete() ){
					result = false;
				}
			}

			if ( !_f.delete() ){
				result = false;
			}
		}
		
		return result;
	}
}