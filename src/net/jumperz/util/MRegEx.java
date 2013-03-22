package net.jumperz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MRegEx {

	public static final String WORD_HEAD = "(?:\\A|[^a-zA-Z]{1})";
	public static final String WORD_TAIL = "(?:$|[^a-zA-Z]{1})";
	public static final String WORD_BETWEEN = "(?:\\W+?.*\\W+?|\\W+?)";


	public static String replaceAllIgnoreCase(String target, String regex, String to) {
		return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(target).replaceAll(to);
	}

	public static String replaceFirst(String target, String regex, String to) {
		String matchStr = getMatch(regex, target);
		if (matchStr.length() == 0) {
			return target;
		} else {
			int index = target.indexOf(matchStr);
			StringBuffer buf = new StringBuffer(target.length());
			buf.append(target.substring(0, index));
			buf.append(to);
			buf.append(target.substring(index + matchStr.length()));
			// System.out.println( buf.toString() );
			return buf.toString();
		}
	}

	public static List getInnerTextAsList(String target, String tagFrom, String tagTo) {
		if (tagFrom.equalsIgnoreCase(tagTo)) {
			// :(
			return new ArrayList();
		}
		try {
			List l = new ArrayList();
			while (true) {
				String matchFrom = getMatchIgnoreCase(tagFrom, target);
				if (matchFrom.equals("")) {
					break;
				}
				int index1 = MStringUtil.indexOf(target, matchFrom);
				target = target.substring(index1 + matchFrom.length());

				String matchTo = getMatchIgnoreCase(tagTo, target);
				if (matchTo.equals("")) {
					break;
				}
				int index2 = MStringUtil.indexOf(target, matchTo);

				l.add(target.substring(0, index2));
				target = target.substring(index2 + matchTo.length());
			}
			return l;
		} catch (Exception e) {
			return new ArrayList();
		}
	}

	public static String getMatch(String patternStr, String target) {
		Pattern pattern = Pattern.compile(patternStr, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(target);
		if (matcher.find()) {
			if (matcher.groupCount() > 0) {
				return matcher.group(1);
			} else {
				return target.substring(matcher.start(), matcher.end());
			}
		} else {
			return "";
		}
	}

	private static int indexOf(String target, Matcher matcher) {
		if (matcher.find()) {
			return target.indexOf(target.substring(matcher.start(), matcher.end()));
		} else {
			return -1;
		}
	}

	public static int indexOfIgnoreCase(String target, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(target);
		return indexOf(target, matcher);
	}

	public static int indexOf(String target, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(target);
		return indexOf(target, matcher);
	}

	public static String getMatchIgnoreCase(String patternStr, String target) {
		Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(target);
		if (matcher.find()) {
			if (matcher.groupCount() > 0) {
				return matcher.group(1);
			} else {
				return target.substring(matcher.start(), matcher.end());
			}
		} else {
			return "";
		}
	}

	public static boolean containsIgnoreCase(String target, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(target);
		return matcher.find();
	}

	public static boolean contains(String target, String patternStr) {
		Pattern pattern = Pattern.compile(patternStr, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(target);
		return matcher.find();
	}

	public static String[] split(String patternStr, String target) {
		return Pattern.compile(patternStr).split(target, -1);
	}
}