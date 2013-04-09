package net.jumperz.util;

import java.util.Comparator;
import java.util.TreeMap;

public class MJSMap extends TreeMap implements MJSObject {
	private static final long serialVersionUID = 1L;

	public static String charset = "Shift_JIS";

	public static boolean debug = false;

	// --------------------------------------------------------------------------------
	public MJSMap(Comparator c) {
		super(c);
	}

	// --------------------------------------------------------------------------------
	public MJSMap() {
	}

	// --------------------------------------------------------------------------------
	public String toString(int spaceLength) {
		return toString(spaceLength, charset);
	}

	// --------------------------------------------------------------------------------
	public String toString(int spaceLength, String _charset) {
		return "";
	}

	// --------------------------------------------------------------------------------
	public String toString() {
		return toString(0);
	}
	// --------------------------------------------------------------------------------
}