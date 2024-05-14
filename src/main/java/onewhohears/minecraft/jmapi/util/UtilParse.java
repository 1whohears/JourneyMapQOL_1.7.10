package onewhohears.minecraft.jmapi.util;

public class UtilParse {
	
	public static Integer decodeInt(String s) {
		try {
			Integer i = Integer.decode(s);
			return i;
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public static boolean hasPrefix(String test, String prefix) {
		return test.length() >= prefix.length() && test.substring(0, prefix.length()).equals(prefix);
	}
	
}
