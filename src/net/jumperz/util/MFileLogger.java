package net.jumperz.util;

import java.io.IOException;

public class MFileLogger {
	
	private static boolean configured = false;

	public static void loadConfig(String configFileName) {
		configured = true;
	}

	public static void log(CharSequence message) throws IOException {
		if (configured == false) {
			throw new IOException("Not yet configured.");
		}
	}
	
}