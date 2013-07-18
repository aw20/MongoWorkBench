package org;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class doNotUse {

	/**
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {

		MongoClient	mc	= new MongoClient( "192.168.26.240", 49140 );
		
		DB	db	= mc.getDB("log");
		
		Object o = db.eval( "db.serverStatus()", (Object[])null );

		System.out.println(o);
	}

}
